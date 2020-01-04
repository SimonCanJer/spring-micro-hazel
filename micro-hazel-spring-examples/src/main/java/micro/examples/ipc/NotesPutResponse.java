package micro.examples.ipc;

import microhazle.channels.abstrcation.hazelcast.IReply;

/**
 * reponse of request to put note
 */
public class NotesPutResponse implements IReply {

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getError() {
        return error;
    }

    final String timeStamp;
    final String error;

    public NotesPutResponse(String timeStamp, String error) {
        this.timeStamp = timeStamp;
        this.error = error;
    }
}
