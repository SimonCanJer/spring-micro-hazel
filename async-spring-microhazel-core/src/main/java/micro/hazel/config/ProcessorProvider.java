package micro.hazel.config;

import microhazle.building.api.IMounter;
import microhazle.channels.abstrcation.hazelcast.IMessage;
import microhazle.processors.api.AbstractProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.Arrays;

/**
 * The abstrcat class prototypes beans exporting
 * message processors to mounter
 * @see AbstractProcessor
 * @see IMounter
 *
 */
public abstract class ProcessorProvider  {
    @Autowired
    IMounter mounter;
    protected <T extends IMessage, S extends Serializable > void addProcessor(AbstractProcessor<T,S> p)
    {
        mounter.addProcessor(p);

    }
    protected abstract   <T extends IMessage, S extends Serializable> AbstractProcessor<T,S>[] getList();
    void purge()
    {
        Arrays.stream(getList()).forEach(p->mounter.addProcessor(p));
    }
}
