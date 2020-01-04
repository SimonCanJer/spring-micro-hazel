package micro.hazel.config;

import microhazle.building.api.IClientProducer;
import microhazle.building.api.IClientRoutingGateway;
import microhazle.channels.abstrcation.hazelcast.*;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.rmi.UnknownHostException;
import java.util.concurrent.Future;
import java.util.function.Consumer;

class InjectedChannelBean<T extends IMessage> implements IClientProducer<T>
{


    private final Class<? extends IMessage> marshalled;
    private final IClientRoutingGateway router;
    private IClientProducer channel;


    InjectedChannelBean(Class<? extends IMessage> clazz, IClientRoutingGateway router, IReadyListener[] listener)
    {
        this.marshalled=clazz;
        this.router = router;
        listener[0]=this::connect;


    }

    void connect()
    {
        channel = router.getChannel( marshalled,(ch)->channel=  ch);

   }

    private <T extends ITransport> void setChannel(IProducerChannel<T> tiProducerChannel) {
    }


    @Override
    public boolean isConnected() {
        if(channel!=null)
            return channel.isConnected();
        return false;
    }

    @Override
    public <Response extends IReply> String post(T t, Consumer<DTOReply<Response>> consumer) throws UnknownHostException {
        if(!isConnected())
            return null;
        return channel.post(t,consumer);

    }

    @Override
    public <R extends IReply> Mono<R> post(T message) throws UnknownHostException {
        if(!isConnected())
            return null;
        return channel.post(message);
    }

    @Override
    public <R extends IReply> Future<R> send(T message) throws UnknownHostException {
        if(!isConnected())
            return null;
        return channel.send(message);
    }
}
