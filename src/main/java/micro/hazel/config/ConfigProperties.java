package micro.hazel.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * The bean collect and exposes main properties
 * (will not have a effect in configuration classes)
 */
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
