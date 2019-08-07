package com.paisabazaar.roes.producer.service;

import com.paisabazaar.roes.producer.exception.types.PayloadEmptyException;
import com.paisabazaar.roes.producer.exception.types.ResourceAlreadyExistsException;
import com.paisabazaar.roes.producer.exception.types.ResourceNotFoundException;
import com.paisabazaar.roes.producer.domain.Producer;
import com.paisabazaar.roes.producer.repository.ProducerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Project: producer
 * Contributed By: Tushar Mudgal
 * On: 2019-08-07 | 17:58
 */
@Service
public class ProducerService {

    private final ProducerRepository producerRepository;

    public ProducerService(ProducerRepository producerRepository) {
        this.producerRepository = producerRepository;
    }

    public ResponseEntity getProducer(String producerId) {
        Optional<Producer> producer = producerRepository.findByProducerId(producerId);
        if (producer.isPresent()) {
            return ResponseEntity.status(HttpStatus.OK).body(producer);
        } else {
            throw new ResourceNotFoundException("Producer id: " + producerId);
        }
    }

    public ResponseEntity createProducer(Producer producer) {
        if (producerRepository.existsByProducerId(producer.getProducerId())) {
            throw new ResourceAlreadyExistsException("Producer id: " + producer.getProducerId());
        } else {
            producer.setProducerId(UUID.randomUUID().toString());
            Producer savedProducer = producerRepository.save(producer);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedProducer);
        }
    }

    public ResponseEntity produceMessages(Producer producer, List<Map<String, Object>> payload) {
        if (payload.size() == 0) {
            throw new PayloadEmptyException("Expect payload to be array of messages");
        } else {
            /*
                Check if producer exists in in-memory, if not then check in mysql, if it exists in mysql,
                fetch it and insert into in-memory and then produce to kafka.
                @method KafkaUtils.produceBatchMessages(payload, key, partition, metadata);
                @return ProducedPayload
             */
            return ResponseEntity.status(200).body(producer);
        }
    }

}
