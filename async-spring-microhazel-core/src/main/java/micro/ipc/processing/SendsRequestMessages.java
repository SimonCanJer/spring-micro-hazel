package micro.ipc.processing;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface SendsRequestMessages {
    RequestMessage[] value();
}
