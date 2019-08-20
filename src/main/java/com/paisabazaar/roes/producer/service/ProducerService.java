package com.paisabazaar.roes.producer.service;

import com.google.gson.Gson;
import com.paisabazaar.roes.kafka.KafkaUtils;
import com.paisabazaar.roes.producer.cache.CacheConfig;
import com.paisabazaar.roes.producer.domain.Producer;
import com.paisabazaar.roes.producer.domain.Response;
import com.paisabazaar.roes.producer.exception.types.InvalidStateException;
import com.paisabazaar.roes.producer.exception.types.PayloadEmptyException;
import com.paisabazaar.roes.producer.exception.types.ResourceAlreadyExistsException;
import com.paisabazaar.roes.producer.exception.types.ResourceNotFoundException;
import com.paisabazaar.roes.producer.payload.ProducerRequest;
import com.paisabazaar.roes.producer.repository.ProducerRepository;
import com.paisabazaar.roes.producer.utils.ResponseCode;
import lombok.extern.log4j.Log4j2;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Project: producer
 * Contributed By: Tushar Mudgal
 * On: 2019-08-07 | 17:58
 */
@Service
@Log4j2
public class ProducerService {
    private final ApplicationUtils applicationUtilsService;
    private final KafkaUtils kafkaUtils;
    private final CacheConfig cacheConfig;
    private final ProducerRepository producerRepository;

    public ProducerService(ApplicationUtils applicationUtilsService, ProducerRepository producerRepository, CacheConfig cacheConfig, KafkaUtils kafkaUtils) {
        this.applicationUtilsService = applicationUtilsService;
        this.producerRepository = producerRepository;
        this.cacheConfig = cacheConfig;
        this.kafkaUtils = kafkaUtils;
    }

    public ResponseEntity getProducer(String producerId) {
        if (producerId == null || Objects.equals(producerId, "")) {
            return ResponseEntity.status(HttpStatus.OK).body(producerRepository.findAll());
        } else {
            Optional<Producer> producer = producerRepository.findByProducerId(producerId);
            if (producer.isPresent()) {
                return ResponseEntity.status(HttpStatus.OK).body(producer);
            } else {
                throw new ResourceNotFoundException("Producer id: " + producerId);
            }
        }
    }

    public ResponseEntity createProducer(ProducerRequest producerRequest) {
        // Check if topic matches the regex
        if (!applicationUtilsService.isValidTopic(producerRequest.getTopic())) {
            throw new InvalidStateException("Topic format");
        }
        // Check if buName.type equals com.paisabazaar.[buName].[type]
        if (!applicationUtilsService.isValidBuNameTypeToTopic(producerRequest)) {
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
            List<String> messageIds = null;
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
            String message = null;
            if (messageIds != null) {
                message = messageIds.size() > 1 ? "Messages produced" : "Message produced";
                Response response = new Response("success", HttpStatus.OK.value(), messageIds, message);
                log.info("producerId: " + producerId + " | " + response);
                return ResponseEntity.status(200).body(new Gson().toJson(response));
            } else {
                log.error(producerId + " | " + "MessageIds empty - [429]");
                return ResponseEntity.status(429).body(null);
            }
        }
    }

    public ResponseEntity updateProducer(String producerId, Producer payload) {
        // Update producer
        if (!producerRepository.existsByProducerId(producerId)) {
            Response response = new Response(
                    "error",
                    ResponseCode.PRODUCER_NOT_RETRIEVED.getCode(),
                    ResponseCode.PRODUCER_NOT_RETRIEVED.getMessage()
            );
            log.error("Producer not found with id: " + producerId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Gson().toJson(response));
        } else {
            Optional<Producer> optional = producerRepository.findByProducerId(producerId);
            if (optional.isPresent()) {
                Producer producer = optional.get();
                applicationUtilsService.copyNonNullProperties(payload, producer);
                producerRepository.save(producer);
                log.info("Producer updated with id: " + producer.getProducerId());
                // Update in producerIdsMap
                Map<String, Producer> cacheMap = cacheConfig.getProducerCache().getProducerIdsMap();
                cacheMap.put(producerId, producer);
                log.info("ProducersMap=" + new JSONObject(cacheMap).toString(4));
            } else {
                throw new ResourceNotFoundException("Producer id: " + producerId);
            }
            Response response = new Response(
                    "success",
                    HttpStatus.CREATED.value(),
                    producerId,
                    "Producer updated for id: " + producerId
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(new Gson().toJson(response));
        }
    }

    public ResponseEntity deleteProducer(String producerId) {
        // If producerId exists
        if (producerRepository.existsByProducerId(producerId)) {
            // delete producer by producerId
            producerRepository.deleteByProducerId(producerId);
            // Delete in producerIdsMap too
            cacheConfig.getProducerCache().getProducerIdsMap().remove(producerId);
            log.info("Producer deleted with id: " + producerId);
            log.info("ProducersMap=" + cacheConfig.getProducerCache().getProducerIdsMap());
        } else if (cacheConfig.getProducerCache().getProducerIdsMap().containsKey(producerId)) {
            // if it not exists in mysql but exists in cache
            // delete producer mapping from cache
            cacheConfig.getProducerCache().getProducerIdsMap().remove(producerId);
            log.info("Invalid mapping of producerId: " + producerId +
                    " exists only in application cache. Deleting it.");
            log.info("ProducersMap=" + new JSONObject(cacheConfig.getProducerCache().getProducerIdsMap()).toString(4));
        } else {
            log.error("Producer not found with id: " + producerId);
            Response response = new Response(
                    "error",
                    ResponseCode.PRODUCER_NOT_RETRIEVED.getCode(),
                    ResponseCode.PRODUCER_NOT_RETRIEVED.getMessage()
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Gson().toJson(response));
        }
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }
}
