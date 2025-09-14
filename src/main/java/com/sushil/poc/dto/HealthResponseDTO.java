package com.sushil.poc.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response DTO for health endpoint")
public class HealthResponseDTO {
    @Schema(description = "Health status", example = "UP")
    private String status;
    @Schema(description = "Details about the health status", example = "All systems operational.")
    private String details;

    public HealthResponseDTO(String status, String details) {
        this.status = status;
        this.details = details;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
