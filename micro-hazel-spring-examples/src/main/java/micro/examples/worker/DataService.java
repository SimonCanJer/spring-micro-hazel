package micro.examples.worker;

import micro.micro.hazel.server.MicrohazelStandaloneApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.net.ServerSocket;

//@SpringBootApplication
//@ComponentScan("micro.examples.worker")
public class DataService {

    private static final int[] PORT_RANGE = new int[]{9090,10000} ;

    static public void main(String[] args)
    {

         System.setProperty("hibernate.dialect","org.hibernate.dialect.H2Dialect");
        System.setProperty("spring.datasource.url","jdbc:h2:mem:dummy;DB_CLOSE_DELAY=-1");
        System.setProperty( "hibernate.hbm2ddl.auto","create");
        System.setProperty( "spring.datasource.driverClassName","org.h2.Driver");
        System.setProperty( "spring.datasource.username","");
        System.setProperty( "spring.datasource.password","");
        System.setProperty("app_instance","Data Service");
        /*SpringApplication.run(DataService.class);*/
        System.setProperty("microhazel.standalone.config","micro.examples.worker");
        MicrohazelStandaloneApplication.main(args);
    }

}
