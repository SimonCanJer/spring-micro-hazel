package micro.examples.facade;

import micro.examples.ipc.NotesPutMessage;
import micro.examples.ipc.NotesPutResponse;
import micro.examples.ipc.NotesQueryMessage;
import micro.examples.ipc.QueryResponse;
import micro.ipc.processing.RequestMessage;
import micro.ipc.processing.SendsRequestMessages;
import microhazle.building.api.IClientProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.rmi.UnknownHostException;
import java.util.Optional;

@RestController
@SendsRequestMessages({@RequestMessage(NotesQueryMessage.class),@RequestMessage(NotesPutMessage.class)})
public class Controller {
    @Autowired
    @Qualifier("micro.examples.ipc.NotesQueryMessage")
    IClientProducer<NotesQueryMessage> queryChannel;
    @Autowired
    @Qualifier("micro.examples.ipc.NotesPutMessage")
    IClientProducer<NotesPutMessage> putChannel;
    @PostConstruct
    void connect()
    {
        System.out.println("connected");

    }
    @GetMapping("/get_notes")
    @ResponseBody
    ResponseEntity<Mono<QueryResponse>> getNotesForThisUser(@RequestParam("for") String user)
    {

        try {
            Mono<QueryResponse> o=queryChannel.post((new NotesQueryMessage(user,null, "any")));
            return ResponseEntity.ok(o);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return (ResponseEntity<Mono<QueryResponse>>) ResponseEntity.of(Optional.empty()).status(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }
    @PutMapping("/put_note")
    @ResponseBody
    ResponseEntity<Mono<NotesPutResponse>> putNote(@RequestParam("for") String user, @RequestParam("what") String theme)
    {

        try {
            System.out.println("for="+user);
            Mono<NotesPutResponse> o=putChannel.post(new NotesPutMessage(user,theme,null));
           return ResponseEntity.ok(o);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return (ResponseEntity<Mono<NotesPutResponse>>) ResponseEntity.of(Optional.empty()).status(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }



}
