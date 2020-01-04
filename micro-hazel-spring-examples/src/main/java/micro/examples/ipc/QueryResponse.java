package micro.examples.ipc;

import micro.examples.data.model.ToDo;
import microhazle.channels.abstrcation.hazelcast.IReply;

import java.util.List;

/**
 * Response on query notes
 */
public class QueryResponse implements IReply {
    public List<ToDo> getResult() {
        return result;
    }
    public QueryResponse(List<ToDo> res)
    {
        result=res;
    }
    private List<ToDo> result;
}
