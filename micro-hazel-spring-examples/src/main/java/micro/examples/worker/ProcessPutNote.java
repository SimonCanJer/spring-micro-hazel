package micro.examples.worker;

import micro.examples.data.model.ToDo;
import micro.examples.ipc.NotesPutMessage;
import micro.examples.ipc.NotesPutResponse;
import micro.examples.repository.IToDoRepository;
import microhazle.channels.abstrcation.hazelcast.IReply;
import microhazle.processors.api.AbstractProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * The class handles the NotePutMessage
 * @see NotesPutMessage
 */
public class ProcessPutNote extends AbstractProcessor<NotesPutMessage,String> {
    ProcessPutNote()
    {

    }
    public ProcessPutNote(IToDoRepository repo)
    {
        this.repo=repo;
        onReady();
    }
    //note that Autowired will have an effect when the class exported as a bean
    @Autowired
    IToDoRepository repo;
    @PostConstruct
    void onReady()
    {
        System.out.println(String.format("instance %s repo %s",String.valueOf(System.getProperty("app_instance")),String.valueOf(repo)));
    }
    @Override
    public void process(NotesPutMessage notesPutMessage) {
        ToDo toDo= new ToDo();
        toDo.setDescription(notesPutMessage.getToDo());
        toDo.setAddressee(notesPutMessage.getToWhom());
        toDo.setDeadLine(new Date(System.currentTimeMillis()+100000));
        repo.save(toDo);
        System.out.println("saved for "+toDo.getAddressee());
        reply(new NotesPutResponse(new Date(System.currentTimeMillis()).toString(),null));

    }

    @Override
    public Set<Class> announceRequestNeeded() {
        return new HashSet<>();
    }

    @Override
    protected <R extends IReply> void listener(String s, R r) {

    }
}
