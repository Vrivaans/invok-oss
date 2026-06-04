---
name: invok-workflow-discovery
description: >
  Given a business automation request, discover the minimum set of Invok tools
  required to implement the workflow, resolve human-readable references into
  concrete platform identifiers, determine the logical workflow structure,
  and provide an export-ready tool list for workflow generation.

  This skill is platform-agnostic and works equally well for n8n, Make,
  Flowise, Langflow, and custom automation systems.
---

# Invok Workflow Discovery & Planning Skill

## Purpose

Given a business automation request, discover the **minimum set of Invok tools** required to implement the workflow, resolve human-readable references into concrete platform identifiers, determine the logical workflow structure, and provide an export-ready tool list for workflow generation.

This skill focuses on **discovery, planning, and context reduction**.

> **This skill does not generate workflow files, platform-specific JSON, or automation definitions.**
> Workflow generation should be handled by the agent using its knowledge of the target platform.
>
> **This skill does not define API behavior or response structures.**
> For Invok runtime patterns, response handling, and defensive extraction, use the `invok-runtime-patterns` skill.

---

## Discovery Strategy

When facing a workflow automation request, follow this sequence:

1. **Identify source systems** — where does the data come from?
2. **Identify destination systems** — where does it need to go?
3. **Search available Invok tools** — filter to candidate tools for each system
4. **Select only the necessary tools** — discard anything not directly required
5. **Resolve identifiers** — use discovery tools to turn human names into real IDs
6. **Build the workflow plan** — describe triggers, steps, filters, and actions
7. **Produce the export recommendation** — list only the tools required for generation

> **Never recommend exporting tools before discovery is complete.**
> Exporting all tools from a provider to "have options" defeats the purpose of this skill.

---

## Core Principles

### 1. Minimize Tool Usage

Identify the **smallest possible set** of Invok tools required to satisfy the request. Avoid pulling in entire providers when only one or two tools are needed.

### 2. Never Assume Identifiers

Human-readable names are **not** reliable identifiers. Resources like boards, lists, projects, pipelines, stages, customers, and repositories must be resolved to their actual IDs using discovery tools.

| ❌ Avoid | ✅ Do instead |
|---|---|
| Assume `list_id = "backlog"` | Use a list/search tool to resolve the actual ID |
| Invent or guess numeric IDs | Discover them with the appropriate tool |
| Ask the user for IDs that can be auto-discovered | Resolve automatically using Invok tools |

### 3. Resolve Configuration Early

Before writing the workflow plan, run any necessary discovery calls to collect real identifiers. Embed those resolved values directly in the plan.

**Generic resolution pattern:**
```
"Backlog" board  →  use <platform>-list-boards  →  board_id: "abc123"
"To Do" list     →  use <platform>-list-items   →  list_id:  "def456"
"ACME Corp"      →  use <crm>-search-contacts   →  contact_id: 7
```

### 4. Build a Logical Plan

Structure the workflow as:
- **Trigger** — what initiates the workflow (schedule, webhook, manual, event)
- **Discovery steps** — resolve IDs and configuration
- **Fetch / filter steps** — retrieve and narrow down data from source systems
- **Deduplication** — track what has already been processed
- **Transform steps** — shape data for the destination system
- **Action steps** — create, update, or notify

### 5. Reduce Context

Never recommend exporting all available tools. Start from the user's objective and narrow down. The goal is to reduce hundreds of tools into a small, focused set.

### 6. Prefer Discovery Over Assumptions

If information can be discovered automatically through available Invok tools, do so. Do not ask the user to look up IDs manually.

### 7. Remain Platform-Agnostic

This skill works equally for n8n, Make, Flowise, Langflow, and custom systems. The output describes **workflow intent and required tools** — not platform-specific JSON, nodes, or configuration syntax.

---

## Tool Selection Heuristics

When selecting tools from the available catalog:

- **Prefer read/list tools** for the discovery phase — they are safe to call and reveal real identifiers
- **Prefer create/update tools only after** required identifiers are fully resolved
- **Avoid exporting administrative tools** (e.g. delete, purge, admin-only operations) unless explicitly required by the workflow
- **Prefer specific tools over broad ones** — a tool scoped to one resource type is better than a general-purpose tool that requires extra filtering
- **When two tools seem equivalent**, prefer the one whose description more closely matches the workflow's intent

---

## Platform-Specific Conventions

During discovery, you may encounter platform-specific data structures, response formats, or field conventions.

**Do not assume standard shapes. Never assume API behavior.**

Use tool descriptions, tool schemas, and actual tool responses as the source of truth.

When a convention is discovered and is relevant to workflow generation, **document it in the workflow plan notes** so that the workflow generator can account for it.

Examples of conventions worth documenting when observed:

- Related entities represented as arrays instead of nested objects
- Identifiers returned as plain scalars instead of wrapped objects
- Non-standard pagination patterns
- Custom or non-HTTP status codes
- Composite or tuple-style fields
- Enum-restricted values for status or type fields

Only document conventions that are **actually observed during discovery** — not assumed in advance.

---

## Expected Output Structure

### 1. Workflow Summary
A concise description of the intended automation and its business value.

### 2. Required Tools
Only the Invok tool codes needed to implement the workflow.
```
<source>-list-records
<destination>-create-item
```

### 3. Resolved Values
All identifiers discovered through Invok calls. Document both the human name and the resolved value.
```
contact_id: 7        (ACME Corp)
board_id:   abc123   (Sales Pipeline board)
list_id:    def456   (New Leads list)
```

### 4. Workflow Plan

Describe each step with: what it does, which tool it calls, what it expects back, and any data transformation or platform-specific convention to account for.

```
Trigger: <describe trigger>

Step 1 — Fetch records from source system
  Tool: <source>-list-records
  Params: { ... }
  Expected response: list of record objects

Step 2 — Filter unprocessed records
  Logic: keep only records newer than last checkpoint
  State: persist last processed identifier

Step 3 — Transform each record for the destination
  Map source fields → destination fields
  Notes: <document any conventions observed here>

Step 4 — Create item in destination system
  Tool: <destination>-create-item
  Params: { <mapped fields> }
  Expected response: new item identifier
```

### 5. Export Recommendation
```
Export the following Invok tools for workflow generation:
  - <source>-list-records
  - <destination>-create-item

No additional tools are required.
```

---

## Non-Goals

This skill must NOT:
- Generate workflow JSON, n8n node configurations, or Make scenarios
- Expose secrets or API keys
- Assume identifiers without discovery
- Require exporting all available tools
- Embed knowledge of specific API response structures
- Optimize for a specific workflow platform

---

## Success Criteria

The skill is successful when:
- ✅ The minimum required tool set is identified
- ✅ All discoverable identifiers are resolved using Invok tools before planning
- ✅ The workflow logic is described step-by-step with clear responsibilities per step
- ✅ Any platform-specific conventions are documented in the plan notes — not assumed
- ✅ The output is consumable by any capable workflow-generation agent on any platform
- ✅ The resulting tool list is significantly smaller than the full catalog
