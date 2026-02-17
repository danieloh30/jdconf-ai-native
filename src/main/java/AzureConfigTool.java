import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AzureConfigTool {
    
    @Inject
    AzureConfig azureConfig;
    
    @Tool("Get the default Azure PostgreSQL connection configuration. Use these values when querying PostgreSQL unless the user specifies different values.")
    public String getAzurePostgresConfig() {
        try {
            return String.format("""
                Default Azure PostgreSQL Configuration:
                - Subscription ID: %s
                - Resource Group: %s
                - PostgreSQL Server: %s
                - Database: %s
                - User: %s
                - Auth Type: %s
                - Password: %s
                
                Use these values when calling postgres_database_query tool.
                Only ask the user if these values are not set (contain 'your-').
                """,
                azureConfig.subscriptionId(),
                azureConfig.resourceGroup(),
                azureConfig.postgres().server(),
                azureConfig.postgres().database(),
                azureConfig.postgres().user()
            );
        } catch (Exception e) {
            return "Error retrieving configuration: " + e.getMessage() +
                   ". Please ensure all environment variables are set: " +
                   "AZURE_SUBSCRIPTION_ID, AZURE_RESOURCE_GROUP, AZURE_POSTGRES_SERVER, " +
                   "AZURE_POSTGRES_DATABASE, AZURE_POSTGRES_USER";
        }
    }
}