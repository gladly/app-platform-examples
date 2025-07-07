MAKEFILE_PATH := $(strip $(MAKEFILE_LIST))
MAKEFILE_DIR := $(shell dirname "$(MAKEFILE_PATH)")

.PHONY: validate test build validate-data customer-data-graphql orders-data-graphql validate-actions cancelOrder-action-graphql returnLineItems-action-graphql integration-tests

default: build

validate:
	appcfg validate -r $(MAKEFILE_DIR)

test: validate
	appcfg test  -r $(MAKEFILE_DIR)

build: test
	appcfg build  -r $(MAKEFILE_DIR)

validate-data:
	appcfg validate data -r $(MAKEFILE_DIR)

# e.g. SECRETS='{"apiToken": "some-token"}' make customer-data-graphql
customer-data-graphql: validate-data
	appcfg run data-graphql -q customer -s '$(SECRETS)' -d success -o none -r $(MAKEFILE_DIR)

# e.g. SECRETS='{"apiToken": "some-token"}' make orders-data-graphql
orders-data-graphql: validate-data
	appcfg run data-graphql -q orders -s '$(SECRETS)' -d success -o none -r $(MAKEFILE_DIR)

validate-actions:
	appcfg validate actions -r $(MAKEFILE_DIR)

# e.g. SECRETS='{"apiToken": "some-token"}' make cancelOrder-action-graphql
cancelOrder-action-graphql: validate-actions
	appcfg run action-graphql -m cancelOrder -i '{"orderId": "o1", "reason": "wrong size"}' -s '$(SECRETS)' -o none -r $(MAKEFILE_DIR)

refundOrder-action-graphql: validate-actions
	appcfg run action-graphql -m refundOrder -i '{"orderId": "o1", "reason": "broken"}' -s '$(SECRETS)' -o none -r $(MAKEFILE_DIR)

returnLineItems-action-graphql: validate-actions
	appcfg run action-graphql -m returnLineItems -i '{"orderId": "o1", "lineItemIds": ["li1", "li2"]}' -s '$(SECRETS)' -o none -r $(MAKEFILE_DIR)

# e.g. SECRETS='{"apiToken": "some-token"}' make integration-tests
integration-tests: customer-data-graphql orders-data-graphql cancelOrder-action-graphql refundOrder-action-graphql returnLineItems-action-graphql