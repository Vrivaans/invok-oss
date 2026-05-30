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
        "name": "CRM - Create lead",
        "code": "odoo-crm-create",
        "description": "Creates a new opportunity or lead in Odoo CRM.",
        "endpointPath": "/json/2/crm.lead/create",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"vals_list\": [{\"name\": \"{{name}}\", \"partner_name\": \"{{partner_name}}\", \"email_from\": \"{{email_from}}\", \"description\": \"{{description}}\", \"type\": \"{{type}}\"}]}",
        "parameters": [
          {
            "name": "name",
            "type": "STRING",
            "description": "Name of the opportunity or lead.",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "partner_name",
            "type": "STRING",
            "description": "Name of the company or contact.",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "email_from",
            "type": "STRING",
            "description": "Email of the contact.",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "description",
            "type": "STRING",
            "description": "Description or notes of the lead.",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "type",
            "type": "STRING",
            "description": "Record type: 'lead' or 'opportunity'.",
            "required": true,
            "defaultValue": "lead"
          }
        ]
      },
      {
        "name": "CRM - Update opportunity",
        "code": "odoo-crm-update",
        "description": "Updates the fields of an existing opportunity in Odoo CRM given its ID.",
        "endpointPath": "/json/2/crm.lead/write",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"ids\": [{{id}}], \"vals\": {\"name\": \"{{name}}\", \"probability\": {{probability}}, \"stage_id\": {{stage_id}}}}",
        "parameters": [
          {
            "name": "id",
            "type": "NUMBER",
            "description": "ID of the opportunity to update.",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "name",
            "type": "STRING",
            "description": "New name of the opportunity.",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "probability",
            "type": "NUMBER",
            "description": "Closing probability in percentage (0-100).",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "stage_id",
            "type": "NUMBER",
            "description": "ID of the new CRM stage.",
            "required": false,
            "defaultValue": ""
          }
        ]
      },
      {
        "name": "CRM - Delete opportunity",
        "code": "odoo-crm-delete",
        "description": "Deletes an existing lead or opportunity in Odoo CRM by its ID.",
        "endpointPath": "/json/2/crm.lead/unlink",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"ids\": [{{id}}]}",
        "parameters": [
          {
            "name": "id",
            "type": "NUMBER",
            "description": "ID of the opportunity or lead to delete.",
            "required": true,
            "defaultValue": ""
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
        "name": "Project - Create task",
        "code": "odoo-project-task-create",
        "description": "Creates a new task in an Odoo project.",
        "endpointPath": "/json/2/project.task/create",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"vals_list\": [{\"name\": \"{{name}}\", \"description\": \"{{description}}\", \"project_id\": {{project_id}}, \"date_deadline\": \"{{date_deadline}}\"}]}",
        "parameters": [
          {
            "name": "name",
            "type": "STRING",
            "description": "Name of the task.",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "description",
            "type": "STRING",
            "description": "Description or detail of the task.",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "project_id",
            "type": "NUMBER",
            "description": "ID of the project to which the task belongs.",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "date_deadline",
            "type": "STRING",
            "description": "Deadline date in YYYY-MM-DD format.",
            "required": false,
            "defaultValue": ""
          }
        ]
      },
      {
        "name": "Project - Update task",
        "code": "odoo-project-task-update",
        "description": "Updates an existing project task in Odoo.",
        "endpointPath": "/json/2/project.task/write",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"ids\": [{{id}}], \"vals\": {\"name\": \"{{name}}\", \"description\": \"{{description}}\", \"date_deadline\": \"{{date_deadline}}\", \"stage_id\": {{stage_id}}}}",
        "parameters": [
          {
            "name": "id",
            "type": "NUMBER",
            "description": "ID of the task to update.",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "name",
            "type": "STRING",
            "description": "New name of the task.",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "description",
            "type": "STRING",
            "description": "New description of the task.",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "date_deadline",
            "type": "STRING",
            "description": "New deadline date (YYYY-MM-DD).",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "stage_id",
            "type": "NUMBER",
            "description": "ID of the new stage of the task.",
            "required": false,
            "defaultValue": ""
          }
        ]
      },
      {
        "name": "Project - Delete task",
        "code": "odoo-project-task-delete",
        "description": "Deletes an existing project task in Odoo by its ID.",
        "endpointPath": "/json/2/project.task/unlink",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"ids\": [{{id}}]}",
        "parameters": [
          {
            "name": "id",
            "type": "NUMBER",
            "description": "ID of the task to delete.",
            "required": true,
            "defaultValue": ""
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
        "name": "Product - Create product",
        "code": "odoo-product-create",
        "description": "Creates a new product template in Odoo.",
        "endpointPath": "/json/2/product.template/create",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"vals_list\": [{\"name\": \"{{name}}\", \"type\": \"{{type}}\", \"list_price\": {{list_price}}}]}",
        "parameters": [
          {
            "name": "name",
            "type": "STRING",
            "description": "Name of the product.",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "type",
            "type": "STRING",
            "description": "Product type: 'consu' (consumible), 'service' (service), or 'product' (storable).",
            "required": true,
            "defaultValue": "consu"
          },
          {
            "name": "list_price",
            "type": "NUMBER",
            "description": "Sale price of the product.",
            "required": false,
            "defaultValue": "0.0"
          }
        ]
      },
      {
        "name": "Product - Update product",
        "code": "odoo-product-update",
        "description": "Updates the price, name, or type of a product in Odoo.",
        "endpointPath": "/json/2/product.template/write",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"ids\": [{{id}}], \"vals\": {\"name\": \"{{name}}\", \"type\": \"{{type}}\", \"list_price\": {{list_price}}}}",
        "parameters": [
          {
            "name": "id",
            "type": "NUMBER",
            "description": "ID of the product to update.",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "name",
            "type": "STRING",
            "description": "New name of the product.",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "type",
            "type": "STRING",
            "description": "New product type ('consu', 'service', 'product').",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "list_price",
            "type": "NUMBER",
            "description": "New sale price.",
            "required": false,
            "defaultValue": ""
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
        "name": "Calendar - Create event",
        "code": "odoo-calendar-event-create",
        "description": "Creates a new event or meeting in Odoo calendar.",
        "endpointPath": "/json/2/calendar.event/create",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"vals_list\": [{\"name\": \"{{name}}\", \"start\": \"{{start}}\", \"stop\": \"{{stop}}\", \"description\": \"{{description}}\"}]}",
        "parameters": [
          {
            "name": "name",
            "type": "STRING",
            "description": "Title of the meeting or event.",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "start",
            "type": "STRING",
            "description": "Start date and time in UTC format (YYYY-MM-DD HH:MM:SS).",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "stop",
            "type": "STRING",
            "description": "End date and time in UTC format (YYYY-MM-DD HH:MM:SS).",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "description",
            "type": "STRING",
            "description": "Description or notes of the meeting.",
            "required": false,
            "defaultValue": ""
          }
        ]
      },
      {
        "name": "Calendar - Update event",
        "code": "odoo-calendar-event-update",
        "description": "Updates fields of an existing calendar event in Odoo.",
        "endpointPath": "/json/2/calendar.event/write",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"ids\": [{{id}}], \"vals\": {\"name\": \"{{name}}\", \"start\": \"{{start}}\", \"stop\": \"{{stop}}\", \"description\": \"{{description}}\"}}",
        "parameters": [
          {
            "name": "id",
            "type": "NUMBER",
            "description": "ID of the event to update.",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "name",
            "type": "STRING",
            "description": "New title of the meeting.",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "start",
            "type": "STRING",
            "description": "New start date and time (YYYY-MM-DD HH:MM:SS).",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "stop",
            "type": "STRING",
            "description": "New end date and time (YYYY-MM-DD HH:MM:SS).",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "description",
            "type": "STRING",
            "description": "New description of the meeting.",
            "required": false,
            "defaultValue": ""
          }
        ]
      },
      {
        "name": "Mail - List activities",
        "code": "odoo-mail-activity-list",
        "description": "Lists pending mail activities in Odoo.",
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
        "name": "Base - List Models",
        "code": "odoo-ir-model-list",
        "description": "Lists the technical models registered in Odoo (needed to find model IDs for activities, etc.).",
        "endpointPath": "/json/2/ir.model/search_read",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"domain\": [[\"model\", \"=\", \"{{model_name}}\"]], \"fields\": [\"id\", \"model\", \"name\"], \"limit\": 1}",
        "parameters": [
          {
            "name": "model_name",
            "type": "STRING",
            "description": "Technical name of the model (e.g., 'crm.lead', 'project.task').",
            "required": true,
            "defaultValue": ""
          }
        ]
      },
      {
        "name": "Mail - Create activity",
        "code": "odoo-mail-activity-create",
        "description": "Creates a follow-up activity linked to a specific Odoo model and record.",
        "endpointPath": "/json/2/mail.activity/create",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"vals_list\": [{\"res_model_id\": {{res_model_id}}, \"res_id\": {{res_id}}, \"activity_type_id\": {{activity_type_id}}, \"summary\": \"{{summary}}\", \"date_deadline\": \"{{date_deadline}}\"}]}",
        "parameters": [
          {
            "name": "res_model_id",
            "type": "NUMBER",
            "description": "Database ID of the target model (retrieve it using odoo-ir-model-list).",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "res_id",
            "type": "NUMBER",
            "description": "ID of the target record in Odoo.",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "activity_type_id",
            "type": "NUMBER",
            "description": "ID of the activity type (e.g., 1 for Email, 2 for Call).",
            "required": true,
            "defaultValue": "1"
          },
          {
            "name": "summary",
            "type": "STRING",
            "description": "Summary or short title of the activity.",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "date_deadline",
            "type": "STRING",
            "description": "Due date in YYYY-MM-DD format.",
            "required": true,
            "defaultValue": ""
          }
        ]
      },
      {
        "name": "Mail - List messages",
        "code": "odoo-mail-message-list",
        "description": "Lists messages and notes from the chatter in Odoo.",
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
        "name": "Mail - Send message",
        "code": "odoo-mail-message-create",
        "description": "Publishes a message (comment) on the chatter of an Odoo record.",
        "endpointPath": "/json/2/mail.message/create",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"vals_list\": [{\"body\": \"{{body}}\", \"model\": \"{{model}}\", \"res_id\": {{res_id}}, \"message_type\": \"comment\"}]}",
        "parameters": [
          {
            "name": "body",
            "type": "STRING",
            "description": "Content of the message or comment.",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "model",
            "type": "STRING",
            "description": "Model of the target record (e.g., 'crm.lead', 'project.task').",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "res_id",
            "type": "NUMBER",
            "description": "ID of the target record.",
            "required": true,
            "defaultValue": ""
          }
        ]
      },
      {
        "name": "Res - List contacts",
        "code": "odoo-res-partner-list",
        "description": "Lists contacts (customers/vendors) in Odoo.",
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
        "name": "Res - Create contact",
        "code": "odoo-res-partner-create",
        "description": "Creates a new contact or company in Odoo.",
        "endpointPath": "/json/2/res.partner/create",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"vals_list\": [{\"name\": \"{{name}}\", \"email\": \"{{email}}\", \"phone\": \"{{phone}}\", \"street\": \"{{street}}\", \"city\": \"{{city}}\"}]}",
        "parameters": [
          {
            "name": "name",
            "type": "STRING",
            "description": "Full name of the contact or company.",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "email",
            "type": "STRING",
            "description": "Email address.",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "phone",
            "type": "STRING",
            "description": "Phone number.",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "street",
            "type": "STRING",
            "description": "Physical address (street and number).",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "city",
            "type": "STRING",
            "description": "City of residence.",
            "required": false,
            "defaultValue": ""
          }
        ]
      },
      {
        "name": "Res - Update contact",
        "code": "odoo-res-partner-update",
        "description": "Updates contact information in Odoo by its ID.",
        "endpointPath": "/json/2/res.partner/write",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"ids\": [{{id}}], \"vals\": {\"name\": \"{{name}}\", \"email\": \"{{email}}\", \"phone\": \"{{phone}}\", \"street\": \"{{street}}\", \"city\": \"{{city}}\"}}",
        "parameters": [
          {
            "name": "id",
            "type": "NUMBER",
            "description": "ID of the contact to update.",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "name",
            "type": "STRING",
            "description": "New name of the contact.",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "email",
            "type": "STRING",
            "description": "New email address.",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "phone",
            "type": "STRING",
            "description": "New phone number.",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "street",
            "type": "STRING",
            "description": "New street.",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "city",
            "type": "STRING",
            "description": "New city.",
            "required": false,
            "defaultValue": ""
          }
        ]
      },
      {
        "name": "Sale - List orders",
        "code": "odoo-sale-order-list",
        "description": "Lists sales orders and quotations in Odoo. Returns name, customer, date, status, and total amount.",
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
        "name": "Sale - Create order",
        "code": "odoo-sale-order-create",
        "description": "Creates a new quotation or sales order in Odoo.",
        "endpointPath": "/json/2/sale.order/create",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"vals_list\": [{\"partner_id\": {{partner_id}}, \"date_order\": \"{{date_order}}\"}]}",
        "parameters": [
          {
            "name": "partner_id",
            "type": "NUMBER",
            "description": "ID of the customer (res.partner) associated with the order.",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "date_order",
            "type": "STRING",
            "description": "Date and time of the order in UTC format (YYYY-MM-DD HH:MM:SS).",
            "required": false,
            "defaultValue": ""
          }
        ]
      },
      {
        "name": "Sale - Update order",
        "code": "odoo-sale-order-update",
        "description": "Updates the status or customer of a sales order.",
        "endpointPath": "/json/2/sale.order/write",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"ids\": [{{id}}], \"vals\": {\"partner_id\": {{partner_id}}, \"state\": \"{{state}}\"}}",
        "parameters": [
          {
            "name": "id",
            "type": "NUMBER",
            "description": "ID of the sales order to update.",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "partner_id",
            "type": "NUMBER",
            "description": "New ID of the customer.",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "state",
            "type": "STRING",
            "description": "New status (e.g., 'draft', 'sent', 'sale', 'done', 'cancel').",
            "required": false,
            "defaultValue": ""
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
        "description": "Lists customer invoices, vendor bills, and journal entries in Odoo.",
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
      },
      {
        "name": "Account - Create invoice",
        "code": "odoo-account-move-create",
        "description": "Creates a new invoice or journal entry in Odoo.",
        "endpointPath": "/json/2/account.move/create",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"vals_list\": [{\"move_type\": \"{{move_type}}\", \"partner_id\": {{partner_id}}, \"journal_id\": {{journal_id}}, \"date\": \"{{date}}\"}]}",
        "parameters": [
          {
            "name": "move_type",
            "type": "STRING",
            "description": "Entry type: 'out_invoice' (customer invoice), 'in_invoice' (vendor bill), etc.",
            "required": true,
            "defaultValue": "out_invoice"
          },
          {
            "name": "partner_id",
            "type": "NUMBER",
            "description": "ID of the associated contact (customer/vendor).",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "journal_id",
            "type": "NUMBER",
            "description": "ID of the target journal.",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "date",
            "type": "STRING",
            "description": "Date of the journal entry (YYYY-MM-DD).",
            "required": false,
            "defaultValue": ""
          }
        ]
      },
      {
        "name": "Account - Update invoice",
        "code": "odoo-account-move-update",
        "description": "Updates the date, journal, or contact of a customer invoice.",
        "endpointPath": "/json/2/account.move/write",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"ids\": [{{id}}], \"vals\": {\"partner_id\": {{partner_id}}, \"date\": \"{{date}}\", \"journal_id\": {{journal_id}}}}",
        "parameters": [
          {
            "name": "id",
            "type": "NUMBER",
            "description": "ID of the customer invoice to update.",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "partner_id",
            "type": "NUMBER",
            "description": "New contact ID.",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "date",
            "type": "STRING",
            "description": "New date of the invoice (YYYY-MM-DD).",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "journal_id",
            "type": "NUMBER",
            "description": "New journal ID.",
            "required": false,
            "defaultValue": ""
          }
        ]
      },
      {
        "name": "Inventory - List Stock Levels",
        "code": "odoo-inventory-stock-list",
        "description": "Lists the current stock levels of products across different locations.",
        "endpointPath": "/json/2/stock.quant/search_read",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"domain\": [[\"quantity\", \">\", 0]], \"fields\": [\"product_id\", \"location_id\", \"quantity\", \"reserved_quantity\"], \"limit\": {{limit}}}",
        "parameters": [
          {
            "name": "limit",
            "type": "NUMBER",
            "description": "Maximum number of stock records to return.",
            "required": false,
            "defaultValue": "10"
          }
        ]
      },
      {
        "name": "Inventory - List Transfers",
        "code": "odoo-inventory-transfer-list",
        "description": "Lists inventory transfers (delivery orders, incoming receipts, internal transfers) and their states.",
        "endpointPath": "/json/2/stock.picking/search_read",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"domain\": [], \"fields\": [\"name\", \"partner_id\", \"picking_type_id\", \"state\", \"origin\"], \"limit\": {{limit}}}",
        "parameters": [
          {
            "name": "limit",
            "type": "NUMBER",
            "description": "Maximum number of transfers to return.",
            "required": false,
            "defaultValue": "10"
          }
        ]
      },
      {
        "name": "Stock - List Justificador de AVCO de inventario",
        "code": "odoo-stock-avco-report-list",
        "description": "Lists records of 'stock.avco.report' (Justificador de AVCO de inventario) with filters and limits.",
        "endpointPath": "/json/2/stock.avco.report/search_read",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"domain\": [], \"fields\": [\"company_id\", \"value\", \"reference\", \"quantity\", \"res_model_name\", \"user_id\", \"date\", \"product_id\", \"id\", \"description\", \"display_name\"], \"limit\": {{limit}}}",
        "parameters": [
          {
            "name": "limit",
            "type": "NUMBER",
            "description": "Maximum number of records to return.",
            "required": false,
            "defaultValue": "10"
          }
        ]
      },
      {
        "name": "Stock - List Confirmación de orden parcial",
        "code": "odoo-stock-backorder-confirmation-list",
        "description": "Lists records of 'stock.backorder.confirmation' (Confirmación de orden parcial) with filters and limits.",
        "endpointPath": "/json/2/stock.backorder.confirmation/search_read",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"domain\": [], \"fields\": [\"display_name\", \"create_date\", \"id\"], \"limit\": {{limit}}}",
        "parameters": [
          {
            "name": "limit",
            "type": "NUMBER",
            "description": "Maximum number of records to return.",
            "required": false,
            "defaultValue": "10"
          }
        ]
      },
      {
        "name": "Stock - List Línea de confirmación de órdenes parciales",
        "code": "odoo-stock-backorder-confirmation-line-list",
        "description": "Lists records of 'stock.backorder.confirmation.line' (Línea de confirmación de órdenes parciales) with filters and limits.",
        "endpointPath": "/json/2/stock.backorder.confirmation.line/search_read",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"domain\": [], \"fields\": [\"display_name\", \"create_date\", \"id\"], \"limit\": {{limit}}}",
        "parameters": [
          {
            "name": "limit",
            "type": "NUMBER",
            "description": "Maximum number of records to return.",
            "required": false,
            "defaultValue": "10"
          }
        ]
      },
      {
        "name": "Stock - List Referencia/motivo del ajuste de inventario",
        "code": "odoo-stock-inventory-adjustment-name-list",
        "description": "Lists records of 'stock.inventory.adjustment.name' (Referencia/motivo del ajuste de inventario) with filters and limits.",
        "endpointPath": "/json/2/stock.inventory.adjustment.name/search_read",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"domain\": [], \"fields\": [\"display_name\", \"create_date\", \"id\"], \"limit\": {{limit}}}",
        "parameters": [
          {
            "name": "limit",
            "type": "NUMBER",
            "description": "Maximum number of records to return.",
            "required": false,
            "defaultValue": "10"
          }
        ]
      },
      {
        "name": "Stock - List Conflicto en el inventario",
        "code": "odoo-stock-inventory-conflict-list",
        "description": "Lists records of 'stock.inventory.conflict' (Conflicto en el inventario) with filters and limits.",
        "endpointPath": "/json/2/stock.inventory.conflict/search_read",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"domain\": [], \"fields\": [\"display_name\", \"create_date\", \"id\"], \"limit\": {{limit}}}",
        "parameters": [
          {
            "name": "limit",
            "type": "NUMBER",
            "description": "Maximum number of records to return.",
            "required": false,
            "defaultValue": "10"
          }
        ]
      },
      {
        "name": "Stock - List Advertencia del ajuste de inventario",
        "code": "odoo-stock-inventory-warning-list",
        "description": "Lists records of 'stock.inventory.warning' (Advertencia del ajuste de inventario) with filters and limits.",
        "endpointPath": "/json/2/stock.inventory.warning/search_read",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"domain\": [], \"fields\": [\"display_name\", \"create_date\", \"id\"], \"limit\": {{limit}}}",
        "parameters": [
          {
            "name": "limit",
            "type": "NUMBER",
            "description": "Maximum number of records to return.",
            "required": false,
            "defaultValue": "10"
          }
        ]
      },
      {
        "name": "Stock - List Ubicaciones de inventario",
        "code": "odoo-stock-location-list",
        "description": "Lists records of 'stock.location' (Ubicaciones de inventario) with filters and limits.",
        "endpointPath": "/json/2/stock.location/search_read",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"domain\": [], \"fields\": [\"location_id\", \"name\", \"warehouse_id\", \"id\", \"display_name\", \"usage\", \"create_date\"], \"limit\": {{limit}}}",
        "parameters": [
          {
            "name": "limit",
            "type": "NUMBER",
            "description": "Maximum number of records to return.",
            "required": false,
            "defaultValue": "10"
          }
        ]
      },
      {
        "name": "Stock - Create Ubicaciones de inventario",
        "code": "odoo-stock-location-create",
        "description": "Creates a new record of 'stock.location' (Ubicaciones de inventario).",
        "endpointPath": "/json/2/stock.location/create",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"vals_list\": [{\"name\": \"{{name}}\", \"usage\": \"{{usage}}\", \"location_id\": {{location_id}}}]}",
        "parameters": [
          {
            "name": "name",
            "type": "STRING",
            "description": "Nombre de la ubicación",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "usage",
            "type": "STRING",
            "description": "Tipo de ubicación",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "location_id",
            "type": "NUMBER",
            "description": "Ubicación principal",
            "required": false,
            "defaultValue": ""
          }
        ]
      },
      {
        "name": "Stock - Update Ubicaciones de inventario",
        "code": "odoo-stock-location-update",
        "description": "Updates fields of an existing record of 'stock.location' (Ubicaciones de inventario) by ID.",
        "endpointPath": "/json/2/stock.location/write",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"ids\": [{{id}}], \"vals\": {\"name\": \"{{name}}\", \"usage\": \"{{usage}}\", \"location_id\": {{location_id}}}}",
        "parameters": [
          {
            "name": "id",
            "type": "NUMBER",
            "description": "ID of the record to update.",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "name",
            "type": "STRING",
            "description": "Nombre de la ubicación (leave empty to not modify)",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "usage",
            "type": "STRING",
            "description": "Tipo de ubicación (leave empty to not modify)",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "location_id",
            "type": "NUMBER",
            "description": "Ubicación principal (leave empty to not modify)",
            "required": false,
            "defaultValue": ""
          }
        ]
      },
      {
        "name": "Stock - Delete Ubicaciones de inventario",
        "code": "odoo-stock-location-delete",
        "description": "Deletes an existing record of 'stock.location' (Ubicaciones de inventario) by ID.",
        "endpointPath": "/json/2/stock.location/unlink",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"ids\": [{{id}}]}",
        "parameters": [
          {
            "name": "id",
            "type": "NUMBER",
            "description": "ID of the record to delete.",
            "required": true,
            "defaultValue": ""
          }
        ]
      },
      {
        "name": "Stock - List Número de serie/lote",
        "code": "odoo-stock-lot-list",
        "description": "Lists records of 'stock.lot' (Número de serie/lote) with filters and limits.",
        "endpointPath": "/json/2/stock.lot/search_read",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"domain\": [], \"fields\": [\"location_id\", \"name\", \"product_id\", \"id\", \"display_name\", \"create_date\"], \"limit\": {{limit}}}",
        "parameters": [
          {
            "name": "limit",
            "type": "NUMBER",
            "description": "Maximum number of records to return.",
            "required": false,
            "defaultValue": "10"
          }
        ]
      },
      {
        "name": "Stock - Create Número de serie/lote",
        "code": "odoo-stock-lot-create",
        "description": "Creates a new record of 'stock.lot' (Número de serie/lote).",
        "endpointPath": "/json/2/stock.lot/create",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"vals_list\": [{\"name\": \"{{name}}\", \"product_id\": {{product_id}}, \"location_id\": {{location_id}}}]}",
        "parameters": [
          {
            "name": "name",
            "type": "STRING",
            "description": "Número de serie/lote",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "product_id",
            "type": "NUMBER",
            "description": "Producto",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "location_id",
            "type": "NUMBER",
            "description": "Ubicación",
            "required": false,
            "defaultValue": ""
          }
        ]
      },
      {
        "name": "Stock - Update Número de serie/lote",
        "code": "odoo-stock-lot-update",
        "description": "Updates fields of an existing record of 'stock.lot' (Número de serie/lote) by ID.",
        "endpointPath": "/json/2/stock.lot/write",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"ids\": [{{id}}], \"vals\": {\"name\": \"{{name}}\", \"product_id\": {{product_id}}, \"location_id\": {{location_id}}}}",
        "parameters": [
          {
            "name": "id",
            "type": "NUMBER",
            "description": "ID of the record to update.",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "name",
            "type": "STRING",
            "description": "Número de serie/lote (leave empty to not modify)",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "product_id",
            "type": "NUMBER",
            "description": "Producto (leave empty to not modify)",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "location_id",
            "type": "NUMBER",
            "description": "Ubicación (leave empty to not modify)",
            "required": false,
            "defaultValue": ""
          }
        ]
      },
      {
        "name": "Stock - Delete Número de serie/lote",
        "code": "odoo-stock-lot-delete",
        "description": "Deletes an existing record of 'stock.lot' (Número de serie/lote) by ID.",
        "endpointPath": "/json/2/stock.lot/unlink",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"ids\": [{{id}}]}",
        "parameters": [
          {
            "name": "id",
            "type": "NUMBER",
            "description": "ID of the record to delete.",
            "required": true,
            "defaultValue": ""
          }
        ]
      },
      {
        "name": "Stock - List Movimiento de stock",
        "code": "odoo-stock-move-list",
        "description": "Lists records of 'stock.move' (Movimiento de stock) with filters and limits.",
        "endpointPath": "/json/2/stock.move/search_read",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"domain\": [], \"fields\": [\"company_id\", \"product_uom_qty\", \"state\", \"location_id\", \"quantity\", \"product_uom\", \"date\", \"procure_method\", \"product_id\", \"warehouse_id\", \"id\", \"location_dest_id\", \"display_name\", \"create_date\"], \"limit\": {{limit}}}",
        "parameters": [
          {
            "name": "limit",
            "type": "NUMBER",
            "description": "Maximum number of records to return.",
            "required": false,
            "defaultValue": "10"
          }
        ]
      },
      {
        "name": "Stock - Create Movimiento de stock",
        "code": "odoo-stock-move-create",
        "description": "Creates a new record of 'stock.move' (Movimiento de stock).",
        "endpointPath": "/json/2/stock.move/create",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"vals_list\": [{\"company_id\": {{company_id}}, \"date\": \"{{date}}\", \"location_dest_id\": {{location_dest_id}}, \"location_id\": {{location_id}}, \"procure_method\": \"{{procure_method}}\", \"product_id\": {{product_id}}, \"product_uom\": {{product_uom}}, \"product_uom_qty\": {{product_uom_qty}}}]}",
        "parameters": [
          {
            "name": "company_id",
            "type": "NUMBER",
            "description": "Empresa",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "date",
            "type": "STRING",
            "description": "Fecha programada",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "location_dest_id",
            "type": "NUMBER",
            "description": "Ubicación intermedia",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "location_id",
            "type": "NUMBER",
            "description": "Ubicación de origen",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "procure_method",
            "type": "STRING",
            "description": "Método de suministro",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "product_id",
            "type": "NUMBER",
            "description": "Producto",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "product_uom",
            "type": "NUMBER",
            "description": "Unidad",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "product_uom_qty",
            "type": "NUMBER",
            "description": "Demanda",
            "required": true,
            "defaultValue": ""
          }
        ]
      },
      {
        "name": "Stock - Update Movimiento de stock",
        "code": "odoo-stock-move-update",
        "description": "Updates fields of an existing record of 'stock.move' (Movimiento de stock) by ID.",
        "endpointPath": "/json/2/stock.move/write",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"ids\": [{{id}}], \"vals\": {\"company_id\": {{company_id}}, \"date\": \"{{date}}\", \"location_dest_id\": {{location_dest_id}}, \"location_id\": {{location_id}}, \"procure_method\": \"{{procure_method}}\", \"product_id\": {{product_id}}, \"product_uom\": {{product_uom}}, \"product_uom_qty\": {{product_uom_qty}}}}",
        "parameters": [
          {
            "name": "id",
            "type": "NUMBER",
            "description": "ID of the record to update.",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "company_id",
            "type": "NUMBER",
            "description": "Empresa (leave empty to not modify)",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "date",
            "type": "STRING",
            "description": "Fecha programada (leave empty to not modify)",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "location_dest_id",
            "type": "NUMBER",
            "description": "Ubicación intermedia (leave empty to not modify)",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "location_id",
            "type": "NUMBER",
            "description": "Ubicación de origen (leave empty to not modify)",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "procure_method",
            "type": "STRING",
            "description": "Método de suministro (leave empty to not modify)",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "product_id",
            "type": "NUMBER",
            "description": "Producto (leave empty to not modify)",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "product_uom",
            "type": "NUMBER",
            "description": "Unidad (leave empty to not modify)",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "product_uom_qty",
            "type": "NUMBER",
            "description": "Demanda (leave empty to not modify)",
            "required": false,
            "defaultValue": ""
          }
        ]
      },
      {
        "name": "Stock - Delete Movimiento de stock",
        "code": "odoo-stock-move-delete",
        "description": "Deletes an existing record of 'stock.move' (Movimiento de stock) by ID.",
        "endpointPath": "/json/2/stock.move/unlink",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"ids\": [{{id}}]}",
        "parameters": [
          {
            "name": "id",
            "type": "NUMBER",
            "description": "ID of the record to delete.",
            "required": true,
            "defaultValue": ""
          }
        ]
      },
      {
        "name": "Stock - List Movimientos de producto (línea de movimiento de stock)",
        "code": "odoo-stock-move-line-list",
        "description": "Lists records of 'stock.move.line' (Movimientos de producto (línea de movimiento de stock)) with filters and limits.",
        "endpointPath": "/json/2/stock.move.line/search_read",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"domain\": [], \"fields\": [\"company_id\", \"state\", \"location_id\", \"product_uom_id\", \"quantity\", \"date\", \"lot_id\", \"product_id\", \"id\", \"location_dest_id\", \"display_name\", \"create_date\"], \"limit\": {{limit}}}",
        "parameters": [
          {
            "name": "limit",
            "type": "NUMBER",
            "description": "Maximum number of records to return.",
            "required": false,
            "defaultValue": "10"
          }
        ]
      },
      {
        "name": "Stock - Create Movimientos de producto (línea de movimiento de stock)",
        "code": "odoo-stock-move-line-create",
        "description": "Creates a new record of 'stock.move.line' (Movimientos de producto (línea de movimiento de stock)).",
        "endpointPath": "/json/2/stock.move.line/create",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"vals_list\": [{\"company_id\": {{company_id}}, \"date\": \"{{date}}\", \"location_dest_id\": {{location_dest_id}}, \"location_id\": {{location_id}}, \"product_uom_id\": {{product_uom_id}}}]}",
        "parameters": [
          {
            "name": "company_id",
            "type": "NUMBER",
            "description": "Empresa",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "date",
            "type": "STRING",
            "description": "Fecha",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "location_dest_id",
            "type": "NUMBER",
            "description": "A",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "location_id",
            "type": "NUMBER",
            "description": "Desde",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "product_uom_id",
            "type": "NUMBER",
            "description": "Unidad",
            "required": true,
            "defaultValue": ""
          }
        ]
      },
      {
        "name": "Stock - Update Movimientos de producto (línea de movimiento de stock)",
        "code": "odoo-stock-move-line-update",
        "description": "Updates fields of an existing record of 'stock.move.line' (Movimientos de producto (línea de movimiento de stock)) by ID.",
        "endpointPath": "/json/2/stock.move.line/write",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"ids\": [{{id}}], \"vals\": {\"company_id\": {{company_id}}, \"date\": \"{{date}}\", \"location_dest_id\": {{location_dest_id}}, \"location_id\": {{location_id}}, \"product_uom_id\": {{product_uom_id}}}}",
        "parameters": [
          {
            "name": "id",
            "type": "NUMBER",
            "description": "ID of the record to update.",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "company_id",
            "type": "NUMBER",
            "description": "Empresa (leave empty to not modify)",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "date",
            "type": "STRING",
            "description": "Fecha (leave empty to not modify)",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "location_dest_id",
            "type": "NUMBER",
            "description": "A (leave empty to not modify)",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "location_id",
            "type": "NUMBER",
            "description": "Desde (leave empty to not modify)",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "product_uom_id",
            "type": "NUMBER",
            "description": "Unidad (leave empty to not modify)",
            "required": false,
            "defaultValue": ""
          }
        ]
      },
      {
        "name": "Stock - Delete Movimientos de producto (línea de movimiento de stock)",
        "code": "odoo-stock-move-line-delete",
        "description": "Deletes an existing record of 'stock.move.line' (Movimientos de producto (línea de movimiento de stock)) by ID.",
        "endpointPath": "/json/2/stock.move.line/unlink",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"ids\": [{{id}}]}",
        "parameters": [
          {
            "name": "id",
            "type": "NUMBER",
            "description": "ID of the record to delete.",
            "required": true,
            "defaultValue": ""
          }
        ]
      },
      {
        "name": "Stock - List Posponer punto de orden",
        "code": "odoo-stock-orderpoint-snooze-list",
        "description": "Lists records of 'stock.orderpoint.snooze' (Posponer punto de orden) with filters and limits.",
        "endpointPath": "/json/2/stock.orderpoint.snooze/search_read",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"domain\": [], \"fields\": [\"display_name\", \"create_date\", \"id\"], \"limit\": {{limit}}}",
        "parameters": [
          {
            "name": "limit",
            "type": "NUMBER",
            "description": "Maximum number of records to return.",
            "required": false,
            "defaultValue": "10"
          }
        ]
      },
      {
        "name": "Stock - List Paquete",
        "code": "odoo-stock-package-list",
        "description": "Lists records of 'stock.package' (Paquete) with filters and limits.",
        "endpointPath": "/json/2/stock.package/search_read",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"domain\": [], \"fields\": [\"location_id\", \"name\", \"id\", \"display_name\", \"create_date\"], \"limit\": {{limit}}}",
        "parameters": [
          {
            "name": "limit",
            "type": "NUMBER",
            "description": "Maximum number of records to return.",
            "required": false,
            "defaultValue": "10"
          }
        ]
      },
      {
        "name": "Stock - Create Paquete",
        "code": "odoo-stock-package-create",
        "description": "Creates a new record of 'stock.package' (Paquete).",
        "endpointPath": "/json/2/stock.package/create",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"vals_list\": [{\"name\": \"{{name}}\", \"location_id\": {{location_id}}}]}",
        "parameters": [
          {
            "name": "name",
            "type": "STRING",
            "description": "Referencia del paquete",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "location_id",
            "type": "NUMBER",
            "description": "Ubicación",
            "required": false,
            "defaultValue": ""
          }
        ]
      },
      {
        "name": "Stock - Update Paquete",
        "code": "odoo-stock-package-update",
        "description": "Updates fields of an existing record of 'stock.package' (Paquete) by ID.",
        "endpointPath": "/json/2/stock.package/write",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"ids\": [{{id}}], \"vals\": {\"name\": \"{{name}}\", \"location_id\": {{location_id}}}}",
        "parameters": [
          {
            "name": "id",
            "type": "NUMBER",
            "description": "ID of the record to update.",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "name",
            "type": "STRING",
            "description": "Referencia del paquete (leave empty to not modify)",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "location_id",
            "type": "NUMBER",
            "description": "Ubicación (leave empty to not modify)",
            "required": false,
            "defaultValue": ""
          }
        ]
      },
      {
        "name": "Stock - Delete Paquete",
        "code": "odoo-stock-package-delete",
        "description": "Deletes an existing record of 'stock.package' (Paquete) by ID.",
        "endpointPath": "/json/2/stock.package/unlink",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"ids\": [{{id}}]}",
        "parameters": [
          {
            "name": "id",
            "type": "NUMBER",
            "description": "ID of the record to delete.",
            "required": true,
            "defaultValue": ""
          }
        ]
      },
      {
        "name": "Stock - List Destino del paquete de existencias",
        "code": "odoo-stock-package-destination-list",
        "description": "Lists records of 'stock.package.destination' (Destino del paquete de existencias) with filters and limits.",
        "endpointPath": "/json/2/stock.package.destination/search_read",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"domain\": [], \"fields\": [\"move_line_ids\", \"id\", \"location_dest_id\", \"display_name\", \"create_date\"], \"limit\": {{limit}}}",
        "parameters": [
          {
            "name": "limit",
            "type": "NUMBER",
            "description": "Maximum number of records to return.",
            "required": false,
            "defaultValue": "10"
          }
        ]
      },
      {
        "name": "Stock - List Historial del paquete en stock",
        "code": "odoo-stock-package-history-list",
        "description": "Lists records of 'stock.package.history' (Historial del paquete en stock) with filters and limits.",
        "endpointPath": "/json/2/stock.package.history/search_read",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"domain\": [], \"fields\": [\"company_id\", \"location_id\", \"package_id\", \"move_line_ids\", \"id\", \"display_name\", \"package_name\", \"create_date\"], \"limit\": {{limit}}}",
        "parameters": [
          {
            "name": "limit",
            "type": "NUMBER",
            "description": "Maximum number of records to return.",
            "required": false,
            "defaultValue": "10"
          }
        ]
      },
      {
        "name": "Stock - List Tipo de paquete de existencias",
        "code": "odoo-stock-package-type-list",
        "description": "Lists records of 'stock.package.type' (Tipo de paquete de existencias) with filters and limits.",
        "endpointPath": "/json/2/stock.package.type/search_read",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"domain\": [], \"fields\": [\"package_use\", \"name\", \"id\", \"display_name\", \"create_date\"], \"limit\": {{limit}}}",
        "parameters": [
          {
            "name": "limit",
            "type": "NUMBER",
            "description": "Maximum number of records to return.",
            "required": false,
            "defaultValue": "10"
          }
        ]
      },
      {
        "name": "Stock - List Transferir",
        "code": "odoo-stock-picking-list",
        "description": "Lists records of 'stock.picking' (Transferir) with filters and limits.",
        "endpointPath": "/json/2/stock.picking/search_read",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"domain\": [], \"fields\": [\"state\", \"location_id\", \"name\", \"lot_id\", \"picking_type_id\", \"product_id\", \"id\", \"move_type\", \"location_dest_id\", \"display_name\", \"create_date\"], \"limit\": {{limit}}}",
        "parameters": [
          {
            "name": "limit",
            "type": "NUMBER",
            "description": "Maximum number of records to return.",
            "required": false,
            "defaultValue": "10"
          }
        ]
      },
      {
        "name": "Stock - Create Transferir",
        "code": "odoo-stock-picking-create",
        "description": "Creates a new record of 'stock.picking' (Transferir).",
        "endpointPath": "/json/2/stock.picking/create",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"vals_list\": [{\"location_dest_id\": {{location_dest_id}}, \"location_id\": {{location_id}}, \"move_type\": \"{{move_type}}\", \"picking_type_id\": {{picking_type_id}}}]}",
        "parameters": [
          {
            "name": "location_dest_id",
            "type": "NUMBER",
            "description": "Ubicación de destino",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "location_id",
            "type": "NUMBER",
            "description": "Ubicación de origen",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "move_type",
            "type": "STRING",
            "description": "Política de envío",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "picking_type_id",
            "type": "NUMBER",
            "description": "Tipo de operación",
            "required": true,
            "defaultValue": ""
          }
        ]
      },
      {
        "name": "Stock - Update Transferir",
        "code": "odoo-stock-picking-update",
        "description": "Updates fields of an existing record of 'stock.picking' (Transferir) by ID.",
        "endpointPath": "/json/2/stock.picking/write",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"ids\": [{{id}}], \"vals\": {\"location_dest_id\": {{location_dest_id}}, \"location_id\": {{location_id}}, \"move_type\": \"{{move_type}}\", \"picking_type_id\": {{picking_type_id}}}}",
        "parameters": [
          {
            "name": "id",
            "type": "NUMBER",
            "description": "ID of the record to update.",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "location_dest_id",
            "type": "NUMBER",
            "description": "Ubicación de destino (leave empty to not modify)",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "location_id",
            "type": "NUMBER",
            "description": "Ubicación de origen (leave empty to not modify)",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "move_type",
            "type": "STRING",
            "description": "Política de envío (leave empty to not modify)",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "picking_type_id",
            "type": "NUMBER",
            "description": "Tipo de operación (leave empty to not modify)",
            "required": false,
            "defaultValue": ""
          }
        ]
      },
      {
        "name": "Stock - Delete Transferir",
        "code": "odoo-stock-picking-delete",
        "description": "Deletes an existing record of 'stock.picking' (Transferir) by ID.",
        "endpointPath": "/json/2/stock.picking/unlink",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"ids\": [{{id}}]}",
        "parameters": [
          {
            "name": "id",
            "type": "NUMBER",
            "description": "ID of the record to delete.",
            "required": true,
            "defaultValue": ""
          }
        ]
      },
      {
        "name": "Stock - List Tipo de recolección",
        "code": "odoo-stock-picking-type-list",
        "description": "Lists records of 'stock.picking.type' (Tipo de recolección) with filters and limits.",
        "endpointPath": "/json/2/stock.picking.type/search_read",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"domain\": [], \"fields\": [\"company_id\", \"sequence_code\", \"create_backorder\", \"default_location_dest_id\", \"reservation_method\", \"code\", \"default_location_src_id\", \"name\", \"warehouse_id\", \"id\", \"move_type\", \"display_name\", \"create_date\"], \"limit\": {{limit}}}",
        "parameters": [
          {
            "name": "limit",
            "type": "NUMBER",
            "description": "Maximum number of records to return.",
            "required": false,
            "defaultValue": "10"
          }
        ]
      },
      {
        "name": "Stock - List Asistente para incluir en el paquete",
        "code": "odoo-stock-put-in-pack-list",
        "description": "Lists records of 'stock.put.in.pack' (Asistente para incluir en el paquete) with filters and limits.",
        "endpointPath": "/json/2/stock.put.in.pack/search_read",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"domain\": [], \"fields\": [\"display_name\", \"create_date\", \"id\"], \"limit\": {{limit}}}",
        "parameters": [
          {
            "name": "limit",
            "type": "NUMBER",
            "description": "Maximum number of records to return.",
            "required": false,
            "defaultValue": "10"
          }
        ]
      },
      {
        "name": "Stock - List Regla de almacenamiento",
        "code": "odoo-stock-putaway-rule-list",
        "description": "Lists records of 'stock.putaway.rule' (Regla de almacenamiento) with filters and limits.",
        "endpointPath": "/json/2/stock.putaway.rule/search_read",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"domain\": [], \"fields\": [\"company_id\", \"location_out_id\", \"product_id\", \"id\", \"location_in_id\", \"display_name\", \"create_date\"], \"limit\": {{limit}}}",
        "parameters": [
          {
            "name": "limit",
            "type": "NUMBER",
            "description": "Maximum number of records to return.",
            "required": false,
            "defaultValue": "10"
          }
        ]
      },
      {
        "name": "Stock - List Reporte de cantidad de existencias",
        "code": "odoo-stock-quant-list",
        "description": "Lists records of 'stock.quant' (Reporte de cantidad de existencias) with filters and limits.",
        "endpointPath": "/json/2/stock.quant/search_read",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"domain\": [], \"fields\": [\"display_name\", \"product_id\", \"location_id\", \"quantity\", \"reserved_quantity\", \"id\"], \"limit\": {{limit}}}",
        "parameters": [
          {
            "name": "limit",
            "type": "NUMBER",
            "description": "Maximum number of records to return.",
            "required": false,
            "defaultValue": "10"
          }
        ]
      },
      {
        "name": "Stock - Create Reporte de cantidad de existencias",
        "code": "odoo-stock-quant-create",
        "description": "Creates a new record of 'stock.quant' (Reporte de cantidad de existencias).",
        "endpointPath": "/json/2/stock.quant/create",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"vals_list\": [{\"product_id\": {{product_id}}}]}",
        "parameters": [
          {
            "name": "product_id",
            "type": "NUMBER",
            "description": "Producto",
            "required": false,
            "defaultValue": ""
          }
        ]
      },
      {
        "name": "Stock - Update Reporte de cantidad de existencias",
        "code": "odoo-stock-quant-update",
        "description": "Updates fields of an existing record of 'stock.quant' (Reporte de cantidad de existencias) by ID.",
        "endpointPath": "/json/2/stock.quant/write",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"ids\": [{{id}}], \"vals\": {\"product_id\": {{product_id}}}}",
        "parameters": [
          {
            "name": "id",
            "type": "NUMBER",
            "description": "ID of the record to update.",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "product_id",
            "type": "NUMBER",
            "description": "Producto (leave empty to not modify)",
            "required": false,
            "defaultValue": ""
          }
        ]
      },
      {
        "name": "Stock - Delete Reporte de cantidad de existencias",
        "code": "odoo-stock-quant-delete",
        "description": "Deletes an existing record of 'stock.quant' (Reporte de cantidad de existencias) by ID.",
        "endpointPath": "/json/2/stock.quant/unlink",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"ids\": [{{id}}]}",
        "parameters": [
          {
            "name": "id",
            "type": "NUMBER",
            "description": "ID of the record to delete.",
            "required": true,
            "defaultValue": ""
          }
        ]
      },
      {
        "name": "Stock - List Traslado de las cantidades de existencias",
        "code": "odoo-stock-quant-relocate-list",
        "description": "Lists records of 'stock.quant.relocate' (Traslado de las cantidades de existencias) with filters and limits.",
        "endpointPath": "/json/2/stock.quant.relocate/search_read",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"domain\": [], \"fields\": [\"display_name\", \"create_date\", \"id\"], \"limit\": {{limit}}}",
        "parameters": [
          {
            "name": "limit",
            "type": "NUMBER",
            "description": "Maximum number of records to return.",
            "required": false,
            "defaultValue": "10"
          }
        ]
      },
      {
        "name": "Stock - List Historial de cantidad de existencias",
        "code": "odoo-stock-quantity-history-list",
        "description": "Lists records of 'stock.quantity.history' (Historial de cantidad de existencias) with filters and limits.",
        "endpointPath": "/json/2/stock.quantity.history/search_read",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"domain\": [], \"fields\": [\"display_name\", \"create_date\", \"id\"], \"limit\": {{limit}}}",
        "parameters": [
          {
            "name": "limit",
            "type": "NUMBER",
            "description": "Maximum number of records to return.",
            "required": false,
            "defaultValue": "10"
          }
        ]
      },
      {
        "name": "Stock - List Referencia entre documentos de inventario",
        "code": "odoo-stock-reference-list",
        "description": "Lists records of 'stock.reference' (Referencia entre documentos de inventario) with filters and limits.",
        "endpointPath": "/json/2/stock.reference/search_read",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"domain\": [], \"fields\": [\"name\", \"display_name\", \"create_date\", \"id\"], \"limit\": {{limit}}}",
        "parameters": [
          {
            "name": "limit",
            "type": "NUMBER",
            "description": "Maximum number of records to return.",
            "required": false,
            "defaultValue": "10"
          }
        ]
      },
      {
        "name": "Stock - List Información de reabastecimiento del proveedor de inventario",
        "code": "odoo-stock-replenishment-info-list",
        "description": "Lists records of 'stock.replenishment.info' (Información de reabastecimiento del proveedor de inventario) with filters and limits.",
        "endpointPath": "/json/2/stock.replenishment.info/search_read",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"domain\": [], \"fields\": [\"product_min_qty\", \"product_max_qty\", \"id\", \"product_id\", \"percent_factor\", \"based_on\", \"display_name\", \"create_date\"], \"limit\": {{limit}}}",
        "parameters": [
          {
            "name": "limit",
            "type": "NUMBER",
            "description": "Maximum number of records to return.",
            "required": false,
            "defaultValue": "10"
          }
        ]
      },
      {
        "name": "Stock - List Opción de reabastecimiento de almacén de existencias",
        "code": "odoo-stock-replenishment-option-list",
        "description": "Lists records of 'stock.replenishment.option' (Opción de reabastecimiento de almacén de existencias) with filters and limits.",
        "endpointPath": "/json/2/stock.replenishment.option/search_read",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"domain\": [], \"fields\": [\"location_id\", \"product_id\", \"warehouse_id\", \"id\", \"display_name\", \"create_date\"], \"limit\": {{limit}}}",
        "parameters": [
          {
            "name": "limit",
            "type": "NUMBER",
            "description": "Maximum number of records to return.",
            "required": false,
            "defaultValue": "10"
          }
        ]
      },
      {
        "name": "Stock - List Solicitar un recuento de inventario",
        "code": "odoo-stock-request-count-list",
        "description": "Lists records of 'stock.request.count' (Solicitar un recuento de inventario) with filters and limits.",
        "endpointPath": "/json/2/stock.request.count/search_read",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"domain\": [], \"fields\": [\"inventory_date\", \"display_name\", \"create_date\", \"id\"], \"limit\": {{limit}}}",
        "parameters": [
          {
            "name": "limit",
            "type": "NUMBER",
            "description": "Maximum number of records to return.",
            "required": false,
            "defaultValue": "10"
          }
        ]
      },
      {
        "name": "Stock - List Recolección de devolución",
        "code": "odoo-stock-return-picking-list",
        "description": "Lists records of 'stock.return.picking' (Recolección de devolución) with filters and limits.",
        "endpointPath": "/json/2/stock.return.picking/search_read",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"domain\": [], \"fields\": [\"display_name\", \"create_date\", \"id\"], \"limit\": {{limit}}}",
        "parameters": [
          {
            "name": "limit",
            "type": "NUMBER",
            "description": "Maximum number of records to return.",
            "required": false,
            "defaultValue": "10"
          }
        ]
      },
      {
        "name": "Stock - List Línea de recolección de devolución",
        "code": "odoo-stock-return-picking-line-list",
        "description": "Lists records of 'stock.return.picking.line' (Línea de recolección de devolución) with filters and limits.",
        "endpointPath": "/json/2/stock.return.picking.line/search_read",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"domain\": [], \"fields\": [\"quantity\", \"product_id\", \"id\", \"display_name\", \"create_date\"], \"limit\": {{limit}}}",
        "parameters": [
          {
            "name": "limit",
            "type": "NUMBER",
            "description": "Maximum number of records to return.",
            "required": false,
            "defaultValue": "10"
          }
        ]
      },
      {
        "name": "Stock - List Rutas de inventario",
        "code": "odoo-stock-route-list",
        "description": "Lists records of 'stock.route' (Rutas de inventario) with filters and limits.",
        "endpointPath": "/json/2/stock.route/search_read",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"domain\": [], \"fields\": [\"name\", \"display_name\", \"create_date\", \"id\"], \"limit\": {{limit}}}",
        "parameters": [
          {
            "name": "limit",
            "type": "NUMBER",
            "description": "Maximum number of records to return.",
            "required": false,
            "defaultValue": "10"
          }
        ]
      },
      {
        "name": "Stock - List Regla de inventario",
        "code": "odoo-stock-rule-list",
        "description": "Lists records of 'stock.rule' (Regla de inventario) with filters and limits.",
        "endpointPath": "/json/2/stock.rule/search_read",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"domain\": [], \"fields\": [\"action\", \"name\", \"procure_method\", \"auto\", \"picking_type_id\", \"id\", \"warehouse_id\", \"route_id\", \"location_dest_id\", \"display_name\", \"create_date\"], \"limit\": {{limit}}}",
        "parameters": [
          {
            "name": "limit",
            "type": "NUMBER",
            "description": "Maximum number of records to return.",
            "required": false,
            "defaultValue": "10"
          }
        ]
      },
      {
        "name": "Stock - List Reporte de reglas de existencias",
        "code": "odoo-stock-rules-report-list",
        "description": "Lists records of 'stock.rules.report' (Reporte de reglas de existencias) with filters and limits.",
        "endpointPath": "/json/2/stock.rules.report/search_read",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"domain\": [], \"fields\": [\"warehouse_ids\", \"product_tmpl_id\", \"product_id\", \"product_has_variants\", \"id\", \"display_name\", \"create_date\"], \"limit\": {{limit}}}",
        "parameters": [
          {
            "name": "limit",
            "type": "NUMBER",
            "description": "Maximum number of records to return.",
            "required": false,
            "defaultValue": "10"
          }
        ]
      },
      {
        "name": "Stock - List Desechar",
        "code": "odoo-stock-scrap-list",
        "description": "Lists records of 'stock.scrap' (Desechar) with filters and limits.",
        "endpointPath": "/json/2/stock.scrap/search_read",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"domain\": [], \"fields\": [\"company_id\", \"state\", \"location_id\", \"product_uom_id\", \"scrap_location_id\", \"name\", \"lot_id\", \"product_id\", \"id\", \"scrap_qty\", \"display_name\", \"create_date\"], \"limit\": {{limit}}}",
        "parameters": [
          {
            "name": "limit",
            "type": "NUMBER",
            "description": "Maximum number of records to return.",
            "required": false,
            "defaultValue": "10"
          }
        ]
      },
      {
        "name": "Stock - Create Desechar",
        "code": "odoo-stock-scrap-create",
        "description": "Creates a new record of 'stock.scrap' (Desechar).",
        "endpointPath": "/json/2/stock.scrap/create",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"vals_list\": [{\"company_id\": {{company_id}}, \"location_id\": {{location_id}}, \"name\": \"{{name}}\", \"product_id\": {{product_id}}, \"product_uom_id\": {{product_uom_id}}, \"scrap_location_id\": {{scrap_location_id}}, \"scrap_qty\": {{scrap_qty}}}]}",
        "parameters": [
          {
            "name": "company_id",
            "type": "NUMBER",
            "description": "Empresa",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "location_id",
            "type": "NUMBER",
            "description": "Ubicación de origen",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "name",
            "type": "STRING",
            "description": "Referencia",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "product_id",
            "type": "NUMBER",
            "description": "Producto",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "product_uom_id",
            "type": "NUMBER",
            "description": "Unidad",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "scrap_location_id",
            "type": "NUMBER",
            "description": "Ubicación de desecho",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "scrap_qty",
            "type": "NUMBER",
            "description": "Cantidad",
            "required": true,
            "defaultValue": ""
          }
        ]
      },
      {
        "name": "Stock - Update Desechar",
        "code": "odoo-stock-scrap-update",
        "description": "Updates fields of an existing record of 'stock.scrap' (Desechar) by ID.",
        "endpointPath": "/json/2/stock.scrap/write",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"ids\": [{{id}}], \"vals\": {\"company_id\": {{company_id}}, \"location_id\": {{location_id}}, \"name\": \"{{name}}\", \"product_id\": {{product_id}}, \"product_uom_id\": {{product_uom_id}}, \"scrap_location_id\": {{scrap_location_id}}, \"scrap_qty\": {{scrap_qty}}}}",
        "parameters": [
          {
            "name": "id",
            "type": "NUMBER",
            "description": "ID of the record to update.",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "company_id",
            "type": "NUMBER",
            "description": "Empresa (leave empty to not modify)",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "location_id",
            "type": "NUMBER",
            "description": "Ubicación de origen (leave empty to not modify)",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "name",
            "type": "STRING",
            "description": "Referencia (leave empty to not modify)",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "product_id",
            "type": "NUMBER",
            "description": "Producto (leave empty to not modify)",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "product_uom_id",
            "type": "NUMBER",
            "description": "Unidad (leave empty to not modify)",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "scrap_location_id",
            "type": "NUMBER",
            "description": "Ubicación de desecho (leave empty to not modify)",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "scrap_qty",
            "type": "NUMBER",
            "description": "Cantidad (leave empty to not modify)",
            "required": false,
            "defaultValue": ""
          }
        ]
      },
      {
        "name": "Stock - Delete Desechar",
        "code": "odoo-stock-scrap-delete",
        "description": "Deletes an existing record of 'stock.scrap' (Desechar) by ID.",
        "endpointPath": "/json/2/stock.scrap/unlink",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"ids\": [{{id}}]}",
        "parameters": [
          {
            "name": "id",
            "type": "NUMBER",
            "description": "ID of the record to delete.",
            "required": true,
            "defaultValue": ""
          }
        ]
      },
      {
        "name": "Stock - List Etiqueta del motivo de descarte",
        "code": "odoo-stock-scrap-reason-tag-list",
        "description": "Lists records of 'stock.scrap.reason.tag' (Etiqueta del motivo de descarte) with filters and limits.",
        "endpointPath": "/json/2/stock.scrap.reason.tag/search_read",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"domain\": [], \"fields\": [\"name\", \"display_name\", \"create_date\", \"id\"], \"limit\": {{limit}}}",
        "parameters": [
          {
            "name": "limit",
            "type": "NUMBER",
            "description": "Maximum number of records to return.",
            "required": false,
            "defaultValue": "10"
          }
        ]
      },
      {
        "name": "Stock - List Categoría de almacenamiento",
        "code": "odoo-stock-storage-category-list",
        "description": "Lists records of 'stock.storage.category' (Categoría de almacenamiento) with filters and limits.",
        "endpointPath": "/json/2/stock.storage.category/search_read",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"domain\": [], \"fields\": [\"allow_new_product\", \"name\", \"id\", \"display_name\", \"create_date\"], \"limit\": {{limit}}}",
        "parameters": [
          {
            "name": "limit",
            "type": "NUMBER",
            "description": "Maximum number of records to return.",
            "required": false,
            "defaultValue": "10"
          }
        ]
      },
      {
        "name": "Stock - List Capacidad de la categoría de almacenamiento",
        "code": "odoo-stock-storage-category-capacity-list",
        "description": "Lists records of 'stock.storage.category.capacity' (Capacidad de la categoría de almacenamiento) with filters and limits.",
        "endpointPath": "/json/2/stock.storage.category.capacity/search_read",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"domain\": [], \"fields\": [\"quantity\", \"storage_category_id\", \"product_id\", \"id\", \"display_name\", \"create_date\"], \"limit\": {{limit}}}",
        "parameters": [
          {
            "name": "limit",
            "type": "NUMBER",
            "description": "Maximum number of records to return.",
            "required": false,
            "defaultValue": "10"
          }
        ]
      },
      {
        "name": "Stock - List Reporte de trazabilidad",
        "code": "odoo-stock-traceability-report-list",
        "description": "Lists records of 'stock.traceability.report' (Reporte de trazabilidad) with filters and limits.",
        "endpointPath": "/json/2/stock.traceability.report/search_read",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"domain\": [], \"fields\": [\"display_name\", \"create_date\", \"id\"], \"limit\": {{limit}}}",
        "parameters": [
          {
            "name": "limit",
            "type": "NUMBER",
            "description": "Maximum number of records to return.",
            "required": false,
            "defaultValue": "10"
          }
        ]
      },
      {
        "name": "Stock - List Almacén",
        "code": "odoo-stock-warehouse-list",
        "description": "Lists records of 'stock.warehouse' (Almacén) with filters and limits.",
        "endpointPath": "/json/2/stock.warehouse/search_read",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"domain\": [], \"fields\": [\"company_id\", \"view_location_id\", \"reception_steps\", \"delivery_steps\", \"lot_stock_id\", \"code\", \"name\", \"id\", \"display_name\", \"create_date\"], \"limit\": {{limit}}}",
        "parameters": [
          {
            "name": "limit",
            "type": "NUMBER",
            "description": "Maximum number of records to return.",
            "required": false,
            "defaultValue": "10"
          }
        ]
      },
      {
        "name": "Stock - Create Almacén",
        "code": "odoo-stock-warehouse-create",
        "description": "Creates a new record of 'stock.warehouse' (Almacén).",
        "endpointPath": "/json/2/stock.warehouse/create",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"vals_list\": [{\"code\": \"{{code}}\", \"company_id\": {{company_id}}, \"delivery_steps\": \"{{delivery_steps}}\", \"lot_stock_id\": {{lot_stock_id}}, \"name\": \"{{name}}\", \"reception_steps\": \"{{reception_steps}}\", \"view_location_id\": {{view_location_id}}}]}",
        "parameters": [
          {
            "name": "code",
            "type": "STRING",
            "description": "Nombre corto",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "company_id",
            "type": "NUMBER",
            "description": "Empresa",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "delivery_steps",
            "type": "STRING",
            "description": "Envíos salientes",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "lot_stock_id",
            "type": "NUMBER",
            "description": "Ubicación de existencias",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "name",
            "type": "STRING",
            "description": "Almacén",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "reception_steps",
            "type": "STRING",
            "description": "Envíos entrantes",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "view_location_id",
            "type": "NUMBER",
            "description": "Ver ubicación",
            "required": true,
            "defaultValue": ""
          }
        ]
      },
      {
        "name": "Stock - Update Almacén",
        "code": "odoo-stock-warehouse-update",
        "description": "Updates fields of an existing record of 'stock.warehouse' (Almacén) by ID.",
        "endpointPath": "/json/2/stock.warehouse/write",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"ids\": [{{id}}], \"vals\": {\"code\": \"{{code}}\", \"company_id\": {{company_id}}, \"delivery_steps\": \"{{delivery_steps}}\", \"lot_stock_id\": {{lot_stock_id}}, \"name\": \"{{name}}\", \"reception_steps\": \"{{reception_steps}}\", \"view_location_id\": {{view_location_id}}}}",
        "parameters": [
          {
            "name": "id",
            "type": "NUMBER",
            "description": "ID of the record to update.",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "code",
            "type": "STRING",
            "description": "Nombre corto (leave empty to not modify)",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "company_id",
            "type": "NUMBER",
            "description": "Empresa (leave empty to not modify)",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "delivery_steps",
            "type": "STRING",
            "description": "Envíos salientes (leave empty to not modify)",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "lot_stock_id",
            "type": "NUMBER",
            "description": "Ubicación de existencias (leave empty to not modify)",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "name",
            "type": "STRING",
            "description": "Almacén (leave empty to not modify)",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "reception_steps",
            "type": "STRING",
            "description": "Envíos entrantes (leave empty to not modify)",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "view_location_id",
            "type": "NUMBER",
            "description": "Ver ubicación (leave empty to not modify)",
            "required": false,
            "defaultValue": ""
          }
        ]
      },
      {
        "name": "Stock - Delete Almacén",
        "code": "odoo-stock-warehouse-delete",
        "description": "Deletes an existing record of 'stock.warehouse' (Almacén) by ID.",
        "endpointPath": "/json/2/stock.warehouse/unlink",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"ids\": [{{id}}]}",
        "parameters": [
          {
            "name": "id",
            "type": "NUMBER",
            "description": "ID of the record to delete.",
            "required": true,
            "defaultValue": ""
          }
        ]
      },
      {
        "name": "Stock - List Regla de inventario mínimo",
        "code": "odoo-stock-warehouse-orderpoint-list",
        "description": "Lists records of 'stock.warehouse.orderpoint' (Regla de inventario mínimo) with filters and limits.",
        "endpointPath": "/json/2/stock.warehouse.orderpoint/search_read",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"domain\": [], \"fields\": [\"company_id\", \"product_min_qty\", \"product_max_qty\", \"location_id\", \"name\", \"trigger\", \"product_id\", \"warehouse_id\", \"id\", \"display_name\", \"create_date\"], \"limit\": {{limit}}}",
        "parameters": [
          {
            "name": "limit",
            "type": "NUMBER",
            "description": "Maximum number of records to return.",
            "required": false,
            "defaultValue": "10"
          }
        ]
      },
      {
        "name": "Stock - Create Regla de inventario mínimo",
        "code": "odoo-stock-warehouse-orderpoint-create",
        "description": "Creates a new record of 'stock.warehouse.orderpoint' (Regla de inventario mínimo).",
        "endpointPath": "/json/2/stock.warehouse.orderpoint/create",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"vals_list\": [{\"company_id\": {{company_id}}, \"location_id\": {{location_id}}, \"name\": \"{{name}}\", \"product_id\": {{product_id}}, \"product_max_qty\": {{product_max_qty}}, \"product_min_qty\": {{product_min_qty}}, \"trigger\": \"{{trigger}}\", \"warehouse_id\": {{warehouse_id}}}]}",
        "parameters": [
          {
            "name": "company_id",
            "type": "NUMBER",
            "description": "Empresa",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "location_id",
            "type": "NUMBER",
            "description": "Ubicación",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "name",
            "type": "STRING",
            "description": "Nombre",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "product_id",
            "type": "NUMBER",
            "description": "Producto",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "product_max_qty",
            "type": "NUMBER",
            "description": "Cantidad máxima",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "product_min_qty",
            "type": "NUMBER",
            "description": "Cantidad mínima",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "trigger",
            "type": "STRING",
            "description": "Activar",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "warehouse_id",
            "type": "NUMBER",
            "description": "Almacén",
            "required": true,
            "defaultValue": ""
          }
        ]
      },
      {
        "name": "Stock - Update Regla de inventario mínimo",
        "code": "odoo-stock-warehouse-orderpoint-update",
        "description": "Updates fields of an existing record of 'stock.warehouse.orderpoint' (Regla de inventario mínimo) by ID.",
        "endpointPath": "/json/2/stock.warehouse.orderpoint/write",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"ids\": [{{id}}], \"vals\": {\"company_id\": {{company_id}}, \"location_id\": {{location_id}}, \"name\": \"{{name}}\", \"product_id\": {{product_id}}, \"product_max_qty\": {{product_max_qty}}, \"product_min_qty\": {{product_min_qty}}, \"trigger\": \"{{trigger}}\", \"warehouse_id\": {{warehouse_id}}}}",
        "parameters": [
          {
            "name": "id",
            "type": "NUMBER",
            "description": "ID of the record to update.",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "company_id",
            "type": "NUMBER",
            "description": "Empresa (leave empty to not modify)",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "location_id",
            "type": "NUMBER",
            "description": "Ubicación (leave empty to not modify)",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "name",
            "type": "STRING",
            "description": "Nombre (leave empty to not modify)",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "product_id",
            "type": "NUMBER",
            "description": "Producto (leave empty to not modify)",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "product_max_qty",
            "type": "NUMBER",
            "description": "Cantidad máxima (leave empty to not modify)",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "product_min_qty",
            "type": "NUMBER",
            "description": "Cantidad mínima (leave empty to not modify)",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "trigger",
            "type": "STRING",
            "description": "Activar (leave empty to not modify)",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "warehouse_id",
            "type": "NUMBER",
            "description": "Almacén (leave empty to not modify)",
            "required": false,
            "defaultValue": ""
          }
        ]
      },
      {
        "name": "Stock - Delete Regla de inventario mínimo",
        "code": "odoo-stock-warehouse-orderpoint-delete",
        "description": "Deletes an existing record of 'stock.warehouse.orderpoint' (Regla de inventario mínimo) by ID.",
        "endpointPath": "/json/2/stock.warehouse.orderpoint/unlink",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"ids\": [{{id}}]}",
        "parameters": [
          {
            "name": "id",
            "type": "NUMBER",
            "description": "ID of the record to delete.",
            "required": true,
            "defaultValue": ""
          }
        ]
      },
      {
        "name": "Stock - List Advertir de cantidad de desecho insuficiente",
        "code": "odoo-stock-warn-insufficient-qty-scrap-list",
        "description": "Lists records of 'stock.warn.insufficient.qty.scrap' (Advertir de cantidad de desecho insuficiente) with filters and limits.",
        "endpointPath": "/json/2/stock.warn.insufficient.qty.scrap/search_read",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"domain\": [], \"fields\": [\"location_id\", \"quantity\", \"product_id\", \"id\", \"product_uom_name\", \"display_name\", \"create_date\"], \"limit\": {{limit}}}",
        "parameters": [
          {
            "name": "limit",
            "type": "NUMBER",
            "description": "Maximum number of records to return.",
            "required": false,
            "defaultValue": "10"
          }
        ]
      },
      {
        "name": "Manufacturing - List production orders",
        "code": "odoo-mrp-production-list",
        "description": "Lists manufacturing production orders from Odoo. Returns reference name, product ID, target quantity, unit of measure, currently producing quantity, dates, and state.",
        "endpointPath": "/json/2/mrp.production/search_read",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"domain\": [], \"fields\": [\"name\", \"product_id\", \"product_qty\", \"product_uom_id\", \"qty_producing\", \"date_start\", \"date_finished\", \"state\", \"bom_id\"], \"limit\": {{limit}}}",
        "parameters": [
          {
            "name": "limit",
            "type": "NUMBER",
            "description": "Maximum number of production orders to return.",
            "required": false,
            "defaultValue": "10"
          }
        ]
      },
      {
        "name": "Manufacturing - Create production order",
        "code": "odoo-mrp-production-create",
        "description": "Creates a new manufacturing production order for a product.",
        "endpointPath": "/json/2/mrp.production/create",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"vals_list\": [{\"product_id\": {{product_id}}, \"product_qty\": {{product_qty}}, \"bom_id\": {{bom_id}}, \"date_start\": \"{{date_start}}\", \"user_id\": {{user_id}}}]}",
        "parameters": [
          {
            "name": "product_id",
            "type": "NUMBER",
            "description": "ID of the product (product.product) to manufacture.",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "product_qty",
            "type": "NUMBER",
            "description": "Quantity of the product to produce.",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "bom_id",
            "type": "NUMBER",
            "description": "ID of the Bill of Materials (mrp.bom) to use. If not specified, Odoo will select the default BoM for the product.",
            "required": false,
            "defaultValue": "null"
          },
          {
            "name": "date_start",
            "type": "STRING",
            "description": "Scheduled start date (YYYY-MM-DD HH:MM:SS) in UTC.",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "user_id",
            "type": "NUMBER",
            "description": "ID of the user responsible for this order.",
            "required": false,
            "defaultValue": "null"
          }
        ]
      },
      {
        "name": "Manufacturing - Update production order",
        "code": "odoo-mrp-production-update",
        "description": "Updates target quantity, start date, or responsible user on an existing production order.",
        "endpointPath": "/json/2/mrp.production/write",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"ids\": [{{id}}], \"vals\": {\"product_qty\": {{product_qty}}, \"date_start\": \"{{date_start}}\", \"user_id\": {{user_id}}}}",
        "parameters": [
          {
            "name": "id",
            "type": "NUMBER",
            "description": "ID of the production order to update.",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "product_qty",
            "type": "NUMBER",
            "description": "New target quantity to produce.",
            "required": false,
            "defaultValue": "null"
          },
          {
            "name": "date_start",
            "type": "STRING",
            "description": "New scheduled start date (YYYY-MM-DD HH:MM:SS) in UTC.",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "user_id",
            "type": "NUMBER",
            "description": "New ID of the responsible user.",
            "required": false,
            "defaultValue": "null"
          }
        ]
      },
      {
        "name": "Manufacturing - Delete production order",
        "code": "odoo-mrp-production-delete",
        "description": "Deletes a production order from Odoo.",
        "endpointPath": "/json/2/mrp.production/unlink",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"ids\": [{{id}}]}",
        "parameters": [
          {
            "name": "id",
            "type": "NUMBER",
            "description": "ID of the production order to delete.",
            "required": true,
            "defaultValue": ""
          }
        ]
      },
      {
        "name": "Manufacturing - Confirm production order",
        "code": "odoo-mrp-production-confirm",
        "description": "Confirms the production order, creating stock moves for components and finished products.",
        "endpointPath": "/json/2/mrp.production/action_confirm",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"ids\": [{{id}}]}",
        "parameters": [
          {
            "name": "id",
            "type": "NUMBER",
            "description": "ID of the production order to confirm.",
            "required": true,
            "defaultValue": ""
          }
        ]
      },
      {
        "name": "Manufacturing - Plan production order",
        "code": "odoo-mrp-production-plan",
        "description": "Plans work orders associated with the production order.",
        "endpointPath": "/json/2/mrp.production/button_plan",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"ids\": [{{id}}]}",
        "parameters": [
          {
            "name": "id",
            "type": "NUMBER",
            "description": "ID of the production order to plan.",
            "required": true,
            "defaultValue": ""
          }
        ]
      },
      {
        "name": "Manufacturing - Unplan production order",
        "code": "odoo-mrp-production-unplan",
        "description": "Unplans work orders associated with the production order.",
        "endpointPath": "/json/2/mrp.production/button_unplan",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"ids\": [{{id}}]}",
        "parameters": [
          {
            "name": "id",
            "type": "NUMBER",
            "description": "ID of the production order to unplan.",
            "required": true,
            "defaultValue": ""
          }
        ]
      },
      {
        "name": "Manufacturing - Mark production order as done",
        "code": "odoo-mrp-production-mark-done",
        "description": "Marks the manufacturing order as completed and processes stock inventory movements.",
        "endpointPath": "/json/2/mrp.production/button_mark_done",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"ids\": [{{id}}]}",
        "parameters": [
          {
            "name": "id",
            "type": "NUMBER",
            "description": "ID of the production order to mark as done.",
            "required": true,
            "defaultValue": ""
          }
        ]
      },
      {
        "name": "Manufacturing - Cancel production order",
        "code": "odoo-mrp-production-cancel",
        "description": "Cancels the manufacturing order and related stock moves.",
        "endpointPath": "/json/2/mrp.production/action_cancel",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"ids\": [{{id}}]}",
        "parameters": [
          {
            "name": "id",
            "type": "NUMBER",
            "description": "ID of the production order to cancel.",
            "required": true,
            "defaultValue": ""
          }
        ]
      },
      {
        "name": "Manufacturing - List Bills of Materials",
        "code": "odoo-mrp-bom-list",
        "description": "Lists Bills of Materials (BoM) from Odoo. Returns template ID, product variant ID, code reference, type, quantity, and unit of measure.",
        "endpointPath": "/json/2/mrp.bom/search_read",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"domain\": [], \"fields\": [\"product_tmpl_id\", \"product_id\", \"code\", \"type\", \"product_qty\", \"product_uom_id\", \"active\"], \"limit\": {{limit}}}",
        "parameters": [
          {
            "name": "limit",
            "type": "NUMBER",
            "description": "Maximum number of BoMs to return.",
            "required": false,
            "defaultValue": "10"
          }
        ]
      },
      {
        "name": "Manufacturing - Create Bill of Materials",
        "code": "odoo-mrp-bom-create",
        "description": "Creates a new Bill of Materials (BoM) for a product template.",
        "endpointPath": "/json/2/mrp.bom/create",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"vals_list\": [{\"product_tmpl_id\": {{product_tmpl_id}}, \"code\": \"{{code}}\", \"type\": \"{{type}}\", \"product_qty\": {{product_qty}}, \"product_uom_id\": {{product_uom_id}}}]}",
        "parameters": [
          {
            "name": "product_tmpl_id",
            "type": "NUMBER",
            "description": "ID of the product template (product.template) this BoM is for.",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "code",
            "type": "STRING",
            "description": "Reference code for this BoM.",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "type",
            "type": "STRING",
            "description": "BoM Type: 'normal' (for manufacturing) or 'phantom' (for kit components).",
            "required": false,
            "defaultValue": "normal"
          },
          {
            "name": "product_qty",
            "type": "NUMBER",
            "description": "Quantity of finished product produced by this BoM.",
            "required": false,
            "defaultValue": "1.0"
          },
          {
            "name": "product_uom_id",
            "type": "NUMBER",
            "description": "ID of the Unit of Measure for the finished product quantity.",
            "required": false,
            "defaultValue": "null"
          }
        ]
      },
      {
        "name": "Manufacturing - Update Bill of Materials",
        "code": "odoo-mrp-bom-update",
        "description": "Updates details such as code, type, or quantity on an existing BoM.",
        "endpointPath": "/json/2/mrp.bom/write",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"ids\": [{{id}}], \"vals\": {\"code\": \"{{code}}\", \"type\": \"{{type}}\", \"product_qty\": {{product_qty}}}}",
        "parameters": [
          {
            "name": "id",
            "type": "NUMBER",
            "description": "ID of the BoM to update.",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "code",
            "type": "STRING",
            "description": "New reference code.",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "type",
            "type": "STRING",
            "description": "New BoM Type ('normal' or 'phantom').",
            "required": false,
            "defaultValue": "normal"
          },
          {
            "name": "product_qty",
            "type": "NUMBER",
            "description": "New quantity produced.",
            "required": false,
            "defaultValue": "null"
          }
        ]
      },
      {
        "name": "Manufacturing - Delete Bill of Materials",
        "code": "odoo-mrp-bom-delete",
        "description": "Deletes a Bill of Materials.",
        "endpointPath": "/json/2/mrp.bom/unlink",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"ids\": [{{id}}]}",
        "parameters": [
          {
            "name": "id",
            "type": "NUMBER",
            "description": "ID of the BoM to delete.",
            "required": true,
            "defaultValue": ""
          }
        ]
      },
      {
        "name": "Manufacturing - List BoM Component Lines",
        "code": "odoo-mrp-bom-line-list",
        "description": "Lists individual component lines belonging to Bills of Materials.",
        "endpointPath": "/json/2/mrp.bom.line/search_read",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"domain\": [], \"fields\": [\"bom_id\", \"product_id\", \"product_qty\", \"product_uom_id\", \"sequence\"], \"limit\": {{limit}}}",
        "parameters": [
          {
            "name": "limit",
            "type": "NUMBER",
            "description": "Maximum number of component lines to return.",
            "required": false,
            "defaultValue": "10"
          }
        ]
      },
      {
        "name": "Manufacturing - Create BoM Component Line",
        "code": "odoo-mrp-bom-line-create",
        "description": "Adds a component product to an existing Bill of Materials.",
        "endpointPath": "/json/2/mrp.bom.line/create",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"vals_list\": [{\"bom_id\": {{bom_id}}, \"product_id\": {{product_id}}, \"product_qty\": {{product_qty}}, \"product_uom_id\": {{product_uom_id}}}]}",
        "parameters": [
          {
            "name": "bom_id",
            "type": "NUMBER",
            "description": "ID of the parent BoM (mrp.bom) to attach this component line to.",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "product_id",
            "type": "NUMBER",
            "description": "ID of the component product (product.product).",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "product_qty",
            "type": "NUMBER",
            "description": "Quantity of the component required.",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "product_uom_id",
            "type": "NUMBER",
            "description": "ID of the Unit of Measure of the component quantity.",
            "required": false,
            "defaultValue": "null"
          }
        ]
      },
      {
        "name": "Manufacturing - Update BoM Component Line",
        "code": "odoo-mrp-bom-line-update",
        "description": "Updates target quantity of a component line.",
        "endpointPath": "/json/2/mrp.bom.line/write",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"ids\": [{{id}}], \"vals\": {\"product_qty\": {{product_qty}}}}",
        "parameters": [
          {
            "name": "id",
            "type": "NUMBER",
            "description": "ID of the component line to update.",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "product_qty",
            "type": "NUMBER",
            "description": "New target quantity of component to consume.",
            "required": true,
            "defaultValue": ""
          }
        ]
      },
      {
        "name": "Manufacturing - Delete BoM Component Line",
        "code": "odoo-mrp-bom-line-delete",
        "description": "Removes a component line from a Bill of Materials.",
        "endpointPath": "/json/2/mrp.bom.line/unlink",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"ids\": [{{id}}]}",
        "parameters": [
          {
            "name": "id",
            "type": "NUMBER",
            "description": "ID of the component line to delete.",
            "required": true,
            "defaultValue": ""
          }
        ]
      },
      {
        "name": "Manufacturing - List Work Orders",
        "code": "odoo-mrp-workorder-list",
        "description": "Lists active work orders from Odoo, containing production parent ID, work center, expected duration, dates, and state.",
        "endpointPath": "/json/2/mrp.workorder/search_read",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"domain\": [], \"fields\": [\"name\", \"production_id\", \"workcenter_id\", \"state\", \"duration\", \"duration_expected\", \"date_start\", \"date_finished\"], \"limit\": {{limit}}}",
        "parameters": [
          {
            "name": "limit",
            "type": "NUMBER",
            "description": "Maximum number of work orders to return.",
            "required": false,
            "defaultValue": "10"
          }
        ]
      },
      {
        "name": "Manufacturing - Update Work Order",
        "code": "odoo-mrp-workorder-update",
        "description": "Updates expected duration of a work order.",
        "endpointPath": "/json/2/mrp.workorder/write",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"ids\": [{{id}}], \"vals\": {\"duration_expected\": {{duration_expected}}}}",
        "parameters": [
          {
            "name": "id",
            "type": "NUMBER",
            "description": "ID of the work order to update.",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "duration_expected",
            "type": "NUMBER",
            "description": "New expected duration in minutes.",
            "required": true,
            "defaultValue": ""
          }
        ]
      },
      {
        "name": "Manufacturing - Start Work Order",
        "code": "odoo-mrp-workorder-start",
        "description": "Starts running the work order.",
        "endpointPath": "/json/2/mrp.workorder/button_start",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"ids\": [{{id}}]}",
        "parameters": [
          {
            "name": "id",
            "type": "NUMBER",
            "description": "ID of the work order to start.",
            "required": true,
            "defaultValue": ""
          }
        ]
      },
      {
        "name": "Manufacturing - Finish Work Order",
        "code": "odoo-mrp-workorder-finish",
        "description": "Completes the work order.",
        "endpointPath": "/json/2/mrp.workorder/button_finish",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"ids\": [{{id}}]}",
        "parameters": [
          {
            "name": "id",
            "type": "NUMBER",
            "description": "ID of the work order to finish.",
            "required": true,
            "defaultValue": ""
          }
        ]
      },
      {
        "name": "Manufacturing - Pause Work Order",
        "code": "odoo-mrp-workorder-pending",
        "description": "Pauses the work order execution.",
        "endpointPath": "/json/2/mrp.workorder/button_pending",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"ids\": [{{id}}]}",
        "parameters": [
          {
            "name": "id",
            "type": "NUMBER",
            "description": "ID of the work order to pause.",
            "required": true,
            "defaultValue": ""
          }
        ]
      },
      {
        "name": "Manufacturing - Unblock Work Order",
        "code": "odoo-mrp-workorder-unblock",
        "description": "Unblocks the work order if it was blocked.",
        "endpointPath": "/json/2/mrp.workorder/button_unblock",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"ids\": [{{id}}]}",
        "parameters": [
          {
            "name": "id",
            "type": "NUMBER",
            "description": "ID of the work order to unblock.",
            "required": true,
            "defaultValue": ""
          }
        ]
      },
      {
        "name": "Manufacturing - List Work Centers",
        "code": "odoo-mrp-workcenter-list",
        "description": "Lists work centers from Odoo. Returns name, code, time efficiency, and target OEE.",
        "endpointPath": "/json/2/mrp.workcenter/search_read",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"domain\": [], \"fields\": [\"name\", \"code\", \"time_efficiency\", \"oee_target\", \"active\"], \"limit\": {{limit}}}",
        "parameters": [
          {
            "name": "limit",
            "type": "NUMBER",
            "description": "Maximum number of work centers to return.",
            "required": false,
            "defaultValue": "10"
          }
        ]
      },
      {
        "name": "Manufacturing - Create Work Center",
        "code": "odoo-mrp-workcenter-create",
        "description": "Creates a new work center resource.",
        "endpointPath": "/json/2/mrp.workcenter/create",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"vals_list\": [{\"name\": \"{{name}}\", \"code\": \"{{code}}\", \"time_efficiency\": {{time_efficiency}}, \"oee_target\": {{oee_target}}}]}",
        "parameters": [
          {
            "name": "name",
            "type": "STRING",
            "description": "Name of the work center.",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "code",
            "type": "STRING",
            "description": "Short code reference.",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "time_efficiency",
            "type": "NUMBER",
            "description": "Time efficiency factor in percent.",
            "required": false,
            "defaultValue": "100.0"
          },
          {
            "name": "oee_target",
            "type": "NUMBER",
            "description": "Overall Equipment Effectiveness target in percent.",
            "required": false,
            "defaultValue": "85.0"
          }
        ]
      },
      {
        "name": "Manufacturing - Update Work Center",
        "code": "odoo-mrp-workcenter-update",
        "description": "Updates name or code of a work center.",
        "endpointPath": "/json/2/mrp.workcenter/write",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"ids\": [{{id}}], \"vals\": {\"name\": \"{{name}}\", \"code\": \"{{code}}\"}}",
        "parameters": [
          {
            "name": "id",
            "type": "NUMBER",
            "description": "ID of the work center to update.",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "name",
            "type": "STRING",
            "description": "New name of the work center.",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "code",
            "type": "STRING",
            "description": "New short code reference.",
            "required": false,
            "defaultValue": ""
          }
        ]
      },
      {
        "name": "Manufacturing - Delete Work Center",
        "code": "odoo-mrp-workcenter-delete",
        "description": "Deletes a work center from Odoo.",
        "endpointPath": "/json/2/mrp.workcenter/unlink",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"ids\": [{{id}}]}",
        "parameters": [
          {
            "name": "id",
            "type": "NUMBER",
            "description": "ID of the work center to delete.",
            "required": true,
            "defaultValue": ""
          }
        ]
      },
      {
        "name": "Manufacturing - List Product Variants",
        "code": "odoo-mrp-product-list",
        "description": "Lists product variants available in Odoo.",
        "endpointPath": "/json/2/product.product/search_read",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"domain\": [], \"fields\": [\"name\", \"default_code\", \"lst_price\", \"standard_price\", \"type\", \"active\"], \"limit\": {{limit}}}",
        "parameters": [
          {
            "name": "limit",
            "type": "NUMBER",
            "description": "Maximum number of product variants to return.",
            "required": false,
            "defaultValue": "10"
          }
        ]
      },
      {
        "name": "Manufacturing - Create Product Variant",
        "code": "odoo-mrp-product-create",
        "description": "Creates a new product variant.",
        "endpointPath": "/json/2/product.product/create",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"vals_list\": [{\"name\": \"{{name}}\", \"default_code\": \"{{default_code}}\", \"lst_price\": {{lst_price}}, \"standard_price\": {{standard_price}}, \"type\": \"{{type}}\"}]}",
        "parameters": [
          {
            "name": "name",
            "type": "STRING",
            "description": "Name of the product variant.",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "default_code",
            "type": "STRING",
            "description": "Internal SKU code.",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "lst_price",
            "type": "NUMBER",
            "description": "Public retail price.",
            "required": false,
            "defaultValue": "0.0"
          },
          {
            "name": "standard_price",
            "type": "NUMBER",
            "description": "Standard cost price.",
            "required": false,
            "defaultValue": "0.0"
          },
          {
            "name": "type",
            "type": "STRING",
            "description": "Product type: 'consu' (consumable), 'service' (service), or 'product' (storable/stockable).",
            "required": false,
            "defaultValue": "consu"
          }
        ]
      },
      {
        "name": "Manufacturing - Update Product Variant",
        "code": "odoo-mrp-product-update",
        "description": "Updates name, SKU, price, or cost of a product variant.",
        "endpointPath": "/json/2/product.product/write",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"ids\": [{{id}}], \"vals\": {\"name\": \"{{name}}\", \"default_code\": \"{{default_code}}\", \"lst_price\": {{lst_price}}, \"standard_price\": {{standard_price}}}}",
        "parameters": [
          {
            "name": "id",
            "type": "NUMBER",
            "description": "ID of the product variant to update.",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "name",
            "type": "STRING",
            "description": "New product name.",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "default_code",
            "type": "STRING",
            "description": "New SKU code.",
            "required": false,
            "defaultValue": ""
          },
          {
            "name": "lst_price",
            "type": "NUMBER",
            "description": "New public retail price.",
            "required": false,
            "defaultValue": "null"
          },
          {
            "name": "standard_price",
            "type": "NUMBER",
            "description": "New standard cost price.",
            "required": false,
            "defaultValue": "null"
          }
        ]
      },
      {
        "name": "Manufacturing - List Unbuild Orders",
        "code": "odoo-mrp-unbuild-list",
        "description": "Lists unbuild orders in Odoo.",
        "endpointPath": "/json/2/mrp.unbuild/search_read",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"domain\": [], \"fields\": [\"name\", \"product_id\", \"product_qty\", \"product_uom_id\", \"bom_id\", \"mo_id\", \"state\"], \"limit\": {{limit}}}",
        "parameters": [
          {
            "name": "limit",
            "type": "NUMBER",
            "description": "Maximum number of unbuild orders to return.",
            "required": false,
            "defaultValue": "10"
          }
        ]
      },
      {
        "name": "Manufacturing - Create Unbuild Order",
        "code": "odoo-mrp-unbuild-create",
        "description": "Creates a new unbuild order for a product and quantity.",
        "endpointPath": "/json/2/mrp.unbuild/create",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"vals_list\": [{\"product_id\": {{product_id}}, \"product_qty\": {{product_qty}}, \"bom_id\": {{bom_id}}, \"mo_id\": {{mo_id}}}]}",
        "parameters": [
          {
            "name": "product_id",
            "type": "NUMBER",
            "description": "ID of the product (product.product) to disassemble.",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "product_qty",
            "type": "NUMBER",
            "description": "Quantity to disassemble.",
            "required": true,
            "defaultValue": ""
          },
          {
            "name": "bom_id",
            "type": "NUMBER",
            "description": "ID of the Bill of Materials to use for disassembly component list.",
            "required": false,
            "defaultValue": "null"
          },
          {
            "name": "mo_id",
            "type": "NUMBER",
            "description": "ID of the source Manufacturing Order (mrp.production) to link.",
            "required": false,
            "defaultValue": "null"
          }
        ]
      },
      {
        "name": "Manufacturing - Validate Unbuild Order",
        "code": "odoo-mrp-unbuild-validate",
        "description": "Validates and processes the disassembly of an unbuild order, restoring components to stock.",
        "endpointPath": "/json/2/mrp.unbuild/action_validate",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"ids\": [{{id}}]}",
        "parameters": [
          {
            "name": "id",
            "type": "NUMBER",
            "description": "ID of the unbuild order to validate.",
            "required": true,
            "defaultValue": ""
          }
        ]
      }
    ]
  }
]
```
