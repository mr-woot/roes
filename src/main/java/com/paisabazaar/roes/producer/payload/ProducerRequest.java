package com.paisabazaar.roes.producer.payload;

import javax.validation.constraints.NotNull;

/**
 * Project: producer
 * Contributed By: Tushar Mudgal
 * On: 2019-08-07 | 20:19
 */
public class ProducerRequest {

    @NotNull(message = "buName cannot be empty")
    private String buName;

    @NotNull(message = "topic cannot be empty")
    private String topic;

    @NotNull(message = "type cannot be empty")
    private String type;

    @NotNull(message = "purpose cannot be empty")
    private String purpose;

    @NotNull(message = "metadata cannot be empty")
    private String metadata;

    public ProducerRequest() {
    }

    public ProducerRequest(@NotNull(message = "buName cannot be empty") String buName,
                           @NotNull(message = "topic cannot be empty") String topic,
                           @NotNull(message = "type cannot be empty") String type,
                           @NotNull(message = "purpose cannot be empty") String purpose,
                           @NotNull(message = "metadata cannot be empty") String metadata) {
        this.buName = buName;
        this.topic = topic;
        this.type = type;
        this.purpose = purpose;
        this.metadata = metadata;
    }

    public String getBuName() {
        return buName;
    }

    public void setBuName(String buName) {
        this.buName = buName;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
}
