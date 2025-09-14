package com.sushil.poc.service;

import com.sushil.poc.exception.LowerVersionException;
import com.sushil.poc.exception.MaturityDateException;
import com.sushil.poc.model.Trade;
import com.sushil.poc.repository.mongo.TradeMongoRepository;
import com.sushil.poc.repository.jpa.TradeRepository;
import com.sushil.poc.repository.jpa.MongoSyncFailureRepository;
import com.sushil.poc.model.MongoSyncFailure;
import java.time.LocalDateTime;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TradeService {
    private final TradeRepository repo;
    private final TradeMongoRepository mongoRepo;
    private final MongoSyncFailureRepository mongoSyncFailureRepo;
    private static final Logger logger = LoggerFactory.getLogger(TradeService.class);

    @PersistenceContext
    private EntityManager entityManager;

    public TradeService(TradeRepository repo, TradeMongoRepository mongoRepo,
            MongoSyncFailureRepository mongoSyncFailureRepo) {
        this.repo = repo;
        this.mongoRepo = mongoRepo;
        this.mongoSyncFailureRepo = mongoSyncFailureRepo;
    }

    @Transactional
    public Trade acceptTrade(Trade incoming) {
        logger.info("Accepting trade: {}", incoming);

        // Check for maturity date validation - reject if maturity date is before today
        if (incoming.getMaturityDate() == null || incoming.getMaturityDate().isBefore(LocalDate.now())) {
            throw new MaturityDateException("Maturity date must be today or in the future");
        }

        Optional<Trade> existingOpt = repo.findTopByTradeIdOrderByVersionDesc(incoming.getTradeId());
        if (existingOpt.isPresent()) {
            Trade existing = existingOpt.get();
            if (incoming.getVersion() < existing.getVersion()) {
                throw new LowerVersionException("Version too low");
            }
            if (incoming.getVersion() == existing.getVersion()) {
                incoming.setId(existing.getId());
            }
        }

        if (incoming.getCreatedDate() == null) {
            incoming.setCreatedDate(LocalDate.now());
        }
        incoming.setExpired(false);

        Trade saved = repo.save(incoming);
        logger.info("Trade saved to SQL: {}", saved);

        saveToMongoAsync(saved); // Non-blocking MongoDB write
        return saved;
    }

    @Async
    public void saveToMongoAsync(Trade trade) {
        int attempts = 0;
        Exception lastException = null;
        while (attempts < 3) {
            try {
                mongoRepo.save(trade);
                logger.info("Trade saved to MongoDB: {}", trade);
                return;
            } catch (Exception e) {
                attempts++;
                lastException = e;
                logger.warn("Attempt {} failed to save trade to MongoDB: {}", attempts, trade, e);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        // All attempts failed, log to SQL table
        if (lastException != null) {
            String reason = lastException.getMessage();
            MongoSyncFailure failure = new MongoSyncFailure(trade.getTradeId(), trade.getVersion(), LocalDateTime.now(),
                    reason);
            mongoSyncFailureRepo.save(failure);
            logger.error("Failed to sync trade to MongoDB after 3 attempts. Logged to mongo_sync_failures: {}",
                    failure);
        }
    }

    public List<Trade> getTradesById(String tradeId) {
        return repo.findByTradeId(tradeId);
    }

    public List<Trade> getAllTrades(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return repo.findAll(pageable).getContent();
    }

    @Transactional
    public int deleteTradesById(String tradeId) {
        List<Trade> toDelete = repo.findByTradeId(tradeId);
        int count = toDelete.size();
        repo.deleteAll(toDelete);

        // Also delete from MongoDB
        try {
            mongoRepo.deleteAll(toDelete);
        } catch (Exception e) {
            logger.warn("Failed to delete trades from MongoDB for tradeId: {}", tradeId, e);
        }

        return count;
    }

    @Transactional
    public int markExpiredTrades() {
        LocalDate today = LocalDate.now();

        // Use the bulk update method for better performance
        int updatedCount = repo.markTradesAsExpired(today);

        // Clear JPA persistence context to ensure subsequent loads reflect updated
        // state
        if (entityManager != null) {
            entityManager.clear();
        }

        if (updatedCount > 0) {
            logger.info("Marked {} trades as expired", updatedCount);

            // Also update MongoDB (this could be optimized for bulk operations)
            List<Trade> expiredTrades = repo.findByMaturityDateBeforeAndExpiredFalse(today);
            for (Trade trade : expiredTrades) {
                try {
                    trade.setExpired(true);
                    mongoRepo.save(trade);
                } catch (Exception e) {
                    logger.warn("Failed to update trade in MongoDB: {}", trade.getTradeId(), e);
                }
            }
        }

        return updatedCount;
    }

}