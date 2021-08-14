package com.phoosop.reactive.config;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import javax.net.ssl.SSLException;
import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

    private final BoredapiProperties boredapiProperties;

    @Bean
    public WebClient boredapiWebClient() throws SSLException {
        SslContext sslContext = SslContextBuilder
                .forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();

        ConnectionProvider connectionProvider = ConnectionProvider.builder("boredapi")
//                .maxConnections(500)
//                .pendingAcquireMaxCount(1000)
                .maxIdleTime(Duration.ofSeconds(20))
                .maxLifeTime(Duration.ofSeconds(60))
                .pendingAcquireTimeout(Duration.ofSeconds(60))
                .build();

        HttpClient httpClient = HttpClient.create(connectionProvider)
                .secure(sslSpec -> sslSpec.sslContext(sslContext));

        return WebClient.builder()
                .baseUrl(boredapiProperties.getHost())
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();

    }
}
