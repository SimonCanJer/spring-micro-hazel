package micro.examples.worker;

import micro.examples.repository.IToDoRepository;
import micro.hazel.config.ProcessorProvider;
import microhazle.channels.abstrcation.hazelcast.IMessage;
import microhazle.processors.api.AbstractProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories("micro.examples.repository")
@EntityScan("micro.examples.data.model")
@ComponentScan("micro.hazel.config")
public class ConfigFlow {

    ConfigFlow()
    {
        System.out.println("config flow");
    }
    @Autowired
    IToDoRepository repo;
    @Bean
    ProcessorProvider provideProcessors()
    {
        return new ProcessorProvider() {
            @Override
            protected <T extends IMessage> AbstractProcessor<T>[] getList() {
                return new AbstractProcessor[]{new ProcessPutNote(repo),new ProcessQuery(repo)};
            }
        };
    }


}
