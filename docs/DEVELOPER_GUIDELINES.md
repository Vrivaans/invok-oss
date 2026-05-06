# Invok Developer Guidelines

## Frontend Considerations

### 1. Form Duplication in Angular
Invok uses two primary templates for managing Provider and Tool creations:
- **`tools.html`**: Handles single Provider/Tool Creation and Editing.
- **`tools-batch.html`**: Handles the Batch Importer UI.

**CRITICAL:** Because these are two separate templates with duplicated structures, any changes made to the configuration enums (e.g., `AuthenticationTypeEnum`, `ApiKeyLocationEnum`, HTTP Methods) in the backend MUST be reflected in **both** HTML files simultaneously.

*Failure to do so will result in mismatched payloads, unrecognized Enums being sent to the backend, or missing dropdown options in one of the interfaces.*

### 2. State Management for Enums
When a new enum value is fundamentally added (e.g., `IN_BODY` for ApiKeyLocation), ensure that the dropdown options `<option value="IN_BODY">` accurately match the String representation of the enum in Java to prevent Jackson deserialization errors.

## Backend Considerations
- Custom headers are treated as an optional JSON string column (`customHeadersJson`) in `ApiProvider`. When writing execution services that consume APIs, ensure you safely parse this JSON before appending the headers to the RestClient.
- API Keys injected directly to the body (`IN_BODY`) must be explicitly handled by the tool execution engines to avoid polluting query strings or risking the keys not being consumed by target endpoints depending on the requested HTTP verb.
