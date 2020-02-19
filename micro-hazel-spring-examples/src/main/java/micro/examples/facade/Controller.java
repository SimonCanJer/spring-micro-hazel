package micro.examples.facade;

import micro.examples.ipc.NotesPutMessage;
import micro.examples.ipc.NotesPutResponse;
import micro.examples.ipc.NotesQueryMessage;
import micro.examples.ipc.QueryResponse;
import micro.ipc.processing.RequestMessage;
import micro.ipc.processing.SendsRequestMessages;
import microhazle.building.api.IClientProducer;
import microhazle.channels.abstrcation.hazelcast.IReply;
import org.graalvm.compiler.core.common.SuppressSVMWarnings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.rmi.UnknownHostException;
import java.util.Optional;
import java.util.concurrent.Future;

/**
 * The class of controller handles Request for asynchronous query
 * and put operations assignments.
 * The class is a facade, all the operations executed by microservices.
 */
@RequestMapping("/toDo")
@RestController
//pay attention to the annotations announcing requests for remote execution:
// injected with the @Autowired channels has qualifier matching  a declared here class of messsages wich
// are to be transmitted via the injected channel
@SendsRequestMessages({@RequestMessage(NotesQueryMessage.class),@RequestMessage(NotesPutMessage.class)})
public class Controller {
    // Message producers for query and put requests are
    // injected
    @Autowired
    @Qualifier("micro.examples.ipc.NotesQueryMessage")
    IClientProducer<NotesQueryMessage> queryChannel;
    @Autowired
    @Qualifier("micro.examples.ipc.NotesPutMessage")
    IClientProducer<NotesPutMessage> putChannel;
    @PostConstruct
    void connect()
    {


    }
    @SuppressWarnings("all")
    @GetMapping("/notes_sync")
    @ResponseBody
    ResponseEntity<QueryResponse> notesForThisUser(@RequestParam("for") String user)
    {

        try {
            Future<QueryResponse> future =queryChannel.send((new NotesQueryMessage(user,null, "any")));

            return ResponseEntity.ok(future.get());
        } catch (Exception e) {
            e.printStackTrace();
            return (ResponseEntity<QueryResponse>) ResponseEntity.of(Optional.empty()).status(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @SuppressWarnings("all")
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
    @SuppressWarnings("all")
    @PutMapping("/put_note")
    @ResponseBody
    ResponseEntity<Mono<NotesPutResponse>> putNote(@RequestParam("for") String user, @RequestParam("what") String theme)
    {

        try {
           Mono<NotesPutResponse> o=putChannel.post(new NotesPutMessage(user,theme,null));
           return ResponseEntity.ok(o);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return (ResponseEntity<Mono<NotesPutResponse>>) ResponseEntity.of(Optional.empty()).status(HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }



}
