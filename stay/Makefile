APP_ROOT := app

.PHONY: validate test build all

all: validate test build

validate:
	appcfg validate -r $(APP_ROOT)

test:
	appcfg test -r $(APP_ROOT)

build:
	appcfg build -r $(APP_ROOT)
