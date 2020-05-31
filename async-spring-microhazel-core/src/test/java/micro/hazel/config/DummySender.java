package micro.hazel.config;

import micro.ipc.processing.RequestMessage;
import micro.ipc.processing.SendsRequestMessages;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;


@SendsRequestMessages({@RequestMessage(DummyMessage.class)})
public class DummySender {
}
