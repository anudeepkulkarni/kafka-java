package com.llyods.payments.service.impl;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.llyods.payments.model.PaymentEvent;

@Service
public class PaymentProducer {

    private static final String TOPIC = "payments.submitted";

    private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;

    public PaymentProducer(KafkaTemplate<String, PaymentEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(PaymentEvent event) {
        kafkaTemplate.send(TOPIC, event.getDebitAccountId(), event);
    }
}