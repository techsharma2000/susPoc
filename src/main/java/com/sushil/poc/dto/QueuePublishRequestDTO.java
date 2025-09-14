package com.sushil.poc.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request DTO for publishing a trade to the queue")
public class QueuePublishRequestDTO {
    @Schema(description = "Trade ID", example = "T1")
    private String tradeId;
    @Schema(description = "Version of the trade", example = "1")
    private int version;
    @Schema(description = "Counter-party ID", example = "CP-1")
    private String counterPartyId;
    @Schema(description = "Book ID", example = "B1")
    private String bookId;
    @Schema(description = "Maturity date in yyyy-MM-dd format", example = "2025-12-31")
    private String maturityDate;
    @Schema(description = "Created date in yyyy-MM-dd format", example = "2025-09-14")
    private String createdDate;
    @Schema(description = "Expired flag", example = "N")
    private String expired;

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

    public String getMaturityDate() {
        return maturityDate;
    }

    public void setMaturityDate(String maturityDate) {
        this.maturityDate = maturityDate;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getExpired() {
        return expired;
    }

    public void setExpired(String expired) {
        this.expired = expired;
    }
}
