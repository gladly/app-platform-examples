# App Platform Apps

## Overview

App Platform Apps provide a mechanism for Gladly to retrieve data as well as take actions in external systems. App platform apps utilize REST based APIs exposed by external systems in order to retrieve data as well as take actions in those systems. An App Platform App defines go text templates for creating the REST requests as well as transforming the data returned by the responses to those requests.

The APIs from these external systems are exposed to Gladly via GraphQL. API requests are mapped to GraphQL Query or Mutation operation fields.

## Relevant go text template documentation

- [go text template language](https://pkg.go.dev/text/template)
- [Sprig go text template functions](https://masterminds.github.io/sprig)
- [miscellaneous go text template functions](https://github.com/gladly/app-platform-appcfg-cli/blob/main/docs/appcfg_platform_template-functions.md)
- [Gladly XML document object model for transforming XML response data](https://github.com/gladly/app-platform-appcfg-cli/blob/main/docs/appcfg_platform_xml.md)

Note: The Gladly app platform only supports a subset of the XML document object model so it is important to consult the Gladly specific XML DOM documentation for the supported fields and functions before creating an XML response transformation.

### go text template range actions

Template context data fields that are referenced within a range action and are not relative to the current range iteration must be referenced relative to the context data root using `$`

e.g.

``` go template
{{ range $i, $customer := .customers}}
Customer {{ $i }} of {{ len $.customers }}
Name: {{ $customer.firstName }} {{ $customer.lastName }}
{{ end }}
```

### Sprig template function clarification

#### contains

The `contains` function returns `true` when the first string parameter is contained in the second string parameter.

e.g. `{{ contains "hello" "hello world" }}` will be `true`

#### date, dateInZone, toDate, mustToDate

These functions use the Go time layout format. Examples of Go time layouts can be found [here](https://pkg.go.dev/time#pkg-constants).

## Structure of an App Platform App

App Platform App configuration is organized using a prescriptive file system directory structure. The root of an app platform app is a directory named after the application.

App platform app actions and data configuration is organized in separate subdirectories due to the differences in configuration. Action configuration is located in the `actions` subdirectory and data configuration is located in the `data` subdirectory.

### App Versioning

App Platform Apps use semantic versioning (semver) to track changes and releases. The app version is specified via the `version` field in the `manifest.json` file located at the root of the application directory.

The version should be incremented according to semantic versioning principles:

- **Major version** (X.0.0): Incremented for breaking changes that are not backward compatible
- **Minor version** (X.Y.0): Incremented for new features that are backward compatible
- **Patch version** (X.Y.Z): Incremented for backward compatible bug fixes

### Authentication

#### Authentication Headers

Authentication header go text templates are located in the `authentication/headers` directory relative to the root of the application. Each header is represented by a file where the name of the file (excluding the .gtpl extension) is the name of the corresponding header. The content of the file is a go text template that generates the value for the corresponding authentication header. Authentication headers apply to all app platform data pull and action REST requests.

Authentication header go text templates have access to the following context data:

```JSONC
{
    "integration": {
        // Dynamic configuration associated with the app platform app
        "configuration": {},
        // Secrets needed for making REST calls against the external system
        // associated with the app platform app
        "secrets": {}
    }
}
```

Where the "configuration" attribute contains data for configuring the behavior of the app as well as the user name required for authenticating against the external system. The "secrets" attribute contains any sensitive credentials that are required for authenticating against the external system.

#### Request Signing Headers

Header go text templates for creating HTTP request signatures are located in the `authentication/request_signing` directory relative to the root of the application. Each header is represented by a file where the name of the file (excluding the .gtpl extension) is the name of the corresponding header. Request signing headers apply to all app platform data pull and action REST requests.

Request signing header go text templates have access to the following context data:

```JSONC
{
    "integration": {
        // Dynamic configuration associated with the app platform app
        "configuration": {},
        // Secrets needed for making REST calls against the external system
        // associated with the app platform app
        "secrets": {}
    },
    // HTTP request information used for creating the signature for the header
    "request": {
        // The request URL as generated by the request_url.gtpl go text template
        "url": "",
        // The HTTP method used for the request as specified in the config.json file
        "method": "",
        // The HTTP headers as specified by the various go text template header files
        // in the "authentication/headers" directory */
        "headers": {
            // The field name is the name of the header and the corresponding array
            // contains one or more header values */
            "header_name": []
        }
    }
}
```

#### OAuth

The configuration for each individual OAuth HTTP request is located in a subdirectory of the `authentication/oauth` directory relative to the root of the application.

Each request configuration subdirectory may also contain a `headers` subdirectory for specifying headers for the given request. Each header is represented by a file where the name of the file (excluding the .gtpl extension) is the name of the corresponding header. The content of the file is a go text template that generates the value for the corresponding authentication header.

The header for specifying the access_token with each data_pull and action request is specified in the `authentication/headers` subdirectory. The header is represented by a file where the name of the file (excluding the .gtpl extension) is the name of the corresponding header. The content of the file is a go text template that generates the value for the corresponding authentication header.

The `authentication/oauth` directory contains the following file:

- `config.json`: specifies the grant type associated with the configuration and any additional configuration needed for that grant type

The `config.json` file has the following format:

```JSONC
{
  "grantType": "authorization_code|client_credentials|password",
  // The domain associated with OAuth authorization server used with the authorization_code grant type
  "authDomain": "company.com"
}
```

##### authorization_code grant type

###### Requesting an authorization code

The configuration for requesting an authorization code is located in `authentication/oauth/auth_code`. The configuration consists of the following files:

- `config.json`: used for specifying the HTTP method used for the request; authorization code requests always use the `GET` HTTP method
- `request_url.gtpl`: the go text template file for generating the HTTP request URL

The `config.json` file has the following format:

```JSONC
{
    // The HTTP method is always GET for this request
    "httpMethod": "GET",
}
```

The `request_url.gtpl` go text template has access to the following context data:

```JSONC
{
    // The correlationId is used for the state query parameter value of the request URL
    "correlationId": "123456",
    "integration": {
        "configuration": {
            "client_id": "OAuth client_id"
        },
        "secrets": {
            "client_secret": "OAuth client_secret"
        }
    },
    "oauth": {
        "redirect_uri": "The URI to which the authorization code request must be redirected"
    }
}
```

###### Requesting an access token

The configuration for requesting an access token is located in `authentication/oauth/access_token`. The configuration consists of the following files:

- `config.json`: used for specifying the HTTP method used for the request as well as the `Content-Type` of the request body when appropriate
- `request_url.gtpl`: the go text template file for generating the HTTP request URL
- `request_body.gtpl`: the go text template file for generating the HTTP request body for HTTP methods that require a body

The `config.json` file has the following format:

```JSONC
{
    // The HTTP method used for making the request
    "httpMethod": "GET|POST",
    // The mime type of the request body when the httpMethod is POST. The 
    // contentType is omitted when the httpMethod is GET.
    "contentType": "",
}
```

The `request_url.gtpl` and `request_body.gtpl` go text templates have access to the following context data:

```JSONC
{
    // The correlationId is used for the state query parameter value of the request URL
    "correlationId": "123456",
    "integration": {
        "configuration": {
            "client_id": "OAuth client_id"
        },
        "secrets": {
            "client_secret": "OAuth client_secret"
        }
    },
    "oauth": {
        // Redirect request from OAuth authorization server
        "request": {
            "url": "https://gladly.com/auth-code?code=secret&state=vXmSEPjVSWCaCMzvjufxZg",
            "method": "GET",
            "headers": {
                "header-name": ["header value"]
            }
        },
        "code": "authorization code from redirect URI query parameters",
        "state": "state from redirect URI query parameters; must be the same as correlationId value"
    }
}
```

###### Refreshing an access token

The configuration for refreshing an access token is located in `authentication/oauth/refresh_token`. The configuration consists of the following files:

- `config.json`: used for specifying the HTTP method used for the request as well as the `Content-Type` of the request body when appropriate
- `request_url.gtpl`: the go text template file for generating the HTTP request URL
- `request_body.gtpl`: the go text template file for generating the HTTP request body for HTTP methods that require a body

The `config.json` file has the following format:

```JSONC
{
    // The HTTP method used for making the request
    "httpMethod": "GET|POST",
    // The mime type of the request body when the httpMethod is POST. The 
    // contentType is omitted when the httpMethod is GET.
    "contentType": "",
}
```

The `request_url.gtpl` and `request_body.gtpl` go text templates have access to the following context data:

```JSONC
{
    "correlationId": "123456",
    "integration": {
        "configuration": {
            "client_id": "OAuth client_id"
        },
        "secrets": {
            "client_secret": "OAuth client_secret",
            "access_token": "expired access token",
            "refresh_token": "refresh token"        }
    }
}
```

##### client_credentials grant type

The configuration for requesting an access token is located in `authentication/oauth/access_token`. The configuration consists of the following files:

- `config.json`: used for specifying the HTTP method used for the request as well as the `Content-Type` of the request body when appropriate
- `request_url.gtpl`: the go text template file for generating the HTTP request URL
- `request_body.gtpl`: the go text template file for generating the HTTP request body for HTTP methods that require a body

The `config.json` file has the following format:

```JSONC
{
    // The HTTP method used for making the request
    "httpMethod": "GET|POST",
    // The mime type of the request body when the httpMethod is POST. The 
    // contentType is omitted when the httpMethod is GET.
    "contentType": "",
}
```

The `request_url.gtpl` and `request_body.gtpl` go text templates have access to the following context data:

```JSONC
{
    "integration": {
        "configuration": {
            "client_id": "OAuth client_id" // if the client was issued a secret
        },
        "secrets": {
            "client_secret": "OAuth client_secret" // if the client was issued a secret
        }
    }
}
```

##### password grant type

The configuration for requesting an access token is located in `authentication/oauth/access_token`. The configuration consists of the following files:

- `config.json`: used for specifying the HTTP method used for the request as well as the `Content-Type` of the request body when appropriate
- `request_url.gtpl`: the go text template file for generating the HTTP request URL
- `request_body.gtpl`: the go text template file for generating the HTTP request body for HTTP methods that require a body

The `config.json` file has the following format:

```JSONC
{
    // The HTTP method used for making the request
    "httpMethod": "GET|POST",
    // The mime type of the request body when the httpMethod is POST. The 
    // contentType is omitted when the httpMethod is GET.
    "contentType": "",
}
```

The `request_url.gtpl` and `request_body.gtpl` go text templates have access to the following context data:

```JSONC
{
    "integration": {
        "configuration": {
            "username": "OAuth username",
            "client_id": "OAuth client_id" // if the client was issued a secret
        },
        "secrets": {
            "password": "OAuth password",
            "client_secret": "OAuth client_secret" // if the client was issued a secret
        }
    }
}
```

### Actions

Actions are typically associate with REST calls that perform some kind of action in the external system. E.g. cancel an order, create a return authorization, modify a subscription, etc.

The configuration for each action is located in a subdirectory of the `actions` directory. The `actions` directory is a subdirectory of the application root directory. The configuration for each individual action is located in a subdirectory named after the action; i.e. `actions/{action name}`.

App platform actions are executed using GraphQL operations. The GraphQL schema file associated with actions is named `actions_schema.graphql` and is located in the `actions` directory relative to the root of the application.

Each action is exposed as a field of either the Query or Mutation type in the `actions_schema.graphql` file. Actions that retrieve data from the external system are defined as a field of the Query type and actions that modify data in the external system are defined as a field of the Mutation type.

GraphQL operation fields are associated with an action via the `@action` GraphQL directive where the `name` parameter of the directive is the name of the action. Action names are taken from the name of the subdirectory in which the configuration for the given action are located.

Any GraphQL type that is referenced by another GraphQL type must be defined before it is referenced in the `actions/actions_schema.graphql` GraphQL schema file; i.e. app platform GraphQL schemas do not support forward type declarations. The GraphQL union type is not supported. Timestamp fields that are not in ISO8601 format should be converted and use the GraphQL DateTime scalar type. Currency fields that represent currency values as strings should use the GraphQL Currency scalar type.

All fields whose name is not self-explanatory in the `actions_schema.graphql` file should include a GraphQL description. These descriptions should be based on the field descriptions provided by the external system's documentation or API reference when available. The description should be placed directly above the field definition using triple quotes (`""" ... """`). This ensures that the schema is self-documenting and that field meanings are clear to consumers of the schema. When in doubt, prefer to add a description.

#### Action Request URL and Body

The go text templates for generating the request URL and body for each individual action are located in a `actions/{action name}` subdirectory. The name of the subdirectory is also the name of the action. The directory for a given action contains the following files:

- `config.json`: used for specifying the HTTP method used for the request as well as the `Content-Type` of the request body when appropriate
- `request_url.gtpl`: the go text template file for generating the HTTP request URL(s); one line per URL
- `request_body.gtpl`: the go text template file for generating the HTTP request body for HTTP methods that require a body

The `config.json` file has the following format:

```JSONC
{
    // The HTTP method used for making the request
    "httpMethod": "GET|POST|PUT|PATCH|DELETE",
    // The mime type of the request body when the httpMethod is one of POST, PUT, 
    // PATCH. The contentType is omitted when the httpMethod is GET.
    "contentType": "application/json|application/xml|etc",
    // Set `rawResponse` to true when the response `Content-Type` is not `application/json`,
    // `text/xml`, `application/xml` or `application/xhtml+xml`. When `rawResponse` is set to
    // `true`, the `rawData` field of the context data for the response_transformation.gtpl
    // will be a byte array.
    "rawResponse": false
}
```

The `request_url.gtpl` and `request_body.gtpl` go text templates have access to the following context data:

```JSONC
{
    "integration": {
        // Dynamic configuration associated with the app platform app
        "configuration": {},
        // Secrets needed for making REST calls against the external system
        // associated with the app platform app
        "secrets": {}
    },
    // Provides the parameters required by either the request_url.gtpl and/or request_body.gtpl go text templates
    "inputs": {}
}
```

The structure of the `inputs` object corresponds to the input values for the GraphQL field associated with the action. Each field of the `inputs` object corresponds to one of the input parameters for the GraphQL field associated with the action.

#### Transforming the response data received from an action REST call

Response data received via the REST call for a given action is not always in JSON format or in a format that is easy to work with in Gladly. Under such circumstances it is possible to transform the data using a go text template. The resulting data of the transformation must be in JSON format. The output of the transformation MUST conform to the GraphQL type of the GraphQL field associated with the action in the `actions/actions_schema.graphql` GraphQL schema file. The go text template for transforming response data is defined in the "response_transformation.gtpl" file located in the subdirectory associated with the corresponding action. The "response_transformation.gtpl" go text template has access to the following context data:

```JSONC
{
    "integration": {
        // Dynamic configuration associated with the app platform app
        "configuration": {},
        // Secrets needed for making REST calls against the external system
        // associated with the app platform app
        "secrets": {}
    },
    // The inputs for the GraphQL field associated with the action
    "inputs": {},
    "request": {
        // The request URL as generated by the request_url.gtpl go text template
        "url": "",
        // The HTTP method used for the request as specified in the config.json file
        "method": "",
        // The HTTP headers as specified by the various go text template header files
        // in the "authentication/headers" directory */
        "headers": {
            // The field name is the name of the header and the corresponding array
            // contains one or more header values */
            "header_name": []
        },
        // The raw bytes of the request body
        "body": ""
    },
    "response": {
        // HTTP response status text
        "status": "",
        // HTP response status code
        "statusCode": 200,
        // The HTTP headers as returned by the external system
        "headers": {
            // The field name is the name of the header and the corresponding array
            // contains one or more header values
            "response_header_name": []
        },
        // The raw bytes of the response body
        "body": ""
    },
    // The raw response data. It is the contents of the `rawData` field that is transformed by the template
    // - when the response `Content-Type` is `application/json` then the `rawData` field is an object or an array representing the JSON data
    // - when the response `Content-Type` is `application/xml`, `text/xml` or `application/xhtml+xml` then the `rawData` field is a reference to the Gladly-specific XML DOM
    // - when the `rawResponse` field is set to `true` in the "data/pull/{data pull name}/config.json" file then the `rawData` field is a byte array representation of the response
    "rawData": {}
}
```

Timestamp fields must be converted to ISO8601 format. Timestamps without a timezone are assumed to be in UTC and should use `Z` for the timezone. Use the Sprig date functions in order to perform any necessary timestamp conversion.

It is the responsibility of the action response transformation template to handle any relevant non success status codes except authentication errors with status code 401 or 403. In most cases, action errors should be transformed in to an appropriate object type that can be interpreted by Gladly when executing the action. Unexpected error status codes should result in calling the `fail` Sprig template library function.

### Data

App platform app data configuration is used for retrieving data (also referred to as pulling data) from external systems via REST calls. Each data configuration (also referred to as a data pull configuration) retrieves one or more objects of a given data type from an external system.

The configuration for each data pull is located in a subdirectory of the `data/pull` directory. The configuration for each individual data pull is located in a directory named after the data pull; i.e. `data/pull/{data pull name}`.

Complex object hierarchies are supported by pulling data from multiple related data types in sequence of dependency. E.g. retrieving order objects may require the ID of the associated customer and customer objects can only be retrieved by email address. In this case it would be necessary to first retrieve the customer object associated with an email address and then use the ID of the resulting customer object to retrieve relevant orders for that customer.

App platform data pulls are executed using a GraphQL query where the data pull is exposed as a field of the Query type. The GraphQL schema file associated with data pulls is named `data_schema.graphql` and is located in the `data` directory relative to the root of the application.

GraphQL type definitions in the `data_schema.graphql` file are associated with a given data pull configuration since each data pull is responsible for retrieving data of a given data type.

A GraphQL type definition is associated with a given data pull via the `@dataType` GraphQL directive. The `name` and `version` parameters of the `@dataType` directive match the dataType name and version values as specified in the corresponding `data/pull/{data pull name}/config.json` file.

Any GraphQL type that is referenced by another GraphQL type must be defined before it is referenced in the `data/data_schema.graphql` GraphQL schema file; i.e. app platform GraphQL schemas do not support forward type declarations. The GraphQL union type is not supported. Timestamp fields in ISO8601 format should use the GraphQL DateTime scalar type. Currency fields that represent currency values as strings should use the GraphQL Currency scalar type.

All fields whose name is not self-explanatory in the `data_schema.graphql` file should include a GraphQL description. These descriptions should be based on the field descriptions provided by the external system's documentation or API reference when available. The description should be placed directly above the field definition using triple quotes (`""" ... """`). This ensures that the schema is self-documenting and that field meanings are clear to consumers of the schema. When in doubt, prefer to add a description.

Custom scalars such as `DateTime` and `Currency` do not need to be defined within your app's `data_schema.graphql` file. You only need to reference them as types for relevant fields. Their definitions are provided by the App Platform runtime.


#### Defining relationships between data types

Relationships between objects are either established by the parent referencing the child by the child's unique ID or by the child referencing the parent by the parent's unique ID. The parent object will contain a field that references one or more child IDs or the child object will have a field that references one parent ID.

The data GraphQL schema defined in `data/data_schema.graphql` exposes parent/child relationships in an object oriented way. GraphQL queries executed against the data schema automatically resolve ID based parent/child relationships and expose children using their corresponding type instead of the fields containing IDs. The type definition of the parent will contain a field where the type of the field is that of the child. The mapping of parent to child is specified using a GraphQL directive on the child field on the parent.

##### When the parent has a field that references child IDs

A parent that references it's children via a field that contains one or more child IDs will specify the `@childIds` directive after the child's field definition. The `@childIds` directive has a `template` parameter which specifies the go text template used for extracting the child IDs from the parent field containing the IDs. The output of the go text template is one or more child IDs separated by a comma.

```JSON
// customer data
{
    "customerId": "customerid",
    "name": "Jane Smith",
    "orderIds": ["order1", "order2", "order3"]
}

// order data
{
    "orderId": "order1",
    "totalAmount": 9.99
}
```

```GraphQL
type Order @dataType(name: "order", version: "1.0") {
    orderId: ID!
    totalAmount: Float
}

type Customer @dataType(name: "customer", version: "1.0") {
    customerId: ID!
    name: String
    orders: [Order] @childIds(template: "{{.orderIds | join ','}}")
}
```

##### When the child has a field that references its parent by ID

A parent/child relationship that is established by a parent ID on the child will use the `@parentId` directive on the child field in the parent type definition. The `@parentId` directive has a `template` parameter which specifies the go text template used for extracting the parent ID from the parent object. The ID of the parent is used to associate all children that have that same parent ID with that parent.


Any data pull that retrieves such child data must also have an `external_parent_id.gtpl` file for extracting the parent ID from the child object.

``` JSONC
// customer data
{
    "id": "customer1",
    "name": "Jane Smith"
}

// order data
{
    "orderId": "order1",
    "customerId": "customer1",
    "totalAmount": 9.99
}
```

```GraphQL
type Order @dataType(name: "order", version: "1.0") {
    orderId: ID!
    customerId: String
    totalAmount: Float
}

type Customer @dataType(name: "customer", version: "1.0") {
    id: ID!
    name: String
    orders: [Order] @parentId(template: "{{.id}}")
}

type Query {
    customers: [Customer]
}
```

order data `external_parent_id.gtpl`

``` go template
{{- .customerId -}}
```

#### Data Pull Request URL and Body

The go text templates for generating the request URL and body for each individual data pull are located in a subdirectory of the `data/pull` directory. The name of the subdirectory is also the name of the data pull. The name of a data pull is typically closely tied to the data that it retrieves; e.g. a data pull that retrieves order objects would be called "orders". The directory for a given data pull contains the following files:

- `config.json`: used for specifying the HTTP method used for the request as well as the `Content-Type` of the request body when appropriate; also specifies the data type associated with the data pull
- `request_url.gtpl`: the go text template file for generating the HTTP request URL(s)
- `request_body.gtpl`: the go text template file for generating the HTTP request body for HTTP methods that require a body

The config.json file has the following format:

```JSONC
{
    // Data type name and version referenced by "@dataType" GraphQL directives
    "dataType": {
        "name": "name of the type", // data type names must be unique within an app platform app
        "version": "1.0" // the semantic version of the data type
    },
    // Data pull requests associated with data types listed in dependsOnDataTypes are
    // executed before the data pull associated with this configuration
    "dependsOnDataTypes": ["name_of_a_dependent_data_type"],
    // The HTTP method used for making the request
    "httpMethod": "GET|POST|PUT|PATCH|DELETE",
    // The mime type of the request body when the httpMethod is one of POST, PUT, 
    // PATCH. The contentType is omitted when the httpMethod is GET.
    "contentType": "application/json|application/xml|etc",
    // Set `rawResponse` to `true` when the response `Content-Type` is not `application/json`,
    // `text/xml`, `application/xml` or `application/xhtml+xml`. When `rawResponse` is set to
    // `true`, the `rawData` field of the context data for the response_transformation.gtpl
    // will be a byte array.
    "rawResponse": false
}
```

The request_url.gtpl and request_body.gtpl go text templates have access to the following context data:

```JSONC
{
    "integration": {
        // Dynamic configuration associated with the app platform app
        "configuration": {},
        // Secrets needed for making REST calls against the external system
        // associated with the app platform app
        "secrets": {}
    },
    // All customer fields are optional. Templates that use this data must
    // check for presence before referencing any of these fields. The most
    // common response to non-present data is to call the template stop function.
    "customer": { 
        "id": "some unique Gladly customer profile ID",
        "name": "John Smith",
        "address": "123 Somewhere Street, CA, USA",
        "primaryEmailAddress": "john_smith@email.com", // the customer's main email address as specified in Gladly
        "emailAddresses": ["john_smith@gmail.com", "js@yahoo.com"],
        "primaryPhoneNumber": { // the customer's main phone number as specified in Gladly
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
    // externalData contains the data that was retrieved by data pulls that
    // this data pull depends on. The data type names will match those specified
    // in the "dependsOnDataTypes" of the "config.json" file for this data pull.
    "externalData": {
        "dependent_data_type_name": [],
        "another_dependent_data_type_name": []
    }
}
```

##### Generating multiple requests for a data pull

A URL template can create multiple requests by outputting each URL on a new line. This approach is mainly applicable to HTTP requests that do not require a body (e.g. GET).

e.g. multiple request URL template


``` go text template
{{- range .customer.emailAddresses -}}
https://api.loopreturns.com/api/v2/returns?customer_email={{urlquery .}}
{{ end -}}
```

It is also possible to create multiple requests by outputting multiple bodies via the response body template `response_body.gtpl`. This approach would be used for example when data is requested via a POST. The start of each body is specified in the template by adding a `#body` marker on the line that immediately precedes the start of the body content. The marker can appear anywhere on the line making it possible to place it just above the actual body content for clarity. For simplicity, it is not necessary to add a `#body` marker to templates that only generate one body. For requests where only the request body changes, the URL template should just generate one URL that will be used with each request body. This will be the most common case for a POST. It is however possible to have a unique URL for each request body by generating as many URLs as there are request bodies.

e.g. multiple request body template


``` go text template
{{- range .customer.emailAddresses -}}
#body
{
    "emailAddress": "{{.}}"
}
{{ end -}}
```

Template actions that are defined after URL or body text (usually an `{{ end -}}` to the `{{ range }}` that generates the multiple URLs and bodies) must not strip the newline after the URL or body. I.e. the beginning of the action definition `{{` that follows a URL or body should not include a minus `-` for stripping preceding white space.

#### Transforming the response data received from a data pull REST call

Response data received via the REST call for a given data pull is not always in JSON format or in a format that is easy to work with in Gladly. Under such circumstances it is possible to transform the data using a go text template. The resulting data of the transformation must be in JSON format. The output of the transformation MUST be an object or an array of objects where each object conforms to the GraphQL type in the `data/data_schema.graphql` GraphQL schema file that is associated with the data type for the data pull. The go text template for transforming response data is defined in the `response_transformation.gtpl` file located in the subdirectory associated with the corresponding data pull. The `response_transformation.gtpl` go text template has access to the following context data:

```JSONC
{
    "integration": {
        // Dynamic configuration associated with the app platform app
        "configuration": {},
        // Secrets needed for making REST calls against the external system
        // associated with the app platform app
        "secrets": {}
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
    // externalData contains the data that was retrieved by data pulls that
    // this data pull depends on. The data type names will match those specified
    // in the "dependsOnDataTypes" of the "config.json" file for this data pull.
    "externalData": {
        "dependent_data_type_name": [],
        "another_dependent_data_type_name": []
    },
    "request": {
        // The request URL as generated by the request_url.gtpl go text template
        "url": "",
        // The HTTP method used for the request as specified in the config.json file
        "method": "",
        // The HTTP headers as specified by the various go text template header files
        // in the authentication/headers directory */
        "headers": {
            // The field name is the name of the header and the corresponding array
            // contains one or more header values */
            "header_name": []
        },
        // The raw bytes of the request body
        "body": ""
    },
    "response": {
        // HTTP response status text
        "status": "",
        // HTP response status code
        "statusCode": 200,
        // The HTTP headers as returned by the external system
        "headers": {
            // The field name is the name of the header and the corresponding array
            // contains one or more header values
            "response_header_name": []
        },
        // The raw bytes of the response body
        "body": ""
    },
    // The raw response data. It is the contents of the `rawData` field that is transformed by the template
    // - when the response `Content-Type` is `application/json` then the `rawData` field is an object or an array representing the JSON data
    // - when the response `Content-Type` is `application/xml`, `text/xml` or `application/xhtml+xml` then the `rawData` field is a reference to the Gladly-specific XML DOM
    // - when the `rawResponse` field is set to `true` in the "data/pull/{data pull name}/config.json" file then the `rawData` field is a byte array representation of the response
    "rawData": {}
}
```

Transformations that do not produce data must return an empty JSON array, i.e. `[]`. Timestamp fields must be converted to ISO8601 format. Timestamps without a timezone are assumed to be in UTC.

A data pull response transformation template does not need to and should not check the `.response.statusCode` value unless the `rawResponse` field in the `data/pull/{data pull name}/config.json` file is present and set to `true`. When raw response processing is not enabled, the app platform will only execute a response transformation template when the HTTP response status code is 200.

When a data pull makes multiple requests then the transformed data from each request will be aggregated into an array of objects.

#### Extracting metadata from a data pull

The app platform extracts certain field values from the objects that are the result of the response transformation. The field values are extracted from each individual object via a go text template. Each metadata template is applied to each object from the response transformation.

The following files are located in the subdirectory associated with the data pull:

- `external_id.gtpl`: this template extracts the unique ID of the object; e.g. `{{- .orderId -}}`
- `external_updated_at.gtpl`: this template extracts the timestamp of when the object was last updated; the value associated with the field referenced by the template must be in ISO8601 format
- `external_parent_id.gtpl`: this template extracts the parent ID from an object that has a parent/child relationship established by a parent ID field on that object

When a field is referenced in either the `external_id.gtpl` or `external_parent_id.gtpl` template that is not a string but a number, then the template should use the Sprig `int64` function in order to retain the original formatting of the number; e.g. `{{- int64 .reviewId -}}`.

Template actions for extracting metadata should trim any leading and trailing white space.

## Testing app platform go text templates

### General approach to testing app platform go text templates

The general approach to testing the various go text templates is to validate the output of the template based on the context data passed to the template. Each template e.g. `request_url.gtpl`, `request_body.gtpl`, `response_transformation.gtpl`, `header_name.gtpl`, etc. is passed a context object relevant to that template and the actual output is compared to the expected output based on the test input context data.

### Directory structure and files used for testing app platform go text templates

Test files are located in a `_test_/{test dataset name}` subdirectory relative to the directory containing the go text templates being tested (i.e. in the action, data pull or headers directory containing the templates). Test input data is in the form of go text template context data where the data that corresponds to each top level field of the context object is defined in a JSON file named after the field name; e.g. the test data for the "rawData" field will be in a file named `rawData.json`. All test input data files are aggregated in to a context object passed to each template.

The output of each template is compared to the expected output given the specified context data. The expected output of each template is specified in a JSON, text or binary file named after the template being tested with an `expected_` prefix and an extension (json|txt|bin) that corresponds to the format of the data; e.g. the expected output for the `request_url.gtpl` template would be specified in a file named `expected_request_url.txt`. The extension of the expected output file depends on the format of the data that the template will generate.

| Template type                | Expected output file name             | Expected output format                                                                                                      |
| ---------------------------- | ------------------------------------- | --------------------------------------------------------------------------------------------------------------------------- |
| request_url.gtpl             | expected_request_url.txt              | Text                                                                                                                        |
| request_body.gtpl            | expected_request_body.\*              | The output format depends on the content type specified in the config.json file; json, txt and bin extensions are supported |
| response_transformation.gtpl | expected_response_transformation.json | JSON                                                                                                                        |
| header_name.gtpl             | expected_header_name.txt              | Text                                                                                                                        |
| external_id.gtpl             | expected_external_id.txt              | Text                                                                                                                        |
| external_updated_at.gtpl     | expected_external_updated_at.txt      | Text                                                                                                                        |
| external_parent_id.gtpl      | expected_external_parent_id.txt       | Text                                                                                                                        |

Test input data and expected output files are organized in to datasets where each test dataset is comprised of test input data files and their corresponding expected output files. Each test dataset is located in a subdirectory of the `_test_` directory. The dataset directory name should reflect what is being tested.

The goal is to test all code paths and edge cases in the various template files. Testing conditional logic in templates will require creating test a dataset for each conditional code path.

In order to avoid duplication of test input data files (e.g. `integration.json`, `rawData.json`), test datasets automatically inherit any test input data files located in their `_test_` parent directory. Test input files in a given dataset subdirectory take precedent over the file with the same name located in the parent `_test_` directory. Inheritance only applies to test input data files and not expected output files (e.g. `expected_request_url.txt`). Expected output files are always located in a `_test_/{test dataset name}` subdirectory.

Expected JSON output uses a data comparison whereas expected plain text output will either perform an exact string comparison or use a regular expression. Expected text output files (`expected_*.txt`) can specify a regular expression by surrounding the text content of the file with a leading and trailing slash "/" character.

Expected output files that verify that either the `fail` or `stop` template function has been called will contain the message provided to the `fail` or `stop` function formatted as a JSON string.

### Testing multiple request bodies

When the `request_body.gtpl` request body template generates multiple bodies (using the `#body` marker) then the `expected_request_body.txt` file will contain the exact matching text of what the template generates.

### Testing data pull metadata templates

The test input data for metadata templates (e.g., `external_id.gtpl`, `external_updated_at.gtpl`, `external_parent_id.gtpl`) is specified in an `externalData.json` file under a field named after the data pull's type. The data type name is defined in the `data/pull/{data pull name}/config.json` file. Metadata templates are applied to each object in the array under this field, and the expected output lists one value per line for each object.

e.g.

data pull `config.json`

```JSON
{
    "dataType": {
        "name": "order",
        "version": "1.0"
    }
}
```

`externalData.json` test input data file

```JSON
{
    "order": [
        {
            "orderId": "order1",
            "customerId": "customer1",
            "totalAmount": 9.99,
            "createdAt": "2003-12-10T20:15:00Z"
        },
        {
            "orderId": "order2",
            "customerId": "customer2",
            "totalAmount": 19.98,
            "createdAt": "2003-12-10T20:15:00Z"
        }
    ]
}
```

`external_id.gtpl`

```go template
{{.orderId}}
```

`expected_external_id.txt`

```plain text
order1
order2
```
