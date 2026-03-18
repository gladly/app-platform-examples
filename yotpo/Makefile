MAKEFILE_PATH := $(strip $(MAKEFILE_LIST))
MAKEFILE_DIR := $(shell dirname "$(MAKEFILE_PATH)")

# Include .env file if it exists
ifneq (,$(wildcard .env))
    include .env
    export
endif

.PHONY: validate test build validate-data test-data-pull \
        customers-data-graphql customers-data-graphql-% \
        customers-data-pull customers-data-pull-% \
        reviews-data-graphql reviews-data-graphql-% \
        reviews-data-pull reviews-data-pull-% \
        loyalty-data-graphql loyalty-data-graphql-% \
        loyalty-data-pull loyalty-data-pull-% \
        run-oauth get-token

default: test

# -----------------------------------------------------------------------------
# Core lifecycle targets
# -----------------------------------------------------------------------------
validate:
	appcfg validate -r $(MAKEFILE_DIR)/app

test: validate
	appcfg test -r $(MAKEFILE_DIR)/app

build: test
	appcfg build -r $(MAKEFILE_DIR)/app

# -----------------------------------------------------------------------------
# OAuth flow
# Usage: make run-oauth
# Requires: YOTPO_CLIENT_ID, YOTPO_CLIENT_SECRET, and YOTPO_STORE_ID in .env
# -----------------------------------------------------------------------------
run-oauth:
	@mkdir -p $(MAKEFILE_DIR)/app/authentication/oauth/_run_/data/makefile
	@echo '{"configuration": {"client_id": "$(YOTPO_CLIENT_ID)", "store_id": "$(YOTPO_STORE_ID)"}}' > $(MAKEFILE_DIR)/app/authentication/oauth/_run_/data/makefile/integration.json
	appcfg run oauth -s '{"client_secret": "$(YOTPO_CLIENT_SECRET)"}' -d makefile -r $(MAKEFILE_DIR)/app

# Usage: make get-token CODE=<authorization_code>
# Requires: YOTPO_CLIENT_ID, YOTPO_CLIENT_SECRET, YOTPO_REDIRECT_URI in .env
get-token:
	curl -s -X POST https://developers.yotpo.com/v2/oauth2/token \
					-H "Content-Type: application/json" \
					-d '{"client_id": "$(YOTPO_CLIENT_ID)", "client_secret": "$(YOTPO_CLIENT_SECRET)", "grant_type": "authorization_code", "code": "$(CODE)", "redirect_uri": "$(YOTPO_REDIRECT_URI)"}'

# -----------------------------------------------------------------------------
# Data validation & tests
# -----------------------------------------------------------------------------
validate-data:
	appcfg validate data -r $(MAKEFILE_DIR)/app

test-data-pull: validate-data
	appcfg test data-pull -r $(MAKEFILE_DIR)/app

# -----------------------------------------------------------------------------
# Customers data pulls
# Usage: make customers-data-pull EMAIL=customer@example.com
# Requires: YOTPO_ACCESS_TOKEN and YOTPO_STORE_ID in .env
# -----------------------------------------------------------------------------
customers-data-pull: validate-data
	@mkdir -p $(MAKEFILE_DIR)/app/data/pull/_run_/data/makefile
	@echo '{"configuration": {"store_id": "$(YOTPO_STORE_ID)"}}' > $(MAKEFILE_DIR)/app/data/pull/_run_/data/makefile/integration.json
	@echo '{"id": "customer1", "name": "Test Customer", "emailAddresses": ["$(or $(EMAIL),test-yotpo@gladly.com)"]}' > $(MAKEFILE_DIR)/app/data/pull/_run_/data/makefile/customer.json
	appcfg run data-pull -s '{"access_token": "$(YOTPO_ACCESS_TOKEN)"}' -d makefile -r $(MAKEFILE_DIR)/app

