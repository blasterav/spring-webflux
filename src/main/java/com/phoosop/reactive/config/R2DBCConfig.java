package com.phoosop.reactive.config;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.r2dbc.ConnectionFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;

import static io.r2dbc.spi.ConnectionFactoryOptions.*;
import static java.time.Duration.ofSeconds;

@Getter
@Setter
@Configuration
@RequiredArgsConstructor
public class R2DBCConfig extends AbstractR2dbcConfiguration {
    
    private final R2DBCProperties r2DBCProperties;

    @Override
    @Bean
    public ConnectionFactory connectionFactory() {
        ConnectionFactory connectionFactory = ConnectionFactoryBuilder.withOptions(ConnectionFactoryOptions.builder()
                .option(DRIVER, "mysql")
                .option(HOST, r2DBCProperties.getHostname())
                .option(PORT, r2DBCProperties.getPort())
                .option(USER, r2DBCProperties.getUsername())
                .option(PASSWORD, r2DBCProperties.getPassword())
                .option(DATABASE, r2DBCProperties.getDatabase())
                .option(CONNECT_TIMEOUT, ofSeconds(r2DBCProperties.getConnectionTimeout()))
                .option(SSL, false)).build();

        ConnectionPoolConfiguration configuration = ConnectionPoolConfiguration
                .builder(connectionFactory)
                .maxAcquireTime(ofSeconds(r2DBCProperties.getPool().getMaxAcquireTime()))
                .maxCreateConnectionTime(ofSeconds(r2DBCProperties.getPool().getMaxCreateConnectionTime()))
                .maxIdleTime(ofSeconds(r2DBCProperties.getPool().getMaxIdleTime()))
                .maxLifeTime(ofSeconds(r2DBCProperties.getPool().getMaxLifeTime()))
                .initialSize(r2DBCProperties.getPool().getInitialSize())
                .maxSize(r2DBCProperties.getPool().getMaxSize())
                .build();

        return new ConnectionPool(configuration);
    }

}
