package micro.hazel.config;

import micro.hazel.server.IStandalone;
import org.springframework.beans.factory.annotation.Value;

/**
 * The bean collect and exposes main properties
 * (the same annotatiojs will not have a effect  in configuration classes)
 */
public class ConfigProperties
{
    @Value("${server.port}")
    String  port;
    @Value("${services.federation.name}")
    String federationName;
    @Value("${app_instance}")
    String application_instance;

    @Value("${spring.application.name}")
    String applicationName;

    @Value("${"+IStandalone.SERVICES_INET_PROTO+"}")
    String inetProto;

    @Value("${"+IStandalone.SERVICES_INET_PATTERNS+"}")
    String patterns;
    String getApplicationName()
    {
        return applicationName;
    }
    int getServicePort()
    {
        if(port==null)
            return -1;
        try
        {
            return Integer.parseInt(port);
        }
        catch(Throwable e)
        {
            return -1;
        }
    }
    public String hazelcastFederation()
    {
        return federationName;
    }
    public String applicationInstance()
    {
        return application_instance;
    }
    ConfigProperties()
    {

    }

    public String[] getPatterns() {
        if(patterns==null)
            return null;
        return patterns.split("//;");
    }
}
