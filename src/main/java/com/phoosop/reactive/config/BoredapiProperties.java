package com.phoosop.reactive.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

@Getter
@Setter
@Validated
@Configuration
@ConfigurationProperties(prefix = "boredapi")
public class BoredapiProperties {

    @NotEmpty
    private String host;

    @Valid
    private Path path;

    @Getter
    @Setter
    public static class Path {

        @NotEmpty
        private String getActivity;

    }

}
