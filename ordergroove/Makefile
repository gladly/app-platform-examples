# Ordergroove App Platform Makefile

# Get the directory where the Makefile resides
MAKEFILE_PATH := $(strip $(MAKEFILE_LIST))
MAKEFILE_DIR := $(shell dirname "$(MAKEFILE_PATH)")

# Define the path to the app configuration directory
APP_DIR := $(MAKEFILE_DIR)/app

# Include .env file if it exgists for secrets
ifneq (,$(wildcard $(MAKEFILE_DIR)/.env))
include $(MAKEFILE_DIR)/.env
export # Export environment variables from .env to be available to appcfg
endif

# Define required secrets (Ordergroove API key)
REQUIRED_SECRETS = \
 ORDERGROOVE_API_KEY

# Check if all required secrets are set
$(foreach secret,$(REQUIRED_SECRETS),\
 $(if $(value $(secret)),,\
 $(error Missing required secret: $(secret). Please define it in $(MAKEFILE_DIR)/.env or export it.)))

# --- Core App Tasks ---

.PHONY: default validate test build clean

default: test # Default target runs unit tests

# Validate the entire app configuration
validate:
	@echo "Validating app configuration in $(APP_DIR)..."
	appcfg validate -r $(APP_DIR)

# Run all unit tests (actions only - no data pulls)
test: validate
	@echo "Running all unit tests..."
	appcfg test -r $(APP_DIR)

# Build the app package (.zip file)
build: test
	@echo "Building app package..."
	appcfg build -r $(APP_DIR)
	@echo "Build complete. Package created in $(MAKEFILE_DIR)/app.zip"

# Clean build artifacts
clean:
	@echo "Cleaning build artifacts..."
	rm -f $(MAKEFILE_DIR)/app.zip

# --- Action Tasks ---

.PHONY: validate-actions test-actions

# Validate only the action configurations
validate-actions:
	@echo "Validating action configurations..."
	appcfg validate actions -r $(APP_DIR)

# Run all action unit tests
test-actions: validate-actions
	@echo "Running action unit tests..."
	appcfg test action -r $(APP_DIR)

# --- Data GraphQL Tasks ---

.PHONY: validate-data-graphql test-data-graphql run-data-graphql

# Validate data-graphql configurations
validate-data-graphql:
	@echo "Validating data-graphql configurations..."
	appcfg validate data-graphql -r $(APP_DIR)

# Run data-graphql unit tests
test-data-graphql: validate-data-graphql
	@echo "Running data-graphql unit tests..."
	appcfg test data-graphql -r $(APP_DIR)

# Run data-graphql against live API
run-data-graphql: validate-data-graphql
	@echo "Running data-graphql (live API calls)..."
	@echo "Ensure .env file is populated with valid secrets: $(REQUIRED_SECRETS)"
	appcfg run data-graphql \
		-s '{"api_key": "$(ORDERGROOVE_API_KEY)"}' \
		-r $(APP_DIR)

# Run specific GraphQL queries
.PHONY: run-data-graphql-customer run-data-graphql-subscription run-data-graphql-order

run-data-graphql-customer:
	@echo "Running data-graphql query: customer..."
	@echo "Using CUSTOMER_EMAIL_ADDRESS=$(CUSTOMER_EMAIL_ADDRESS)"
	appcfg run data-graphql --root $(APP_DIR) \
		-s '{"api_key": "$(ORDERGROOVE_API_KEY)"}' \
		-c '{"primaryEmailAddress": "$(CUSTOMER_EMAIL_ADDRESS)", "emailAddresses":["$(CUSTOMER_EMAIL_ADDRESS)"]}' \
		-q customer

run-data-graphql-subscription:
	@echo "Running data-graphql query: subscription..."
	@echo "Using SUBSCRIPTION_ID=$(SUBSCRIPTION_ID)"
	appcfg run data-graphql --root $(APP_DIR) \
		-s '{"api_key": "$(ORDERGROOVE_API_KEY)"}' \
		-c '{"subscriptionId": "$(SUBSCRIPTION_ID)"}' \
		-q subscription

