#!/bin/bash

echo "Starting Azure MCP Server (Podman) in HTTP mode on port 3000..."
echo "This will use your Azure CLI credentials"
echo ""

# Check if Podman is available
if ! command -v podman &> /dev/null; then
    echo "Error: Podman is not installed. Please install Podman and try again."
    echo "Install: brew install podman (macOS) or see https://podman.io/getting-started/installation"
    exit 1
fi

# Check if Podman machine exists and is running (macOS/Windows)
if [[ "$OSTYPE" == "darwin"* ]] || [[ "$OSTYPE" == "msys" ]]; then
    if ! podman machine list 2>/dev/null | grep -q "podman-machine-default"; then
        echo "Podman machine does not exist. Initializing..."
        podman machine init
        echo "Starting Podman machine..."
        podman machine start
    elif ! podman machine list 2>/dev/null | grep -q "Currently running"; then
        echo "Podman machine is not running. Starting..."
        podman machine start
    fi
fi

# Get Azure CLI token directory for mounting
AZURE_CONFIG_DIR="${HOME}/.azure"

if [ ! -d "$AZURE_CONFIG_DIR" ]; then
    echo "Error: Azure CLI config not found at $AZURE_CONFIG_DIR"
    echo "Please run 'az login' first"
    exit 1
fi

echo "Using Azure config from: $AZURE_CONFIG_DIR"
echo ""

# Note: Azure MCP Server Docker image may not be publicly available yet
# Check available tags at: https://mcr.microsoft.com/v2/azure-mcp-server/tags/list
echo "Note: If the image pull fails, the Azure MCP Server Docker image may not be publicly available yet."
echo "Alternative: Use the NPM version with stdio transport (see start-azure-mcp.sh)"
echo ""

# Start Azure MCP server using Podman with HTTP transport
# Try common version tags if latest doesn't exist
for tag in "latest" "2.0.0-beta.19" "2.0" "1.0"; do
    echo "Trying to pull mcr.microsoft.com/azure-mcp-server:${tag}..."
    if podman pull mcr.microsoft.com/azure-mcp-server:${tag} 2>/dev/null; then
        echo "Successfully pulled image with tag: ${tag}"
        podman run --rm -it \
          --name azure-mcp-server \
          -p 3000:3000 \
          -v "${AZURE_CONFIG_DIR}:/root/.azure:ro,Z" \
          -e AZURE_SUBSCRIPTION_ID="${AZURE_SUBSCRIPTION_ID}" \
          mcr.microsoft.com/azure-mcp-server:${tag} \
          server start --transport http --port 3000 --namespace postgres
        exit 0
    fi
done

echo ""
echo "Error: Could not find Azure MCP Server Docker image."
echo "The Docker image may not be publicly available yet."
echo ""
echo "Alternative solution: Use NPM version with stdio transport"
echo "Run: ./start-azure-mcp.sh (stdio mode)"
exit 1

echo ""
echo "Azure MCP Server stopped."

# Made with Bob
