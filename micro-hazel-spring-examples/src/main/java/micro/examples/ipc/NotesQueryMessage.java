package micro.examples.ipc;

import microhazle.channels.abstrcation.hazelcast.IMessage;

import java.util.Date;

public class NotesQueryMessage implements IMessage {
    public String getNoteReason() {
        return noteReason;
    }

    public Date getExecutionDate() {
        return executionDate;
    }

    public String getUser() {
        return user;
    }

    final String noteReason;
    final Date   executionDate;
    final String user;

    public NotesQueryMessage(String user, Date executionDate, String reason) {
        this.noteReason =reason;
        this.executionDate = executionDate;
        this.user = user;
    }
}
