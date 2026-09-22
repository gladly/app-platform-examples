# App Platform Admin UI - AI Context Document

This document provides guidance for creating Admin UI form configurations that allow Gladly administrators to configure app integrations from the admin interface.

## Overview

The Admin UI form gathers configuration and secret values required by an app's integration. Unlike agent-facing UI forms (which use Go templates and multiple files), the Admin UI consists of a single static JSON file that defines the form layout.

## Directory Structure

The Admin UI form lives in the `ui/admin` directory from the app's root directory:

```text
app-root/
├── ui/
│   └── admin/
│       └── form.json
```

Only one file is needed:
1. **form.json** - Static JSON defining the form configuration for gathering integration settings

## Relationship to Integration Configuration

The Admin UI form collects values that populate two integration objects:

- **`.integration.configuration`** - Non-sensitive settings (e.g., subdomain, API version)
- **`.integration.secrets`** - Sensitive values (e.g., API tokens, passwords)

The `attr` field on each input section determines where the value is stored by using a dotted path prefix. The frontend requires these prefixes to route values to the correct storage:
- `configuration.<fieldName>` stores the value in `.integration.configuration.<fieldName>`
- `secrets.<fieldName>` stores the value in `.integration.secrets.<fieldName>`

Every input section `attr` **must** include one of these prefixes. Attrs without a prefix will not be stored correctly.

Multi-segment dotted paths after the prefix are supported for nested structures:
- `secrets.credentials.username` stores to `.integration.secrets.credentials.username`
- `configuration.api.version` stores to `.integration.configuration.api.version`

## form.json Structure