run-data-graphql-order:
	@echo "Running data-graphql query: order..."
	@echo "Using ORDER_ID=$(ORDER_ID)"
	appcfg run data-graphql --root $(APP_DIR) \
		-s '{"api_key": "$(ORDERGROOVE_API_KEY)"}' \
		-c '{"orderId": "$(ORDER_ID)"}' \
		-q order

# --- Data Pull Tasks ---

.PHONY: validate-data-pulls test-data-pulls run-data-pulls

# Validate only the data-pull configurations
validate-data-pulls:
	@echo "Validating data-pull configurations..."
	appcfg validate data-pulls -r $(APP_DIR)

# Run all data-pull unit tests
test-data-pulls: validate-data-pulls
	@echo "Running data-pull unit tests..."
	appcfg test data-pull -r $(APP_DIR)

# Run all data-pulls against live API
run-data-pulls: validate-data-pulls
	@echo "Running all data-pulls (live API calls)..."
	@echo "Ensure .env file is populated with valid secrets: $(REQUIRED_SECRETS)"
	appcfg run data-pulls \
		-s '{"api_key": "$(ORDERGROOVE_API_KEY)"}' \
		-r $(APP_DIR)

# --- Individual Data Pull Unit Tests ---

# Naming convention: test-data-pull-[data-pull-name]
# Add specific data-pull test targets here as needed

.PHONY: test-data-pull-subscriptions test-data-pull-customers test-data-pull-orders

test-data-pull-subscriptions:
	@echo "Running unit test for data-pull: subscriptions..."
	appcfg test data-pull subscriptions -r $(APP_DIR)

test-data-pull-customers:
	@echo "Running unit test for data-pull: customers..."
	appcfg test data-pull customers -r $(APP_DIR)

test-data-pull-orders:
	@echo "Running unit test for data-pull: orders..."
	appcfg test data-pull orders -r $(APP_DIR)

# --- Individual Data Pull Integration Tests ---

# Add specific data-pull run targets here as needed

.PHONY: run-data-pull-subscriptions run-data-pull-customers run-data-pull-orders

run-data-pull-subscriptions:
	@echo "Running data-pull: subscriptions..."
	appcfg run data-pull subscriptions \
		-s '{"api_key": "$(ORDERGROOVE_API_KEY)"}' \
		-r $(APP_DIR)

run-data-pull-customers:
	@echo "Running data-pull: customers..."
	appcfg run data-pull customers \
		-s '{"api_key": "$(ORDERGROOVE_API_KEY)"}' \
		-r $(APP_DIR)

run-data-pull-orders:
	@echo "Running data-pull: orders..."
	appcfg run data-pull orders \
		-s '{"api_key": "$(ORDERGROOVE_API_KEY)"}' \
		-r $(APP_DIR)

# --- Individual Action Unit Tests ---

# Naming convention: test-action-[action-name]

.PHONY: test-action-getSubscriptionsByCustomerId test-action-getSubscriptionById test-action-getCancellationReasons test-action-cancelSubscription test-action-reactivateSubscription test-action-skipSubscription test-action-getOrderItems test-action-getShippingAddress

# Query actions
test-action-getSubscriptionsByCustomerId:
	@echo "Running unit test for action: getSubscriptionsByCustomerId..."
	appcfg test action getSubscriptionsByCustomerId -r $(APP_DIR)

test-action-getSubscriptionById:
	@echo "Running unit test for action: getSubscriptionById..."
	appcfg test action getSubscriptionById -r $(APP_DIR)

test-action-getCancellationReasons:
	@echo "Running unit test for action: getCancellationReasons..."
	appcfg test action getCancellationReasons -r $(APP_DIR)

# Mutation actions
test-action-cancelSubscription:
	@echo "Running unit test for action: cancelSubscription..."
	appcfg test action cancelSubscription -r $(APP_DIR)



