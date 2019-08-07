package com.paisabazaar.roes.producer.cache;

import com.google.gson.Gson;
import com.paisabazaar.roes.producer.domain.Producer;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Project: producer
 * Contributed By: Tushar Mudgal
 * On: 2019-08-07 | 21:08
 */
@Component
@Log4j2
public class ProducerCache {
    private ProducerCache producerCache;
    private static Map<String, Object> producerIdsMap;

    private static HikariDataSource dataSource;

    @PostConstruct
    public void init() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.query("SELECT * from producer", (resultSet, i) -> {
            Producer p = new Producer();
            p.setId(resultSet.getLong("id"));
            p.setProducerId(resultSet.getString("producer_id"));
            p.setBuName(resultSet.getString("bu_name"));
            p.setType(resultSet.getString("type"));
            p.setTopic(resultSet.getString("topic"));
            p.setPurpose(resultSet.getString("purpose"));
            p.setMetadata(resultSet.getString("metadata"));
            p.setCreatedAt(resultSet.getLong("created_at"));
            p.setUpdatedAt(resultSet.getLong("updated_at"));
            producerIdsMap.put(p.getProducerId(), p);
            return null;
        });
        log.info("ProducersMap=" + new Gson().toJson(producerIdsMap));
    }

    public ProducerCache(HikariDataSource dataSource) throws IllegalAccessException {
        if (producerIdsMap == null) {
            producerIdsMap = new LinkedHashMap<>();
            ProducerCache.dataSource = dataSource;
        } else {
            throw new IllegalAccessException("Producer cache already initialized before");
        }
    }

    public static Map<String, Object> getProducerIdsMap() {
        return producerIdsMap;
    }
}
