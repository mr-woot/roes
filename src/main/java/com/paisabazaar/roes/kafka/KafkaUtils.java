package com.paisabazaar.roes.kafka;

import java.util.List;
import java.util.Map;

/**
 * Project: producer
 * Contributed By: Tushar Mudgal
 * On: 2019-08-08 | 10:16
 */
public interface KafkaUtils {

    /**
     * This function produces messages to kafka
     * @param payload               Array of messages to be produced into kafka
     * @param key (Optional)        Used to send messages to the same partition based on hash key
     * @param partition (Optional)  Used to send messages to the same partition based on partition number
     * @param metadata (Optional)   Used for validating payload messages with the schema. // ## TODO
     * @return String[]             Array of Message Ids produced to kafka
     */
    List<String> produceMessages(List<Map<String, Object>> payload, String topic, Integer partition, String key, String metadata);

    /**
     * This function produces messages to kafka
     * @param payload               A single message (Map) to be produced into kafka
     * @param key (Optional)        Used to send messages to the same partition based on hash key
     * @param partition (Optional)  Used to send messages to the same partition based on partition number
     * @param metadata (Optional)   Used for validating payload messages with the schema. // ## TODO
     * @return String               Message Id produced to kafka
     */
    String produceMessages(Map<String, Object> payload, String topic, Integer partition, String key, String metadata);
}
