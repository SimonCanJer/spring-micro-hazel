package micro.examples.worker;

import micro.examples.ipc.NotesPutMessage;
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

/**
 * The configuration class configures
 * back end processors handling  note put and query requests.
 * Works for backend microservice which collaborates to facade
 * @see NotesPutMessage
 * @see micro.examples.ipc.NotesQueryMessage
 * @see IToDoRepository
 * @see ProcessorProvider
 */
@Configuration
@EnableJpaRepositories("micro.examples.repository")
@EntityScan("micro.examples.data.model")
@ComponentScan("micro.hazel.config")
public class ConfigFlow {

    ConfigFlow()
    {

    }
    @Autowired
    IToDoRepository repo;

    /**
     * Populates a processor provider class exporting message processors
     * @return
     */
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
