package com.sushil.poc.queue;

import com.sushil.poc.model.Trade;
import com.sushil.poc.service.TradeService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.sushil.poc.metrics.QueueMetrics;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * In-memory queue for trade processing. For demo purposes only.
 * In production, use a distributed streaming platform (e.g., Kafka).
 * Thread-safe, observable, and extensible for future enhancements.
 */
@Component
public class InMemoryQueue {
    private static final Logger logger = LoggerFactory.getLogger(InMemoryQueue.class);

    // Configurable queue capacity for backpressure (default: 1000)
    private final BlockingQueue<Trade> queue;
    private final ExecutorService consumerExecutor;
    private final TradeService tradeService;
    private final QueueMetrics queueMetrics;
    private volatile boolean running = true;

    /**
     * Constructor for dependency injection.
     * 
     * @param tradeService  TradeService for processing trades
     * @param queueCapacity Optional queue capacity (default: 1000)
     */
    public InMemoryQueue(TradeService tradeService, QueueMetrics queueMetrics,
            @Value("${queue.capacity:1000}") int queueCapacity) {
        this.tradeService = Objects.requireNonNull(tradeService);
        this.queueMetrics = Objects.requireNonNull(queueMetrics);
        this.queue = new LinkedBlockingQueue<>(queueCapacity);
        this.consumerExecutor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r, "InMemoryQueue-Consumer");
            t.setDaemon(true);
            return t;
        });
    }

    /**
     * Enqueue a trade for processing. Returns true if accepted, false if full.
     * 
     * @param trade Trade to enqueue
     * @return true if enqueued, false if queue is full
     */
    public boolean send(Trade trade) {
        boolean offered = queue.offer(trade);
        queueMetrics.setQueueSize(queue.size());
        if (!offered) {
            logger.warn("Queue is full. Dropping trade: {}", trade);
            queueMetrics.incrementDropped();
        } else {
            logger.info("Trade enqueued: {}", trade);
        }
        return offered;
    }

    /**
     * @return current queue size
     */
    public int size() {
        int size = queue.size();
        queueMetrics.setQueueSize(size);
        return size;
    }

    /**
     * Start the background consumer thread for processing trades.
     */
    @PostConstruct
    public void startConsumer() {
        logger.info("Starting InMemoryQueue consumer thread");
        consumerExecutor.submit(() -> {
            while (running) {
                try {
                    Trade trade = queue.poll(500, TimeUnit.MILLISECONDS);
                    if (trade != null) {
                        processTrade(trade);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.warn("Consumer thread interrupted, shutting down");
                    break;
                } catch (Exception e) {
                    logger.error("Unexpected error in consumer thread", e);
                }
            }
            logger.info("InMemoryQueue consumer thread stopped");
        });
    }

    /**
     * Process a single trade with robust error handling.
     * 
     * @param trade Trade to process
     */
    private void processTrade(Trade trade) {
        try {
            tradeService.acceptTrade(trade);
            logger.info("Trade processed: {}", trade);
            queueMetrics.incrementProcessed();
        } catch (Exception ex) {
            logger.error("Failed to process trade: {}", trade, ex);
            queueMetrics.incrementFailed();
        }
    }

    /**
     * Cleanly stop the consumer thread and shutdown the executor.
     */
    @PreDestroy
    public void stop() {
        logger.info("Stopping InMemoryQueue consumer thread");
        running = false;
        consumerExecutor.shutdownNow();
        try {
            if (!consumerExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                logger.warn("Consumer executor did not terminate in time");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}