test-action-reactivateSubscription:
	@echo "Running unit test for action: reactivateSubscription..."
	appcfg test action reactivateSubscription -r $(APP_DIR)

test-action-skipSubscription:
	@echo "Running unit test for action: skipSubscription..."
	appcfg test action skipSubscription -r $(APP_DIR)

test-action-getOrderItems:
	@echo "Running unit test for action: getOrderItems..."
	appcfg test action getOrderItems -r $(APP_DIR)

test-action-getShippingAddress:
	@echo "Running unit test for action: getShippingAddress..."
	appcfg test action getShippingAddress -r $(APP_DIR)

# --- Integration Tests (Live API Calls) ---

# These targets run components against the live external API.
# They REQUIRE secrets to be set in the .env file.

.PHONY: integration-tests run-action-getSubscriptionsByCustomerId

# Run all integration tests
integration-tests: validate
	@echo "Running all integration tests (live API calls)..."
	@echo "Ensure .env file is populated with valid secrets: $(REQUIRED_SECRETS)"
	# Uncomment the targets you want to run as part of integration tests
	# $(MAKE) run-action-getSubscriptionsByCustomerId
	# $(MAKE) run-action-getSubscriptionById
	# $(MAKE) run-action-getCancellationReasons
	# $(MAKE) run-action-getOrderItems
	# $(MAKE) run-action-getShippingAddress
	# $(MAKE) run-data-pull-subscriptions
	# $(MAKE) run-data-pull-customers
	# $(MAKE) run-data-pull-orders
	# $(MAKE) run-data-graphql-customer

.PHONY: run-action-getSubscriptionsByCustomerId
run-action-getSubscriptionsByCustomerId:
	@echo "Running integration test for action: getSubscriptionsByCustomerId..."
	appcfg run action getSubscriptionsByCustomerId \
		-s '{"api_key": "$(ORDERGROOVE_API_KEY)"}' \
		-i '{"customerId": "23486119412084"}' \
		-r $(APP_DIR)

.PHONY: run-action-graphql-cancelSubscription
run-action-graphql-cancelSubscription:
	@echo "Running integration test for action: cancelSubscription..."
	@echo "Using SUBSCRIPTION_ID=$(SUBSCRIPTION_ID)"
	appcfg run action-graphql -m cancelSubscription \
		-s '{"api_key": "$(ORDERGROOVE_API_KEY)"}' \
		-i '{"subscriptionId": "$(SUBSCRIPTION_ID)", "cancelReasonCode": "1", "cancelReasonDetails": "Test cancellation"}' \
		-r $(APP_DIR)

.PHONY: run-action-cancelSubscription
run-action-cancelSubscription:
	@echo "Running integration test for action: cancelSubscription..."
	@echo "Using SUBSCRIPTION_ID=$(SUBSCRIPTION_ID)"
	appcfg run action cancelSubscription \
		-s '{"api_key": "$(ORDERGROOVE_API_KEY)"}' \
		-i '{"subscriptionId": "$(SUBSCRIPTION_ID)", "cancelReasonCode": "1", "cancelReasonDetails": "Test cancellation"}' \
		-r $(APP_DIR)

.PHONY: run-action-getCancellationReasons
run-action-getCancellationReasons:
	@echo "Running integration test for action: getCancellationReasons..."
	appcfg run action getCancellationReasons \
		-s '{"api_key": "$(ORDERGROOVE_API_KEY)"}' \
		-i '{}' \
		-r $(APP_DIR)

.PHONY: run-action-getSubscriptionById
run-action-getSubscriptionById:
	@echo "Running integration test for action: getSubscriptionById..."
	appcfg run action getSubscriptionById \
		-s '{"api_key": "$(ORDERGROOVE_API_KEY)"}' \
		-i '{"subscriptionId": "$(SUBSCRIPTION_ID)"}' \
		-r $(APP_DIR)

