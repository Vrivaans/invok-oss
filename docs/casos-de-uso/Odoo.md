### Odoo

```json
[
  {
    "name": "Odoo 19",
    "code": "odoo19",
    "baseUrl": "http://localhost:8069",
    "authenticationType": "BEARER_TOKEN",
    "apiKeyLocation": "HEADER",
    "apiKeyName": "Authorization",
    "apiKeyValue": "<YOUR_API_KEY>",
    "customHeaders": {
      "X-Odoo-Database": "odoo",
      "Content-Type": "application/json"
    },
    "tools": [

      {
        "name": "CRM - Listar oportunidades",
        "code": "odoo-crm-list",
        "description": "Lista las oportunidades del CRM de Odoo. Devuelve nombre, empresa, email, etapa y probabilidad de cierre.",
        "endpointPath": "/json/2/crm.lead/search_read",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"domain\": [], \"fields\": [\"name\", \"partner_name\", \"email_from\", \"stage_id\", \"probability\", \"expected_revenue\"], \"limit\": {{limit}}}",
        "parameters": [
          {
            "name": "limit",
            "type": "NUMBER",
            "description": "Cantidad máxima de oportunidades a devolver.",
            "required": false,
            "defaultValue": "10"
          }
        ]
      },

      {
        "name": "CRM - Crear lead",
        "code": "odoo-crm-create",
        "description": "Crea una nueva oportunidad o lead en el CRM de Odoo.",
        "endpointPath": "/json/2/crm.lead/create",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"vals_list\": [{\"name\": \"{{name}}\", \"partner_name\": \"{{partner_name}}\", \"email_from\": \"{{email_from}}\", \"description\": \"{{description}}\"}]}",
        "parameters": [
          {
            "name": "name",
            "type": "STRING",
            "description": "Nombre de la oportunidad o lead.",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "partner_name",
            "type": "STRING",
            "description": "Nombre de la empresa o contacto.",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "email_from",
            "type": "STRING",
            "description": "Email del contacto.",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "description",
            "type": "STRING",
            "description": "Descripción o notas del lead.",
            "required": false,
            "defaultValue": ""
          }
        ]
      },

      {
        "name": "CRM - Actualizar oportunidad",
        "code": "odoo-crm-update",
        "description": "Actualiza los campos de una oportunidad existente en el CRM de Odoo dado su ID.",
        "endpointPath": "/json/2/crm.lead/write",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"ids\": [{{id}}], \"vals\": {\"name\": \"{{name}}\", \"probability\": {{probability}}}}",
        "parameters": [
          {
            "name": "id",
            "type": "NUMBER",
            "description": "ID de la oportunidad a actualizar.",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "name",
            "type": "STRING",
            "description": "Nuevo nombre de la oportunidad.",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "probability",
            "type": "NUMBER",
            "description": "Probabilidad de cierre en porcentaje (0-100).",
            "required": false,
            "defaultValue": ""
          }
        ]
      },

      {
        "name": "Project - Listar proyectos",
        "code": "odoo-project-list",
        "description": "Lista todos los proyectos activos en Odoo con su nombre, descripción y fechas.",
        "endpointPath": "/json/2/project.project/search_read",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"domain\": [], \"fields\": [\"name\", \"description\", \"date_start\", \"date\"], \"limit\": {{limit}}}",
        "parameters": [
          {
            "name": "limit",
            "type": "NUMBER",
            "description": "Cantidad máxima de proyectos a devolver.",
            "required": false,
            "defaultValue": "10"
          }
        ]
      },

      {
        "name": "Project - Listar tareas",
        "code": "odoo-project-task-list",
        "description": "Lista las tareas de proyectos en Odoo. Devuelve nombre, proyecto, etapa, responsable y fecha límite.",
        "endpointPath": "/json/2/project.task/search_read",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"domain\": [], \"fields\": [\"name\", \"project_id\", \"stage_id\", \"user_ids\", \"date_deadline\"], \"limit\": {{limit}}}",
        "parameters": [
          {
            "name": "limit",
            "type": "NUMBER",
            "description": "Cantidad máxima de tareas a devolver.",
            "required": false,
            "defaultValue": "10"
          }
        ]
      },

      {
        "name": "Project - Crear tarea",
        "code": "odoo-project-task-create",
        "description": "Crea una nueva tarea en un proyecto de Odoo.",
        "endpointPath": "/json/2/project.task/create",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"vals_list\": [{\"name\": \"{{name}}\", \"description\": \"{{description}}\", \"date_deadline\": \"{{date_deadline}}\"}]}",
        "parameters": [
          {
            "name": "name",
            "type": "STRING",
            "description": "Nombre de la tarea.",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "description",
            "type": "STRING",
            "description": "Descripción o detalle de la tarea.",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "date_deadline",
            "type": "STRING",
            "description": "Fecha límite en formato YYYY-MM-DD.",
            "required": false,
            "defaultValue": ""
          }
        ]
      }

    ]
  }
]
```
