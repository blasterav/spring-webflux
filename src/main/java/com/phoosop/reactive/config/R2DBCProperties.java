package com.phoosop.reactive.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Validated
@Configuration
@ConfigurationProperties(prefix = "r2dbc")
public class R2DBCProperties {

    @NotEmpty
    private String hostname;

    @NotEmpty
    private String database;

    @NotNull
    private Integer port;

    @NotEmpty
    private String username;

    @NotEmpty
    private String password;

    @NotNull
    private Integer connectionTimeout;

    @Valid
    private Pool pool;

    @Getter
    @Setter
    public static class Pool {

        @NotNull
        private Integer initialSize;

        @NotNull
        private Integer maxSize;

        @NotNull
        private Integer maxIdleTime;

        @NotNull
        private Integer maxAcquireTime;

        @NotNull
        private Integer maxCreateConnectionTime;

        @NotNull
        private Integer maxLifeTime;

    }

}
