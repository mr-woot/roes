package com.paisabazaar.roes.producer.repository;

import com.paisabazaar.roes.producer.domain.Producer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Project: producer
 * Contributed By: Tushar Mudgal
 * On: 2019-08-07 | 17:36
 */
@Repository
public interface ProducerRepository extends JpaRepository<Producer, Long>, CustomProducerRepository {
}