.PHONY: run-action-reactivateSubscription
run-action-reactivateSubscription:
	@echo "Running integration test for action: reactivateSubscription..."
	appcfg run action reactivateSubscription \
		-s '{"api_key": "$(ORDERGROOVE_API_KEY)"}' \
		-i '{"subscriptionId": "$(SUBSCRIPTION_ID)", "every": 3, "everyPeriod": 1, "startDate": "2025-08-14"}' \
		-r $(APP_DIR)

.PHONY: run-action-skipSubscription
run-action-skipSubscription:
	@echo "Running integration test for action: skipSubscription..."
	@echo "Using ORDER_ID=$(ORDER_ID)"
	@echo "Using SUBSCRIPTION_ID=$(SUBSCRIPTION_ID)"
	appcfg run action skipSubscription \
		-s '{"api_key": "$(ORDERGROOVE_API_KEY)"}' \
		-i '{"orderId": "$(ORDER_ID)", "subscriptionId": "$(SUBSCRIPTION_ID)"}' \
		-r $(APP_DIR)

.PHONY: run-action-getOrderItems
run-action-getOrderItems:
	@echo "Running integration test for action: getOrderItems..."
	@echo "Using ORDER_ID=$(ORDER_ID)"
	appcfg run action getOrderItems \
		-s '{"api_key": "$(ORDERGROOVE_API_KEY)"}' \
		-i '{"orderId": "$(ORDER_ID)"}' \
		-r $(APP_DIR)

.PHONY: run-action-getShippingAddress
run-action-getShippingAddress:
	@echo "Running integration test for action: getShippingAddress..."
	@echo "Using SUBSCRIPTION_ID=$(SUBSCRIPTION_ID)"
	appcfg run action getShippingAddress \
		-s '{"api_key": "$(ORDERGROOVE_API_KEY)"}' \
		-i '{"subscriptionId": "$(SUBSCRIPTION_ID)"}' \
		-r $(APP_DIR)

# --- Helper Targets ---

.PHONY: help debug-env list-actions list-data-pulls setup-env

# Show help message
help:
	@echo "Usage: make [target]"
	@echo ""
	@echo "Core Targets:"
	@echo "  validate                    Validate the entire app configuration."
	@echo "  test                       Run all unit tests (no live API calls)."
	@echo "  build                      Build the app package (.zip file)."
	@echo "  clean                      Remove build artifacts."
	@echo ""
	@echo "Component Validation:"
	@echo "  validate-actions            Validate only action configurations."
	@echo "  validate-data-pulls         Validate only data-pull configurations."
	@echo "  validate-data-graphql       Validate only data-graphql configurations."
	@echo ""
	@echo "Component Unit Tests:"
	@echo "  test-actions                Run all action unit tests."
	@echo "  test-data-pulls             Run all data-pull unit tests."
	@echo "  test-data-graphql           Run all data-graphql unit tests."
	@echo ""
	@echo "Query Actions:"
	@echo "  test-action-getSubscriptionsByCustomerId  Run unit test for getSubscriptionsByCustomerId action."
	@echo "  test-action-getSubscriptionById            Run unit test for getSubscriptionById action."
	@echo "  test-action-getCancellationReasons        Run unit test for getCancellationReasons action."
	@echo "  test-action-getOrderItems                 Run unit test for getOrderItems action."
	@echo "  test-action-getShippingAddress            Run unit test for getShippingAddress action."
	@echo ""
	@echo "Mutation Actions:"
	@echo "  test-action-cancelSubscription            Run unit test for cancelSubscription action."
	@echo "  test-action-reactivateSubscription        Run unit test for reactivateSubscription action."
	@echo "  test-action-skipSubscription             Run unit test for skipSubscription action."
	@echo ""
	@echo "Data Pulls:"
	@echo "  run-data-pulls              Run all data-pulls against live API."
	@echo "  test-data-pull-subscriptions  Run unit test for subscriptions data-pull."
	@echo "  test-data-pull-customers      Run unit test for customers data-pull."
	@echo "  test-data-pull-orders         Run unit test for orders data-pull."
	@echo "  run-data-pull-subscriptions   Run subscriptions data-pull against live API."
	@echo "  run-data-pull-customers       Run customers data-pull against live API."
	@echo "  run-data-pull-orders          Run orders data-pull against live API."
	@echo ""
	@echo "Data GraphQL:"
	@echo "  run-data-graphql            Run all data-graphql against live API."
	@echo "  run-data-graphql-customer   Run customer GraphQL query (requires CUSTOMER_EMAIL_ADDRESS)."
	@echo "  run-data-graphql-subscription Run subscription GraphQL query (requires SUBSCRIPTION_ID)."
	@echo "  run-data-graphql-order      Run order GraphQL query (requires ORDER_ID)."
	@echo ""
	@echo "Integration Tests (Live API - Requires .env):"
	@echo "  integration-tests           Run all defined integration test targets."
	@echo "  run-action-[name]          Run a specific action against the live API."
	@echo "  run-data-pulls             Run all data-pulls against live API."
	@echo "  run-data-graphql           Run all data-graphql against live API."
	@echo ""
	@echo "Other Targets:"
	@echo "  help                       Show this help message."
	@echo "  debug-env                  Show environment variables (for debugging .env)."
	@echo "  list-actions               List all available actions."
	@echo "  list-data-pulls            List all available data-pulls."
	@echo "  setup-env                  Create a template .env file."

