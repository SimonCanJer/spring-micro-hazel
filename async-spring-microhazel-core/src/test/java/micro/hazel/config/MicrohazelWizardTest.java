package micro.hazel.config;

import micro.hazel.commons.Utilities;
import micro.hazel.server.IStandalone;
import microhazle.building.api.*;
import microhazle.channels.abstrcation.hazelcast.DTOReply;
import microhazle.channels.abstrcation.hazelcast.IMessage;
import microhazle.channels.abstrcation.hazelcast.IReply;
import microhazle.channels.abstrcation.hazelcast.ITransport;
import microhazle.processors.api.AbstractProcessor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.rmi.UnknownHostException;
import java.util.*;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(loader= AnnotationConfigContextLoader.class, classes={ MicrohazelWizardTest.Initial.class})
public class MicrohazelWizardTest {
  @Configuration
    static class Initial extends MicrohazelWizard
    { @Bean
      public static DummySender dummySender()
     {
        return new DummySender();
     }
     @Bean
     ProcessorProvider  processorProvider()
     {
         return new ProcessorProvider() {
             @Override
             protected <T extends IMessage, S extends Serializable> AbstractProcessor<T, S>[] getList() {
                 return new AbstractProcessor[] {new AbstractProcessor() {
                     @Override
                     public void process(ITransport iTransport) {

                     }

                     @Override
                     public Set<Class> announceRequestNeeded() {
                         return new HashSet<>();
                     }

                     @Override
                     protected void listener(String s, IReply iReply) {

                     }
                 }}  ;
             }
         };
     }


       @Bean
        ApplicationContextAware checkApplicationContext()
        {
            return new ApplicationContextAware() {
                @Override
                public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
                    Assert.assertNotNull(applicationContext.getBean(DummyMessage.class.getName()));
                }
            };
        }
    }

    static
    {
        Utilities.spyField(null, IBuild.class, "INSTANCE",IBuild.class,new SpyBuilder() );
        System.setProperty("server.port","8080");
        System.setProperty("services.federation.name","federation");
        System.setProperty("app_instance","1");
        System.setProperty("spring.application.name","app2");
        System.setProperty("services.populate.protocol","http");
        System.getProperties().put(IStandalone.SERVICES_INET_PATTERNS,"10.\\d{1,3}.\\d{1,3}.\\d{1,3}");
        //System.setProperty("hazelcast.discovery.skip","true");
    }
  @Autowired
    @Qualifier("micro.hazel.config.DummyMessage")
    IClientProducer<DummyMessage> queryChannel;
    @Autowired
    ConfigProperties cfg;

    static List<AbstractProcessor> processors = new ArrayList<>();
    static List<Class>             handled= new ArrayList<>();
    static String application;
    static boolean bHoldCalled = false;
    static  CustomEndPoint endPointReg;
     static class SpyBuilder implements IBuild
    {

        @Override
        public IMounter forApplication(String s) {
            application=s;
            return new IMounter() {


                @Override
                public <T extends IMessage, S extends Serializable> void addProcessor(AbstractProcessor<T, S> abstractProcessor) {
                    processors.add(abstractProcessor);
                }

                @Override
                public <T extends IMessage> void addRequestClass(Class<T> aClass) {
                    handled.add(aClass);
                }

                @Override
                public IClientRoutingGateway mountAndStart(Consumer<IClientRoutingGateway> consumer) {
                    return new IClientRoutingGateway() {
                        @Override
                        public <T extends IMessage> IClientProducer<T> getChannel(Class<T> aClass, Consumer<IClientProducer<T>> consumer) {
                            return new IClientProducer<T>() {
                                @Override
                                public boolean isConnected() {
                                    return true;
                                }

                                @Override
                                public <Response extends IReply> String post(T t, Consumer<DTOReply<Response>> consumer) throws UnknownHostException {
                                    return "OK";
                                }

                                @Override
                                public <R extends IReply> Mono<R> post(T t) throws UnknownHostException {
                                    return null;
                                }

                                @Override
                                public <R extends IReply> Future<R> send(T t) throws UnknownHostException {
                                    return null;
                                }
                            };
                        }
                    };
                }

                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void destroy() {

                }

                @Override
                public void holdServer() {
                    bHoldCalled=true;
               }

                @Override
                public IAServicePopulator endPointPopulator() {
                    return new IAServicePopulator() {
                        @Override
                        public void populateNameOnPort(CustomEndPoint customEndPoint, Collection<Pattern> collection, String s, int i) {
                            endPointReg = customEndPoint;
                        }

                        @Override
                        public void revokePopulated(String s) {

                        }

                        @Override
                        public void queryEndPoint(String s, List<CustomEndPoint> list, Consumer<List<CustomEndPoint>> consumer) {

                        }

                        @Override
                        public void complaignInvalid(CustomEndPoint customEndPoint) {

                        }
                    };
                }
            };
        }
    }
    @Test
   public void test()
   {
     assertNotNull(queryChannel);
     assertEquals(processors.size(),1);
     assertEquals(handled.size(),1);
     assertTrue("must contain DummyMessage",handled.contains(DummyMessage.class));
     assertFalse(bHoldCalled);
     assertNotNull(endPointReg);
     assertEquals(endPointReg.getName(),"app2");

   }


}