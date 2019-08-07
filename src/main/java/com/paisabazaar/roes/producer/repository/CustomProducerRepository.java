package com.paisabazaar.roes.producer.repository;

import com.paisabazaar.roes.producer.domain.Producer;

import java.util.List;
import java.util.Optional;

/**
 * Project: producer
 * Contributed By: Tushar Mudgal
 * On: 2019-08-07 | 17:37
 */
public interface CustomProducerRepository {

    /**
     * This function finds Producer by given producerId
     * @param producerId Producer id (uuid)
     * @return Producer
     */
    Optional<Producer> findByProducerId(String producerId);

    /**
     * This function finds Producer list by given buName
     * @param buName BuName
     * @return Producer[]
     */
    Optional<List<Producer>> findAllByBuName(String buName);

    /**
     * This function finds Producer list by given topic
     * @param topic Topic
     * @return Producer[]
     */
    Optional<List<Producer>> findAllByTopic(String topic);

    /**
     * This function checks if te given Producer id exists
     * @param producerId Producer id
     * @return boolean
     */
    boolean existsByProducerId(String producerId);
}
