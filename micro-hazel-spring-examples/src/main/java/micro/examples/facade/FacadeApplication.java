package micro.examples.facade;

import micro.micro.hazel.server.IStandalone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.net.ServerSocket;

/**
 * The class initiaizes web application on facade of
 * ToDo assignments.
 */
@SpringBootApplication
@EnableWebMvc
@ComponentScan({"micro.examples.facade","micro.hazel.config"})
public class FacadeApplication {
    private static final int[] PORT_RANGE =new int[]{8080,8099} ;

    static public void main(String[] args)
    {
        for(int port=PORT_RANGE[0];port<=PORT_RANGE[1];port++) {
            try {
                ServerSocket server = new ServerSocket(port);
                server.close();
                System.getProperties().setProperty("server.port",String.valueOf(port));
                break;

            } catch (Exception e) {

            }
        }
        System.getProperties().put("services.federation.name","services.federation");
        System.getProperties().put("spring.application.name","notes_service");
        System.getProperties().put(IStandalone.SERVICES_INET_PROTO,"http");
        System.getProperties().put("app_instance","notes_service");
        System.getProperties().put(IStandalone.SERVICES_INET_PATTERNS,"10.\\d{1,3}.\\d{1,3}.\\d{1,3}");
        SpringApplication.run(FacadeApplication.class);
    }
}
