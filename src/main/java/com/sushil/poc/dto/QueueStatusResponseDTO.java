package com.sushil.poc.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response DTO for queue status")
public class QueueStatusResponseDTO {
    @Schema(description = "Current queue size", example = "5")
    private int queueSize;

    public QueueStatusResponseDTO(int queueSize) {
        this.queueSize = queueSize;
    }

    public int getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }
}
