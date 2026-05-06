# Invok OSS

Open-source **Model Context Protocol (MCP)** dynamic tool registry.  
Connect any LLM agent to any REST API without writing custom integration code.

<p align="center">
  <a href="https://www.youtube.com/watch?v=7seKdWbP6U0" target="_blank">
    <img src="docs/assets/lista-herramientas-home.png" alt="Invok dashboard" width="80%">
  </a>
</p>

## What is Invok?

Invok acts as a universal bridge between MCP clients (Claude Desktop, Cursor, Antigravity, etc.) and external REST APIs. Define API providers and tools once, and any MCP-compatible agent can discover and call them dynamically.

- **No auth required** — runs locally, everything is public
- **No API keys exposed to LLMs** — secrets are encrypted with Jasypt and injected server-side
- **OpenAPI import** — paste a Swagger/OpenAPI spec and get tools instantly
- **Export/Import** — share tool definitions as portable JSON with anyone

## See it in action

<p align="center">
  <a href="https://www.youtube.com/watch?v=7seKdWbP6U0" target="_blank">
    <img src="https://img.youtube.com/vi/7seKdWbP6U0/0.jpg" alt="Invok + Odoo demo" width="45%">
  </a>
  <a href="https://youtu.be/pvSSlQ3orAQ" target="_blank">
    <img src="https://img.youtube.com/vi/pvSSlQ3orAQ/0.jpg" alt="Invok + LinkedIn/Bluesky demo" width="45%">
  </a>
</p>

**Demo 1**: [Invok operando Odoo desde lenguaje natural](https://www.youtube.com/watch?v=7seKdWbP6U0) — ejecuta acciones reales sin integraciones a medida.  
**Demo 2**: [Posteando en LinkedIn y Bluesky con un solo prompt](https://youtu.be/pvSSlQ3orAQ) — múltiples herramientas coordinadas desde una instrucción.

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

Then open `http://localhost:8080` in your browser for the web UI, or connect directly via MCP.

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

Download the [Invok Bridge](https://github.com/Vrivaans/handsai-bridge/releases) binary for your OS.

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

Create a `config.json` next to the binary:
```json
{"invokUrl": "http://localhost:8080/"}
```

<p align="center">
  <img src="docs/assets/antigravity-detecta-tools.png" alt="MCP client detecting Invok tools" width="70%">
</p>

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

Paste a Swagger/OpenAPI JSON and get providers + tools instantly. Invok auto-detects paths, parameters, and HTTP methods.

```bash
curl -X POST http://localhost:8080/api/import \
  -H "Content-Type: application/json" \
  -d @openapi-spec.json
```

<p align="center">
  <img src="docs/assets/importacion-multiple-png.png" alt="Batch import from OpenAPI" width="70%">
</p>

## Export your definitions

Share your provider + tool configurations as portable JSON.

```bash
curl http://localhost:8080/api/export > my-tools.json
```

## Use cases

Invok ships with ready-to-use examples in [`docs/casos-de-uso/`](docs/casos-de-uso/):

| Use case | Description |
|----------|-------------|
| [Crypto APIs](docs/casos-de-uso/APIS_PUBLICAS_CRYPTO.json) | Coinbase, Binance, CoinGecko — import directo |
| [Odoo](docs/casos-de-uso/Odoo.md) | CRM/ERP operado desde lenguaje natural |
| [Trello](docs/casos-de-uso/Trello.md) | Gestión de tarjetas y tableros |
| [VPS](docs/casos-de-uso/CUBEPATH_VPS_STATUS.json) | Estado de VPS desde el agente |
| [Clima](docs/casos-de-uso/CLIMA.md) | Weather API multi-proveedor |

Each `.json` file can be imported directly via `POST /api/import`.

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
| `GET` | `/api/guide` | Integration guide (also `invok_guide` tool for LLMs) |
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

GNU Affero General Public License v3 (AGPL-3.0)
