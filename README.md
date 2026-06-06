# Invok

**One MCP server. Every API you own.**

Invok is a self-hosteable dynamic tool proxy that turns any REST API into MCP tools — instantly, without writing a single line of server code. Connect your internal systems, third-party services, or any API with a spec to any AI agent in minutes.

> 📖 **Looking for the user guide?** Read the detailed [User Manual](docs/USER_MANUAL.md) to learn about connection configurations, dynamic authentication (like Bluesky), rules for providers vs. tools, security designs, and how to use the dynamic `invok_guide` assistant.

```
  Claude Desktop · Cursor · Claude Code · Open WebUI · Any MCP Client
                              │
                              ▼
                    ┌─────────────────┐
                    │   Invok Server  │  ← single MCP endpoint
                    │                 │
                    │  [Parser]       │  zero LLM token cost
                    │  [Auth Inject]  │  credentials never reach the LLM
                    │  [Context Filter│  expose only what the agent needs
                    └────────┬────────┘
                             │
           ┌─────────────────┼─────────────────┐
           ▼                 ▼                 ▼
      Your Internal      HubSpot API      Any REST API
         API                                you define
```

---

## Why Invok?

Most teams end up writing a custom MCP server for every API they want agents to use. That means maintaining N servers, managing credentials in N places, and hoping none of them drift out of sync.

Invok is one server for all of them.

| | Traditional approach | Invok |
|---|---|---|
| Add a new API | Write & deploy a new MCP server | Import an OpenAPI spec or JSON recipe |
| Credentials | Exposed or hardcoded per server | Encrypted at rest, injected server-side |
| Context control | All-or-nothing | Toggle specific endpoints as tools |
| Share integrations | Copy-paste code | Export a secret-free JSON recipe |
| Self-hosted | Per-server setup | Single Docker container |

---

## See It In Action

