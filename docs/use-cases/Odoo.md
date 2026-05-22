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
        "bodyPayloadTemplate": "{\"vals_list\": [{\"name\": \"{{name}}\", \"partner_name\": \"{{partner_name}}\", \"email_from\": \"{{email_from}}\", \"description\": \"{{description}}\", \"type\": \"{{type}}\"}]}",
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
          },
          {
            "name": "type",
            "type": "STRING",
            "description": "Tipo de registro: 'lead' o 'opportunity'.",
            "required": true,
            "defaultValue": "lead"
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
        "bodyPayloadTemplate": "{\"ids\": [{{id}}], \"vals\": {\"name\": \"{{name}}\", \"probability\": {{probability}}, \"stage_id\": {{stage_id}}}}",
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
          },
          {
            "name": "stage_id",
            "type": "NUMBER",
            "description": "ID de la nueva etapa del CRM.",
            "required": false,
            "defaultValue": ""
          }
        ]
      },
      {
        "name": "CRM - Eliminar oportunidad",
        "code": "odoo-crm-delete",
        "description": "Elimina un lead u oportunidad existente en el CRM de Odoo por su ID.",
        "endpointPath": "/json/2/crm.lead/unlink",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"ids\": [{{id}}]}",
        "parameters": [
          {
            "name": "id",
            "type": "NUMBER",
            "description": "ID de la oportunidad o lead a eliminar.",
            "required": true,
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
        "bodyPayloadTemplate": "{\"vals_list\": [{\"name\": \"{{name}}\", \"description\": \"{{description}}\", \"project_id\": {{project_id}}, \"date_deadline\": \"{{date_deadline}}\"}]}",
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
            "name": "project_id",
            "type": "NUMBER",
            "description": "ID del proyecto al que pertenece la tarea.",
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
      },
      {
        "name": "Project - Actualizar tarea",
        "code": "odoo-project-task-update",
        "description": "Actualiza una tarea de proyecto existente en Odoo.",
        "endpointPath": "/json/2/project.task/write",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"ids\": [{{id}}], \"vals\": {\"name\": \"{{name}}\", \"description\": \"{{description}}\", \"date_deadline\": \"{{date_deadline}}\", \"stage_id\": {{stage_id}}}}",
        "parameters": [
          {
            "name": "id",
            "type": "NUMBER",
            "description": "ID de la tarea a actualizar.",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "name",
            "type": "STRING",
            "description": "Nuevo nombre de la tarea.",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "description",
            "type": "STRING",
            "description": "Nueva descripción de la tarea.",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "date_deadline",
            "type": "STRING",
            "description": "Nueva fecha límite (YYYY-MM-DD).",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "stage_id",
            "type": "NUMBER",
            "description": "ID de la nueva etapa de la tarea.",
            "required": false,
            "defaultValue": ""
          }
        ]
      },
      {
        "name": "Project - Eliminar tarea",
        "code": "odoo-project-task-delete",
        "description": "Elimina una tarea de proyecto existente en Odoo por su ID.",
        "endpointPath": "/json/2/project.task/unlink",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"ids\": [{{id}}]}",
        "parameters": [
          {
            "name": "id",
            "type": "NUMBER",
            "description": "ID de la tarea a eliminar.",
            "required": true,
            "defaultValue": ""
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
        "name": "Producto - Crear producto",
        "code": "odoo-product-create",
        "description": "Crea una nueva plantilla de producto en Odoo.",
        "endpointPath": "/json/2/product.template/create",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"vals_list\": [{\"name\": \"{{name}}\", \"type\": \"{{type}}\", \"list_price\": {{list_price}}}]}",
        "parameters": [
          {
            "name": "name",
            "type": "STRING",
            "description": "Nombre del producto.",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "type",
            "type": "STRING",
            "description": "Tipo de producto: 'consu' (consumible), 'service' (servicio) o 'product' (almacenable).",
            "required": true,
            "defaultValue": "consu"
          },
          {
            "name": "list_price",
            "type": "NUMBER",
            "description": "Precio de venta del producto.",
            "required": false,
            "defaultValue": "0.0"
          }
        ]
      },
      {
        "name": "Producto - Actualizar producto",
        "code": "odoo-product-update",
        "description": "Actualiza el precio, nombre o tipo de un producto en Odoo.",
        "endpointPath": "/json/2/product.template/write",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"ids\": [{{id}}], \"vals\": {\"name\": \"{{name}}\", \"type\": \"{{type}}\", \"list_price\": {{list_price}}}}",
        "parameters": [
          {
            "name": "id",
            "type": "NUMBER",
            "description": "ID del producto a actualizar.",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "name",
            "type": "STRING",
            "description": "Nuevo nombre del producto.",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "type",
            "type": "STRING",
            "description": "Nuevo tipo de producto ('consu', 'service', 'product').",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "list_price",
            "type": "NUMBER",
            "description": "Nuevo precio de venta.",
            "required": false,
            "defaultValue": ""
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
        "name": "Calendar - Crear evento",
        "code": "odoo-calendar-event-create",
        "description": "Crea un nuevo evento o reunión en el calendario de Odoo.",
        "endpointPath": "/json/2/calendar.event/create",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"vals_list\": [{\"name\": \"{{name}}\", \"start\": \"{{start}}\", \"stop\": \"{{stop}}\", \"description\": \"{{description}}\"}]}",
        "parameters": [
          {
            "name": "name",
            "type": "STRING",
            "description": "Título de la reunión o evento.",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "start",
            "type": "STRING",
            "description": "Fecha y hora de inicio en formato UTC (YYYY-MM-DD HH:MM:SS).",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "stop",
            "type": "STRING",
            "description": "Fecha y hora de finalización en formato UTC (YYYY-MM-DD HH:MM:SS).",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "description",
            "type": "STRING",
            "description": "Descripción o notas de la reunión.",
            "required": false,
            "defaultValue": ""
          }
        ]
      },
      {
        "name": "Calendar - Actualizar evento",
        "code": "odoo-calendar-event-update",
        "description": "Actualiza los campos de un evento de calendario existente en Odoo.",
        "endpointPath": "/json/2/calendar.event/write",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"ids\": [{{id}}], \"vals\": {\"name\": \"{{name}}\", \"start\": \"{{start}}\", \"stop\": \"{{stop}}\", \"description\": \"{{description}}\"}}",
        "parameters": [
          {
            "name": "id",
            "type": "NUMBER",
            "description": "ID del evento a actualizar.",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "name",
            "type": "STRING",
            "description": "Nuevo título de la reunión.",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "start",
            "type": "STRING",
            "description": "Nueva fecha y hora de inicio (YYYY-MM-DD HH:MM:SS).",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "stop",
            "type": "STRING",
            "description": "Nueva fecha y hora de fin (YYYY-MM-DD HH:MM:SS).",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "description",
            "type": "STRING",
            "description": "Nueva descripción de la reunión.",
            "required": false,
            "defaultValue": ""
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
        "name": "Mail - Crear actividad",
        "code": "odoo-mail-activity-create",
        "description": "Crea una actividad de seguimiento vinculada a un modelo y registro específico de Odoo.",
        "endpointPath": "/json/2/mail.activity/create",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"vals_list\": [{\"res_model\": \"{{res_model}}\", \"res_id\": {{res_id}}, \"activity_type_id\": {{activity_type_id}}, \"summary\": \"{{summary}}\", \"date_deadline\": \"{{date_deadline}}\"}]}",
        "parameters": [
          {
            "name": "res_model",
            "type": "STRING",
            "description": "Nombre técnico del modelo objetivo (ej. 'crm.lead', 'project.task').",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "res_id",
            "type": "NUMBER",
            "description": "ID del registro objetivo en Odoo.",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "activity_type_id",
            "type": "NUMBER",
            "description": "ID del tipo de actividad (ej. 1 para Correo, 2 para Llamada).",
            "required": true,
            "defaultValue": "1"
          },
          {
            "name": "summary",
            "type": "STRING",
            "description": "Resumen o título corto de la actividad.",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "date_deadline",
            "type": "STRING",
            "description": "Fecha de vencimiento en formato YYYY-MM-DD.",
            "required": true,
            "defaultValue": ""
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
        "name": "Mail - Enviar mensaje",
        "code": "odoo-mail-message-create",
        "description": "Publica un mensaje (comentario) en el chatter de un registro de Odoo.",
        "endpointPath": "/json/2/mail.message/create",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"vals_list\": [{\"body\": \"{{body}}\", \"model\": \"{{model}}\", \"res_id\": {{res_id}}, \"message_type\": \"comment\"}]}",
        "parameters": [
          {
            "name": "body",
            "type": "STRING",
            "description": "Contenido del mensaje o comentario.",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "model",
            "type": "STRING",
            "description": "Modelo del registro objetivo (ej. 'crm.lead', 'project.task').",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "res_id",
            "type": "NUMBER",
            "description": "ID del registro objetivo.",
            "required": true,
            "defaultValue": ""
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
        "name": "Res - Crear contacto",
        "code": "odoo-res-partner-create",
        "description": "Crea un nuevo contacto o empresa en Odoo.",
        "endpointPath": "/json/2/res.partner/create",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"vals_list\": [{\"name\": \"{{name}}\", \"email\": \"{{email}}\", \"phone\": \"{{phone}}\", \"street\": \"{{street}}\", \"city\": \"{{city}}\"}]}",
        "parameters": [
          {
            "name": "name",
            "type": "STRING",
            "description": "Nombre completo del contacto o de la empresa.",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "email",
            "type": "STRING",
            "description": "Dirección de correo electrónico.",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "phone",
            "type": "STRING",
            "description": "Número de teléfono.",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "street",
            "type": "STRING",
            "description": "Dirección física (calle y número).",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "city",
            "type": "STRING",
            "description": "Ciudad de residencia.",
            "required": false,
            "defaultValue": ""
          }
        ]
      },
      {
        "name": "Res - Actualizar contacto",
        "code": "odoo-res-partner-update",
        "description": "Actualiza la información de un contacto en Odoo por su ID.",
        "endpointPath": "/json/2/res.partner/write",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"ids\": [{{id}}], \"vals\": {\"name\": \"{{name}}\", \"email\": \"{{email}}\", \"phone\": \"{{phone}}\", \"street\": \"{{street}}\", \"city\": \"{{city}}\"}}",
        "parameters": [
          {
            "name": "id",
            "type": "NUMBER",
            "description": "ID del contacto a actualizar.",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "name",
            "type": "STRING",
            "description": "Nuevo nombre del contacto.",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "email",
            "type": "STRING",
            "description": "Nuevo correo electrónico.",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "phone",
            "type": "STRING",
            "description": "Nuevo teléfono.",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "street",
            "type": "STRING",
            "description": "Nueva calle.",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "city",
            "type": "STRING",
            "description": "Nueva ciudad.",
            "required": false,
            "defaultValue": ""
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
        "name": "Sale - Crear pedido",
        "code": "odoo-sale-order-create",
        "description": "Crea una nueva cotización u orden de venta en Odoo.",
        "endpointPath": "/json/2/sale.order/create",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"vals_list\": [{\"partner_id\": {{partner_id}}, \"date_order\": \"{{date_order}}\"}]}",
        "parameters": [
          {
            "name": "partner_id",
            "type": "NUMBER",
            "description": "ID del cliente (res.partner) al que se asocia la orden.",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "date_order",
            "type": "STRING",
            "description": "Fecha y hora del pedido en formato UTC (YYYY-MM-DD HH:MM:SS).",
            "required": false,
            "defaultValue": ""
          }
        ]
      },
      {
        "name": "Sale - Actualizar pedido",
        "code": "odoo-sale-order-update",
        "description": "Actualiza el estado o el cliente de una orden de venta.",
        "endpointPath": "/json/2/sale.order/write",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"ids\": [{{id}}], \"vals\": {\"partner_id\": {{partner_id}}, \"state\": \"{{state}}\"}}",
        "parameters": [
          {
            "name": "id",
            "type": "NUMBER",
            "description": "ID de la orden de venta a actualizar.",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "partner_id",
            "type": "NUMBER",
            "description": "Nuevo ID del cliente.",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "state",
            "type": "STRING",
            "description": "Nuevo estado (ej. 'draft', 'sent', 'sale', 'done', 'cancel').",
            "required": false,
            "defaultValue": ""
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
      },
      {
        "name": "Account - Crear factura",
        "code": "odoo-account-move-create",
        "description": "Crea una nueva factura o asiento contable en Odoo.",
        "endpointPath": "/json/2/account.move/create",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"vals_list\": [{\"move_type\": \"{{move_type}}\", \"partner_id\": {{partner_id}}, \"journal_id\": {{journal_id}}, \"date\": \"{{date}}\"}]}",
        "parameters": [
          {
            "name": "move_type",
            "type": "STRING",
            "description": "Tipo de asiento: 'out_invoice' (factura de cliente), 'in_invoice' (factura de proveedor), etc.",
            "required": true,
            "defaultValue": "out_invoice"
          },
          {
            "name": "partner_id",
            "type": "NUMBER",
            "description": "ID del contacto (cliente/proveedor) asociado.",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "journal_id",
            "type": "NUMBER",
            "description": "ID del diario contable de destino.",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "date",
            "type": "STRING",
            "description": "Fecha del movimiento contable (YYYY-MM-DD).",
            "required": false,
            "defaultValue": ""
          }
        ]
      },
      {
        "name": "Account - Actualizar factura",
        "code": "odoo-account-move-update",
        "description": "Actualiza la fecha, diario o contacto de una factura contable.",
        "endpointPath": "/json/2/account.move/write",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"ids\": [{{id}}], \"vals\": {\"partner_id\": {{partner_id}}, \"date\": \"{{date}}\", \"journal_id\": {{journal_id}}}}",
        "parameters": [
          {
            "name": "id",
            "type": "NUMBER",
            "description": "ID de la factura contable a actualizar.",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "partner_id",
            "type": "NUMBER",
            "description": "Nuevo ID del contacto.",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "date",
            "type": "STRING",
            "description": "Nueva fecha de la factura (YYYY-MM-DD).",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "journal_id",
            "type": "NUMBER",
            "description": "Nuevo ID del diario contable.",
            "required": false,
            "defaultValue": ""
          }
        ]
      }
    ]
  }
]
```
