# App Platform UI Forms

This document provides comprehensive guidance for creating and managing App Platform UI Forms that allow agents to perform actions from the agent desktop composition "+" menu.

## Overview

App Platform UI Forms enable agents to interact with external systems directly from the agent desktop by:

- Displaying input forms generated from app platform data
- Executing corresponding actions (mutations or queries) defined in the app's GraphQL action schema
- Providing feedback on success or errors

## Directory Structure

UI forms live in the `ui/forms` directory from the app's root directory:

```
app-root/
├── ui/
│   └── forms/
│       ├── form_name_1/
│       │   ├── config.json
│       │   ├── form.gtpl
│       │   ├── action_inputs.gtpl
│       │   └── action_result.gtpl
│       ├── form_name_2/
│       │   ├── config.json
│       │   ├── form.gtpl
│       │   ├── action_inputs.gtpl
│       │   └── action_result.gtpl
│       └── ...
```

Each form requires three files:
1. **config.json** - Defines the action and form data sources
2. **form.gtpl** - Go template that generates the form configuration
3. **action_inputs.gtpl** - Optional Go template for transforming inputs gathered from the form
4. **action_result.gtpl** - Go template that formats the action result

## config.json Structure

The `config.json` file specifies what action to execute and what data to use for generating the form.

### Schema

```json
{
  "action": {
    "graphqlOperation": "mutation or query (optional, defaults to mutation)",
    "graphqlField": "field name from actions schema"
  },
  "formData": [
    {
      "graphqlSchema": "data or actions",
      "graphqlField": "field name from Query type",
      "inputsTemplate": "template that generates JSON object where each top level field corresponds to one of the graphqlField inputs"
    }
  ]
}
```

### Fields

#### action (Object, Required)

Specifies the action that will be executed when form input is submitted.

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| graphqlField | String | Yes | Field name from the Mutation or Query type in the app's action GraphQL schema |
| graphqlOperation | String | No | Either "mutation" or "query". Defaults to "mutation" if not specified |

#### formData (Array, Required)

Specifies what data is available to the `form.gtpl` template. Data is retrieved in the order specified in the array, which is relevant when an actions schema Query field needs inputs from previously retrieved formData.

Each form data object contains:

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| graphqlSchema | String | Yes | Either "data" or "actions" |
| graphqlField | String | Yes | Field name from the Query type in the specified schema |
| inputsTemplate | String | No | Only needed when the `graphqlField` has inputs. The template MUST create a JSON object where each top level field corresponds to an input of the `graphqlField` |

### Field Input Templates

Field input templates have access to the following template data:

```json
{
  "data": {
    "dataQueryField1": ...,
    "dataQueryField2": ...
  },
  "actions": {
    "actionsQueryField1": ...,
    "actionsQueryField2": ...
  },
  "customer": {
    "id": "some unique Gladly customer profile ID",
    "name": "John Smith",
    "address": "123 Somewhere Street, CA, USA",
    "primaryEmailAddress": "john_smith@email.com",
    "emailAddresses": ["john_smith@gmail.com", "js@yahoo.com"],
    "primaryPhoneNumber": {
        "number": "+15551234567", // in E.164 format
        "type": "HOME|MOBILE|OFFICE|OTHER"
    },
    "phoneNumbers": [
        {
            "number": "+15551234567", // in E.164 format
            "type": "HOME|MOBILE|OFFICE|OTHER"
        }
    ]
  },
}
```

### Example config.json

Configuration for canceling orders:

```json
{
  "action": {
    "graphqlOperation": "mutation",
    "graphqlField": "cancelOrder"
  },
  "formData": [
    {
      "graphqlSchema": "data",
      "graphqlField": "orders"
    },
    {
      "graphqlSchema": "actions",
      "graphqlField": "cancellationReasons"
    }
  ]
}
```

## form.gtpl Template

The `form.gtpl` template generates the form configuration that determines how the form is displayed to agents.

### Template Data

The template receives data structured as follows:

```json
{
  "data": {
    // Results from data schema Query fields
  },
  "actions": {
    // Results from actions schema Query fields
  },
  "customer": {
    // Current customer profile data
  }
}
```

### Form Configuration Schema

The template **MUST** output JSON conforming to this structure:

