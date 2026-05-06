### Trello

```json
[
  {
    "name": "Trello",
    "code": "trello",
    "baseUrl": "https://api.trello.com",
    "authenticationType": "API_KEY",
    "apiKeyLocation": "QUERY_PARAMETER",
    "apiKeyName": "key",
    "apiKeyValue": "<YOUR_TRELLO_API_KEY>",
    "customHeaders": {
      "Content-Type": "application/json",
      "Accept": "application/json"
    },
    "tools": [
      {
        "name": "Trello - Listar mis tableros",
        "code": "trello-get-my-boards",
        "description": "Obtiene la lista de tableros a los que tiene acceso el usuario autenticado. Útil para obtener el board_id.",
        "endpointPath": "/1/members/me/boards?token=<YOUR_TRELLO_TOKEN>",
        "httpMethod": "GET",
        "enabled": true,
        "isExportable": true,
        "parameters": []
      },
      {
        "name": "Trello - Listar listas de un tablero",
        "code": "trello-list-lists",
        "description": "Lista todas las columnas (listas) de un tablero de Trello. Útil para obtener los IDs de las columnas (list_id) antes de crear o mover tarjetas.",
        "endpointPath": "/1/boards/{board_id}/lists?token=<YOUR_TRELLO_TOKEN>",
        "httpMethod": "GET",
        "enabled": true,
        "isExportable": true,
        "parameters": [
          {
            "name": "board_id",
            "type": "STRING",
            "description": "ID del tablero de Trello.",
            "required": true
          }
        ]
      },
      {
        "name": "Trello - Listar tarjetas de un tablero",
        "code": "trello-list-cards",
        "description": "Lista todas las tarjetas de un tablero de Trello. Devuelve nombre, descripción, lista y estado de cada tarjeta.",
        "endpointPath": "/1/boards/{board_id}/cards?token=<YOUR_TRELLO_TOKEN>",
        "httpMethod": "GET",
        "enabled": true,
        "isExportable": true,
        "parameters": [
          {
            "name": "board_id",
            "type": "STRING",
            "description": "ID del tablero de Trello.",
            "required": true
          }
        ]
      },
      {
        "name": "Trello - Crear tarjeta",
        "code": "trello-create-card",
        "description": "Crea una nueva tarjeta en una lista específica de Trello.",
        "endpointPath": "/1/cards?token=<YOUR_TRELLO_TOKEN>",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"idList\": \"{{list_id}}\", \"name\": \"{{name}}\", \"desc\": \"{{description}}\"}",
        "parameters": [
          {
            "name": "list_id",
            "type": "STRING",
            "description": "ID de la lista (columna) donde crear la tarjeta. Usá trello-list-lists para obtenerlo.",
            "required": true
          },
          {
            "name": "name",
            "type": "STRING",
            "description": "Nombre de la tarjeta.",
            "required": true
          },
          {
            "name": "description",
            "type": "STRING",
            "description": "Descripción o detalle de la tarjeta.",
            "required": false
          }
        ]
      },
      {
        "name": "Trello - Mover tarjeta a otra lista",
        "code": "trello-move-card",
        "description": "Mueve una tarjeta existente a otra lista (columna) del tablero.",
        "endpointPath": "/1/cards/{card_id}?token=<YOUR_TRELLO_TOKEN>",
        "httpMethod": "PUT",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"idList\": \"{{list_id}}\"}",
        "parameters": [
          {
            "name": "card_id",
            "type": "STRING",
            "description": "ID de la tarjeta a mover.",
            "required": true
          },
          {
            "name": "list_id",
            "type": "STRING",
            "description": "ID de la lista destino.",
            "required": true
          }
        ]
      },
      {
        "name": "Trello - Actualizar tarjeta",
        "code": "trello-update-card",
        "description": "Actualiza el nombre o descripción de una tarjeta existente en Trello.",
        "endpointPath": "/1/cards/{card_id}?token=<YOUR_TRELLO_TOKEN>",
        "httpMethod": "PUT",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"name\": \"{{name}}\", \"desc\": \"{{description}}\"}",
        "parameters": [
          {
            "name": "card_id",
            "type": "STRING",
            "description": "ID de la tarjeta a actualizar.",
            "required": true
          },
          {
            "name": "name",
            "type": "STRING",
            "description": "Nuevo nombre de la tarjeta.",
            "required": false
          },
          {
            "name": "description",
            "type": "STRING",
            "description": "Nueva descripción de la tarjeta.",
            "required": false
          }
        ]
      },
      {
        "name": "Trello - Agregar comentario a tarjeta",
        "code": "trello-add-comment",
        "description": "Agrega un comentario a una tarjeta de Trello.",
        "endpointPath": "/1/cards/{card_id}/actions/comments?token=<YOUR_TRELLO_TOKEN>",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"text\": \"{{comment}}\"}",
        "parameters": [
          {
            "name": "card_id",
            "type": "STRING",
            "description": "ID de la tarjeta donde agregar el comentario.",
            "required": true
          },
          {
            "name": "comment",
            "type": "STRING",
            "description": "Texto del comentario.",
            "required": true
          }
        ]
      }
    ]
  }
]
```