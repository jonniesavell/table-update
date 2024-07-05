package com.indigententerprises;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.indigententerprises.dto.ApiGatewayResponse;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ConditionCheck;
import software.amazon.awssdk.services.dynamodb.model.Delete;
import software.amazon.awssdk.services.dynamodb.model.TransactWriteItem;
import software.amazon.awssdk.services.dynamodb.model.TransactWriteItemsRequest;

import java.util.HashMap;
import java.util.Map;

public class App implements RequestHandler<APIGatewayProxyRequestEvent, ApiGatewayResponse> {

    private final DynamoDbClient dynamoDbClient;
    private final String tablename;

    public App() {
        dynamoDbClient = DynamoDbClient.builder()
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .region(Region.US_WEST_1)
                .httpClientBuilder(UrlConnectionHttpClient.builder())
                .build();
        tablename = "Organization";
    }

    @Override
    public ApiGatewayResponse handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {

        final Map<String, String> pathParameters = input.getPathParameters();
        final APIGatewayProxyRequestEvent.RequestIdentity requestIdentity =
                input.getRequestContext().getIdentity();

        try {
            final String username = requestIdentity.getUser();
            final String orgId = pathParameters.get("orgId");
            final StringBuilder conditionKeyBuilder = new StringBuilder("Admins#");
            final String conditionStringPk = conditionKeyBuilder.append(orgId).toString();
            final HashMap<String, AttributeValue> conditionKey = new HashMap<>();

            try {
                conditionKey.put("PK", AttributeValue.builder().s(conditionStringPk).build());

                final HashMap<String, String> conditionExpressionAttributeNames = new HashMap<>();

                try {
                    conditionExpressionAttributeNames.put("#admins", "Admins");

                    final HashMap<String, AttributeValue> conditionExpressionAttributeValues = new HashMap<>();

                    try {
                        final AttributeValue conditionExpressionAttributeValue =
                                AttributeValue.builder().ss(username).build();
                        conditionExpressionAttributeValues.put(":user", conditionExpressionAttributeValue);

                        final ConditionCheck conditionCheck = ConditionCheck
                                .builder()
                                .tableName(tablename)
                                .key(conditionKey)
                                .conditionExpression("contains(#admins, :user)")
                                .expressionAttributeNames(conditionExpressionAttributeNames)
                                .expressionAttributeValues(conditionExpressionAttributeValues)
                                .build();
                        final TransactWriteItem conditionCheckItem = TransactWriteItem
                                .builder()
                                .conditionCheck(conditionCheck)
                                .build();
                        final HashMap<String, AttributeValue> deleteKey = new HashMap<>();

                        try {
                            final StringBuilder deleteKeyBuilder = new StringBuilder("Billing#");
                            final String deleteStringPk = deleteKeyBuilder.append(orgId).toString();
                            deleteKey.put("PK", AttributeValue.builder().s(deleteStringPk).build());

                            final Delete delete = Delete
                                    .builder()
                                    .key(deleteKey)
                                    .tableName(tablename)
                                    .build();
                            final TransactWriteItem deleteItem = TransactWriteItem.builder()
                                    .delete(delete)
                                    .build();
                            final TransactWriteItemsRequest transactWriteItemsRequest = TransactWriteItemsRequest
                                    .builder()
                                    .transactItems(conditionCheckItem, deleteItem)
                                    .build();
                            dynamoDbClient.transactWriteItems(transactWriteItemsRequest);
                        } finally {
                            deleteKey.clear();
                        }
                    } finally {
                        conditionExpressionAttributeValues.clear();
                    }
                } finally {
                    conditionExpressionAttributeNames.clear();
                }
            } finally {
                conditionKey.clear();
            }

            final ApiGatewayResponse response = new ApiGatewayResponse(200, "{ \"tableUpdated\": true }");
            return response;
        } catch (Exception e) {
            // TODO: distinguish client fault from server fault
            // TODO: presumably, a server fault will be retried => this exception corresponds to a client fault.
            final ApiGatewayResponse response = new ApiGatewayResponse(400, "{ \"tableUpdated\": false }");
            return response;
        }
    }
}
