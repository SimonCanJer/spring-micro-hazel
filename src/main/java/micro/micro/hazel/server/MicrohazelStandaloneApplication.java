package micro.micro.hazel.server;

import microhazle.building.api.IBuild;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;

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
