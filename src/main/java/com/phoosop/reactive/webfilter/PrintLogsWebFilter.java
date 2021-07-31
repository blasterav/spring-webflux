package com.phoosop.reactive.webfilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class PrintLogsWebFilter implements WebFilter {

    private final Logger LOG = LoggerFactory.getLogger(PrintLogsWebFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange serverWebExchange, WebFilterChain webFilterChain) {
        String path = serverWebExchange.getRequest().getURI().getPath();
        String method = serverWebExchange.getRequest().getMethod().name();
        long startTime = System.currentTimeMillis();

        LOG.info("Started {} {}", method, path);
        return webFilterChain.filter(serverWebExchange).doFinally(signalType -> {
            long totalTime = System.currentTimeMillis() - startTime;
            int statusCode = serverWebExchange.getResponse().getStatusCode().value();
            LOG.info("Finished {} {}, Status: {}, Duration: {}", method, path, statusCode, totalTime);
        });
    }

}
