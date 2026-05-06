# AI Agents Contribution Guide 🤖🛠️

Welcome, fellow agent! If you are reading this, your human operator has tasked you with writing code, fixing bugs, or adding new features to **Invok**.

This document outlines the core technical architecture, stack, and rules of the project. **Read this carefully before modifying the codebase.**

## Core Identity

Invok is a bridging API. It acts as a universal dynamic registry that connects Model Context Protocol (MCP) clients with any external REST APIs without requiring humans to code custom integrations plugin-by-plugin.

Agents discover tools dynamically using MCP via the `invok-go-bridge`, which speaks `stdio` to the client and `HTTP REST` to the Invok Spring Boot backend.

## Tech Stack

- **Java 21 LTS**: Use modern language features like Records, Pattern Matching, and Virtual Threads (already enabled).
- **Spring Boot 3.5.4**: Foundation for the REST API and dependency injection.
- **GraalVM Native Image**: **CRITICAL!** The project must be compatible with GraalVM Ahead-Of-Time (AOT) compilation. See the GraalVM section below.
- **PostgreSQL**: The backing database stored in a dedicated instance. Configuration relies on `PostgreSQLDialect`.
- **Lombok**: Heavily used for Getters, Setters, Builders, and SLF4J loggers.

## GraalVM Compatibility Rules

To maintain the lightning-fast (< 1.5 seconds) startup times essential for local AI agents, Invok is compiled as a standalone GraalVM native executable.

When contributing, you **MUST** ensure GraalVM compatibility:
1. **Reflection & Dynamic Proxies**: GraalVM's "closed-world assumption" means it removes classes that aren't explicitly referenced. If your feature uses reflection (like Jasypt, Jackson mixins, or dynamic class loading), you **MUST** register the types in `src/main/java/org/dynamcorp/invok/config/NativeHintsConfig.java` using `RuntimeHintsRegistrar`.
2. **Resource Loading**: Do not rely on dynamic classpath scanning at runtime. Register resources (like bundled `*.json` templates or files in `src/main/resources`) in `NativeHintsConfig` if they are loaded dynamically.
3. **Testing**: Always ask the user to verify the native compilation after introducing complex architectural changes: `./mvnw -Pnative native:compile`.

## Database Conventions

We use **PostgreSQL**. 
- Schema changes currently rely on Hibernate `update`.
- DTOs and Entities are strictly separated. Do not return JPA Entities directly from Controllers. Use mapping structures.

## Logging

- Use `@Slf4j` (Lombok) for all logging.
- Invok is deeply instrumented to provide visibility. Log important execution paths (especially in `ToolExecutionService` and MCP handlers). Remember, the Human relies on the console logs to see what the active AI agent is doing.

## MCP Protocol Reminders

If editing MCP communication:
- Invok serves JSON-RPC 2.0.
- Ensure strict compliance with standard MCP error codes and structures.
- Tools are resolved dynamically from the Database and cached in `ToolCacheManager`.

Good luck, agent! Keep the code clean, the startup time low, and don't break the native image execution.
