package com.phoosop.reactive.service.webclient;

import com.phoosop.reactive.config.BoredapiProperties;
import com.phoosop.reactive.model.command.ActivityCommand;
import com.phoosop.reactive.model.external.ActivityExternal;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class BoredapiClientService {

    private final WebClient boredapiWebClient;
    private final BoredapiProperties boredapiProperties;
    private final ConversionService conversionService;

    public Mono<ActivityCommand> getActivity() {
        return boredapiWebClient.get()
                .uri(boredapiProperties.getPath().getGetActivity())
//                .headers(headers -> headers.setBasicAuth("user", "userpwd"))
                .retrieve()
                .bodyToMono(ActivityExternal.class)
                .map(item -> conversionService.convert(item, ActivityCommand.class));
    }

}
