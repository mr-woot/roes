package com.paisabazaar.roes.producer.domain;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

/**
 * Project: producer
 * Contributed By: Tushar Mudgal
 * On: 2019-08-07 | 17:27
 */
@Entity
@Table(name = "producer")
@NamedQuery(name = "Producer.findAll", query = "SELECT p from Producer p")
public class Producer implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    @Column(name = "producer_id", nullable = false)
    private String producerId;

    @Column(name = "bu_name", nullable = false)
    private String buName;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String topic;

    @Column(nullable = false)
    private String purpose;

    @Column
    private String metadata;

    @Column(name = "updated_at", nullable = false)
    private Long updatedAt;

    @Column(name = "created_at", nullable = false)
    private Long createdAt;

    public Producer() {
    }

    public Producer(String buName, String type, String topic, String purpose, String metadata) {
        super();
        this.producerId = UUID.randomUUID().toString();
        this.buName = buName;
        this.type = type;
        this.topic = topic;
        this.purpose = purpose;
        this.metadata = metadata;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now().toEpochMilli();
        this.updatedAt = Instant.now().toEpochMilli();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now().toEpochMilli();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProducerId() {
        return producerId;
    }

    public void setProducerId(String producerId) {
        this.producerId = producerId;
    }

    public String getBuName() {
        return buName;
    }

    public void setBuName(String buName) {
        this.buName = buName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
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

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }
}