| Demo | Description |
|------|-------------|
| [▶ Invok + Odoo CRM](https://youtu.be/7seKdWbP6U0) | Control an ERP via natural language through a single MCP tool |
| [▶ LinkedIn + Bluesky with one prompt](https://youtu.be/pvSSlQ3orAQ) | Post to two platforms simultaneously — one agent, two APIs, zero extra servers |
| [▶ Export & Import Recipes](https://youtu.be/bXTKyPiqpLc) | Build an integration once, export it as JSON, share it with anyone |
| [▶ Agent-assisted recipe creation with invok_guide](https://www.youtube.com/watch?v=TaQCede3fg0) | Agent generates a ready-to-import recipe from API docs — human reviews and imports |

---

## How It Works

### 1. Zero-cost Parsing
Invok parses OpenAPI/Swagger specs programmatically — not by feeding them into an LLM. The agent receives clean, pre-formatted tool schemas with zero token overhead.

### 2. Credential Isolation
API keys, bearer tokens, and passwords are encrypted at rest with Jasypt and injected server-side on every request. The LLM never sees them — not in the prompt, not in logs.

### 3. Granular Context Control
A full Odoo or HubSpot API can have hundreds of endpoints. Exposing all of them to an LLM inflates token costs and causes hallucinations. Invok lets you selectively toggle which endpoints become tools, so the agent only sees what it needs.

### 4. Portable JSON Recipes
Once you configure an integration, export it as a clean, credential-free JSON file. Anyone on your team can import it in 30 seconds with their own credentials.

```
  [ Configure tools in dashboard ]
              │
              ▼
  [ Export recipe (secrets stripped) ]
              │
     ┌────────┴────────┐
     ▼                 ▼
  Share with team   Commit to git
     │                 │
     ▼                 ▼
  Import in 30s    Run in CI/CD
```

### 5. Prompt Injection Protection
External API responses are sanitized before reaching the LLM. Injection patterns like `ignore previous instructions` are redacted, and all external content is wrapped in semantic XML isolation tags so the model treats it as data, not instructions.

---

## Agent-Assisted Recipe Creation (Human-in-the-Loop)

One of the most powerful workflows in Invok is using your agent to **generate integration recipes** — while keeping a human in control of what actually gets imported.

Invok exposes a built-in tool called `invok_guide`. When called, it returns the full JSON schema, authentication patterns, validation checklist, and authoring rules needed to build a valid recipe.

**The workflow:**

```
  You: "Generate a recipe to integrate the GitHub API"
              │
              ▼
  Agent calls invok_guide → gets schema + rules
              │
              ▼
  Agent analyzes the API docs → produces ready-to-import JSON
              │
              ▼
  You review the recipe → you decide to import it
              │
              ▼
  POST /api/import → tools are now available to the agent
```

**Why the human does the import — not the agent:**

An agent that can register its own tools is an agent that can be manipulated into registering malicious ones. A prompt injection attack from an external API response could instruct the agent to silently add a rogue tool pointing to an attacker-controlled endpoint.

Invok's design is explicit: **you decide what the agent can do**. The agent helps you build integrations faster, but it never expands its own capabilities without your approval. This is consistent with the rest of Invok's security model — credential isolation, response sanitization, and context control all serve the same principle.

In practice this means:
- You ask the agent to generate a recipe for any API
- The agent produces the JSON using `invok_guide` as reference
- You review it, add your credentials, and import it
- The agent now has access to exactly what you approved

---

## Quick Start

### Run with Docker
```bash
docker compose up
```

### Run locally (Java 21 required)
```bash
./mvnw spring-boot:run
```

Server starts at `http://localhost:8080`. A SQLite database is created automatically at `data/invok.db`.

---

## Connect to Your MCP Client

### Streamable HTTP (Cursor, Claude Code, Open WebUI, most modern clients)
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

### Stdio Bridge (Claude Desktop, VS Code)
Download the [Invok Bridge binary](https://github.com/Vrivaans/handsai-bridge/releases) for your OS.

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

Create a `config.json` in the same directory as the binary:
```json
{
  "invokUrl": "http://localhost:8080/"
}
```

---

## Verify Your Setup

Import this public test recipe (no credentials required) to confirm everything works end-to-end:

```bash
curl -X POST http://localhost:8080/api/import \
  -H "Content-Type: application/json" \
  -d '[
    {
      "name": "Coinbase Public",
      "code": "coinbase-public",
      "baseUrl": "https://api.coinbase.com",
      "authenticationType": "NONE",
      "apiKeyLocation": "NONE",
      "isDynamicAuth": false,
      "customHeaders": {
        "Accept": "application/json",
        "User-Agent": "Invok/1.0"
      },
      "tools": [
        {
          "name": "Get Spot Price",
          "code": "coinbase-spot-price",
          "description": "Get the spot market price for a currency pair. Common pairs: BTC-USD, ETH-USD, SOL-USD.",
          "endpointPath": "/v2/prices/{currency_pair}/spot",
          "httpMethod": "GET",
          "enabled": true,
          "parameters": [
            {
              "name": "currency_pair",
              "type": "STRING",
              "description": "Currency pair with hyphen (e.g. BTC-USD, ETH-EUR).",
              "required": true,
              "defaultValue": "BTC-USD"
            }
          ]
        }
      ]
    }
  ]'
```

Then ask your agent: *"What is the current BTC-USD price?"*

> **Note:** The dashboard shows an orange status indicator until the first successful tool execution is recorded. After your agent calls the Coinbase tool, it will turn green.

---

## Real-World Example: Odoo CRM

Odoo uses a JSON-RPC structure that's difficult to feed directly to an LLM. Invok wraps it into a clean tool the agent can call naturally.

**Recipe definition:**
```json
{
  "name": "CRM - List Opportunities",
  "code": "odoo-crm-list",
  "description": "Lists active CRM opportunities returning name, company, stage, and probability.",
  "endpointPath": "/json/2/crm.lead/search_read",
  "httpMethod": "POST",
  "bodyPayloadTemplate": "{\"domain\": [], \"fields\": [\"name\", \"partner_name\", \"email_from\", \"stage_id\", \"probability\", \"expected_revenue\"], \"limit\": {{limit}}}",
  "parameters": [
    {
      "name": "limit",
      "type": "NUMBER",
      "description": "Maximum number of opportunities to retrieve.",
      "required": false,
      "defaultValue": "10"
    }
  ]
}
```

**What the agent calls:**
```json
{ "name": "odoo-crm-list", "arguments": { "limit": 5 } }
```

Invok handles the rest: injects auth headers, renders the body template, executes the request, returns the response.

---

## Pre-built Recipes

| Integration | File | Description |
|-------------|------|-------------|
| **Odoo** | [Odoo.md](docs/use-cases/Odoo.md) | CRM and ERP actions via natural language |
| **Trello** | [Trello.md](docs/use-cases/Trello.md) | Boards, lists, and cards |
| **Crypto APIs** | [APIS_PUBLICAS_CRYPTO.json](docs/use-cases/APIS_PUBLICAS_CRYPTO.json) | Coinbase, Binance, CoinGecko — no API key needed |
| **VPS Management** | [CUBEPATH_VPS_STATUS.json](docs/use-cases/CUBEPATH_VPS_STATUS.json) | Monitor and manage VPS instances |
| **Weather** | [CLIMA.md](docs/use-cases/CLIMA.md) | Multi-provider weather retrieval |

Import any of these via `POST /api/import`.

---

## Connect to Automation Platforms (n8n, Make, Zapier)

Invok can act as a secure, unified API Gateway for automation tools. You can expose all your active tools via a dynamic OpenAPI specification and execute them directly through raw HTTP requests without token formatting (TOON) or XML wrapping.

### n8n Workflow Exporter
Invok provides a built-in exporter that generates a fully formed, importable n8n workflow JSON containing pre-configured HTTP Request nodes for all selected tools.

To use it:
1. Click **Export Tools** in the dashboard header.
2. Select the specific tools or providers you want to export.
3. Choose one of the export options:
   * **n8n (Proxy)**: Nodes execute calls through your local Invok instance (using `/api/v1/execute/{code}`). Requires setting your Invok PAT Token.
   * **n8n (Direct)**: Nodes call the target API directly. Invok generates the workflow with correct endpoints, body structures, and authentication structures (using expressions for dynamic tokens). Credentials are exported masked so you can safely fill them in n8n.

### Automation Endpoints
* **Get OpenAPI Specification**: `GET /api/v1/recipes/openapi.json`
* **Execute raw tool**: `POST /api/v1/execute/{toolCode}`
* **Get n8n Workflow**: `GET /api/export/n8n-workflow?ids=...&name=...`

Since Invok OSS runs in a trusted local environment, these endpoints do not require authentication headers by default.

### n8n Configuration
1. Add an **HTTP Request** node.
2. Click **Import cURL** and paste the following to test executing a tool (e.g., `coinbase-spot-price`):
   ```bash
   curl -X POST http://localhost:8080/api/v1/execute/coinbase-spot-price \
     -H "Content-Type: application/json" \
     -d '{"currency_pair": "BTC-USD"}'
   ```
3. Execute the step to receive the raw JSON response directly.

### Make Configuration
1. Add the **HTTP -> Make an OpenAPI request** node.
2. Specify the OpenAPI document URL: `http://localhost:8080/api/v1/recipes/openapi.json`
3. That's it! Make will automatically import the list of all your tools and their corresponding parameters as dropdowns.

---

## API Reference

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/providers` | List registered API providers |
| `POST` | `/api/providers` | Register a new provider |
| `PUT` | `/api/providers/{id}` | Update a provider |
| `DELETE` | `/api/providers/{id}` | Remove a provider |
| `GET` | `/api/tools` | List registered tools |
| `POST` | `/api/tools` | Register a tool |
| `PUT` | `/api/tools/{id}` | Update a tool |
| `DELETE` | `/api/tools/{id}` | Remove a tool |
| `POST` | `/api/tools/{id}/validate` | Health check a tool endpoint |
| `POST` | `/api/tools/batch` | Batch register tools |
| `POST` | `/api/tools/call` | Execute a tool directly (for testing) |
| `POST` | `/api/tools/cache/refresh` | Manually clear and reload the tool cache |
| `POST` | `/api/import` | Import recipes or OpenAPI specs |
| `GET` | `/api/export` | Export full configuration as a recipe |
| `GET` | `/api/guide` | Integration guidelines for agent self-integration |
| `GET` | `/api/v1/recipes/openapi.json` | Get dynamic OpenAPI v3 spec for automation platforms |
| `POST` | `/api/v1/execute/{toolCode}` | Execute a tool raw, returning direct JSON |
| `GET` | `/llms.txt` | Get system conceptual specification and mental model (llms.txt) |
| `POST` | `/mcp` | Standard JSON-RPC endpoint (Streamable HTTP) |
| `GET` | `/mcp/tools/list` | List tools (stdio bridge) |
| `POST` | `/mcp/tools/call` | Execute a tool (stdio bridge) |

---

## Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `PORT` | `8080` | Server port |
| `JASYPT_ENCRYPTOR_PASSWORD` | `invok-oss-default-key` | Encryption key for stored credentials |
| `invok.health-check.enabled` | `false` | Enable periodic background health checks |

---

## Tech Stack

- **Java 21** with Virtual Threads
- **Spring Boot 3.5.4**
- **SQLite** via Hibernate Community Dialects
- **Jasypt** for symmetric credential encryption
- **Angular 19** dashboard (English and Spanish)
- **GraalVM Native Image** compatible
- **Docker** support included

---

### Invok OSS vs Invok Cloud

| Feature | OSS | Cloud |
| :--- | :---: | :---: |
| REST API → MCP tools | ✅ | ✅ |
| OpenAPI/Swagger parser | ✅ | ✅ |
| Admin dashboard | ✅ | ✅ |
| Credential isolation (Jasypt) | ✅ | ✅ |
| Prompt injection protection | ✅ | ✅ |
| JSON Recipes (import/export) | ✅ | ✅ |
| `invok_guide` tool | ✅ | ✅ |
| Session tokens / authentication | ❌ | ✅ |
| TOON response compression | ❌ | ✅ |
| Execution analytics & traceability | ❌ | ✅ |
| Multi-tenancy | ❌ | ✅ |
| Hosted infrastructure | ❌ | ✅ |

The OSS version is fully functional for self-hosted setups. The admin API has no session-based authentication, so it should run in a trusted environment (local machine or private network).


## Invok Cloud

Need analytics, full request/response traceability, team collaboration, and hosted infrastructure?

**[Invok Cloud →](https://useinvok.run)** is the production-ready hosted version with everything included.

---

## Specification for LLMs (llms.txt)

To help large language models (LLMs) and AI agents quickly understand the architecture, mental model, and boundaries of Invok, the project includes and dynamically serves a standard `llms.txt` file:

* **Specification File:** [llms.txt](file:///Users/ivanv/Desktop/VIDAL/programacion/invok/llms.txt)
* **Public Endpoint:** `GET /llms.txt`

### Dynamic Unification
The endpoint is dynamically served by the backend:
1. In **development**, it reads the `llms.txt` file directly from the project's root folder, allowing instant updates to be served without rebuilding.
2. In **production**, it falls back to the static resource packaged in the classpath (`static/browser/llms.txt`).

---

## License

GNU Affero General Public License v3 (AGPL-3.0)