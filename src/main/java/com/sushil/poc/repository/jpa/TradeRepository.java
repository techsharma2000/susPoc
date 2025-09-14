package com.sushil.poc.repository.jpa;

import com.sushil.poc.model.Trade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TradeRepository extends JpaRepository<Trade, Long> {
    Optional<Trade> findTopByTradeIdOrderByVersionDesc(String tradeId);

    List<Trade> findByTradeId(String tradeId);

    List<Trade> findByMaturityDateBeforeAndExpiredFalse(LocalDate date);

    Optional<Trade> findByTradeIdAndVersion(String tradeId, int version);

    // Alternative query-based method for finding expired trades
    @Query("SELECT t FROM Trade t WHERE t.maturityDate < :today AND t.expired = false")
    List<Trade> findExpiredTrades(@Param("today") LocalDate today);

    // Bulk update for marking trades as expired (more efficient)
    @Modifying
    @Transactional
    @Query("UPDATE Trade t SET t.expired = true WHERE t.maturityDate < :today AND t.expired = false")
    int markTradesAsExpired(@Param("today") LocalDate today);

    // Find all trades ordered by tradeId and version for better readability
    List<Trade> findAllByOrderByTradeIdAscVersionAsc();
}