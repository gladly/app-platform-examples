# Copy this file to your app root directory and modify it as needed.
MAKEFILE_PATH := $(strip $(MAKEFILE_LIST))
MAKEFILE_DIR := $(shell dirname "$(MAKEFILE_PATH)")

.PHONY: validate test build run_inboxes_action_graphql run_topics_action_graphql

default: build

validate:
	appcfg validate -r $(MAKEFILE_DIR)

test: validate
	appcfg test  -r $(MAKEFILE_DIR)

build: test
	appcfg build  -r $(MAKEFILE_DIR)

run_inboxes_action_graphql:
	appcfg run action-graphql -q inboxes -s '{"apiToken": "$(GLADLY_API_TOKEN)"}' -i '{}' -r $(MAKEFILE_DIR)

run_topics_action_graphql:
	appcfg run action-graphql -q topics -s '{"apiToken": "$(GLADLY_API_TOKEN)"}' -i '{}' -r $(MAKEFILE_DIR)