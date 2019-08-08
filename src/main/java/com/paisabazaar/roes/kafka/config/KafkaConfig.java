package com.paisabazaar.roes.kafka.config;

import lombok.extern.log4j.Log4j2;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Log4j2
public class KafkaConfig {
    @Value(value = "${kafka.bootstrapAddress}")
    private String bootstrapAddress;

    @Value(value = "${kafka.clientId}")
    private String clientId;

    @Value(value = "${kafka.acks}")
    private String acks;

    @Value(value = "${kafka.retries}")
    private String retries;

    @Value(value = "${kafka.batchSize}")
    private String batchSize;

    @Value(value = "${kafka.lingerMs}")
    private String lingerMs;

    @Value(value = "${kafka.bufferMemory}")
    private String bufferMemory;

    @Value(value = "${kafka.compressionType}")
    private String compressionType;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        // bootstrap.servers
        configProps.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapAddress);
        // producer key serializer
        configProps.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
        // produer value serializer
        configProps.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
        // clientId
        configProps.put(ProducerConfig.CLIENT_ID_CONFIG, clientId);
        // acks
        configProps.put(ProducerConfig.ACKS_CONFIG, acks);
        // retries
        configProps.put(ProducerConfig.RETRIES_CONFIG, retries);
        // batch.size
        configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, batchSize);
        // linger.ms
        configProps.put(ProducerConfig.LINGER_MS_CONFIG, lingerMs);
        // buffer.memory
        configProps.put(ProducerConfig.BUFFER_MEMORY_CONFIG, bufferMemory);
        // exactly once
        configProps.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, compressionType);

        log.debug("Kafka Configuration --- " + new JSONObject(configProps).toString(4));

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

}

