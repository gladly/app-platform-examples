# Generating Admin UI form.json for an Existing App

When asked to create or generate an admin UI `form.json` for an app, follow these steps to discover all required fields and produce the correct configuration.

Refer to [app_platform_admin_ui_ai_context.md](app_platform_admin_ui_ai_context.md) for the form.json schema and field definitions.

## Step 1: Check for OAuth Configuration

Check whether the app uses OAuth authentication by looking for `authentication/oauth/config.json` relative to the app root:

```text
Glob for: authentication/oauth/config.json
```

If the file exists, read it and note the `grantType` value (`authorization_code`, `client_credentials`, or `password`).

**This determines how OAuth fields are handled in later steps:**

- **`authorization_code`**: The platform manages the entire OAuth flow including `client_id`, `client_secret`, `access_token`, and `refresh_token`. **Exclude all of these from the admin form.**
- **`client_credentials` or `password`**: The platform cannot populate tokens automatically for these grant types — the admin must supply everything. **Include `client_id`, `client_secret`, `access_token`, and `refresh_token`** as regular secret fields when found during template scanning (Step 2). Treat `access_token` as required and `refresh_token` as optional.
- **No OAuth** (no `authentication/oauth/config.json`): Do not apply any OAuth-specific filtering. Process every discovered `.integration.configuration.*` and `.integration.secrets.*` field normally — do not drop fields just because their names match OAuth keys.

## Step 2: Scan for Integration Field References

Search all `.gtpl` files in the app directory for references to `.integration.configuration` and `.integration.secrets`:

```text
Grep for: \.integration\.configuration\.\w+
Grep for: \.integration\.secrets\.\w+
```

Search all `.gtpl` files across the entire app directory — do not limit the search to specific folders.

Extract every unique field name and its parent (`configuration` or `secrets`).

**OAuth field filtering:** When the grant type is `authorization_code`, exclude `client_id`, `client_secret`, `access_token`, and `refresh_token` from the scanned fields — the platform manages all of these through the OAuth redirect flow. When the grant type is `client_credentials` or `password`, include all four (`client_id`, `client_secret`, `access_token`, `refresh_token`) as admin-provided fields — the platform cannot populate tokens automatically for these grant types. When there is no OAuth config file, do not apply any OAuth-based filtering.

## Step 3: Cross-Reference with Test Fixtures

Check `_test_/data/integration.json` files throughout the app for the expected structure and sample values. These files show:
- The full set of fields the app expects
- The data types (string, boolean, number, object, array)
- Sample values that hint at the expected format

```text
Glob for: **/_test_/data/integration.json
```

Test fixtures may reveal fields not found in templates (e.g., fields used conditionally or in less common code paths). They are the most reliable source of truth for the complete field set.

Apply the same OAuth field filtering rules from Step 2 — for `authorization_code`, exclude all four OAuth fields; for `client_credentials` or `password`, include all four as admin-provided fields; when there is no OAuth config, apply no OAuth-based filtering.

## Step 4: Check README and Documentation

Look at the app's `README.md` and any `CLAUDE.md` for documented integration fields. These often list the required configuration with descriptions of where to find values in the external system.

## Step 5: Classify Fields

The `attr` prefix (`configuration.` or `secrets.`) must match exactly how the field is referenced in the app's templates. Step 2 already extracts this — use it directly:

- Field found under `.integration.configuration.<field>` → `configuration.<field>`
- Field found under `.integration.secrets.<field>` → `secrets.<field>`

For fields discovered only in test fixtures (Step 3) or documentation (Step 4) that don't appear in templates, use these heuristics:

| Signal | Classification |
|--------|---------------|
| Tokens, keys, passwords, credentials, secrets | `secrets.<field>` |
| URLs, subdomains, hostnames, regions, feature flags, limits | `configuration.<field>` |
| Usernames used for auth (e.g., Basic auth) | Could be either — check how the app uses it. If it's sent as a credential, use `secrets.`. If it's a display/routing value, use `configuration.` |

## Step 6: Determine Input Types

Map each field to an appropriate input type:

| Field Pattern | Input Type | Notes |
|---------------|-----------|-------|
| Tokens, keys, passwords | `text` input | Sensitive free-form strings |
| Subdomains, hostnames, URLs | `text` input | With placeholder showing expected format |
| Boolean flags (`isTestRun`, `sandbox`) | `checkbox` input | Use `defaultValue` for the common setting |
| Fields with a known set of valid values (regions, environments) | `select` input | List the options |
| Numeric limits (`ordersLimit`, `lineItemsLimit`) | `text` input | With hint about accepted values |

## Step 7: Handle Complex or Nested Fields

Some apps use nested objects in secrets (e.g., `secrets.credentials` with `username`, `password`, `realm`). The admin UI `attr` field uses a dotted path, so nested structures are flattened into individual inputs:

- `secrets.credentials.username` — separate text input
- `secrets.credentials.password` — separate text input
- `secrets.credentials.realm` — separate text input

The frontend reconstructs flattened attrs back into the composite object structure when saving the configuration. This means using nested objects is fully supported, as long as each individual field within the object has its own input defined in the form.

## Step 8: Compose the form.json

Assemble the final file following this order:

1. **Text section** — Brief instruction telling the admin what credentials are needed and where to find them in the external system
2. **Configuration inputs** — Non-sensitive settings first (subdomain, region, limits, flags)
3. **Secrets inputs** — Sensitive credentials (API keys, tokens, passwords, and for `client_credentials`/`password` grant types: `client_id`, `client_secret`, `access_token`, and `refresh_token`)

For each input:
- Write a clear `label` (e.g., "API Token", not "apiToken")
- Add a `hint` when the field name alone isn't obvious — especially for where to find the value in the external system
- Add a `placeholder` showing the expected format (e.g., `"e.g. mycompany"` for subdomain)
- Set `defaultValue` for fields with a common default

## Step 9: Validate

After generating:
1. Confirm every `.integration.configuration.*` and `.integration.secrets.*` reference in the app's templates has a corresponding input in `form.json`. For `authorization_code`, exclude `client_id`, `client_secret`, `access_token`, and `refresh_token` (platform-managed). For `client_credentials` and `password`, include all four as admin-provided fields.
2. Verify all `attr` values exactly match the field paths used in templates (including prefix and field name)
3. Verify all `attr` values are unique
4. Verify the JSON is valid
