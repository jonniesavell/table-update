package com.indigententerprises.dto;

public class ApiGatewayResponse {
    private final int statusCode;
    private final String body;

    public ApiGatewayResponse(
            final int statusCode,
            final String body
    ) {
        this.statusCode = statusCode;
        this.body = body;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getBody() {
        return body;
    }
}
