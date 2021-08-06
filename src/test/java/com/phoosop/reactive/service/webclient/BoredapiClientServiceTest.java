package com.phoosop.reactive.service.webclient;

import com.phoosop.reactive.config.BoredapiProperties;
import com.phoosop.reactive.model.command.ActivityCommand;
import com.phoosop.reactive.model.external.ActivityExternal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.ConversionService;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class BoredapiClientServiceTest {

    @InjectMocks
    private BoredapiClientService boredapiClientService;

    @Mock
    private WebClient boredapiWebClient;

    @Mock
    private BoredapiProperties boredapiProperties;

    @Mock
    private ConversionService conversionService;

    @SuppressWarnings("rawtypes")
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpecMock;

    @SuppressWarnings("rawtypes")
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock;

    @Mock
    private WebClient.ResponseSpec responseSpecMock;

    @BeforeEach
    void setUpBeforeEach() {
        BoredapiProperties.Path path = new BoredapiProperties.Path();
        path.setGetActivity("activityUrl");
        Mockito.when(boredapiProperties.getPath()).thenReturn(path);
    }

    @Test
    public void testGetActivity() {

        ActivityExternal response = new ActivityExternal()
                .setActivity("activity")
                .setAccessibility(1D)
                .setKey("key")
                .setLink("link")
                .setPrice(100)
                .setType("type")
                .setParticipants(3);

        ActivityCommand returnConvert = new ActivityCommand()
                .setActivity("activity")
                .setAccessibility(1D)
                .setKey("key")
                .setLink("link")
                .setPrice(100)
                .setType("type")
                .setParticipants(3);

        Mockito.when(boredapiWebClient.get()).thenReturn(requestHeadersUriSpecMock);
        Mockito.when(requestHeadersUriSpecMock.uri(Mockito.anyString())).thenReturn(requestHeadersSpecMock);
        Mockito.when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        Mockito.when(responseSpecMock.bodyToMono(
                ArgumentMatchers.<Class<ActivityExternal>>notNull())).thenReturn(Mono.just(response));

        Mockito.when(conversionService.convert(response, ActivityCommand.class)).thenReturn(returnConvert);

        Mono<ActivityCommand> activityCommandMono = boredapiClientService.getActivity();

        StepVerifier.create(activityCommandMono)
                .expectNextMatches(activityCommand -> activityCommand.getActivity().equals("activity")
                        && activityCommand.getAccessibility().equals(1D)
                        && activityCommand.getKey().equals("key")
                        && activityCommand.getLink().equals("link")
                        && activityCommand.getPrice().equals(100)
                        && activityCommand.getType().equals("type")
                        && activityCommand.getParticipants().equals(3))
                .verifyComplete();

        Mockito.verify(conversionService, Mockito.times(1)).convert(response, ActivityCommand.class);
        Mockito.verify(boredapiWebClient, Mockito.times(1)).get();
        Mockito.verify(requestHeadersUriSpecMock, Mockito.times(1)).uri(Mockito.anyString());
        Mockito.verify(requestHeadersSpecMock, Mockito.times(1)).retrieve();
        Mockito.verify(responseSpecMock, Mockito.times(1)).bodyToMono(ArgumentMatchers.<Class<ActivityExternal>>notNull());
    }

}