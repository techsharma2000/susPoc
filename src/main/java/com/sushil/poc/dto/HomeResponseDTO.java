package com.sushil.poc.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response DTO for home endpoint status message")
public class HomeResponseDTO {
    @Schema(description = "Status message", example = "Sushil_POC Spring Boot App is running!")
    private String message;

    public HomeResponseDTO(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
