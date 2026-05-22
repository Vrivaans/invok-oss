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
        "name": "Mail - Create activity",
        "code": "odoo-mail-activity-create",
        "description": "Creates a follow-up activity linked to a specific Odoo model and record.",
        "endpointPath": "/json/2/mail.activity/create",
        "httpMethod": "POST",
        "enabled": true,
        "isExportable": true,
        "bodyPayloadTemplate": "{\"vals_list\": [{\"res_model\": \"{{res_model}}\", \"res_id\": {{res_id}}, \"activity_type_id\": {{activity_type_id}}, \"summary\": \"{{summary}}\", \"date_deadline\": \"{{date_deadline}}\"}]}",
        "parameters": [
          {
            "name": "res_model",
            "type": "STRING",
            "description": "Technical name of the target model (e.g., 'crm.lead', 'project.task').",
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
      }
    ]
  }
]
```
