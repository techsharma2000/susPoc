package com.sushil.poc.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Schema(description = "Request DTO for creating or updating a trade")
public class TradeRequestDTO {
    @Schema(description = "Trade identifier", example = "T1", required = true)
    @NotBlank
    private String tradeId;

    @Schema(description = "Trade version", example = "1", required = true)
    @NotNull
    private Integer version;

    @Schema(description = "Counter party identifier", example = "CP-1", required = true)
    @NotBlank
    private String counterPartyId;

    @Schema(description = "Book identifier", example = "B1", required = true)
    @NotBlank
    private String bookId;

    @Schema(description = "Maturity date (yyyy-MM-dd)", example = "2025-12-31", required = true)
    @NotNull
    @FutureOrPresent
    private LocalDate maturityDate;

    @Schema(description = "Created date (yyyy-MM-dd)", example = "2025-09-14")
    private LocalDate createdDate;

    // Getters and setters
    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
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
}
