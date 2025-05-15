MAKEFILE_PATH := $(strip $(MAKEFILE_LIST))
MAKEFILE_DIR := $(shell dirname "$(MAKEFILE_PATH)")

# Include .env file if it exists
ifneq (,$(wildcard .env))
    include .env
    export
endif

.PHONY: validate test build validate-data storefront-user-data-graphql storefront-user-data-pull validate-actions test-actions integration-tests cancel-subscription cancel-subscription-graphql get-subscription-by-id-graphql get-subscription-by-id ship-now pause-subscription-graphql pause-subscription-not-found get-customers get-customers-graphql

default: integration-tests

validate:
	appcfg validate -r $(MAKEFILE_DIR)/app

test: validate
	appcfg test -r $(MAKEFILE_DIR)/app

build: test
	appcfg build -r $(MAKEFILE_DIR)/app

validate-data:
	appcfg validate data -r $(MAKEFILE_DIR)/app

# Run storefront user data pull using GraphQL with pattern matching
storefront-user-data-graphql-%: validate-data
	appcfg run data-graphql -q storefront_user -s '{"apiToken": "$(SKIO_API_TOKEN)"}' -d $* -r $(MAKEFILE_DIR)/app

# Run storefront user data pull with pattern matching
storefront-user-data-pull-%: validate-data
	appcfg run data-pull -s '{"apiToken": "$(SKIO_API_TOKEN)"}' -d $* -r $(MAKEFILE_DIR)/app

storefront-user-data-graphql: validate-data
	appcfg run data-graphql -q storefront_user -s '{"apiToken": "$(SKIO_API_TOKEN)"}' -d by-all-contact-info -r $(MAKEFILE_DIR)/app

subscriptions-data-graphql: validate-data
	appcfg run data-graphql -q subscriptions -s '{"apiToken": "$(SKIO_API_TOKEN)"}' -d by-all-contact-info -r $(MAKEFILE_DIR)/app

storefront-user-data-pull: validate-data
	appcfg run data-pull -s '{"apiToken": "$(SKIO_API_TOKEN)"}' -d by-all-contact-info -r $(MAKEFILE_DIR)/app

validate-actions:
	appcfg validate actions -r $(MAKEFILE_DIR)/app

# Run action tests
test-actions: validate-actions
	appcfg test action -r $(MAKEFILE_DIR)/app

# Run data-pull tests
test-data-pull: validate-data
	appcfg test data-pull -r $(MAKEFILE_DIR)/app

# Get Subscription By ID action using action-graphql
get-subscription-by-id-graphql:
	appcfg run action-graphql -q getSubscriptionById -s '{"apiToken": "$(SKIO_API_TOKEN)"}' -i '{"subscriptionId": "9843ae37-021c-423e-bfe0-36367ceddf82"}' -r $(MAKEFILE_DIR)/app

# Get Subscription By ID action using action
get-subscription-by-id:
	appcfg run action getSubscriptionById -s '{"apiToken": "$(SKIO_API_TOKEN)"}' -d success -r $(MAKEFILE_DIR)/app

get-customers:
	appcfg run action getCustomers -s '{"apiToken": "$(SKIO_API_TOKEN)"}' -d success -r $(MAKEFILE_DIR)/app

get-customers-graphql:
	appcfg run action-graphql -q getCustomers -s '{"apiToken": "$(SKIO_API_TOKEN)"}' -i '{"email": "skio-customer@example.com"}' -r $(MAKEFILE_DIR)/app

# Ship Now action
ship-now:
	appcfg run action-graphql -m shipNow -s '{"apiToken": "$(SKIO_API_TOKEN)"}' -i '{"subscriptionId": "1e671650-4876-4e80-a0b7-130f355281ac"}' -r $(MAKEFILE_DIR)/app

# Run pause subscription action using GraphQL
pause-subscription-graphql:
	appcfg run action-graphql -m pauseSubscription -s '{"apiToken": "$(SKIO_API_TOKEN)"}' -i '{"subscriptionId": "8b753087-391a-44c8-a7f3-9adf2e6dfebe"}' -r $(MAKEFILE_DIR)/app

# Run pause subscription action with a non-existent ID
pause-subscription-not-found:
	appcfg run action-graphql -m pauseSubscription -s '{"apiToken": "$(SKIO_API_TOKEN)"}' -i '{"subscriptionId": "11111111-1111-1111-1111-111111111111"}' -r $(MAKEFILE_DIR)/app

# Run all integration tests
integration-tests: test test-actions test-data-pull storefront-user-data-graphql storefront-user-data-pull cancel-subscription cancel-subscription-graphql get-subscription-by-id-graphql get-subscription-by-id ship-now pause-subscription-graphql pause-subscription-not-found get-customers get-customers-graphql

# Run cancel subscription action
cancel-subscription:
	appcfg run action cancelSubscription -s '{"apiToken": "$(SKIO_API_TOKEN)"}' -d success -r $(MAKEFILE_DIR)/app

# Run cancel subscription action using GraphQL
cancel-subscription-graphql:
	appcfg run action-graphql -m cancelSubscription -s '{"apiToken": "$(SKIO_API_TOKEN)"}' -i '{"input": {"subscriptionId": "1e671650-4876-4e80-a0b7-130f355281ac", "shouldSendNotif": true}}' -r $(MAKEFILE_DIR)/app


