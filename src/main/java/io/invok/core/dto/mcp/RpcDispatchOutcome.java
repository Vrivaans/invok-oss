package io.invok.core.dto.mcp;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Result of handling one JSON-RPC message for Streamable HTTP (JSON responses only).
 */
public sealed interface RpcDispatchOutcome permits RpcDispatchOutcome.JsonRpcEnvelope, RpcDispatchOutcome.AcceptedNotification {

    /**
     * Return as HTTP 200 with {@code Content-Type: application/json}.
     */
    record JsonRpcEnvelope(JsonNode body) implements RpcDispatchOutcome {
    }

    /**
     * Client notification accepted; HTTP 202 with no body (Streamable HTTP).
     */
    record AcceptedNotification() implements RpcDispatchOutcome {
    }
}
