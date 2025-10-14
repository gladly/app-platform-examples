# Gladly APIs for App Platform

The Gladly APIs App for App Platform app provides access to a variety of actions in Gladly via its REST API. This app allows you to automate and manage customer Conversations, Conversation Items, Tasks, SMS, and more, directly from Gladly Sidekick.

Gladly's API documentation: [https://developer.gladly.com/rest/](https://developer.gladly.com/rest/)

## Benefits

- Automate customer support workflows with Gladly Sidekick
- Create Conversation Items (customer activity) for Customers
- Manage Tasks and Assignees for Conversations
- Send SMS messages to customers
- Add Topics and update a Conversation's Status

## Example Use Cases

- Automatically log customer activity (email, phone) as Conversation Items
- Create Tasks for agents based on Customer requests
- Send SMS notifications to customers
- Assign or update Conversation Topics and the Conversation's Status

## Available Actions

The Gladly APIs for App Platform app supports the following management actions:

- **add_topics_to_conversation**  
  Add one or more topics to an existing Conversation.

- **create_customer_activity_item_for_email**  
  Create a Conversation Item for a Customer based on an email interaction.

- **create_customer_activity_item_for_phone**  
  Create a Conversation Item for a Customer based on a phone interaction.

- **create_task_customerid**  
  Create a new Task for a Customer using their customer ID.

- **create_task_email**  
  Create a new Task for a Customer using their email address.

- **create_task_phone**  
  Create a new Task for a Customer using their phone number.

- **inboxes**  
  Retrieve a list of available Inboxes.

- **lookup_customer_by_id**  
  Retrieve a Gladly Customer Profile using their customer ID.

- **lookup_customers_by_email**  
  Retrieve a list of Gladly Customer Profiles using their email address.

- **lookup_customers_by_phone**  
  Retrieve a list of Gladly Customer Profiles using their phone number.

- **send_sms**  
  Send an SMS message to a Customer.

- **topics**  
  Retrieve a list of available Topics.

- **update_conversation_assignee**  
  Update the assignee for a Conversation.

- **update_conversation_status**  
  Update the status of a Conversation.

## Who maintains the integration

The Gladly APIs for App Platform integration is built and maintained by Gladly.

## How the integration works

This app integrates with Gladly via its REST API ([API documentation](https://developer.gladly.com/rest/)). Actions are performed by making authenticated HTTP requests to Gladly endpoints. Authentication is handled via API tokens included in the request headers.

## Setup & Installation

### Before You Start

You will need:
- A Gladly API Token with permissions to perform the desired actions. This can be generated from within the Gladly UI by an Administrator or API User, under Settings > App Developer Tools.

### Installation

1. Obtain your Gladly API Token
2. When creating a new configuration, provide the following objects:

#### Configuration Object

The configuration object includes any non-sensitive settings required for the app, such as the username or other integration parameters.

Example:
```json
{
  "configuration": {
    "username": "your-gladly-username",
    "gladlyHost": "your-domain.us-1.gladly.com"
  }
}
```

#### Secrets Object

The secrets object should include sensitive credentials, such as your Gladly API Token.

Example:
```json
{
  "secrets": {
    "apiToken": "YOUR_GLADLY_API_TOKEN"
  }
}
```

These objects are used to generate authentication headers for all REST requests made by the app.

## Gladly APIs for App Platform Custom App

If you want to dive deeper into the technical details of the app, you can find it in our [app-platform-examples repo](https://github.com/gladly/app-platform-examples). You can always clone it and adapt it to your needs.