# Usage: make customers-data-graphql EMAIL=customer@example.com
# Requires: YOTPO_ACCESS_TOKEN and YOTPO_STORE_ID in .env
customers-data-graphql: validate-data
	@mkdir -p $(MAKEFILE_DIR)/app/data/_run_/data/makefile
	@echo '{"configuration": {"store_id": "$(YOTPO_STORE_ID)"}}' > $(MAKEFILE_DIR)/app/data/_run_/data/makefile/integration.json
	@echo '{"id": "customer1", "name": "Test Customer", "emailAddresses": ["$(or $(EMAIL),test-yotpo@gladly.com)"]}' > $(MAKEFILE_DIR)/app/data/_run_/data/makefile/customer.json
	appcfg run data-graphql -q customers -s '{"access_token": "$(YOTPO_ACCESS_TOKEN)"}' -d makefile -r $(MAKEFILE_DIR)/app

# -----------------------------------------------------------------------------
# Reviews data pulls / GraphQL queries
# Usage: make reviews-data-pull EMAIL=customer@example.com
# Requires: YOTPO_ACCESS_TOKEN and YOTPO_STORE_ID in .env
# -----------------------------------------------------------------------------
reviews-data-graphql: validate-data
	@mkdir -p $(MAKEFILE_DIR)/app/data/_run_/data/makefile
	@echo '{"configuration": {"store_id": "$(YOTPO_STORE_ID)"}}' > $(MAKEFILE_DIR)/app/data/_run_/data/makefile/integration.json
	@echo '{"id": "customer1", "name": "Test Customer", "emailAddresses": ["$(or $(EMAIL),test-yotpo@gladly.com)"]}' > $(MAKEFILE_DIR)/app/data/_run_/data/makefile/customer.json
	appcfg run data-graphql -q reviews -s '{"access_token": "$(YOTPO_ACCESS_TOKEN)"}' -d makefile -r $(MAKEFILE_DIR)/app

reviews-data-pull: validate-data
	@mkdir -p $(MAKEFILE_DIR)/app/data/pull/_run_/data/makefile
	@echo '{"configuration": {"store_id": "$(YOTPO_STORE_ID)"}}' > $(MAKEFILE_DIR)/app/data/pull/_run_/data/makefile/integration.json
	@echo '{"id": "customer1", "name": "Test Customer", "emailAddresses": ["$(or $(EMAIL),test-yotpo@gladly.com)"]}' > $(MAKEFILE_DIR)/app/data/pull/_run_/data/makefile/customer.json
	appcfg run data-pull -s '{"access_token": "$(YOTPO_ACCESS_TOKEN)"}' -d makefile -r $(MAKEFILE_DIR)/app

# -----------------------------------------------------------------------------
# Loyalty data pulls / GraphQL queries
# Usage: make loyalty-data-pull EMAIL=customer@example.com
# Requires: YOTPO_ACCESS_TOKEN and YOTPO_STORE_ID in .env
# -----------------------------------------------------------------------------
loyalty-data-graphql: validate-data
	@mkdir -p $(MAKEFILE_DIR)/app/data/_run_/data/makefile
	@echo '{"configuration": {"store_id": "$(YOTPO_STORE_ID)"}}' > $(MAKEFILE_DIR)/app/data/_run_/data/makefile/integration.json
	@echo '{"id": "customer1", "name": "Test Customer", "emailAddresses": ["$(or $(EMAIL),test-yotpo@gladly.com)"]}' > $(MAKEFILE_DIR)/app/data/_run_/data/makefile/customer.json
	appcfg run data-graphql -q loyalty -s '{"access_token": "$(YOTPO_ACCESS_TOKEN)"}' -d makefile -r $(MAKEFILE_DIR)/app

loyalty-data-pull: validate-data
	@mkdir -p $(MAKEFILE_DIR)/app/data/pull/_run_/data/makefile
	@echo '{"configuration": {"store_id": "$(YOTPO_STORE_ID)"}}' > $(MAKEFILE_DIR)/app/data/pull/_run_/data/makefile/integration.json
	@echo '{"id": "customer1", "name": "Test Customer", "emailAddresses": ["$(or $(EMAIL),test-yotpo@gladly.com)"]}' > $(MAKEFILE_DIR)/app/data/pull/_run_/data/makefile/customer.json
	appcfg run data-pull -s '{"access_token": "$(YOTPO_ACCESS_TOKEN)"}' -d makefile -r $(MAKEFILE_DIR)/app