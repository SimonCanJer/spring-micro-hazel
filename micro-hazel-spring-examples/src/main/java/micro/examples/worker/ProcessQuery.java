package micro.examples.worker;

import micro.examples.data.model.ToDo;
import micro.examples.ipc.NotesQueryMessage;
import micro.examples.ipc.QueryResponse;
import micro.examples.repository.IToDoRepository;
import microhazle.channels.abstrcation.hazelcast.IReply;
import microhazle.processors.api.AbstractProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

/**
 * The class handles  the
 * @see NotesQueryMessage
 */
public class ProcessQuery extends AbstractProcessor<NotesQueryMessage,String> {
    ProcessQuery(){}
    public ProcessQuery(IToDoRepository repo)
    {
        this.repository=repo;

    }
    //Autowired has an effectwhen exported as a bean.
    @Autowired
    IToDoRepository repository;
    @Override
    public void process(NotesQueryMessage notesQueryMessage) {
        List<ToDo> qRes= repository.queryByAdressee(notesQueryMessage.getUser());
        reply(new QueryResponse(qRes));

    }

    @Override
    public Set<Class> announceRequestNeeded() {
        return null;
    }

    @Override
    protected <R extends IReply> void listener(String s, R r) {

    }
}
