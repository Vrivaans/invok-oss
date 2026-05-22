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
        "name": "Producto - Listar productos",
        "code": "odoo-product-list",
        "description": "Lista los productos en Odoo. Devuelve nombre, tipo, precio y costo.",
        "endpointPath": "/json/2/product.template/search_read",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"domain\": [], \"fields\": [\"name\", \"type\", \"list_price\", \"standard_price\"], \"limit\": {{limit}}}",
        "parameters": [
          {
            "name": "limit",
            "type": "NUMBER",
            "description": "Cantidad máxima de productos a devolver.",
            "required": false,
            "defaultValue": "10"
          }
        ]
      },
      {
        "name": "Calendar - Listar eventos",
        "code": "odoo-calendar-event-list",
        "description": "Lista los eventos y reuniones de calendario en Odoo.",
        "endpointPath": "/json/2/calendar.event/search_read",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"domain\": [], \"fields\": [\"name\", \"start\", \"stop\", \"description\", \"partner_ids\"], \"limit\": {{limit}}}",
        "parameters": [
          {
            "name": "limit",
            "type": "NUMBER",
            "description": "Cantidad máxima de eventos a devolver.",
            "required": false,
            "defaultValue": "10"
          }
        ]
      },
      {
        "name": "Mail - Listar actividades",
        "code": "odoo-mail-activity-list",
        "description": "Lista las actividades pendientes de correo en Odoo.",
        "endpointPath": "/json/2/mail.activity/search_read",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"domain\": [], \"fields\": [\"res_model\", \"res_id\", \"summary\", \"date_deadline\", \"state\", \"user_id\"], \"limit\": {{limit}}}",
        "parameters": [
          {
            "name": "limit",
            "type": "NUMBER",
            "description": "Cantidad máxima de actividades a devolver.",
            "required": false,
            "defaultValue": "10"
          }
        ]
      },
      {
        "name": "Mail - Listar mensajes",
        "code": "odoo-mail-message-list",
        "description": "Lista los mensajes y notas del chatter en Odoo.",
        "endpointPath": "/json/2/mail.message/search_read",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"domain\": [], \"fields\": [\"subject\", \"body\", \"date\", \"model\", \"res_id\"], \"limit\": {{limit}}}",
        "parameters": [
          {
            "name": "limit",
            "type": "NUMBER",
            "description": "Cantidad máxima de mensajes a devolver.",
            "required": false,
            "defaultValue": "10"
          }
        ]
      },
      {
        "name": "Res - Listar contactos",
        "code": "odoo-res-partner-list",
        "description": "Lista los contactos (clientes/proveedores) de Odoo.",
        "endpointPath": "/json/2/res.partner/search_read",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"domain\": [], \"fields\": [\"name\", \"email\", \"phone\", \"street\", \"city\"], \"limit\": {{limit}}}",
        "parameters": [
          {
            "name": "limit",
            "type": "NUMBER",
            "description": "Cantidad máxima de contactos a devolver.",
            "required": false,
            "defaultValue": "10"
          }
        ]
      },
      {
        "name": "Sale - Listar pedidos",
        "code": "odoo-sale-order-list",
        "description": "Lista las órdenes de venta y cotizaciones en Odoo. Devuelve nombre, cliente, fecha, estado e importe total.",
        "endpointPath": "/json/2/sale.order/search_read",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"domain\": [], \"fields\": [\"name\", \"partner_id\", \"date_order\", \"state\", \"amount_total\"], \"limit\": {{limit}}}",
        "parameters": [
          {
            "name": "limit",
            "type": "NUMBER",
            "description": "Cantidad máxima de pedidos a devolver.",
            "required": false,
            "defaultValue": "10"
          }
        ]
      },
      {
        "name": "Resource - Listar recursos",
        "code": "odoo-resource-list",
        "description": "Lista los recursos (humanos o materiales) disponibles en Odoo.",
        "endpointPath": "/json/2/resource.resource/search_read",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"domain\": [], \"fields\": [\"name\", \"resource_type\", \"time_efficiency\", \"active\"], \"limit\": {{limit}}}",
        "parameters": [
          {
            "name": "limit",
            "type": "NUMBER",
            "description": "Cantidad máxima de recursos a devolver.",
            "required": false,
            "defaultValue": "10"
          }
        ]
      },
      {
        "name": "Account - Listar facturas",
        "code": "odoo-account-move-list",
        "description": "Lista las facturas de clientes, proveedores y apuntes contables en Odoo.",
        "endpointPath": "/json/2/account.move/search_read",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"domain\": [], \"fields\": [\"name\", \"date\", \"move_type\", \"state\", \"amount_total\"], \"limit\": {{limit}}}",
        "parameters": [
          {
            "name": "limit",
            "type": "NUMBER",
            "description": "Cantidad máxima de facturas a devolver.",
            "required": false,
            "defaultValue": "10"
          }
        ]
      }
    ]
  }
]
```
