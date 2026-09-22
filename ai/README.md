# AI Context for App Platform Development

This directory contains context files to help AI coding agents understand and work with Gladly App Platform applications.

## Context Files

- [app_platform_apps_ai_context.md](app_platform_apps_ai_context.md) - Core concepts for building an app: data pulls, actions, authentication, and go text templates
- [app-public-documentation-template.md](app-public-documentation-template.md) - Template for writing an app's public-facing documentation
- [app_platform_admin_ui_ai_context.md](app_platform_admin_ui_ai_context.md) - How to build the `ui/admin/form.json` that Gladly admins use to configure an app's integration
- [app_platform_admin_ui_generation_prompt.md](app_platform_admin_ui_generation_prompt.md) - Step-by-step prompt for generating an admin UI form.json for an existing app
- [app_platform_ui_forms_ai_context.md](app_platform_ui_forms_ai_context.md) - How to build agent-facing UI forms surfaced in the agent desktop composition "+" menu

## Usage with AI Coding Agents

### Recommended Setup

- For best results, use the most advanced AI model available when working with App Platform code
- While 'ask' mode works for general questions, it's recommended to use coding agents that can actively build and modify App Platform apps
- Combine with the [appcfg CLI tool](https://github.com/gladly/app-platform-appcfg-cli) for testing and deployment

### Example Prompts

Here are some example prompts where including the app platform context would be helpful:

#### Creating New Apps

```txt
https://github.com/gladly/app-platform-appcfg-cli/ai/app_platform_apps_ai_context.md

Create a new App Platform app for Shopify that:
- Uses OAuth 2.0 authorization_code flow for authentication
- Pulls customer and order data
- Includes an action to cancel orders
```

#### Working with Authentication

```txt
https://github.com/gladly/app-platform-appcfg-cli/ai/app_platform_apps_ai_context.md

Help me implement OAuth client_credentials authentication for this app. I need to set up the authentication headers and access token request templates.
```

#### Testing and Validation

```txt
https://github.com/gladly/app-platform-appcfg-cli/ai/app_platform_apps_ai_context.md

Create test cases for these actions and data-pulls. I need to test different scenarios including:
- Missing customer data
- Multiple email addresses
- API error responses
```

### Tips for Best Results

1. Be clear about the external API you're integrating with and include relevant API docs or examples when available
2. Mention your authentication method (OAuth, API keys, custom headers, etc.)
3. Describe how the app should handle API errors or missing data
