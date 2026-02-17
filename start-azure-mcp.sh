#!/bin/bash

# Disable interactive browser authentication
export AZURE_IDENTITY_DISABLE_INTERACTIVE_BROWSER_CREDENTIAL=true
export AZURE_IDENTITY_DISABLE_MANAGED_IDENTITY_CREDENTIAL=true
export AZURE_IDENTITY_DISABLE_VISUAL_STUDIO_CODE_CREDENTIAL=true
export AZURE_IDENTITY_DISABLE_AZURE_POWERSHELL_CREDENTIAL=true
export AZURE_IDENTITY_DISABLE_AZURE_DEVELOPER_CLI_CREDENTIAL=true

# Start Azure MCP server
exec npx -y -p @azure/mcp@latest azmcp server start --transport stdio --namespace postgres --outgoing-auth-strategy UseHostingEnvironmentIdentity
