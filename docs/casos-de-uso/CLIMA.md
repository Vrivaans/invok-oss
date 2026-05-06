# Caso de Uso: API del Clima (WeatherAPI)

Este ejemplo muestra cómo integrar [WeatherAPI](https://www.weatherapi.com/) con Invok para que tu LLM pueda consultar el clima actual de cualquier ciudad.

## 1. Registrar el proveedor y la herramienta

Importá el siguiente JSON desde la UI de Invok (`/api/import/providers`) o directamente via `curl`:

```bash
curl -X POST http://localhost:8080/api/import/providers \
  -H "Content-Type: application/json" \
  -d @CASO_DE_USO_CLIMA.json
```

```json
[
  {
    "name": "API Weather",
    "code": "weather",
    "baseUrl": "https://api.weatherapi.com",
    "authenticationType": "API_KEY",
    "apiKeyLocation": "QUERY_PARAMETER",
    "apiKeyName": "key",
    "apiKeyValue": "<YOUR_API_KEY>",
    "tools": [
      {
        "name": "Servicio de clima",
        "code": "api-clima",
        "description": "Obtiene el clima actual para una ciudad específica.",
        "endpointPath": "/v1/current.json",
        "httpMethod": "GET",
        "parameters": [
          {
            "name": "q",
            "type": "STRING",
            "description": "Nombre de la ciudad",
            "required": true,
            "defaultValue": ""
          }
        ]
      }
    ]
  }
]
```

> Reemplazá `<YOUR_API_KEY>` con tu clave de [WeatherAPI](https://www.weatherapi.com/). El registro es gratuito.

## 2. Verificar que la herramienta está disponible

Una vez importado, verificá que el LLM puede descubrirla:

```bash
curl http://localhost:8080/mcp/tools/list
```

Deberías ver `"Servicio de clima"` en la lista de herramientas.

## 3. Resultado en tu LLM

Con Invok Bridge corriendo, tu LLM ya puede usar la herramienta directamente. Por ejemplo, al preguntarle:

> *"¿Cuál es el clima actual en Buenos Aires?"*

El modelo llama automáticamente a `Servicio de clima` con `q: "Buenos Aires"` y recibe la respuesta de WeatherAPI en tiempo real.
