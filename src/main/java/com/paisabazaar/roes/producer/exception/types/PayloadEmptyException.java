package com.paisabazaar.roes.producer.exception.types;

/**
 * Project: producer
 * Contributed By: Tushar Mudgal
 * On: 2019-08-07 | 20:52
 */
public class PayloadEmptyException extends RuntimeException {
    public PayloadEmptyException(String message) {
        super(message);
    }
}