```json
{
  "title": "Form title displayed at top",
  "submitButton": "Text for submit button",
  "closeButton": "Text for close/cancel button",
  "sections": [
    // Array of section objects (see below)
  ]
}
```

### Top-Level Form Fields

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| title | String | Yes | Form title that appears at the top |
| submitButton | String | No | Text for the submit button |
| closeButton | String | No | Text for the close/cancel button |
| sections | Array | Yes | Array of section objects defining the form content |

### Section Types

#### Text Section

Displays static text in the form.

```json
{
  "type": "text",
  "text": "This is informational text displayed in the form"
}
```

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| type | String | Yes | Must be "text" |
| text | String | Yes | The text to display |

#### Input Section

Wraps an input element (text, select, or checkbox).

```json
{
  "type": "input",
  "label": "Field label",
  "attr": "fieldName",
  "defaultValue": "optional default value",
  "input": {
    // Input element definition (see below)
  },
  "hint": "Optional hint text displayed beneath the input"
}
```

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| type | String | Yes | Must be "input" |
| label | String | Yes | Label displayed next to the input field |
| attr | String | Yes | Field name in the JSON response. **Must be unique across all form fields.** This must match the input parameter name in the action's GraphQL schema |
| defaultValue | Any | No | Initial value for the field |
| input | Object | Yes | Input element (text, select, or checkbox) |
| hint | String | No | Optional hint string that appears beneath the input |

### Input Types

#### Text Input

Single-line text input.

```json
{
  "type": "text",
  "placeholder": "e.g. SALE20",
  "optional": false
}
```

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| type | String | Yes | Must be "text" |
| placeholder | String | No | Placeholder text shown when input is empty |
| optional | Boolean | No | Whether the field is optional. If false, form displays error if empty. Default is false |

#### Select Input

Dropdown menu with options.

```json
{
  "type": "select",
  "placeholder": "Please select an option...",
  "options": [
    {
      "text": "Display Text",
      "value": "submitted-value"
    }
  ],
  "optional": false
}
```

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| type | String | Yes | Must be "select" |
| placeholder | String | No | Placeholder text shown when no option is selected |
| options | Array | Yes | Array of option objects |
| optional | Boolean | No | Whether selection is optional. Default is false |

**Option Object:**

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| text | String | Yes | Text displayed in the dropdown |
| value | String | No | Value submitted in form data. If not provided, the text value is used |

#### Checkbox Input

Single checkbox.

```json
{
  "type": "checkbox",
  "text": "Send a notification to the customer"
}
```

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| type | String | Yes | Must be "checkbox" |
| text | String | Yes | Label for the checkbox |

### Important Constraints

1. **`attr` values must be unique** across all input sections in the form
2. **`attr` values must match** the input parameter names defined in the action's GraphQL schema when there is no `action_inputs.gtpl` template
3. Form must have at least one section
4. Input sections must have exactly one child input element
5. All JSON output must be valid and properly escaped

### Example form.gtpl

```go
{
  "title": "Cancel order",
{{if and .data .data.orders}}
  "submitButton": "Cancel Order",
{{end}}
  "closeButton": "Close",
  "sections": [
{{if and .data .data.orders}}
    {
      "type": "input",
      "label": "Order",
      "attr": "orderNumber",
      "input": {
        "type": "select",
        "placeholder": "Please select an order",
        "options": [
        {{range $i, $order := .data.orders}}{{if gt $i 0}},{{end}}
          {
            "text": "Order #{{$order.orderNumber}} {{$order.productName}}",
            "value": "{{$order.orderNumber}}"
          }
        {{end}}
        ],
        "optional": false
      }
    },
    {
      "type": "input",
      "attr": "reason",
      "label": "Reason",
      "input": {
        "type": "select",
        "placeholder": "Please select a cancellation reason",
        "options": [
        {{range $i, $reason := .actions.cancellationReasons}}{{if gt $i 0}},{{end}}
          {
            "text": "{{$reason}}",
            "value": "{{$reason}}"
          }
        {{end}}
        ],
        "optional": false
      }
    },
    {
      "type": "input",
      "attr": "notify",
      "label": "Notifications",
      "input": {
        "type": "checkbox",
        "text": "Send a notification to the customer"
      }
    }
{{else}}
    {
      "type": "text",
      "text": "The customer does not have any orders to cancel."
    }
{{end}}
  ]
}
```

