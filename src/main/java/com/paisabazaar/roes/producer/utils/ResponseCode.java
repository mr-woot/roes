package com.paisabazaar.roes.producer.utils;

public enum ResponseCode {
    PRODUCER_NOT_RETRIEVED(3001, "Producer not retrieved"),
    PRODUCER_ID_IS_REQUIRED(3002, "Producer id is required"),
    MESSAGES_NOT_PRODUCED(3201, "Messages not retrieved"),

    INVALID_MESSAGES_FORMAT(1002, "Invalid messages format"),
    INVALID_MESSAGES(1003, "Invalid messages"),
    INVALID_PARTITION(1007, "Invalid partition"),
    BUNAME_TYPE_DUPLICATE(1201, "BuName & type (buName.type) already exists"),

    KAFKA_BROKER_UNREACHABLE(8000, "Kafka Broker unreachable"),
    REDIS_UNREACHABLE(8001, "Redis unreachable")
    ;
    private int code;
    private String message;
    private String text;

    ResponseCode(int code, String message) {
        this.code = code;
        this.message = message;
        this.text = Integer.toString(code);
    }

    /**
     * Gets the HTTP status code
     *
     * @return the status code number
     */
    public int getCode() {
        return code;
    }

    /**
     * Gets the HTTP status code as a text string
     *
     * @return the status code as a text string
     */
    public String asText() {
        return text;
    }

    /**
     * Get the messageription
     *
     * @return the messageription of the status code
     */
    public String getMessage() {
        return message;
    }
}