import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AzureConnectionService {
    
    @Inject
    AzureConfig azureConfig;
    
    public String getConnectionDetails() {
        return String.format("""
            Default Azure PostgreSQL Connection:
            - Subscription: %s
            - Resource Group: %s
            - Server: %s
            - Database: %s
            - User: %s
            """,
            azureConfig.subscriptionId(),
            azureConfig.resourceGroup(),
            azureConfig.postgres().server(),
            azureConfig.postgres().database(),
            azureConfig.postgres().user()
        );
    }
    
}

// Made with Bob
