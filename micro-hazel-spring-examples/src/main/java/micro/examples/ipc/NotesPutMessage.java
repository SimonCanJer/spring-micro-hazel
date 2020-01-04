package micro.examples.ipc;

import microhazle.channels.abstrcation.hazelcast.IMessage;

import java.util.Date;

public class NotesPutMessage implements IMessage {
    public String getToWhom() {
        return toWhom;
    }

    public String getToDo() {
        return toDo;
    }

    public Date getDeadLine() {
        return deadLine;
    }

    String toWhom;
    String toDo;
    Date deadLine;

    public NotesPutMessage(String toWhom, String toDo, Date deadLine) {
        this.toWhom = toWhom;
        this.toDo = toDo;
        this.deadLine = deadLine;
    }
}
