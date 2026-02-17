# Running the Application with Azure MCP Server

This guide explains how to run the application with the Azure MCP Server using Podman and HTTP transport.

## Prerequisites

1. **Podman** - Installed and running
   ```bash
   podman --version
   # macOS: brew install podman
   # Linux: sudo dnf install podman (Fedora/RHEL) or sudo apt install podman (Ubuntu)
   ```

2. **Azure CLI** - Installed and authenticated
   ```bash
   az login
   az account show  # Verify you're logged in
   ```

3. **Environment Variables** - Set your Azure PostgreSQL configuration
   ```bash
   export AZURE_OPENAI_API_KEY="your-azure-openai-key"
   export AZURE_SUBSCRIPTION_ID="your-subscription-id"
   export AZURE_RESOURCE_GROUP="your-resource-group"
   export AZURE_POSTGRES_SERVER="your-postgres-server"
   export AZURE_POSTGRES_DATABASE="your-database-name"
   export AZURE_POSTGRES_USER="your-username"
   export AZURE_POSTGRES_PASSWORD="your-password"  # Optional
   export AZURE_POSTGRES_AUTH_TYPE="MicrosoftEntra"  # or "PostgreSQL"
   ```

## Running the Application

### Step 1: Start the Azure MCP Server (Podman)

Open a **new terminal** and run:

```bash
./start-azure-mcp-http.sh
```

The script will:
1. Check if Podman is installed
2. Start Podman machine if needed (macOS/Windows)
3. Verify Azure CLI credentials exist
4. Start the Azure MCP Server container on port 3000
5. Mount your Azure CLI credentials into the container

You should see:
```
Starting Azure MCP Server (Podman) in HTTP mode on port 3000...
This will use your Azure CLI credentials
Using Azure config from: /Users/yourname/.azure

[Podman container starting...]
[MCP Server ready on port 3000]
```

**Keep this terminal running!** The MCP server container must be running for the application to work.

### Step 2: Start the Quarkus Application

Open a **second terminal** and run:

```bash
mvn quarkus:dev
```

The application will start and connect to the MCP server on http://localhost:3000

### Step 3: Test the Application

Once both are running, you can interact with the AI agent:

**Example Query:**
```
User: "Get all customer data from Azure postgres"
```

**Expected Behavior:**
1. AI calls `getAzurePostgresConfig()` to get connection details
2. AI uses those details with `postgres_database_query` MCP tool
3. Results are returned - no prompting for connection details!

## Troubleshooting

### Podman Not Installed

**Error:** "Podman is not installed"
- **Solution:** Install Podman
  - macOS: `brew install podman`
  - Linux: `sudo dnf install podman` or `sudo apt install podman`
- **Verify:** `podman --version`

### Podman Machine Not Running (macOS/Windows)

**Error:** "Podman machine is not running"
- **Solution:** The script will automatically start it, or run `podman machine start`
- **Verify:** `podman machine list`

### MCP Server Won't Start

**Error:** "Azure CLI config not found"
- **Solution:** Run `az login` and ensure you're authenticated
- **Verify:** `az account show` should display your account info
- **Check:** `ls ~/.azure` should show config files

**Error:** "Cannot pull container image"
- **Solution:** Ensure you have internet connection
- **Try:** `podman pull mcr.microsoft.com/azure-mcp-server:latest`

### Application Can't Connect to MCP Server

**Error:** "Connection refused" or "Failed to connect"
- **Solution:** Ensure the MCP server is running in the first terminal
- **Check:** Visit http://localhost:3000 in your browser (should show MCP server info)

### Configuration Not Found

**Error:** "Error retrieving configuration"
- **Solution:** Ensure all environment variables are set
- **Verify:** Run `env | grep AZURE` to see your variables

### Wrong Tools Being Called

**Error:** "Invalid resource" or tool errors
- **Solution:** The MCP server is configured to only expose PostgreSQL tools
- **Check:** Restart both the MCP server and application

## Architecture

```
┌─────────────────────┐
│  Quarkus App        │
│  (Port 8080)        │
│                     │
│  - CustomerAgent    │
│  - AzureConfigTool  │
└──────────┬──────────┘
           │ HTTP
           │
           ▼
┌─────────────────────┐
│  Podman Container   │
│  Azure MCP Server   │
│  (Port 3000)        │
│                     │
│  - PostgreSQL Tools │
│  - Azure CLI Auth   │
└──────────┬──────────┘
           │
           ▼
┌─────────────────────┐
│  Azure PostgreSQL   │
│                     │
│  - Your Database    │
└─────────────────────┘
```

## Stopping the Application

1. **Stop Quarkus:** Press `Ctrl+C` in the Quarkus terminal
2. **Stop MCP Server:** Press `Ctrl+C` in the MCP server terminal

## Production Deployment

For production, consider:
1. Running the MCP server as a systemd service with Podman
2. Using Podman's systemd integration: `podman generate systemd`
3. Using a reverse proxy (nginx) for the MCP server
4. Securing the MCP server with authentication
5. Using Azure Managed Identity instead of Azure CLI credentials
6. Running as rootless container for better security

## Support

For issues with:
- **MCP Server:** https://github.com/Microsoft/mcp
- **Quarkus LangChain4j:** https://docs.quarkiverse.io/quarkus-langchain4j/
- **This Application:** Check AZURE_MCP_SETUP.md