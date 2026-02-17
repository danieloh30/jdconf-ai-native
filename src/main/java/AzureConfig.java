import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "azure")
public interface AzureConfig {
    String subscriptionId();
    String resourceGroup();
    
    PostgresConfig postgres();
    
    interface PostgresConfig {
        String server();
        String database();
        String user();
    }
}