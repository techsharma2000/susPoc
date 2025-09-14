package com.sushil.poc.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import com.sushil.poc.dto.HomeResponseDTO;
import com.sushil.poc.dto.HealthResponseDTO;

@RestController
public class HomeController {
    /**
     * Home endpoint for health check and welcome message.
     * 
     * @return HomeResponseDTO with status message
     */
    @Operation(summary = "Home endpoint", description = "Returns a welcome message and health check status.", responses = {
            @ApiResponse(responseCode = "200", description = "Service is running", content = @Content(schema = @Schema(implementation = HomeResponseDTO.class)))
    })
    @GetMapping("/")
    public HomeResponseDTO home() {
        return new HomeResponseDTO("Sushil_POC Spring Boot App is running!");
    }

    /**
     * Health check endpoint for readiness/liveness probes.
     * 
     * @return HealthResponseDTO with health status and details
     */
    @Operation(summary = "Health endpoint", description = "Returns application health status.", responses = {
            @ApiResponse(responseCode = "200", description = "Health status", content = @Content(schema = @Schema(implementation = HealthResponseDTO.class)))
    })
    @GetMapping("/api/v1/health")
    public HealthResponseDTO health() {
        // In a real app, you might check DB, queue, etc. Here we return static info.
        return new HealthResponseDTO("UP", "All systems operational.");
    }
}
