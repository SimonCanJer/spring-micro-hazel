package micro.hazel.config;

import microhazle.building.api.IMounter;
import microhazle.channels.abstrcation.hazelcast.IMessage;
import microhazle.processors.api.AbstractProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

public abstract class ProcessorProvider  {
    @Autowired
    IMounter mounter;
    protected <T extends IMessage > void addProcessor(AbstractProcessor<T> p)
    {
        mounter.addProcessor(p);

    }
    protected abstract   <T extends IMessage > AbstractProcessor<T>[] getList();
    void purge()
    {
        Arrays.stream(getList()).forEach(p->mounter.addProcessor(p));
    }
}
