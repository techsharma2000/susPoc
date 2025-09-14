package com.sushil.poc.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Gauge;
import org.springframework.stereotype.Component;

@Component
public class QueueMetrics {
    private final Counter processedTrades;
    private final Counter failedTrades;
    private final Counter droppedTrades;
    private volatile int queueSize;

    public QueueMetrics(MeterRegistry meterRegistry) {
        this.processedTrades = meterRegistry.counter("queue.trades.processed");
        this.failedTrades = meterRegistry.counter("queue.trades.failed");
        this.droppedTrades = meterRegistry.counter("queue.trades.dropped");
        Gauge.builder("queue.size", this, QueueMetrics::getQueueSize)
                .description("Current size of the in-memory trade queue")
                .register(meterRegistry);
    }

    public void incrementProcessed() {
        processedTrades.increment();
    }

    public void incrementFailed() {
        failedTrades.increment();
    }

    public void incrementDropped() {
        droppedTrades.increment();
    }

    public int getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(int size) {
        this.queueSize = size;
    }
}
