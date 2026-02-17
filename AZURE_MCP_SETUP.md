# Azure MCP Server Setup - Complete Configuration

## Overview
This document describes the complete setup for using Azure MCP Server with Quarkus LangChain4j to query Azure PostgreSQL databases.

## Files Created/Modified

### 1. application.properties
```properties
# Azure OpenAI Configuration
quarkus.langchain4j.azure-openai.api-key=${AZURE_OPENAI_API_KEY}
quarkus.langchain4j.azure-openai.api-version=2024-10-21
quarkus.langchain4j.azure-openai.deployment-name=gpt-4o-mini
quarkus.langchain4j.azure-openai.resource-name=doh-quarkus-mcp
quarkus.langchain4j.timeout=PT60S

# Azure MCP Server Configuration
quarkus.langchain4j.mcp.azure-rg.transport-type=stdio
quarkus.langchain4j.mcp.azure-rg.command=npx,-y,-p,@azure/mcp@latest,azmcp,server,start,--transport,stdio

# Azure PostgreSQL Default Configuration
azure.subscription-id=${AZURE_SUBSCRIPTION_ID}
azure.resource-group=${AZURE_RESOURCE_GROUP}
azure.postgres.server=${AZURE_POSTGRES_SERVER}
azure.postgres.database=${AZURE_POSTGRES_DATABASE}
azure.postgres.user=${AZURE_POSTGRES_USER}
azure.postgres.password=${AZURE_POSTGRES_PASSWORD:}
azure.postgres.auth-type=${AZURE_POSTGRES_AUTH_TYPE:MicrosoftEntra}

# Pass Azure subscription to MCP server
quarkus.langchain4j.mcp.azure-rg.environment.AZURE_SUBSCRIPTION_ID=${azure.subscription-id}

# Enable detailed logging
quarkus.log.category."dev.langchain4j.mcp".level=DEBUG
quarkus.log.category."io.quarkiverse.langchain4j.mcp".level=DEBUG
```

### 2. AzureConfig.java
Type-safe configuration mapping for Azure settings.

### 3. AzureConfigTool.java
LangChain4j tool that provides Azure configuration to the AI agent.

### 4. AzureConnectionService.java
Service class for managing Azure connection details.

### 5. CustomerAgent.java
AI service interface with MCP toolbox and configuration tool integration.

## Environment Variables Required

Set these environment variables before running the application:

```bash
export AZURE_OPENAI_API_KEY="your-azure-openai-key"
export AZURE_SUBSCRIPTION_ID="your-subscription-id"
export AZURE_RESOURCE_GROUP="your-resource-group"
export AZURE_POSTGRES_SERVER="your-postgres-server"
export AZURE_POSTGRES_DATABASE="your-database-name"
export AZURE_POSTGRES_USER="your-username"
export AZURE_POSTGRES_PASSWORD="your-password"  # Optional, only for PostgreSQL auth
export AZURE_POSTGRES_AUTH_TYPE="MicrosoftEntra"  # or "PostgreSQL"
```

## Azure Authentication

Before using the application, authenticate with Azure:

```bash
az login
```

Or use device code authentication:

```bash
az login --use-device-code
```

## How It Works

1. **User Query**: "Get all customer data from Azure postgres"

2. **AI Agent Workflow**:
   - Calls `getAzurePostgresConfig` tool to retrieve default configuration
   - Uses the configuration values (subscription, resource group, server, database, user)
   - Calls `postgres_database_query` MCP tool with the connection details
   - Returns the query results

3. **No User Prompting**: The agent uses configured defaults automatically

## Testing

1. Start the application:
   ```bash
   mvn quarkus:dev
   ```

2. Send a query:
   ```
   User: "Get all customer data from Azure postgres"
   ```

3. The agent will:
   - Retrieve configuration automatically
   - Query the database
   - Return results without asking for connection details

## Troubleshooting

### MCP Server Connection Issues
- Check that Node.js and npx are installed: `node --version && npx --version`
- Verify Azure MCP server is accessible: `npx -y @azure/mcp@latest --help`
- Check debug logs in the application output

### Authentication Issues
- Ensure `az login` was successful
- Verify Azure credentials: `az account show`
- Check that AZURE_SUBSCRIPTION_ID matches your active subscription

### Configuration Issues
- Verify all environment variables are set: `env | grep AZURE`
- Check application.properties for correct property names
- Review logs for configuration warnings

## Key Features

✅ Automatic MCP server connection
✅ Type-safe configuration with @ConfigMapping
✅ AI agent automatically uses default configuration
✅ No user prompting for connection details
✅ Debug logging for troubleshooting
✅ Seamless Azure PostgreSQL integration