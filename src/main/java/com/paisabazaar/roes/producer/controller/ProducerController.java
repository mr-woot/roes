package com.paisabazaar.roes.producer.controller;

import com.paisabazaar.roes.producer.domain.Producer;
import com.paisabazaar.roes.producer.payload.ProducerRequest;
import com.paisabazaar.roes.producer.service.ProducerService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * Project: producer
 * Contributed By: Tushar Mudgal
 * On: 2019-08-07 | 17:43
 */
@RestController
@RequestMapping("/PB_DATAPIPE_PRODUCER")
@Log4j2
public class ProducerController {

    private final ProducerService producerService;

    public ProducerController(ProducerService producerService) {
        this.producerService = producerService;
    }

    @GetMapping(value = "/producer", produces = "application/json")
    public ResponseEntity<?> getProducer(@Valid @RequestParam String producerId) {
        log.info("test info log .");
        return producerService.getProducer(producerId);
    }

    @PostMapping(value = "/producer", produces = "application/json")
    public ResponseEntity<?> createProducer(@Valid @RequestBody ProducerRequest producer) {
        return producerService.createProducer(new Producer(
                producer.getBuName(),
                producer.getType(),
                producer.getTopic(),
                producer.getPurpose(),
                producer.getMetadata())
        );
    }

    @PostMapping(value = "/produce_messages", produces = "application/json")
    public ResponseEntity<?> createProducer(@Valid @RequestBody List<Map<String, Object>> payload, @RequestParam String producerId) {
        return producerService.produceMessages(producerId, payload);
    }
}
