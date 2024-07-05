# App

This project contains an AWS Lambda maven application with [AWS Java SDK 2.x](https://github.com/aws/aws-sdk-java-v2)
dependencies.

## Prerequisites
- Java 17+
- Apache Maven
- [AWS SAM CLI](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/serverless-sam-cli-install.html)
- Docker

## Development

The function handler attempts to delete billing records provided that the function is invoked by an administrator. The
configured AWS Java SDK client is created in the constructor.

#### Building the project
```
mvn clean install
```

#### Testing it locally
```
sam local invoke
```

#### Adding more SDK clients
To add more service clients, you need to add the specific services modules in `pom.xml` and create the clients in the
constructor following the same pattern as dynamoDbClient.

## Deployment

The generated project contains a default [SAM template](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/sam-resource-function.html)
file `template.yaml` where you can configure different properties of your lambda function such as memory size and
timeout. You might also need to add specific policies to the lambda function so that it can access other AWS resources.

To deploy the application, you can run the following command:

```
sam deploy --guided
```

See [Deploying Serverless Applications](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/serverless-deploying.html)
for more info.



