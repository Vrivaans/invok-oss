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
        "name": "CRM - List leads",
        "code": "odoo-crm-list",
        "description": "Lists the leads/opportunities from Odoo CRM. Returns name, company, email, stage, and probability of closing.",
        "endpointPath": "/json/2/crm.lead/search_read",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"domain\": [], \"fields\": [\"name\", \"partner_name\", \"email_from\", \"stage_id\", \"probability\", \"expected_revenue\"], \"limit\": {{limit}}}",
        "parameters": [
          {
            "name": "limit",
            "type": "NUMBER",
            "description": "Maximum number of leads to return.",
            "required": false,
            "defaultValue": "10"
          }
        ]
      },
      {
        "name": "Project - List projects",
        "code": "odoo-project-list",
        "description": "Lists all active projects in Odoo with their name, description, and dates.",
        "endpointPath": "/json/2/project.project/search_read",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"domain\": [], \"fields\": [\"name\", \"description\", \"date_start\", \"date\"], \"limit\": {{limit}}}",
        "parameters": [
          {
            "name": "limit",
            "type": "NUMBER",
            "description": "Maximum number of projects to return.",
            "required": false,
            "defaultValue": "10"
          }
        ]
      },
      {
        "name": "Project - List tasks",
        "code": "odoo-project-task-list",
        "description": "Lists the project tasks in Odoo. Returns name, project, stage, assignee, and deadline.",
        "endpointPath": "/json/2/project.task/search_read",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"domain\": [], \"fields\": [\"name\", \"project_id\", \"stage_id\", \"user_ids\", \"date_deadline\"], \"limit\": {{limit}}}",
        "parameters": [
          {
            "name": "limit",
            "type": "NUMBER",
            "description": "Maximum number of tasks to return.",
            "required": false,
            "defaultValue": "10"
          }
        ]
      },
      {
        "name": "Product - List products",
        "code": "odoo-product-list",
        "description": "Lists the products in Odoo. Returns name, type, price, and cost.",
        "endpointPath": "/json/2/product.template/search_read",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"domain\": [], \"fields\": [\"name\", \"type\", \"list_price\", \"standard_price\"], \"limit\": {{limit}}}",
        "parameters": [
          {
            "name": "limit",
            "type": "NUMBER",
            "description": "Maximum number of products to return.",
            "required": false,
            "defaultValue": "10"
          }
        ]
      },
      {
        "name": "Calendar - List events",
        "code": "odoo-calendar-event-list",
        "description": "Lists the calendar events and meetings in Odoo.",
        "endpointPath": "/json/2/calendar.event/search_read",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"domain\": [], \"fields\": [\"name\", \"start\", \"stop\", \"description\", \"partner_ids\"], \"limit\": {{limit}}}",
        "parameters": [
          {
            "name": "limit",
            "type": "NUMBER",
            "description": "Maximum number of events to return.",
            "required": false,
            "defaultValue": "10"
          }
        ]
      },
      {
        "name": "Mail - List activities",
        "code": "odoo-mail-activity-list",
        "description": "Lists the pending mail activities in Odoo.",
        "endpointPath": "/json/2/mail.activity/search_read",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"domain\": [], \"fields\": [\"res_model\", \"res_id\", \"summary\", \"date_deadline\", \"state\", \"user_id\"], \"limit\": {{limit}}}",
        "parameters": [
          {
            "name": "limit",
            "type": "NUMBER",
            "description": "Maximum number of activities to return.",
            "required": false,
            "defaultValue": "10"
          }
        ]
      },
      {
        "name": "Mail - List messages",
        "code": "odoo-mail-message-list",
        "description": "Lists the messages and notes from the chatter in Odoo.",
        "endpointPath": "/json/2/mail.message/search_read",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"domain\": [], \"fields\": [\"subject\", \"body\", \"date\", \"model\", \"res_id\"], \"limit\": {{limit}}}",
        "parameters": [
          {
            "name": "limit",
            "type": "NUMBER",
            "description": "Maximum number of messages to return.",
            "required": false,
            "defaultValue": "10"
          }
        ]
      },
      {
        "name": "Res - List contacts",
        "code": "odoo-res-partner-list",
        "description": "Lists the contacts (customers/vendors) in Odoo.",
        "endpointPath": "/json/2/res.partner/search_read",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"domain\": [], \"fields\": [\"name\", \"email\", \"phone\", \"street\", \"city\"], \"limit\": {{limit}}}",
        "parameters": [
          {
            "name": "limit",
            "type": "NUMBER",
            "description": "Maximum number of contacts to return.",
            "required": false,
            "defaultValue": "10"
          }
        ]
      },
      {
        "name": "Sale - List orders",
        "code": "odoo-sale-order-list",
        "description": "Lists the sales orders and quotations in Odoo. Returns name, customer, date, status, and total amount.",
        "endpointPath": "/json/2/sale.order/search_read",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"domain\": [], \"fields\": [\"name\", \"partner_id\", \"date_order\", \"state\", \"amount_total\"], \"limit\": {{limit}}}",
        "parameters": [
          {
            "name": "limit",
            "type": "NUMBER",
            "description": "Maximum number of orders to return.",
            "required": false,
            "defaultValue": "10"
          }
        ]
      },
      {
        "name": "Resource - List resources",
        "code": "odoo-resource-list",
        "description": "Lists the resources (human or material) available in Odoo.",
        "endpointPath": "/json/2/resource.resource/search_read",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"domain\": [], \"fields\": [\"name\", \"resource_type\", \"time_efficiency\", \"active\"], \"limit\": {{limit}}}",
        "parameters": [
          {
            "name": "limit",
            "type": "NUMBER",
            "description": "Maximum number of resources to return.",
            "required": false,
            "defaultValue": "10"
          }
        ]
      },
      {
        "name": "Account - List invoices",
        "code": "odoo-account-move-list",
        "description": "Lists the customer invoices, vendor bills, and journal entries in Odoo.",
        "endpointPath": "/json/2/account.move/search_read",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"domain\": [], \"fields\": [\"name\", \"date\", \"move_type\", \"state\", \"amount_total\"], \"limit\": {{limit}}}",
        "parameters": [
          {
            "name": "limit",
            "type": "NUMBER",
            "description": "Maximum number of invoices to return.",
            "required": false,
            "defaultValue": "10"
          }
        ]
      }
    ]
  }
]
```
