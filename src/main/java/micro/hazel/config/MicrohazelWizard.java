package micro.hazel.config;

import micro.ipc.processing.RequestMessage;
import micro.ipc.processing.SendsRequestMessages;
import micro.micro.hazel.server.IStandalone;
import microhazle.building.api.IBuild;
import microhazle.building.api.IClientProducer;
import microhazle.building.api.IClientRoutingGateway;
import microhazle.building.api.IMounter;
import microhazle.channels.abstrcation.hazelcast.*;
import microhazle.processors.api.AbstractProcessor;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Field;
import java.net.ResponseCache;
import java.util.*;
import java.util.function.Consumer;


@Configuration
public class MicrohazelWizard {
    Logger logger= Logger.getLogger(this.getClass());
   Set<Class<? extends microhazle.processors.api.AbstractProcessor>> processorBeans = new HashSet<>();
    Set<Class<? extends IMessage>> usedMessages = new HashSet<>();
    List<PosponedConnector> connectors = new ArrayList<>();
    Set<IReadyListener> theyNeedHazelReady = new HashSet<>();
    private Map<Class,String> mapClassToSystemId = new HashMap<>();
   /// Map<Class,InjectedChannel> mapInjectedChannels = new HashMap<>();


    @Bean
    ConfigProperties configProperties()
    {
        return new ConfigProperties();
    }

 @Bean
   public   BeanFactoryPostProcessor beanFactoryConfigured()
    {
        BeanFactoryPostProcessor handler = new BeanFactoryPostProcessor() {
            @Override
            public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

                Iterator<String> it= configurableListableBeanFactory.getBeanNamesIterator();
                while(it.hasNext())
                {

                    try {
                        BeanDefinition bd= configurableListableBeanFactory.getBeanDefinition(it.next());
                        handleBeanDefinition(bd,configurableListableBeanFactory);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        };
        return handler;

    };
    IClientRoutingGateway stub;

    static class PosponedConnector
    {
        final Class req;
        final Consumer consumer;


        PosponedConnector(Class req, Consumer consumer) {
            this.req = req;
            this.consumer = consumer;
        }
    }
   IClientRoutingGateway router = new IClientRoutingGateway() {


       @Override
       public <T extends IMessage> IClientProducer<T> getChannel(Class<T> aClass, Consumer<IClientProducer<T>> consumer) {
           if(stub==null)
           {
               connectors.add(new PosponedConnector(aClass,consumer));
               return null;
           }
           else
               return stub.getChannel(aClass,consumer);
       }
   };


    void handleBeanDefinition(BeanDefinition bd, ConfigurableListableBeanFactory configurableListableBeanFactory) throws ClassNotFoundException {
        Class c=Class.forName(bd.getBeanClassName());
        SendsRequestMessages ann= (SendsRequestMessages) c.getAnnotation(SendsRequestMessages.class);
        if(ann!=null)
        {

            logger.trace("bean which uses microhazel destination queue detectected : "+c);
            Arrays.stream(ann.value()).forEach(rm->handleRequestClass(rm,configurableListableBeanFactory));
        }


    }
    IReadyListener[] refReadyListener= new IReadyListener[1];

    private void handleRequestClass(RequestMessage rm, ConfigurableListableBeanFactory configurableListableBeanFactory) {
        usedMessages.add(rm.value());

        configurableListableBeanFactory.registerSingleton(rm.value().getName(),new InjectedChannelBean(rm.value(),router,refReadyListener));
        theyNeedHazelReady.add(refReadyListener[0]);
        logger.trace("request class handled "+rm.value()+" registered injectable bean for related channel");


    }
    @Bean IMounter mounter()
    {
        return new IMounter() {
            @Override
            public <T extends IMessage> void addProcessor(AbstractProcessor<T> abstractProcessor) {
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
        };
    }
    IMounter mounter;
    @Bean
   public  ApplicationContextAware onContextReady()
    {
        return new ApplicationContextAware() {
            @Override
            public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

                ConfigProperties properties= applicationContext.getBean(ConfigProperties.class);
                logger.trace("##Context ready, this is component of the federation: "+properties.hazelcastFederation());
                mounter= IBuild.INSTANCE.forApplication(properties.hazelcastFederation());
                Map<String, AbstractProcessor> map=applicationContext.getBeansOfType(AbstractProcessor.class);
                logger.trace("scanning processor beans: their map is "+map);
                map.values().stream().forEach(p-> mounter.addProcessor(p));
                try {
                    ProcessorProvider pp = applicationContext.getBean(ProcessorProvider.class);
                    if (pp != null) {
                        logger.trace("ProcessorProvider bean found, purging...");
                        pp.purge();
                    }
                }
                catch(Exception e)
                {

                }
                logger.trace("adding request classes");

                usedMessages.stream().forEach(c->mounter.addRequestClass(c));
                stub= mounter.mountAndStart((r)->{stub=r;System.out.println("Spring Microhazel : stub ready");});
                theyNeedHazelReady.stream().forEach(r->r.ready());
                IStandalone.HOLDER_ASSIGNER.accept(new Runnable() {
                    @Override
                    public void run() {
                        mounter.holdServer();
                    }
                });


            }
        };

    }



    private void connect(PosponedConnector c) {
        stub.getChannel(c.req,c.consumer);
    }


}
