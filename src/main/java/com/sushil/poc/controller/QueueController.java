package com.sushil.poc.controller;

import com.sushil.poc.dto.QueuePublishRequestDTO;
import com.sushil.poc.dto.QueueStatusResponseDTO;
import com.sushil.poc.model.Trade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.time.LocalDate;
import com.sushil.poc.queue.InMemoryQueue;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/queue")
public class QueueController {
    private final InMemoryQueue inMemoryQueue;

    public QueueController(InMemoryQueue inMemoryQueue) {
        this.inMemoryQueue = inMemoryQueue;
    }

    /**
     * Publishes a trade to the in-memory queue.
     * 
     * @param requestDTO Trade publish request DTO
     * @return Success message
     */
    @Operation(summary = "Publish a trade to the queue", description = "Publishes a trade to the in-memory queue for processing.", requestBody = @RequestBody(required = true, content = @Content(schema = @Schema(implementation = QueuePublishRequestDTO.class))), responses = {
            @ApiResponse(responseCode = "200", description = "Trade published successfully", content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PostMapping("/publish")
    public ResponseEntity<String> publishTrade(
            @org.springframework.web.bind.annotation.RequestBody QueuePublishRequestDTO requestDTO) {
        Trade trade = new Trade();
        trade.setTradeId(requestDTO.getTradeId());
        trade.setVersion(requestDTO.getVersion());
        trade.setCounterPartyId(requestDTO.getCounterPartyId());
        trade.setBookId(requestDTO.getBookId());
        trade.setMaturityDate(LocalDate.parse(requestDTO.getMaturityDate()));
        trade.setCreatedDate(LocalDate.parse(requestDTO.getCreatedDate()));
        trade.setExpired("Y".equalsIgnoreCase(requestDTO.getExpired()));
        inMemoryQueue.send(trade);
        return ResponseEntity.ok("Trade published to queue");
    }

    /**
     * Gets the current queue size.
     * 
     * @return Queue status response DTO
     */
    @Operation(summary = "Get queue status", description = "Returns the current size of the in-memory queue.", responses = {
            @ApiResponse(responseCode = "200", description = "Queue status returned", content = @Content(schema = @Schema(implementation = QueueStatusResponseDTO.class)))
    })
    @GetMapping("/status")
    public ResponseEntity<QueueStatusResponseDTO> getQueueStatus() {
        return ResponseEntity.ok(new QueueStatusResponseDTO(inMemoryQueue.size()));
    }
}
