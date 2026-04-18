package com.llyods.payments.service.impl;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.llyods.payments.entity.PaymentOutcomeEntity;
import com.llyods.payments.model.PaymentEvent;
import com.llyods.payments.repository.PaymentOutcomeRepository;

@Service
public class PaymentProcessingListener {

    private static final BigDecimal HOLD_LIMIT =
            new BigDecimal("250000");

    private final PaymentOutcomeRepository repo;
    private final KafkaTemplate<String, PaymentOutcomeEntity> kafkaTemplate;

    private final AtomicLong totalProcessed = new AtomicLong();
    private final AtomicLong totalHeld = new AtomicLong();
    private final AtomicLong totalRejected = new AtomicLong();
    private final AtomicLong totalTime = new AtomicLong();

    public PaymentProcessingListener(
            PaymentOutcomeRepository repo,
            KafkaTemplate<String, PaymentOutcomeEntity> kafkaTemplate) {
        this.repo = repo;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(
        topics = "payments.submitted",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(PaymentEvent event) {

        long start = System.currentTimeMillis();
        String status;

        if (event.getAmount().compareTo(HOLD_LIMIT) > 0) {
            status = "HELD";
            totalHeld.incrementAndGet();
        }
        else if (event.getDebitAccountId().equals(event.getCreditAccountId())) {
            status = "REJECTED";
            totalRejected.incrementAndGet();
        }
        else {
            status = "PROCESSED";
            totalProcessed.incrementAndGet();
        }

        long duration = System.currentTimeMillis() - start;
        totalTime.addAndGet(duration);

        PaymentOutcomeEntity entity = new PaymentOutcomeEntity();
        entity.setPaymentId(event.getPaymentId());
        entity.setDebitAccountId(event.getDebitAccountId());
        entity.setCreditAccountId(event.getCreditAccountId());
        entity.setAmount(event.getAmount());
        entity.setCurrency(event.getCurrency());
        entity.setStatus(status);
        entity.setProcessedAt(Instant.now());
        entity.setProcessingTimeMs(duration);

        repo.save(entity);

        kafkaTemplate.send(
                "payments.processed",
                event.getDebitAccountId(),
                entity
        );
    }

    public long getTotalProcessed() { return totalProcessed.get(); }
    public long getTotalHeld() { return totalHeld.get(); }
    public long getTotalRejected() { return totalRejected.get(); }
    public long getAvgProcessingTime() {
        long count = totalProcessed.get()
                   + totalHeld.get()
                   + totalRejected.get();
        return count == 0 ? 0 : totalTime.get() / count;
    }
}