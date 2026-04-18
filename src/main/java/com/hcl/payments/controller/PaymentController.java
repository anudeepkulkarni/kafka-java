package com.hcl.payments.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.hcl.payments.entity.AccountEntity;
import com.hcl.payments.model.PaymentEvent;
import com.hcl.payments.model.PaymentRequest;
import com.hcl.payments.repository.AccountRepository;
import com.hcl.payments.service.impl.PaymentProducer;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class PaymentController {

    private final AccountRepository accountRepo;
    private final PaymentProducer producer;

    // ✅ constructor injection
    public PaymentController(AccountRepository accountRepo,
                             PaymentProducer producer) {
        this.accountRepo = accountRepo;
        this.producer = producer;
    }

    @PostMapping("/payments")
    public ResponseEntity<?> submit(@Valid @RequestBody PaymentRequest req) {

        // ✅ PaymentRequest is a RECORD → use field() access
        AccountEntity debit = accountRepo.findById(req.debitAccountId())
                .orElseThrow(() -> new ResponseStatusException(
                        404,
                        "Debit account not found: " + req.debitAccountId(), null
                ));

        AccountEntity credit = accountRepo.findById(req.creditAccountId())
                .orElseThrow(() -> new ResponseStatusException(
                        404,
                        "Credit account not found: " + req.creditAccountId(), null
                ));

        // ✅ AccountEntity getters
        if (!"ACTIVE".equals(debit.getStatus())) {
            throw new ResponseStatusException(
                    422,
                    "Account is suspended: " + debit.getAccountId(), null
            );
        }

        if (!"ACTIVE".equals(credit.getStatus())) {
            throw new ResponseStatusException(
                    422,
                    "Account is suspended: " + credit.getAccountId(), null
            );
        }

        // ✅ PaymentEvent is Lombok @Data → use setters
        PaymentEvent event = new PaymentEvent();
        event.setPaymentId(req.paymentId());
        event.setDebitAccountId(req.debitAccountId());
        event.setCreditAccountId(req.creditAccountId());
        event.setAmount(req.amount());
        event.setCurrency(req.currency());
        event.setTimestamp(req.timestamp());

        producer.publish(event);

        return ResponseEntity.accepted()
                .body(Map.of("paymentId", req.paymentId()));
    }
}