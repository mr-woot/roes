package com.paisabazaar.roes.producer.cache;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Project: producer
 * Contributed By: Tushar Mudgal
 * On: 2019-08-08 | 00:26
 */
@Configuration
public class CacheConfig {

    private final HikariDataSource dataSource;

    public CacheConfig(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    public ProducerCache getProducerCache() {
        return ProducerCache.getInstance(dataSource);
    }
}
