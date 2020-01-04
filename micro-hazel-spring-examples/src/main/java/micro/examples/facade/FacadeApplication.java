package micro.examples.facade;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.net.ServerSocket;

@SpringBootApplication
@EnableWebMvc
@ComponentScan({"micro.examples.facade","micro.hazel.config",})
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
        SpringApplication.run(FacadeApplication.class);
    }
}
