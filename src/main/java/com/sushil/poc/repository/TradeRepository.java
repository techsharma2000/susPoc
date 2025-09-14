package com.sushil.poc.repository;
import com.sushil.poc.model.Trade; import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface TradeRepository extends JpaRepository<Trade, Long> {
Optional<Trade> findTopByTradeIdOrderByVersionDesc(String tradeId);
}