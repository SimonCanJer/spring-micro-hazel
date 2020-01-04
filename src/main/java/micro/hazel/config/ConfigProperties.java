package micro.hazel.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


public class ConfigProperties
{
    @Value("${services.federation.name}")
    String federationName;
    public String hazelcastFederation()
    {
        return federationName;
    }
    ConfigProperties()
    {

    }
}
