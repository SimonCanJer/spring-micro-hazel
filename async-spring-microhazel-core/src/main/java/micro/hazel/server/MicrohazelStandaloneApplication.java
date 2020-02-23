package micro.hazel.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;

/**
 * The API class provides initializer of non MVC/non tomcat, as wel other serverless
 * microservice communication over underlaying Hazelcast Queues mechanism
 * "micro.hazel.config' is mandatory, whewhile ${microhazel.standalone.config}"
 * refers to  classes configuring of custom business logic, which  is served by the underlaying Hazelcast
 * based communication mechanism.
 * @see #main (server is kept by waiting for join for message pooling threads
 */
@SpringBootApplication
@ComponentScans({@ComponentScan("micro.hazel.config"),@ComponentScan("${microhazel.standalone.config}")})
public class MicrohazelStandaloneApplication extends SpringApplication {
    static Runnable HOLDER;
    static void setHolder(Runnable r)
    {
        HOLDER=r;
    }

    /**
     * the standard main method to be called as for standalone application
     * @param args standard args
     */
    static public void main(String [] args)
    {
        if(null!=System.getProperty("hazelcast.discovery.skip")) {
            if (null == System.getProperties().get("server.port")) {
                System.setProperty("server.port", "-1");
            }
            if (null == System.getProperties().get("spring.application.name")) {
                System.setProperty("spring.application.name", "noName");
            }
            if (null == System.getProperties().get("services.populate.protocol")) {
                System.setProperty("services.populate.protocol", "noName");

            }
            if(System.getProperty(IStandalone.SERVICES_INET_PROTO)==null)
            {
                System.setProperty(IStandalone.SERVICES_INET_PROTO, "http");
            }
            if(System.getProperty(IStandalone.SERVICES_INET_PATTERNS)==null)
            {
                System.setProperty(IStandalone.SERVICES_INET_PATTERNS, "");
            }
        }

        SpringApplicationBuilder builder= new SpringApplicationBuilder();
        builder.web(WebApplicationType.NONE).sources(MicrohazelStandaloneApplication.class).run(args);
        HOLDER.run();

    }

    /**
     * This method should be called in a case when calling underlaying initialization from
     * upper level main method and another source class also must be considered(it cam be SpringApplication,of @Config annotatated for example)
     * @param upper
     * @param args
     */
    static public void main(Class upper,String [] args)
    {
        SpringApplicationBuilder builder= new SpringApplicationBuilder();
        builder.web(WebApplicationType.NONE).sources(upper,MicrohazelStandaloneApplication.class).run(args);
        HOLDER.run();

    }

}
