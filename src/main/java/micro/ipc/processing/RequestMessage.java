package micro.ipc.processing;

import microhazle.channels.abstrcation.hazelcast.IMessage;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMessage {
    Class<? extends IMessage> value();
    String systemId() default "";
}
