package com.example.springaiworkshop.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.client.RestClientBuilderConfigurer;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

// https://github.com/spring-projects/spring-ai/discussions/450#discussioncomment-8813608
@Configuration
public class RestClientBuilderConfig {

    @Bean
    @ConditionalOnProperty(name="logging-rest-client.enabled", havingValue = "true")
    RestClient.Builder loggingRestClientBuilder(RestClientBuilderConfigurer restClientBuilderConfigurer) {
        RestClient.Builder builder = RestClient.builder()
            .requestFactory(ClientHttpRequestFactoryBuilder.simple().build())
            .requestInterceptor(new RestClientInterceptor());
        return restClientBuilderConfigurer.configure(builder);
    }
}
