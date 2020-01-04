package micro.micro.hazel.server;

import microhazle.building.api.IBuild;
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
 * refers to configure  classes of business logic is served by underlaying Hazelcast based communication.
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
    static public void main(String [] args)
    {
        SpringApplicationBuilder builder= new SpringApplicationBuilder();
        builder.web(WebApplicationType.NONE).sources(MicrohazelStandaloneApplication.class).run(args);
        HOLDER.run();

    }
    static public void main(Class upper,String [] args)
    {
        SpringApplicationBuilder builder= new SpringApplicationBuilder();
        builder.web(WebApplicationType.NONE).sources(upper,MicrohazelStandaloneApplication.class).run(args);
        HOLDER.run();

    }

}