## action_inputs.gtpl Template

The optional `action_inputs.gtpl` template transforms input values submitted via `form.gtpl` to the format expected by the action. This template is only needed when it is not possible to have a one to one relationship between `form.gtpl` `attr` values and action inputs. For example, there may be instances when a `select` input in a form may need to return multiple values due to the required action inputs.

The template data provided to `action_inputs.gtpl` consists of an object where each field corresponds to one of the `attr` values from the `formt.gtpl` file. The output of the template must be a JSON object where each field corresponds to one of the inputs of the action.

E.g. a `returnLineItem` action may require both an `orderNumber` and a `lineItemId` which would require the attr of a select input to provide both values. In this case we could provide both the `orderNumber` and `lineItemId` as a single value by encoding the values as JSON (this is completely arbitrary and the formatting will depend on the use case and developer preference). In this case the action_inputs.gtpl template can pass on the value as is since it contains the input fields and values as expected by the action.

### form.gtpl snippet

```json
{
  "type": "input",
  "label": "Line Item",
  "attr": "orderNumberAndLineItemId",
  "input": {
    "type": "select",
    "placeholder": "Please select an order",
    "options": [
    {{$firstLineItem := true}}
    {{range $order := .data.orders}}
      {{range $lineItem := $order.lineItems}}{{if $firstLineItem}}{{$firstLineItem = false}}{{else}},{{end}}
      {
        "text": "Order #{{$order.orderNumber}} {{$lineItem.productName}}",
        {{$value := dict
          "orderNumber" $order.orderNumber
          "lineItemId" $lineItem.id
        }}        
        "value": "{{toJson $value | b64enc}}" {{/* base 64 encode the value to avoid having to escape the resulting JSON */}}
      }
      {{end}}
    {{end}}
    ],
    "optional": false
  }
}
```

### Template Data

``` json
{
  "orderNumberAndLineItemId": "eyJvcmRlck51bWJlciI6Im9yZGVyMSIsImxpbmVJdGVtSWQiOiJsaW5lSXRlbUlkNTYifQ=="
}
```

### action_inputs.gtpl

``` go
{{/* decoded value is {"orderNumber":"order1","lineItemId":"lineItemId56"} */}}
{{b64dec .orderNumberAndLineItemId}}
```

## action_result.gtpl Template

The `action_result.gtpl` template formats the action result for display in the customer timeline and/or for showing errors in the form.

### Template Data

The template receives:

```json
{
  "action": {
    "inputs": {
      "inputField1": "value for inputField1",
      "inputFieldN": "value for inputFieldN"
    },
    "result": {
      // The GraphQL mutation/query result
    }
  },
  "formAttrs": {
    "attr1": "input value gathered for form attr1",
    "attrN": "input value gathered for form attrN"
  }
}
```

### Result Format Requirements

#### Success Response

For successful actions, return JSON in this format:

```json
{
  "message": "Order Cancelled",
  "detail": "Order #1391 was cancelled for reason: customer request"
}
```

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| message | String | Yes | Bolded summary message shown in the action record on the timeline |
| detail | String | Yes | Detailed message shown in the action record. URLs automatically become clickable links. Can use HTML for formatting (bold, underline, etc.) |

**Important:** Both `message` and `detail` must be provided to avoid displaying a default action message. If either is missing, Gladly shows a generic default message.

#### Error Response

```json
{
  "errors": [
    {
      "attr": "fieldName",
      "detail": "Error description for this field"
    }
  ]
}
```

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| errors | Array | Yes | Array of error objects |

**Error Object:**

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| attr | String | Yes | The form field `attr` to associate with the error. Must match an input section's `attr`. If no match, error won't display in form |
| detail | String | Yes | Descriptive error text |

**Behavior:** The form stays open with errors displayed, allowing the agent to correct and resubmit.

**Best Practice:** Set the `attr` to the last field in the form so the error appears at the bottom.


### Example action_result.gtpl

