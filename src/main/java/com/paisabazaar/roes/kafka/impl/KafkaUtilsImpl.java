package com.paisabazaar.roes.kafka.impl;

import com.paisabazaar.roes.kafka.KafkaUtils;
import org.json.JSONObject;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Project: producer
 * Contributed By: Tushar Mudgal
 * On: 2019-08-08 | 10:16
 */
@Service
public class KafkaUtilsImpl implements KafkaUtils {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaUtilsImpl(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    private String sendMessage(String topicName, Integer partition, String key, Map<String, Object> message) {
        String messageId = UUID.randomUUID().toString();
        JSONObject obj = new JSONObject();
        obj.put("MessageID", messageId);
        obj.put("Payload", message);
        kafkaTemplate.send(topicName, partition, key, String.valueOf(obj));
        return messageId;
    }

    @Override
    public List<String> produceMessages(List<Map<String, Object>> payload, String topic, Integer partition, String key, String metadata) {
        List<String> messagesArr = new ArrayList<>();
        for (Map<String, Object> stringObjectMap : payload) {
            // ## Validate message with metadata
            if (partition != null) {
                messagesArr.add(this.sendMessage(topic, partition, null, stringObjectMap));
            } else if (key != null && !Objects.equals(key, "")) {
                messagesArr.add(this.sendMessage(topic, null, key, stringObjectMap));
            } else {
                messagesArr.add(this.sendMessage(topic, null, null, stringObjectMap));
            }
        }
        return messagesArr;
    }

    @Override
    public String produceMessages(Map<String, Object> payload, String topic, Integer partition, String key, String metadata) {
        // ## Validate message with metadata
        if (partition != null) {
            return this.sendMessage(topic, partition, null, payload);
        } else if (key != null && !Objects.equals(key, "")) {
            return this.sendMessage(topic, null, key, payload);
        } else {
            return this.sendMessage(topic, null, null, payload);
        }
    }
}