The form configuration format is a subset of the [LUA action form configuration](https://help.gladly.com/developer-tutorials/docs/app-actions-setup#action-form-response-fields), with some fields not applicable to the Admin UI context.

### Top-Level Fields

```json
{
  "title": "Configure My App",
  "sections": [
    // Array of section objects
  ]
}
```

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| title | String | Yes | Form title. Appears at the top of the form. |
| sections | Array | No | Input fields. See **Text Section** and **Input Section** below. |
| actionUrl | URL | No | Not applicable for Admin UI. Omit. |
| closeButton | String | No | Not applicable for Admin UI. Omit. |
| submitButton | String | No | Not applicable for Admin UI. Omit. |

### Section Types

Sections can be either **text** or **input**.

#### Text Section

Displays static informational text in the form.

```json
{
  "type": "text",
  "text": "Enter your API credentials below. You can find these in your account settings."
}
```

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| type | String | Yes | Must be `text` |
| text | String | Yes | The text to display |

#### Input Section

Wraps a single input element for gathering a configuration or secret value.

```json
{
  "type": "input",
  "label": "API Token",
  "attr": "secrets.apiToken",
  "input": {
    "type": "text",
    "placeholder": "Enter your API token"
  },
  "hint": "Found in Settings > API > Tokens"
}
```

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| type | String | Yes | Must be `input` |
| label | String | Yes | The label displayed next to the input field |
| attr | String | Yes | Field name including the `configuration` or `secrets` parent prefix. Must be unique across all form fields. E.g. `configuration.subdomain`, `secrets.apiToken` |
| defaultValue | Any | No | Initial value for the field. Use when there is a common default setting, e.g. a checkbox that is enabled or disabled by default. |
| input | Object | Yes | Input element definition. One of `text`, `select`, or `checkbox`. See **Input Types** below. |
| hint | String | No | Optional hint string that appears beneath the input |

### Input Types

Input types follow the same definitions as the [LUA Action form text input](https://help.gladly.com/developer-tutorials/docs/app-actions-setup#text-input).

#### Text Input

Single-line text input for free-form values.

```json
{
  "type": "text",
  "placeholder": "e.g. mycompany",
  "optional": false
}
```

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| type | String | Yes | Must be `text` |
| placeholder | String | No | Placeholder text shown when input is empty |
| optional | Boolean | No | Whether the field is optional. If false, form displays error if empty. Default is false |

#### Select Input

Dropdown menu with predefined options.

```json
{
  "type": "select",
  "placeholder": "Select a region...",
  "options": [
    { "text": "US East", "value": "us-east-1" },
    { "text": "US West", "value": "us-west-2" },
    { "text": "EU", "value": "eu-west-1" }
  ],
  "optional": false
}
```

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| type | String | Yes | Must be `select` |
| placeholder | String | No | Placeholder text shown when no option is selected |
| options | Array | Yes | Array of option objects |
| optional | Boolean | No | Whether selection is optional. Default is false |

**Option Object:**

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| text | String | Yes | Text displayed in the dropdown |
| value | String | No | Value submitted. If not provided, the text value is used |

#### Checkbox Input

Single checkbox for boolean settings.

```json
{
  "type": "checkbox",
  "text": "Enable sandbox mode"
}
```

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| type | String | Yes | Must be `checkbox` |
| text | String | Yes | Label for the checkbox |

## Key Differences from Agent UI Forms

| Aspect | Agent UI Forms (`ui/forms/`) | Admin UI (`ui/admin/`) |
|--------|------------------------------|------------------------|
| File format | Go templates (`.gtpl`) generating JSON | Static JSON file |
| Files required | `config.json`, `form.gtpl`, `action_result.gtpl`, optional `action_inputs.gtpl` | Single `form.json` |
| Data sources | Dynamic data from GraphQL queries | None - static form only |
| `attr` values | Must match action GraphQL input parameter names | Must use `configuration.` or `secrets.` prefix |
| `actionUrl` | Not present | Not applicable, omit |
| `submitButton` / `closeButton` | Functional | Not applicable, omit |
| Purpose | Agent performs actions on customer data | Admin configures integration settings |

## Complete Example

A typical `form.json` for an app requiring an API subdomain, token, and optional sandbox mode:

```json
{
  "title": "Configure Acme Integration",
  "sections": [
    {
      "type": "text",
      "text": "Enter your Acme account details below. You can find your API credentials in the Acme admin panel under Settings > API."
    },
    {
      "type": "input",
      "label": "Subdomain",
      "attr": "configuration.subdomain",
      "input": {
        "type": "text",
        "placeholder": "e.g. mycompany"
      },
      "hint": "Your Acme subdomain (the part before .acme.com)"
    },
    {
      "type": "input",
      "label": "API Region",
      "attr": "configuration.region",
      "input": {
        "type": "select",
        "placeholder": "Select your API region",
        "options": [
          { "text": "US", "value": "us" },
          { "text": "EU", "value": "eu" }
        ]
      }
    },
    {
      "type": "input",
      "label": "API Token",
      "attr": "secrets.apiToken",
      "input": {
        "type": "text",
        "placeholder": "Enter your API token"
      },
      "hint": "Found in Settings > API > Tokens"
    },
    {
      "type": "input",
      "label": "Sandbox Mode",
      "attr": "configuration.sandbox",
      "defaultValue": false,
      "input": {
        "type": "checkbox",
        "text": "Enable sandbox mode (use test environment)"
      }
    }
  ]
}
```

## OAuth Integration

Apps that use OAuth authentication have an `authentication/oauth/config.json` file that specifies the grant type. The grant type determines whether the admin form needs OAuth-specific sections.

### Determining the Grant Type

The OAuth config file is located at `authentication/oauth/config.json` relative to the app root. Its format is:

```json
{
  "grantType": "authorization_code|client_credentials|password",
  "authDomain": "company.com"
}
```

See the [App Platform Apps AI Context](app_platform_apps_ai_context.md#authentication) for full details on OAuth configuration and grant types.

### Grant Type Impact on Admin UI

#### `authorization_code`

The `authorization_code` grant type uses an interactive OAuth flow where the admin is redirected to the external system to authorize access. The platform manages `client_id`, `client_secret`, `access_token`, and `refresh_token` as part of this flow. **These fields must be excluded from the admin form** — the platform handles their storage and lifecycle through the OAuth redirect mechanism.

#### `client_credentials` and `password`

These grant types do **not** use an interactive OAuth flow, and the platform has no way to populate tokens automatically. The admin must supply everything the integration needs through the admin form:

- `client_id` and `client_secret` — **included** as regular configuration/secret fields when found during template scanning
- `access_token` — **included** as a required secret field; the admin must provide it
- `refresh_token` — **included** as an optional secret field; the admin may provide it when the external system issues one

### Rule

**Exclude all OAuth-managed fields (`client_id`, `client_secret`, `access_token`, `refresh_token`) from the admin form when the grant type is `authorization_code`.** For `client_credentials` and `password` grant types, include every OAuth credential field (`client_id`, `client_secret`, `access_token`, and `refresh_token`) — `access_token` is required and `refresh_token` is optional, since the platform cannot populate them automatically.

## Best Practices

### Form Design

1. **Start with a text section** - Provide context about what credentials are needed and where to find them
2. **Group logically** - Place configuration fields before secret fields
3. **Use clear labels** - Make field purposes obvious to administrators
4. **Provide hints** - Use the `hint` field to tell admins where to find values (e.g., "Found in Settings > API")
5. **Set sensible defaults** - Use `defaultValue` for common settings like checkboxes or region selectors

### Attr Naming

1. **Always prefix with `configuration.` or `secrets.`** - This determines where the value is stored
2. **Use `secrets.` for sensitive values** - API keys, tokens, passwords, client secrets
3. **Use `configuration.` for non-sensitive settings** - Subdomains, regions, feature flags
4. **Match template field names exactly** - The field name after the prefix must match the attr as referenced in the app's `.gtpl` templates (e.g., if a template uses `{{.integration.configuration.apiVersion}}`, the attr must be `configuration.apiVersion`)

### Constraints

1. **`attr` values must be unique** across all input sections
2. **All JSON must be valid** - This is a static file, not a template, so syntax errors will prevent the form from loading
