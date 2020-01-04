package micro.micro.hazel.server;

import micro.micro.hazel.server.MicrohazelStandaloneApplication;

import java.util.function.Consumer;

public interface IStandalone {
    Consumer<Runnable> HOLDER_ASSIGNER=MicrohazelStandaloneApplication::setHolder;
}
