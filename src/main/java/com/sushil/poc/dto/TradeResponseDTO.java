package com.sushil.poc.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

@Schema(description = "Response DTO for trade details")
public class TradeResponseDTO {
    @Schema(description = "Trade identifier", example = "T1")
    private String tradeId;

    @Schema(description = "Trade version", example = "1")
    private int version;

    @Schema(description = "Counter party identifier", example = "CP-1")
    private String counterPartyId;

    @Schema(description = "Book identifier", example = "B1")
    private String bookId;

    @Schema(description = "Maturity date (yyyy-MM-dd)", example = "2025-12-31")
    private LocalDate maturityDate;

    @Schema(description = "Created date (yyyy-MM-dd)", example = "2025-09-14")
    private LocalDate createdDate;

    @Schema(description = "Expired flag", example = "false")
    private boolean expired;

    // Getters and setters
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

    public String getCounterPartyId() {
        return counterPartyId;
    }

    public void setCounterPartyId(String counterPartyId) {
        this.counterPartyId = counterPartyId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public LocalDate getMaturityDate() {
        return maturityDate;
    }

    public void setMaturityDate(LocalDate maturityDate) {
        this.maturityDate = maturityDate;
    }

    public LocalDate getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDate createdDate) {
        this.createdDate = createdDate;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }
}
