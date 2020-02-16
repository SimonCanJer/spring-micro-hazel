package micro.examples.worker;

import micro.micro.hazel.server.MicrohazelStandaloneApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import java.net.ServerSocket;


/**
 * The main application class, initializing microservice
 * handling the NotePutMessage and NotesQueryMessage handling. DB operations,
 * The main method only initializes properties and  calls the API class
 * @see MicrohazelStandaloneApplication
 * The underlaying framework and API initialzies Hazelcast initialization,
 * queues population, IPC configures and handles message listening
 * involving the processors
 * @see ProcessPutNote
 * @see ProcessQuery
 */
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
        System.setProperty("microhazel.standalone.config","micro.examples.worker");//reference to root package of running application
        //basically we need here path to package, where concrete configuration is beeing located
        System.getProperties().put("services.federation.name","services.federation");
        /**
         * additionally we can populate over hazelcast port range where our service instances must be initialized in nw
         */
        System.getProperties().put("services.run_ports","9090,10000");

        System.getProperties().put("hazelcast.discovery.skip","true");
        MicrohazelStandaloneApplication.main(args);
    }

}
