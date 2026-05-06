# AI Agents Contribution Guide

Welcome, fellow agent! If you are reading this, your human operator has tasked you with writing code, fixing bugs, or adding new features to **Invok OSS**.

This document outlines the core technical architecture, stack, and rules of the project. **Read this carefully before modifying the codebase.**

## Core Identity

Invok OSS is a bridging API. It acts as a universal dynamic registry that connects Model Context Protocol (MCP) clients with any external REST APIs without requiring humans to code custom integrations plugin-by-plugin.

Agents discover tools dynamically using MCP via the `invok-go-bridge`, which speaks `stdio` to the client and `HTTP REST` to the Invok OSS Spring Boot backend. Streamable HTTP (direct `POST /mcp`) is also supported.

## Tech Stack

- **Java 21 LTS**: Use modern language features like Records, Pattern Matching, and Virtual Threads (already enabled).
- **Spring Boot 3.5.4**: Foundation for the REST API and dependency injection.
- **GraalVM Native Image**: **CRITICAL!** The project must be compatible with GraalVM Ahead-Of-Time (AOT) compilation. See the GraalVM section below.
- **SQLite**: File-based database (`data/invok.db`), no external DB server needed. Hibernate via `hibernate-community-dialects`.
- **Lombok**: Heavily used for Getters, Setters, Builders, and SLF4J loggers.

## Project Conventions

- **No authentication** — all endpoints are public. No tokens, no sessions, no multi-tenancy.
- **Package**: `io.invok.core` — base package for all source code.
- **DTOs and Entities** are strictly separated. Do not return JPA Entities directly from Controllers. Use mapping structures.
- **Encryption**: API keys and secrets are encrypted via Jasypt (`PBEWithMD5AndDES`) before storage.
- **ID generation**: Uses `GenerationType.IDENTITY` (SQLite native autoincrement). Do NOT use TABLE or SEQUENCE strategies.

## GraalVM Compatibility Rules

To maintain the lightning-fast (< 1.5 seconds) startup times, Invok OSS can be compiled as a standalone GraalVM native executable.

When contributing, you **MUST** ensure GraalVM compatibility:
1. **Reflection & Dynamic Proxies**: If your feature uses reflection (like Jasypt, Jackson mixins, or dynamic class loading), you **MUST** register the types in `src/main/java/io/invok/core/config/NativeHintsConfig.java` using `RuntimeHintsRegistrar`.
2. **Resource Loading**: Do not rely on dynamic classpath scanning at runtime. Register resources in `NativeHintsConfig` if they are loaded dynamically.
3. **Testing**: Always ask the user to verify the native compilation after introducing complex architectural changes: `./mvnw -Pnative native:compile`.

## Database Conventions

We use **SQLite** via `hibernate-community-dialects` (`SQLiteDialect`).
- File location: `data/invok.db` (auto-created on first run).
- Schema: `ddl-auto=update` (Hibernate manages schema).
- Connection pool: `maximum-pool-size=1` (SQLite is single-writer by design).
- WAL journal mode enabled for reliability.

## Logging

- Use `@Slf4j` (Lombok) for all logging.
- Invok OSS is deeply instrumented to provide visibility. Log important execution paths (especially in `ToolExecutionService` and MCP handlers).

## MCP Protocol Reminders

If editing MCP communication:
- Invok OSS serves JSON-RPC 2.0 over Streamable HTTP (`POST /mcp`).
- Legacy bridge endpoints: `GET /mcp/tools/list`, `POST /mcp/tools/call`.
- Ensure strict compliance with standard MCP error codes and structures.
- Tools are resolved dynamically from the Database and cached in `ToolCacheManager`.
- The built-in `invok_guide` tool provides documentation to LLM agents on how to define new providers and tools.

Good luck, agent! Keep the code clean, the startup time low, and don't break the native image execution.
