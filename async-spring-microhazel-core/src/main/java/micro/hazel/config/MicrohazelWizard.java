package micro.hazel.config;

import micro.ipc.processing.RequestMessage;
import micro.ipc.processing.SendsRequestMessages;
import micro.hazel.server.IStandalone;
import microhazle.building.api.*;
import microhazle.channels.abstrcation.hazelcast.*;
import microhazle.processors.api.AbstractProcessor;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Pattern;

/**
 * This is the essential configuration class
 * The configuration class exports interception beans
 * reacting on BeanFactory ready and context ready events,
 * initalize hazelcast mechanism and  exports request channels as beansd fro injection
 */

@Configuration
public class MicrohazelWizard {
    private Logger logger = Logger.getLogger(this.getClass());
    private Set<Class<? extends IMessage>> usedMessages = new HashSet<>();
    // private List<PosponedConnector> connectors = new ArrayList<>();
    private Set<IReadyListener> theyNeedHazelReady = new HashSet<>();
    private IClientRoutingGateway stub;

    @Autowired
    @Bean
    ConfigProperties configProperties() {
        return new ConfigProperties();
    }

    @Bean
    public BeanFactoryPostProcessor beanFactoryConfigured() {
        BeanFactoryPostProcessor handler = new BeanFactoryPostProcessor() {
            @Override
            public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

                Iterator<String> it = configurableListableBeanFactory.getBeanNamesIterator();
                String strBean = null;
                while (it.hasNext()) {
                    try {

                        BeanDefinition bd = configurableListableBeanFactory.getBeanDefinition(strBean = it.next());
                        handleBeanDefinition(bd, configurableListableBeanFactory);
                    } catch (Exception e) {
                        logger.info(" bean named as '" + strBean + "' is not processed, exception message +" + String.valueOf(e.getMessage()));
                    }
                }
            }

        };
        return handler;
    }


    static class PosponedConnector {
        final Class req;
        final Consumer consumer;
         PosponedConnector(Class req, Consumer consumer) {
            this.req = req;
            this.consumer = consumer;
        }
    }

    /**
     * we need the construction because router is passed to channel beam before context
     * is ready (when bean factory is ready), real router is not obtained still
     */
    IClientRoutingGateway router = new IClientRoutingGateway() {
        @Override
        public <T extends IMessage> IClientProducer<T> getChannel(Class<T> aClass, Consumer<IClientProducer<T>> consumer) {
            return stub.getChannel(aClass, consumer);
        }
    };

    /**
     * collects annotations marking classes as emitters of
     * request messages
     *
     * @param bd                               bean definition
     * @param configurableListableBeanFactory- factory
     * @throws ClassNotFoundException
     * @see SendsRequestMessages
     */
    void handleBeanDefinition(BeanDefinition bd, ConfigurableListableBeanFactory configurableListableBeanFactory) throws ClassNotFoundException {
        Class c = bd.getResolvableType().resolve();
        System.out.println("##Bean class "+bd.getBeanClassName());
        SendsRequestMessages ann = (SendsRequestMessages) c.getAnnotation(SendsRequestMessages.class);
        if (ann != null) {

            logger.trace("bean which uses microhazel destination queue detectected : " + c);
            Arrays.stream(ann.value()).forEach(rm -> handleRequestClass(rm, configurableListableBeanFactory));
        }
    }

    IReadyListener[] refReadyListener = new IReadyListener[1];

    /**
     * extracts information about request message emitted by a bean,
     * dynamically defines a bean backed by implementation of IClientProducer, to be injected
     * into beans are marked as message emiters
     *
     * @param rm                              request message definition
     * @param configurableListableBeanFactory - bean factory
     * @see IClientProducer
     * @see InjectedChannelBean
     */
    private void handleRequestClass(RequestMessage rm, ConfigurableListableBeanFactory configurableListableBeanFactory) {
        usedMessages.add(rm.value());
        configurableListableBeanFactory.registerSingleton(rm.value().getName(), new InjectedChannelBean(rm.value(), router, refReadyListener));
        theyNeedHazelReady.add(refReadyListener[0]);
        logger.trace("request class handled " + rm.value() + " registered injectable bean for related channel");
    }

    /**
     * the bean exports proxy of porcessor provider.
     * The proxy is necessary because IMounter used in the ProcessorProvider as autowired bean, which are created
     * before application context is ready
     *
     * @return
     * @see ProcessorProvider
     * @see IMounter
     * @see #handleRequestClass(RequestMessage, ConfigurableListableBeanFactory)
     */
    @Bean
    IMounter mounter() {
        return new IMounter() {
            @Override
            public <T extends IMessage, S extends Serializable> void addProcessor(AbstractProcessor<T, S> abstractProcessor) {
                mounter.addProcessor(abstractProcessor);
            }

            @Override
            public <T extends IMessage> void addRequestClass(Class<T> aClass) {
                mounter.addRequestClass(aClass);

            }

            @Override
            public IClientRoutingGateway mountAndStart(Consumer<IClientRoutingGateway> consumer) {
                return mounter.mountAndStart(consumer);
            }

            @Override
            public boolean isReady() {
                return mounter.isReady();
            }

            @Override
            public void destroy() {
                mounter.destroy();

            }

            @Override
            public void holdServer() {
                mounter.holdServer();
            }

            @Override
            public IAServicePopulator endPointPopulator() {
                return endPointPopulator();
            }
        };
    }

    IMounter mounter;

    /**
     * The handler is dedicated to initialize hazelcast infrastructure layer,
     * transmit message processors and list of expected requests.
     * It works both of behalf of producer(requestor) and consumer.
     *
     * @return
     */

    @Bean
    public ApplicationContextAware onContextReady() {
        return new ApplicationContextAware() {
            @Override
            public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

                ConfigProperties properties = applicationContext.getBean(ConfigProperties.class);
                logger.trace("##Context ready, this is component of the federation: " + properties.hazelcastFederation());
                // declare application id as it must be see by all the consumers processing hazelcast queue
                mounter = IBuild.INSTANCE.forApplication(properties.hazelcastFederation());
                // Normally it will work only on server(consumer)
                addProcessorsWhichAreDeclaredAsBeans(applicationContext);
                addProcessorsUsingProviderBean(applicationContext);
                logger.trace("adding request classes");
                usedMessages.forEach(c -> mounter.addRequestClass(c));
                if (null == System.getProperty("hazelcast.discovery.skip")) {
                    populateServiceLocation(applicationContext);
                }
                stub = mounter.mountAndStart((r) -> {
                    stub = r;
                    System.out.println("Spring Microhazel : stub ready");
                });
                theyNeedHazelReady.forEach(IReadyListener::ready);

                IStandalone.HOLDER_ASSIGNER.accept(new Runnable() {
                    @Override
                    public void run() {
                        mounter.holdServer();
                    }
                });
            }
        };
    }

    /**
     * adds processors which are exported way of using extensions of the
     * abstrcat class ProcessorProvider
     *
     * @param applicationContext
     * @see ProcessorProvider
     */
    private void addProcessorsUsingProviderBean(ApplicationContext applicationContext) {
        try {
            ProcessorProvider pp = applicationContext.getBean(ProcessorProvider.class);
            logger.trace("ProcessorProvider bean found, purging...");
            pp.purge();
        } catch (Exception e) {

        }
    }

    /**
     * adds message processors which are provided as beans / componnets/ services
     *
     * @param applicationContext
     */
    private void addProcessorsWhichAreDeclaredAsBeans(ApplicationContext applicationContext) {
        Map<String, AbstractProcessor> map = applicationContext.getBeansOfType(AbstractProcessor.class);
        logger.trace("scanning processor beans: their map is " + map);
        map.values().forEach(p -> mounter.addProcessor(p));
    }

   /* private void connect(PosponedConnector c) {
        stub.getChannel(c.req,c.consumer);
    }*/

    /**
     * The method searches for properties initializing and managing registration
     * embedded Tomcat server in Hazelcast populator.
     * The condition for the initialization is port and name properties are set to up
     * and have correct values. The properties are readen from ConfigProperties
     *
     * @param ctx
     * @see IStandalone
     * @see ConfigProperties
     */
    private final void populateServiceLocation(ApplicationContext ctx) {
        ConfigProperties cfg = ctx.getBean(ConfigProperties.class);
        int port = cfg.getServicePort();
        String name = cfg.getApplicationName();
        if (name != null && port > 1) {
            CustomEndPoint ep = new CustomEndPoint(name, "http");
            String[] patterns = cfg.getPatterns();
            List<Pattern> patternList = new ArrayList<>();
            if (patterns != null) {
                for (String s : patterns) {
                    Pattern p = Pattern.compile(s);
                    patternList.add(p);
                }
            }
            mounter.endPointPopulator().populateNameOnPort(ep, patternList, null, port);
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                @Override
                public void run() {
                    mounter.endPointPopulator().revokePopulated(ep.getName());
                }
            }));
        }
    }
}
