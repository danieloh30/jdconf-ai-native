import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AzureConfigTool {
    
    @Inject
    AzureConfig azureConfig;
    
    @Tool("Get Azure PostgreSQL connection. Returns parameters ready to use with postgres_database_query tool.")
    public String getAzurePostgresConfig() {
        try {
            String subscriptionId = azureConfig.subscriptionId().orElse("NOT_SET");
            String resourceGroup = azureConfig.resourceGroup();
            String server = azureConfig.postgres().server();
            String database = azureConfig.postgres().database();
            String user = azureConfig.postgres().user();
            String password = System.getenv("AZURE_POSTGRES_PASSWORD");
            
            return String.format("""
                Use these EXACT values for postgres_database_query tool:
                subscription="%s"
                resource-group="%s"
                server="%s"
                database="%s"
                user="%s"
                password="%s"
                auth-type="PostgreSQL"
                query="SELECT * FROM customers"
                
                Copy each parameter exactly as shown above.
                """,
                subscriptionId,
                resourceGroup,
                server,
                database,
                user,
                password != null ? password : "NOT_SET"
            );
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}