STACK_NAME=lottery-streams
ARTIFACTS_BUCKET=lottery-kinesis
PARAMETERS_FILE=template-config.json
PARAMETERS=$(shell cat $(PARAMETERS_FILE) | jqn 'get("Parameters") | entries | map(x => x[0] + "=" + x[1]) | join(" ")')
REGION=us-east-1

CAPABILITIES=CAPABILITY_IAM CAPABILITY_AUTO_EXPAND

APPLICATION_VERSION_ID=$(shell aws kinesisanalyticsv2 describe-application --application-name LotteryApp | jqn 'get("ApplicationDetail.ApplicationVersionId")' | sed 's/\x1b\[[0-9;]*m//g')
OBJECT_VERSION=$(shell aws s3api list-object-versions --bucket lottery-kinesis --prefix flink-lottery-1.0.jar | jqn 'get("Versions.0.VersionId")')

.PHONY: deploy

mvn-package:
	cd ../flink-lottery/ && mvn package && cp ./target/flink-lottery*.jar ../infra

upload-app-code: mvn-package
	aws s3 cp bidclub-1.0.jar s3://$(ARTIFACTS_BUCKET)/flink-lottery-1.0.jar

lambdas.zip:
	(cd ../lambdas/ && npm install && npm run build && npx node-lambda zip --no-optionalDependencies -n lambdas -A ../infra)

make-artifacts-bucket:
	aws s3 mb s3://$(ARTIFACTS_BUCKET) --region $(REGION)

deploy:
	sam deploy --template-file template.yml --stack-name $(STACK_NAME) --capabilities $(CAPABILITIES)  --region $(REGION) --s3-bucket $(ARTIFACTS_BUCKET) --parameter-overrides $(PARAMETERS)

destroy:
	aws cloudformation delete-stack --stack-name $(STACK_NAME)   --region $(REGION)

start-app:
	aws kinesisanalyticsv2 start-application --cli-input-json file://start_request.json

stop-app:
	aws kinesisanalyticsv2 stop-application --cli-input-json file://stop_request.json

update-app: upload-app-code
	cat update_request_template.json | \
		jqn "set(\"ApplicationConfigurationUpdate.ApplicationCodeConfigurationUpdate.CodeContentUpdate.S3ContentLocationUpdate.ObjectVersionUpdate\", \"$(OBJECT_VERSION)\") | \
		set(\"CurrentApplicationVersionId\", $(APPLICATION_VERSION_ID))" | \
		sed 's/\x1b\[[0-9;]*m//g' \
		> update_request.json
	aws kinesisanalyticsv2 update-application --cli-input-json file://update_request.json
