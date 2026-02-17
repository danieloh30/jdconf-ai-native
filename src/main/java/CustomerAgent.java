import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import io.quarkiverse.langchain4j.mcp.runtime.McpToolBox;

@RegisterAiService(tools = AzureConfigTool.class)
public interface CustomerAgent {
    @SystemMessage("""
        You are a helpful Azure assistant with access to Azure PostgreSQL databases.
        Use the available tools to help users query and manage their PostgreSQL data.
        Answer questions directly and concisely.
        """)
    @McpToolBox("azure-rg")
    String chat(@UserMessage String userMessage);
}

// Made with Bob
