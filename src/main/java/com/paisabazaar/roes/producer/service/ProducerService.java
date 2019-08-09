package com.paisabazaar.roes.producer.service;

import com.paisabazaar.roes.kafka.KafkaUtils;
import com.paisabazaar.roes.producer.cache.CacheConfig;
import com.paisabazaar.roes.producer.domain.Producer;
import com.paisabazaar.roes.producer.exception.types.InvalidStateException;
import com.paisabazaar.roes.producer.exception.types.PayloadEmptyException;
import com.paisabazaar.roes.producer.exception.types.ResourceAlreadyExistsException;
import com.paisabazaar.roes.producer.exception.types.ResourceNotFoundException;
import com.paisabazaar.roes.producer.payload.ProducerRequest;
import com.paisabazaar.roes.producer.repository.ProducerRepository;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Project: producer
 * Contributed By: Tushar Mudgal
 * On: 2019-08-07 | 17:58
 */
@Service
@Log4j2
public class ProducerService {

    private final KafkaUtils kafkaUtils;

    private final CacheConfig cacheConfig;

    private final ProducerRepository producerRepository;

    public ProducerService(ProducerRepository producerRepository, CacheConfig cacheConfig, KafkaUtils kafkaUtils) {
        this.producerRepository = producerRepository;
        this.cacheConfig = cacheConfig;
        this.kafkaUtils = kafkaUtils;
    }

    public ResponseEntity getProducer(String producerId) {
        Optional<Producer> producer = producerRepository.findByProducerId(producerId);
        if (producer.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(producer);
        } else {
            throw new ResourceNotFoundException("Producer id: " + producerId);
        }
    }

    private boolean isValidTopic(String s) {
//        return Pattern.matches("^com.paisabazaar.([a-zA-Z0-9-_]+).([a-zA-Z0-9-_]+)$", s);
        return s.matches("^com.paisabazaar.([a-zA-Z0-9-_]+).([a-zA-Z0-9-_]+)$");
    }

    private boolean isValidBuNameTypeToTopic(ProducerRequest producerRequest) {
        String buNameDotType = producerRequest.getBuName() + "." + producerRequest.getType();
        String tBuNameDotType = producerRequest.getTopic().substring(16);
        return tBuNameDotType.equals(buNameDotType);
    }

    public ResponseEntity createProducer(ProducerRequest producerRequest) {
        // Check if topic matches the regex
        if (!isValidTopic(producerRequest.getTopic())) {
            throw new InvalidStateException("Topic format");
        }
        // Check if buName.type equals com.paisabazaar.[buName].[type]
        if (!isValidBuNameTypeToTopic(producerRequest)) {
            throw new InvalidStateException("buName and type should match com.paisabazaar.(buName).(type). Format");
        }
        // Check if topic already exists
        if (producerRepository.existsByTopic(producerRequest.getTopic())) {
            throw new ResourceAlreadyExistsException("Topic: " + producerRequest.getTopic());
        }
        Producer producer = new Producer(
                producerRequest.getBuName(),
                producerRequest.getType(),
                producerRequest.getTopic(),
                producerRequest.getPurpose(),
                producerRequest.getMetadata()
        );
        Producer savedProducer = producerRepository.save(producer);
        // Update in ConcurrentMap
        Map<String, Producer> cacheMap = cacheConfig.getProducerCache().getProducerIdsMap();
        cacheMap.put(producer.getProducerId(), producer);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProducer);
    }

    public ResponseEntity produceMessages(String producerId, List<Map<String, Object>> payload, Integer partition, String key) {
        if (payload.size() == 0) {
            throw new PayloadEmptyException("Expect payload to be array of messages");
        } else {
            List<String> messageIds;
            Optional<Producer> optional;
            /*
                Check if producer exists in in-memory, if not then check in mysql, if it exists in mysql,
                fetch it and insert into in-memory and then produce to kafka.
                @method KafkaUtils.produceBatchMessages(payload, key, partition, metadata);
                @return ProducedPayload
             */
            Map<String, Producer> cacheMap = cacheConfig.getProducerCache().getProducerIdsMap();
            if (cacheMap.containsKey(producerId)) {
                // produce to kafka
                Producer p = cacheMap.get(producerId);
                messageIds = kafkaUtils.produceMessages(payload, p.getTopic(), partition, key, p.getMetadata());
                log.info("Produced to kafka");
            } else {
                // not present in cache, insert into cache config map from mysql
                optional = producerRepository.findByProducerId(producerId);
                if (optional.isPresent()) {
                    cacheMap.put(optional.get().getProducerId(), optional.get());
                    log.info(cacheConfig.getProducerCache().getProducerIdsMap());
                    // produce to kafka
                    Producer p = cacheMap.get(producerId);
                    messageIds = kafkaUtils.produceMessages(payload, p.getTopic(), partition, key, p.getMetadata());
                    log.info("Updated in memory and then produced to kafka");
                } else {
                    throw new ResourceNotFoundException("Producer id: " + producerId);
                }
            }
            // Response build
            JSONObject response = new JSONObject();
            response.put("success", HttpStatus.OK.value());
            response.put("data", messageIds);
            response.put("message", messageIds.size() > 1 ? "Messages produced" : "Message produced");
            log.info(response.put("producerId", producerId).toString(4));
            return ResponseEntity.status(200).body(response.toMap());
        }
    }

}
