package com.paisabazaar.roes.producer.cache;

import com.google.gson.Gson;
import com.paisabazaar.roes.producer.domain.Producer;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.log4j.Log4j2;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Project: producer
 * Contributed By: Tushar Mudgal
 * On: 2019-08-07 | 21:08
 */
@Log4j2
public class ProducerCache {
    private static ProducerCache producerCache;
    private static Map<String, Object> producerIdsMap;

    private ProducerCache(HikariDataSource dataSource) {
        producerIdsMap = new LinkedHashMap<>();
        this.init(dataSource);
    }

    static ProducerCache getInstance(HikariDataSource dataSource) {
        if (producerCache == null) {
            producerCache = new ProducerCache(dataSource);
        }
        return producerCache;
    }

    private void init(HikariDataSource dataSource) {
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

    public Map<String, Object> getProducerIdsMap() {
        return producerIdsMap;
    }
}
