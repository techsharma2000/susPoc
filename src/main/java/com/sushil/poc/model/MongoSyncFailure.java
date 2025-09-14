package com.sushil.poc.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mongo_sync_failures")
public class MongoSyncFailure {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tradeId;

    @Column(nullable = false)
    private int version;

    @Column(nullable = false)
    private LocalDateTime failureTime;

    @Column(nullable = false, length = 1024)
    private String reason;

    public MongoSyncFailure() {
    }

    public MongoSyncFailure(String tradeId, int version, LocalDateTime failureTime, String reason) {
        this.tradeId = tradeId;
        this.version = version;
        this.failureTime = failureTime;
        this.reason = reason;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public LocalDateTime getFailureTime() {
        return failureTime;
    }

    public void setFailureTime(LocalDateTime failureTime) {
        this.failureTime = failureTime;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
