package com.paisabazaar.roes.producer.exception.types;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Project: producer
 * Contributed By: Tushar Mudgal
 * On: 2019-08-07 | 18:02
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class ResourceAlreadyExistsException extends RuntimeException {
    public ResourceAlreadyExistsException(String exception) {
        super(exception);
    }
}