# Debug target to show environment variables
debug-env:
	@echo "--- Debug Info ---"
	@echo "MAKEFILE_DIR = $(MAKEFILE_DIR)"
	@echo "APP_DIR = $(APP_DIR)"
	@echo "Found .env file: $(wildcard $(MAKEFILE_DIR)/.env)"
	@echo "Required Secrets: $(REQUIRED_SECRETS)"
	$(foreach secret,$(REQUIRED_SECRETS),\
		@echo "  $(secret) = $(value $(secret))")
	@echo "--- End Debug Info ---"

# List all available actions
list-actions:
	@echo "Available actions:"
	@find $(APP_DIR)/actions -maxdepth 1 -type d ! -name actions | sed 's|.*/||' | grep -v '^$$' | sort

# List all available data-pulls
list-data-pulls:
	@echo "Available data-pulls:"
	@find $(APP_DIR)/data/pull -maxdepth 1 -type d ! -name pull | sed 's|.*/||' | grep -v '^$$' | sort

# Create a template .env file
setup-env:
	@if [ ! -f $(MAKEFILE_DIR)/.env ]; then \
		echo "Creating template .env file..."; \
		echo "# Ordergroove App Platform Environment Variables" > $(MAKEFILE_DIR)/.env; \
		echo "# Copy this file and populate with actual values" >> $(MAKEFILE_DIR)/.env; \
		echo "" >> $(MAKEFILE_DIR)/.env; \
		echo "# Required secrets" >> $(MAKEFILE_DIR)/.env; \
		echo "ORDERGROOVE_API_KEY=your_api_key_here" >> $(MAKEFILE_DIR)/.env; \
		echo "" >> $(MAKEFILE_DIR)/.env; \
		echo "# Optional test variables for integration tests" >> $(MAKEFILE_DIR)/.env; \
		echo "SUBSCRIPTION_ID=your_subscription_id_here" >> $(MAKEFILE_DIR)/.env; \
		echo "ORDER_ID=your_order_id_here" >> $(MAKEFILE_DIR)/.env; \
		echo "CUSTOMER_EMAIL_ADDRESS=test@example.com" >> $(MAKEFILE_DIR)/.env; \
		echo "Template .env file created at $(MAKEFILE_DIR)/.env"; \
	else \
		echo ".env file already exists at $(MAKEFILE_DIR)/.env"; \
	fi
