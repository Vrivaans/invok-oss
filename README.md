# Invok OSS

Open-source **Model Context Protocol (MCP)** dynamic tool registry.  
Connect any LLM agent to any REST API without writing custom integration code.

## What is Invok?

Invok acts as a universal bridge between MCP clients (Claude Desktop, Cursor, Continue, etc.) and external REST APIs. Define API providers and tools once, and any MCP-compatible agent can discover and call them dynamically.

## Quick Start

```bash
docker compose up
```

The app starts on `http://localhost:8080` and a SQLite database is created automatically at `data/invok.db`.

On first run, a demo provider (JSONPlaceholder) with a `get_posts` tool is seeded automatically.

## Defining your first provider + tool

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

## Connect from an MCP client

Add to your MCP client configuration:

```json
{
  "mcpServers": {
    "invok": {
      "type": "http",
      "url": "http://localhost:8080/mcp"
    }
  }
}
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
| `POST` | `/api/import` | Import (Invok or OpenAPI format) |
| `GET` | `/api/export` | Export all definitions |
| `POST` | `/mcp` | MCP JSON-RPC endpoint |
| `GET` | `/mcp/tools/list` | List tools (legacy) |
| `POST` | `/mcp/tools/call` | Call tool (legacy) |

## Compatibility with Invok SaaS

The import/export format is compatible with the Invok SaaS platform. You can export definitions from the OSS version and import them into the SaaS version, and vice versa.

## Tech Stack

- Java 21
- Spring Boot 3.5.4
- SQLite
- Hibernate with Community Dialects
- Jasypt for secret encryption
- GraalVM Native Image compatible

## License

MIT
