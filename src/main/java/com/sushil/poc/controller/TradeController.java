
package com.sushil.poc.controller;

import com.sushil.poc.dto.TradeRequestDTO;
import com.sushil.poc.dto.TradeResponseDTO;
import com.sushil.poc.model.Trade;
import com.sushil.poc.service.TradeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller for managing trades.
 */
@RestController
@RequestMapping("/api/v1/trades")
@Tag(name = "Trade API", description = "Endpoints for managing trades")
public class TradeController {
    private final TradeService tradeService;

    public TradeController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    /**
     * Create a new trade.
     */
    @Operation(summary = "Add a new trade", description = "Creates a new trade or replaces if version matches.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trade created or replaced successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error or business rule violation")
    })
    @PostMapping
    public ResponseEntity<TradeResponseDTO> addTrade(@Valid @RequestBody TradeRequestDTO tradeDto) {
        Trade trade = toEntity(tradeDto);
        Trade saved = tradeService.acceptTrade(trade);
        return ResponseEntity.ok(toResponseDTO(saved));
    }

    /**
     * Get all trades for a tradeId.
     */
    @Operation(summary = "Get trades by tradeId", description = "Returns all trades for a given tradeId.")
    @GetMapping("/{tradeId}")
    public ResponseEntity<List<TradeResponseDTO>> getTrades(
            @Parameter(description = "Trade identifier") @PathVariable("tradeId") String tradeId) {
        List<Trade> trades = tradeService.getTradesById(tradeId);
        return ResponseEntity.ok(trades.stream().map(this::toResponseDTO).collect(Collectors.toList()));
    }

    /**
     * List all trades.
     */
    @Operation(summary = "List all trades", description = "Returns all trades in the store with paging support.")
    @GetMapping
    public ResponseEntity<List<TradeResponseDTO>> getAllTrades(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        List<Trade> trades = tradeService.getAllTrades(page, size);
        return ResponseEntity.ok()
                .header("Cache-Control", "max-age=60, public")
                .body(trades.stream().map(this::toResponseDTO).collect(Collectors.toList()));
    }

    /**
     * Update a trade by tradeId (idempotent, replaces latest version).
     */
    @Operation(summary = "Update trade", description = "Updates or replaces a trade by tradeId and version.")
    @PutMapping("/{tradeId}")
    public ResponseEntity<TradeResponseDTO> updateTrade(
            @Parameter(description = "Trade identifier") @PathVariable("tradeId") String tradeId,
            @Valid @RequestBody TradeRequestDTO tradeDto) {
        Trade trade = toEntity(tradeDto);
        trade.setTradeId(tradeId);
        Trade updated = tradeService.acceptTrade(trade);
        return ResponseEntity.ok(toResponseDTO(updated));
    }

    /**
     * Delete all trades for a tradeId.
     */
    @Operation(summary = "Delete trades by tradeId", description = "Deletes all trades for a given tradeId.")
    @DeleteMapping("/{tradeId}")
    public ResponseEntity<String> deleteTrade(
            @Parameter(description = "Trade identifier") @PathVariable("tradeId") String tradeId) {
        int deleted = tradeService.deleteTradesById(tradeId);
        return ResponseEntity.ok("Deleted " + deleted + " trade(s) with tradeId=" + tradeId);
    }

    /**
     * Mark expired trades.
     */
    @Operation(summary = "Mark expired trades", description = "Marks all trades with maturity date before today as expired.")
    @PostMapping("/markExpired")
    public ResponseEntity<String> markExpiredTrades() {
        int count = tradeService.markExpiredTrades();
        return ResponseEntity.ok("Marked " + count + " trade(s) as expired.");
    }

    // --- DTO Mappers ---
    private Trade toEntity(TradeRequestDTO dto) {
        Trade t = new Trade();
        t.setTradeId(dto.getTradeId());
        t.setVersion(dto.getVersion());
        t.setCounterPartyId(dto.getCounterPartyId());
        t.setBookId(dto.getBookId());
        t.setMaturityDate(dto.getMaturityDate());
        t.setCreatedDate(dto.getCreatedDate());
        return t;
    }

    private TradeResponseDTO toResponseDTO(Trade t) {
        TradeResponseDTO dto = new TradeResponseDTO();
        dto.setTradeId(t.getTradeId());
        dto.setVersion(t.getVersion());
        dto.setCounterPartyId(t.getCounterPartyId());
        dto.setBookId(t.getBookId());
        dto.setMaturityDate(t.getMaturityDate());
        dto.setCreatedDate(t.getCreatedDate());
        dto.setExpired(t.isExpired());
        return dto;
    }
}
