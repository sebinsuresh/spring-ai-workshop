package com.example.springaiworkshop.config;

import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.util.StreamUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

// https://github.com/spring-projects/spring-ai/discussions/450#discussioncomment-8813608
public class RestClientInterceptor implements ClientHttpRequestInterceptor {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLUE = "\u001B[34m";

    @Override
    @NonNull
    public ClientHttpResponse intercept(
        @NonNull HttpRequest request,
        @NonNull byte[] body,
        @NonNull ClientHttpRequestExecution execution
    ) throws IOException {
        logRequest(request, body);
        try (ClientHttpResponse response = execution.execute(request, body)) {
            var responseWrapper = new BufferingClientHttpResponseWrapper(response);
            logResponse(responseWrapper);
            return responseWrapper;
        }
    }

    static void logRequest(HttpRequest request, byte[] bytes) {
        System.out.println("Request: " + request.getMethod() + " " + request.getURI());

        // Headers contain sensitive info:
        // System.out.println("Headers: " + request.getHeaders());

        System.out.println("Body: " +
            ANSI_BLUE +
            new String(bytes) +
            ANSI_RESET);
    }

    static void logResponse(ClientHttpResponse response) throws IOException {
        System.out.println("Response: " + response.getStatusCode() + " " + response.getStatusText());

        // Headers contain sensitive info:
        // System.out.println("Headers: " + response.getHeaders());

        System.out.println("Body: " +
            ANSI_BLUE +
            IOUtils.toString(response.getBody(), StandardCharsets.UTF_8) +
            ANSI_RESET
        );
    }

    static class BufferingClientHttpResponseWrapper implements ClientHttpResponse {
        private final ClientHttpResponse response;
        byte[] body;

        BufferingClientHttpResponseWrapper(ClientHttpResponse response) {
            this.response = response;
        }

        @Override
        @NonNull
        public InputStream getBody() throws IOException {
            // TODO: Memory / perf implications?

            if (this.body == null) {
                this.body = StreamUtils.copyToByteArray(this.response.getBody());
            }
            return new ByteArrayInputStream(this.body);
        }

        @Override
        @NonNull
        public HttpStatusCode getStatusCode() throws IOException {
            return this.response.getStatusCode();
        }

        @Override
        @NonNull
        public String getStatusText() throws IOException {
            return this.response.getStatusText();
        }

        @Override
        public void close() {
            this.response.close();
        }

        @Override
        @NonNull
        public HttpHeaders getHeaders() {
            return this.response.getHeaders();
        }
    }
}
