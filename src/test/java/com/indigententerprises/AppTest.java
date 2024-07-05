package com.indigententerprises;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;

import com.indigententerprises.dto.ApiGatewayResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AppTest {

    @Test
    public void handleRequest_shouldReturnConstantValue() {
        final App function = new App();
        final Map<String, String> pathParameters = new HashMap<>();
        pathParameters.put("orgId", UUID.randomUUID().toString());
        final APIGatewayProxyRequestEvent.RequestIdentity requestIdentity =
                new APIGatewayProxyRequestEvent.RequestIdentity().withUser("jonniesavell");
        final APIGatewayProxyRequestEvent.ProxyRequestContext proxyRequestContext =
                new APIGatewayProxyRequestEvent.ProxyRequestContext().withIdentity(requestIdentity);
        final APIGatewayProxyRequestEvent apiGatewayEvent = new APIGatewayProxyRequestEvent();
        apiGatewayEvent.withPathParameters(pathParameters)
                       .withRequestContext(proxyRequestContext);

        // this lambda does not consult the context
        final ApiGatewayResponse result = function.handleRequest(apiGatewayEvent, null);
        assertEquals(400, result.getStatusCode());
    }
}
