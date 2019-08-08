package com.paisabazaar.roes.kafka.impl;

import com.paisabazaar.roes.kafka.KafkaUtils;

import java.util.List;
import java.util.Map;

/**
 * Project: producer
 * Contributed By: Tushar Mudgal
 * On: 2019-08-08 | 10:16
 */
public class IKafkaUtils implements KafkaUtils {
    @Override
    public String[] produceMessages(List<Map<String, Object>> payload, String key, int partition, String metadata) {
        return new String[0];
    }

    @Override
    public String produceMessages(Map<String, Object> payload, String key, int partition, String metadata) {
        return null;
    }
}
