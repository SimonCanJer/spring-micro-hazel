package micro.hazel.server;

import java.util.function.Consumer;

public interface IStandalone {
    Consumer<Runnable> HOLDER_ASSIGNER=MicrohazelStandaloneApplication::setHolder;
    String PORT_RANGE_PROPERTY = "services.run_ports";
    String SERVICES_INET_PROTO="services.populate.protocol";
    String SERVICES_INET_PATTERNS="services.populate.inet_patterns";
}