```go
{{if .action.result.success}}
{
  "message": "Order Cancelled",
  "detail": "Order #{{.action.inputs.orderNumber}} was cancelled. Reason: {{.action.inputs.reason}}{{if .action.inputs.notify}}. Customer notification sent.{{end}}"
}
{{else}}
{
  "errors": [
    {
      "attr": "reason",
      "detail": "{{.action.result.failureReason}}"
    }
  ]
}
{{end}}
```

## Best Practices

### Form Design

1. **Keep forms simple** - Only include necessary fields
2. **Use clear labels** - Make field purposes obvious
3. **Provide helpful hints** - Use the `hint` field for additional guidance
4. **Handle empty states** - Show appropriate message when no data available
5. **Use appropriate input types** - Select for predefined choices, text for free-form input
6. **Order fields logically** - Most important or commonly used fields first

### Error Handling

1. **Provide clear error messages** - Include relevant output from the action
2. **Associate errors with fields** - Use the `attr` field to place errors near relevant inputs when the error(s) are due to invalid input. When the error is not due to input validation, use the last `attr` in the form for displaying the error
3. **Don't expose sensitive information** in error messages

### Template Writing

1. **Validate data exists** before accessing nested properties
2. **Handle edge cases** - Empty arrays, null values, missing fields
3. **Escape values properly** - Ensure JSON output is valid
4. **Format data appropriately** - Dates, currency, etc.
5. **Keep templates maintainable** - Use clear variable names and logical structure

### Action Results

1. **Always provide message and detail** for success - Avoid default messages
2. **Make timeline entries informative** - Include key details from the action
3. **Use HTML formatting sparingly** in detail field - Keep it readable
4. **Consider what agents need to see** - Show confirmation of what was done
5. **Include submitted values** when relevant - Order numbers, amounts, etc.

## Common Patterns

### Conditional Form Fields

Show different fields based on available data:

```go
{{if .data.hasActiveSubscription}}
    {
      "type": "input",
      "label": "Subscription Action",
      "attr": "subscriptionAction",
      "input": {
        "type": "select",
        "options": [...]
      }
    },
{{end}}
```

### Dynamic Options from Data

Generate select options from API data:

```go
{
  "type": "select",
  "options": [
  {{range $i, $item := .data.items}}{{if gt $i 0}},{{end}}
    {
      "text": "{{$item.name}}",
      "value": "{{$item.id}}"
    }
  {{end}}
  ]
}
```

### Combining Multiple Data Sources

Use both data and actions schema data:

```go
{
  "title": "Process {{.data.itemType}}",
  "sections": [
    {
      "type": "input",
      "label": "Select {{.data.itemType}}",
      "attr": "itemId",
      "input": {
        "type": "select",
        "options": [
        {{range $i, $item := .data.items}}{{if gt $i 0}},{{end}}
          {"text": "{{$item.name}}", "value": "{{$item.id}}"}
        {{end}}
        ]
      }
    },
    {
      "type": "input",
      "label": "Action Type",
      "attr": "actionType",
      "input": {
        "type": "select",
        "options": [
        {{range $i, $type := .actions.actionTypes}}{{if gt $i 0}},{{end}}
          {"text": "{{$type}}", "value": "{{$type}}"}
        {{end}}
        ]
      }
    }
  ]
}
```

### Conditional Success Messages

Vary success message based on action result:

```go
{{if .action.result.refunded}}
{
  "message": "Order Refunded",
  "detail": "Order #{{.action.inputs.orderNumber}} was refunded ${{.action.result.refundAmount}}."
}
{{else if .action.result.cancelled}}
{
  "message": "Order Cancelled",
  "detail": "Order #{{.action.inputs.orderNumber}} was cancelled."
}
{{end}}
```

### Error Handling with Multiple Possible Failures

```go
{{if .action.result.success}}
{
  "message": "Action Completed",
  "detail": "{{.action.result.message}}"
}
{{else}}
{
  "errors": [
    {{if .action.result.invalidOrder}}
    {
      "attr": "orderNumber",
      "detail": "This order cannot be modified"
    }
    {{else if .action.result.invalidReason}}
    {
      "attr": "reason",
      "detail": "Please select a valid reason"
    }
    {{else}}
    {
      "detail": "{{.action.result.errorMessage}}"
    }
    {{end}}
  ]
}
{{end}}
```