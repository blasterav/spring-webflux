package com.phoosop.reactive.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

    private final BoredapiProperties boredapiProperties;

    @Bean
    public WebClient boredapiWebClient() {
        return WebClient.create(boredapiProperties.getHost());

    }
}
