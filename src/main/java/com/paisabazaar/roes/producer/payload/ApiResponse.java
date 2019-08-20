package com.paisabazaar.roes.producer.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@AllArgsConstructor
@Getter
@Setter
public class ApiResponse {
    private String status;
    private Integer code;
    private Map<String, Object> data;
    private String message;
}
