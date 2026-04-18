package com.llyods.payments.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.llyods.payments.entity.AcceptedPaymentEntity;
import com.llyods.payments.entity.AccountEntity;
import com.llyods.payments.model.PaymentEvent;
import com.llyods.payments.model.PaymentRequest;
import com.llyods.payments.repository.AcceptedPaymentRepository;
import com.llyods.payments.repository.AccountRepository;
import com.llyods.payments.service.PaymentProcessingService;

import org.springframework.http.HttpStatus;

@Service
public class PaymentProcessingServiceImpl implements PaymentProcessingService {

    private final AccountRepository accountRepo;
    private final AcceptedPaymentRepository acceptedRepo;
    private final com.llyods.payments.service.impl.PaymentProducer producer;

    public PaymentProcessingServiceImpl(AccountRepository accountRepo,
                                        AcceptedPaymentRepository acceptedRepo,
                                        com.llyods.payments.service.impl.PaymentProducer producer) {
        this.accountRepo = accountRepo;
        this.acceptedRepo = acceptedRepo;
        this.producer = producer;
    }

    /**
     * Each call runs in its own transaction.
     * Safe for parallel execution.
     */
    @Override
    @Transactional
    public void process(PaymentRequest req) {

        // ✅ Idempotency (hard guarantee via DB primary key)
        if (acceptedRepo.existsById(req.paymentId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Duplicate payment ID — request already accepted"
            );
        }

        // ✅ Debit checked FIRST
        AccountEntity debit = accountRepo.findById(req.debitAccountId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Debit account not found: " + req.debitAccountId()
                ));

        AccountEntity credit = accountRepo.findById(req.creditAccountId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Credit account not found: " + req.creditAccountId()
                ));

        // ✅ Suspended accounts
        if (!"ACTIVE".equals(debit.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "Account is suspended: " + debit.getAccountId()
            );
        }

        if (!"ACTIVE".equals(credit.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.UNPROCESSABLE_ENTITY,
                    "Account is suspended: " + credit.getAccountId()
            );
        }

        // ✅ Persist first (prevents race conditions)
        AcceptedPaymentEntity entity = new AcceptedPaymentEntity();
        entity.setPaymentId(req.paymentId());
        acceptedRepo.save(entity);

        // ✅ Publish Kafka event
        PaymentEvent event = new PaymentEvent();
        event.setPaymentId(req.paymentId());
        event.setDebitAccountId(req.debitAccountId());
        event.setCreditAccountId(req.creditAccountId());
        event.setAmount(req.amount());
        event.setCurrency(req.currency());
        event.setTimestamp(req.timestamp());

        producer.publish(event);
    }
}