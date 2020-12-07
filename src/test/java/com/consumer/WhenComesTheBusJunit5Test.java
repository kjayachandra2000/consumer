package com.consumer;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.DslPart;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import io.pactfoundation.consumer.dsl.LambdaDsl;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "BusService")
public class WhenComesTheBusJunit5Test {

    @BeforeEach
    public void setUp(MockServer mockServer) {
        assertThat(mockServer, CoreMatchers.is(notNullValue()));
    }

    @Test
    @PactTestFor(pactMethod = "createPact")
    void validateFromProvider(MockServer mockServer) throws IOException {
        HttpResponse httpResponse = Request.Get(mockServer.getUrl() + "/bus/Hammersmith/613")
                .execute()
                .returnResponse();
        assertEquals(httpResponse.getStatusLine()
                .getStatusCode(), 200);
    }

    @Test
    @PactTestFor(pactMethod = "createPactWithNoNode")
    void validateFromProvider2(MockServer mockServer) throws IOException {
        HttpResponse httpResponse = Request.Get(mockServer.getUrl() + "/bus/Hammersmith/613")
                .execute()
                .returnResponse();
        assertEquals(httpResponse.getStatusLine()
                .getStatusCode(), 200);
    }

    @Pact(consumer = "BusServiceNewClient")
    public RequestResponsePact createPactWithNoNode(PactDslWithProvider builder) {
        DslPart body = LambdaDsl.newJsonBody((object) -> {
            object.stringType("station", "Hammersmith")
                    .stringType("nr", "613");
        })
                .build();

        return create("With missing node in the response body",
                body,
                builder);
    }

    @Pact(consumer = "BusServiceNewClient")
    public RequestResponsePact createPact(PactDslWithProvider builder) {

//        DslPart body = new PactDslJsonBody()
//                .stringType("station", "Hammersmith")
//                .stringType("nr", "613")
//                .asBody();

        DslPart body = LambdaDsl.newJsonBody((object) -> {
            object.stringType("station", "Hammersmith")
                    .stringType("nr", "613")
                    .stringType("eta", "6");
        }).build();

        return create("There is a bus with number 613 arriving to Hammersmith bus station",
                body,
                builder);
    }

    private RequestResponsePact create(String state, DslPart body, PactDslWithProvider builder) {
        Map<String, String> headers = new HashMap();
        headers.put("Content-Type", "application/json");
        return builder
                .given(state)
                .uponReceiving(state)
                .path("/bus/Hammersmith/613")
                .method("GET")
                .willRespondWith()
                .status(200)
                .headers(headers)
                .body(body)
                .toPact();
    }
}
