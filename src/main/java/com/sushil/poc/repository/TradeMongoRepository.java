package com.sushil.poc.repository;

import com.sushil.poc.model.Trade;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface TradeMongoRepository extends MongoRepository<Trade, Long> {
    List<Trade> findByTradeId(String tradeId);
}
