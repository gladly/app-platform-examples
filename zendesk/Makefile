MAKEFILE_PATH := $(strip $(MAKEFILE_LIST))
MAKEFILE_DIR := $(shell dirname "$(MAKEFILE_PATH)")

# Define the path to the app configuration directory
APP_DIR := $(MAKEFILE_DIR)/app

# Include .env file if it exists
ifneq (,$(wildcard $(MAKEFILE_DIR)/.env))
    include $(MAKEFILE_DIR)/.env
    export
endif

# Secrets JSON for OAuth authentication
SECRETS_JSON := { "access_token": "$(ZENDESK_ACCESS_TOKEN)"}

.PHONY: validate test build validate-actions createTicket-action-graphql addComment-action-graphql searchUsersByPhone-action-graphql createEndUser-action-graphql run-oauth

default: build

validate:
	appcfg validate -r $(APP_DIR)

test: validate
	appcfg test -r $(APP_DIR)

build: test
	appcfg build -r $(APP_DIR)

validate-actions:
	appcfg validate actions -r $(APP_DIR)

# Run createTicket action using GraphQL with requester_id
# Usage: make createTicket-action-graphql REQUESTER_ID=123456789
createTicket-action-graphql: validate-actions
	@if [ -z "$(REQUESTER_ID)" ]; then \
		echo "Error: REQUESTER_ID is required. Usage: make createTicket-action-graphql REQUESTER_ID=123456789"; \
		exit 1; \
	fi
	appcfg run action-graphql -m createTicket -i '{"subject": "Test Ticket", "comment": {"body": "Test body"}, "requester_id": "$(REQUESTER_ID)"}' -s '$(or $(SECRETS),$(SECRETS_JSON))' -r $(APP_DIR)

# Run addComment action using GraphQL
# Usage: make addComment-action-graphql TICKET_ID=123456789
addComment-action-graphql: validate-actions
	@if [ -z "$(TICKET_ID)" ]; then \
		echo "Error: TICKET_ID is required. Usage: make addComment-action-graphql TICKET_ID=123456789"; \
		exit 1; \
	fi
	appcfg run action-graphql -m addComment -i '{"ticket_id": $(TICKET_ID), "comment": {"body": "Test comment", "public": false}}' -s '$(or $(SECRETS),$(SECRETS_JSON))' -r $(APP_DIR)

# Run searchUsersByPhone action using GraphQL
# Usage: make searchUsersByPhone-action-graphql PHONE=+15551234567
searchUsersByPhone-action-graphql: validate-actions
	appcfg run action-graphql -q searchUsersByPhone -i '{"phone": "$(or $(PHONE),+15551234567)"}' -s '$(or $(SECRETS),$(SECRETS_JSON))' -r $(APP_DIR)

# Run createEndUser action using GraphQL
# Example: make createEndUser-action-graphql USER='{"name": "John Doe", "email": "john@example.com", "phone": "+15551234567"}'
createEndUser-action-graphql: validate-actions
	appcfg run action-graphql -m createEndUser -i '{"user": $(USER)}' -s '$(or $(SECRETS),$(SECRETS_JSON))' -r $(APP_DIR)

# Run OAuth flow to get access_token
# Requires ZENDESK_CLIENT_SECRET in .env
# Note: Add http://localhost:8080/oauth/auth-code to your Zendesk OAuth client redirect URLs
run-oauth:
	appcfg run oauth -s '{"client_secret": "$(ZENDESK_CLIENT_SECRET)"}' -r $(APP_DIR)
	@echo ""
	@echo "Copy access_token from the browser output to .env"

