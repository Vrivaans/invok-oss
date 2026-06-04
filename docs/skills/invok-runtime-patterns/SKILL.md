---
name: invok-runtime-patterns
description: >
  Documents the Invok HTTP execution API contract: how to call tools, how
  responses are structured, how to handle errors, and how to write defensive
  extraction code for workflow nodes.

  Use this skill when generating workflow code that calls Invok's
  /api/v1/execute/{toolCode} endpoint — not during the discovery and planning
  phase.
---

# Invok Runtime Patterns

## When to Use This Skill

Use this skill **after** workflow discovery and planning are complete, when you need to:

- Generate code nodes that call Invok and process responses
- Handle response extraction in workflow automation tools (n8n, Make, etc.)
- Understand why a response shape may differ from expectations
- Debug a failed tool execution

For discovery, planning, and tool selection, use the `invok-workflow-discovery` skill instead.

---

## Invok Execution Endpoint

```
POST http://<host>:8080/api/v1/execute/{toolCode}
Content-Type: application/json
X-HandsAI-Token: <access_token>

Body: { ...tool parameters as flat JSON object... }
```

All parameters defined in the tool's schema are passed as a single flat JSON object in the request body.

---

## Authentication

All calls to `/api/v1/execute/*` require the `X-HandsAI-Token` header.

```
X-HandsAI-Token: <access_token>
```

The token is **opaque** — it encodes tenant identity and scope. Invok decrypts and injects the real external API credentials server-side. The calling agent or workflow node **never sees raw API keys**.

On missing or invalid token: **HTTP `401`**.

---

## Response Format — The Golden Rule

**Invok passes through the raw upstream API response directly. There is no wrapper object.**

The HTTP response body IS the result itself. Do not expect:
- `{ result: ... }`
- `{ data: ... }`
- `{ success: true, result: ... }`

The response is exactly what the underlying external API returned, with no modification.

---

## Return Type Patterns

The shape of the response depends entirely on what the upstream API returns. Common patterns:

| Operation type | Typical response shape | Notes |
|---|---|---|
| **Create** (RPC-style) | Plain scalar — the new record ID | e.g. `42`, `"uuid-abc"` |
| **Create** (REST-style) | JSON object with the created resource | e.g. `{ "id": "abc", "name": "..." }` |
| **List / Search** | JSON array `[...]` directly | e.g. `[{ "id": 1 }, { "id": 2 }]` |
| **Read single** | JSON object `{...}` directly | e.g. `{ "id": 1, "status": "active" }` |
| **Update** | Updated object, `true`, or empty | Varies by API |
| **Delete** | `true`, `null`, or empty | Varies by API |

> You cannot know the exact shape in advance without testing or checking the tool's documentation.
> **Always write defensive extraction code.**

### Illustrative Examples

```jsonc
// Pattern A — Create returns a plain scalar (RPC-style APIs)
POST /api/v1/execute/some-tool-create
Body: { "name": "Jane Doe", "email": "jane@example.com" }
Response: 42               // ← plain integer, NOT { id: 42 }

// Pattern B — Create returns a JSON object (REST-style APIs)
POST /api/v1/execute/some-tool-create
Body: { "list_id": "abc123", "name": "Fix login bug" }
Response: {                // ← full resource object
  "id": "task-xyz",
  "name": "Fix login bug",
  "status": "open"
}

// Pattern C — List returns a JSON array directly
POST /api/v1/execute/some-tool-list
Body: { "limit": 10 }
Response: [                // ← plain array, NOT { result: [...] }
  { "id": 1, "name": "Jane Doe" },
  { "id": 2, "name": "John Smith" }
]
```

---

## Defensive Extraction Patterns

### Extracting a record ID after a create operation

```javascript
const raw = $input.first().json;

let recordId = null;
if (typeof raw === 'number') {
  recordId = raw;                               // plain integer (RPC-style)
} else if (typeof raw === 'string' && !isNaN(Number(raw))) {
  recordId = Number(raw);                       // stringified number
} else if (raw && typeof raw.id !== 'undefined') {
  recordId = raw.id;                            // REST-style { id: ... }
} else {
  // Last resort: find first positive numeric value in the object
  for (const val of Object.values(raw || {})) {
    if (typeof val === 'number' && val > 0) { recordId = val; break; }
  }
}

if (recordId === null || recordId === undefined) {
  throw new Error('Could not extract ID. Raw: ' + JSON.stringify(raw));
}
```

### Extracting a list from a list/search operation

```javascript
const raw = $input.first().json;

let items = [];
if (Array.isArray(raw)) {
  items = raw;                                  // direct array (most common)
} else if (raw && Array.isArray(raw.result)) {
  items = raw.result;                           // nested under "result"
} else if (raw && Array.isArray(raw.data)) {
  items = raw.data;                             // nested under "data"
} else if (raw && Array.isArray(raw.items)) {
  items = raw.items;                            // nested under "items"
} else {
  // Deep search: find first array value in the response object
  for (const val of Object.values(raw || {})) {
    if (Array.isArray(val)) { items = val; break; }
  }
}

if (items.length === 0) {
  throw new Error('No items found. Raw: ' + JSON.stringify(raw).substring(0, 300));
}
```

### Logging for debugging

Always log the raw response in code nodes while developing a workflow. Remove or reduce verbosity once stable.

```javascript
const raw = $input.first().json;
console.log('RAW Invok response:', JSON.stringify(raw).substring(0, 500));
```

---

## Error Responses

On execution failure, Invok returns **HTTP `500`**:

```json
{
  "error": "Execution failed",
  "message": "<human-readable reason from the upstream API or Invok>"
}
```

Common causes:
- The upstream API rejected the request (invalid parameters, missing required fields, quota exceeded)
- The tool is disabled or marked unhealthy in Invok
- A dynamic auth token expired and could not be refreshed

On missing or invalid `X-HandsAI-Token`: **HTTP `401`**.

---

## Platform-Specific Conventions

Some APIs expose field shapes or conventions that differ from REST norms. Do not assume standard shapes.

When you encounter an unusual convention, handle it explicitly in your code node and document it in a comment. Examples of conventions you may encounter:

- Related entities as **arrays** `[id, display_name]` instead of nested objects
- Identifiers as **plain scalars** instead of `{ id: ... }` objects
- **Non-standard pagination** (cursor-based, offset-based, page tokens)
- **Composite status fields** encoding multiple attributes
- **Enum-restricted values** for status, type, or category fields

The only source of truth is the actual response from the tool. Test with real calls during development.
