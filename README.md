# Invok OSS

Open-source **Model Context Protocol (MCP)** dynamic tool registry.  
Connect any LLM agent to any REST API without writing custom integration code.

## What is Invok?

Invok acts as a universal bridge between MCP clients (Claude Desktop, Cursor, Antigravity, etc.) and external REST APIs. Define API providers and tools once, and any MCP-compatible agent can discover and call them dynamically.

- **No auth required** — runs locally, everything is public
- **No API keys exposed to LLMs** — secrets are encrypted with Jasypt and injected server-side
- **OpenAPI import** — paste a Swagger/OpenAPI spec and get tools instantly
- **Export/Import** — share tool definitions as portable JSON

## Quick Start

### Docker

```bash
docker compose up
```

The app starts on `http://localhost:8080` and a SQLite database is created automatically at `data/invok.db`.

### Local

```bash
./mvnw spring-boot:run
```

Then open `http://localhost:8080` in your browser to access the web UI.

## Connect from an MCP client

### Streamable HTTP (remote)

```json
{
  "mcpServers": {
    "invok": {
      "type": "streamable-http",
      "url": "http://localhost:8080/mcp"
    }
  }
}
```

### Bridge (stdio)

Download the [Invok Bridge](https://github.com/Vrivaans/handsai-bridge/releases) binary for your OS, then:

**Claude Desktop** (`claude_desktop_config.json`):
```json
{
  "mcpServers": {
    "invok": {
      "command": "/path/to/invok-mcp",
      "args": ["mcp"]
    }
  }
}
```

**VS Code** (`.vscode/settings.json`):
```json
{
  "mcp": {
    "servers": {
      "invok": {
        "command": "/path/to/invok-mcp",
        "args": ["mcp"]
      }
    }
  }
}
```

Create a `config.json` next to the bridge binary to set the Invok URL:
```json
{"invokUrl": "http://localhost:8080/"}
```

## Defining providers and tools

### Create a provider

```bash
curl -X POST http://localhost:8080/api/providers \
  -H "Content-Type: application/json" \
  -d '{
    "name": "My API",
    "code": "my_api",
    "baseUrl": "https://api.example.com",
    "authenticationType": "API_KEY",
    "apiKeyLocation": "HEADER",
    "apiKeyName": "X-API-Key",
    "apiKeyValue": "your-secret-key"
  }'
```

### Create a tool

```bash
curl -X POST http://localhost:8080/api/tools \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Get Users",
    "code": "get_users",
    "description": "Fetches users from the API",
    "providerId": 1,
    "endpointPath": "/users",
    "httpMethod": "GET",
    "parameters": [
      { "name": "page", "type": "NUMBER", "required": false }
    ]
  }'
```

## Import from OpenAPI spec

```bash
curl -X POST http://localhost:8080/api/import \
  -H "Content-Type: application/json" \
  -d @openapi-spec.json
```

## Export your definitions

```bash
curl http://localhost:8080/api/export > my-tools.json
```

## API Reference

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/providers` | List all providers |
| `POST` | `/api/providers` | Create provider |
| `PUT` | `/api/providers/{id}` | Update provider |
| `DELETE` | `/api/providers/{id}` | Delete provider |
| `GET` | `/api/tools` | List all tools |
| `POST` | `/api/tools` | Create tool |
| `PUT` | `/api/tools/{id}` | Update tool |
| `DELETE` | `/api/tools/{id}` | Delete tool |
| `POST` | `/api/tools/{id}/validate` | Health check tool |
| `POST` | `/api/tools/batch` | Create tools in batch |
| `POST` | `/api/tools/call` | Execute a tool directly |
| `POST` | `/api/import` | Import (Invok or OpenAPI format) |
| `GET` | `/api/export` | Export all definitions |
| `GET` | `/api/guide` | Integration guide (also available as `invok_guide` tool for LLMs) |
| `POST` | `/mcp` | MCP JSON-RPC (Streamable HTTP) |
| `GET` | `/mcp/tools/list` | List tools (legacy bridge) |
| `POST` | `/mcp/tools/call` | Call tool (legacy bridge) |

## Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `PORT` | `8080` | Server port |
| `JASYPT_ENCRYPTOR_PASSWORD` | `invok-oss-default-key` | Encryption key for stored secrets |
| `invok.health-check.enabled` | `false` | Enable automatic periodic health checks |

## Tech Stack

- Java 21 with Virtual Threads
- Spring Boot 3.5.4
- SQLite via Hibernate Community Dialects
- Jasypt for secret encryption
- Angular 19 frontend with i18n (EN/ES)
- GraalVM Native Image compatible
- Docker support

## License

MIT
