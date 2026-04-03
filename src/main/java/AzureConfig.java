import io.smallrye.config.ConfigMapping;
import java.util.Optional;

@ConfigMapping(prefix = "azure")
public interface AzureConfig {
    Optional<String> subscriptionId();
    String resourceGroup();
    
    PostgresConfig postgres();
    
    interface PostgresConfig {
        String server();
        String database();
        String user();
    }
}