package com.hcl.payments.service.impl;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.hcl.payments.model.PaymentEvent;

@Service
public class PaymentProducer {

	private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;

	public PaymentProducer(KafkaTemplate<String, PaymentEvent> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

	public void publish(PaymentEvent event) {
		kafkaTemplate.send("payments.submitted", event.getDebitAccountId(), // partition key ✅
				event);
	}
}