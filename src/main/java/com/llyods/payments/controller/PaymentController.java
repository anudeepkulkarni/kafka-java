package com.llyods.payments.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.llyods.payments.entity.AccountEntity;
import com.llyods.payments.model.PaymentRequest;
import com.llyods.payments.repository.AccountRepository;
import com.llyods.payments.service.PaymentProcessingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
@Validated
public class PaymentController {

    private final AccountRepository accountRepo;
    private final PaymentProcessingService processingService;
    private final Executor paymentExecutor;

    public PaymentController(AccountRepository accountRepo,
                             PaymentProcessingService processingService,
                             Executor paymentExecutor) {
        this.accountRepo = accountRepo;
        this.processingService = processingService;
        this.paymentExecutor = paymentExecutor;
    }

    @PostMapping("/payments")
    public ResponseEntity<?> submit(
            @Valid @RequestBody List<PaymentRequest> requests) {

        if (requests == null || requests.isEmpty()) {
            throw new ResponseStatusException(400, "At least one payment required", null);
        }

        List<CompletableFuture<UUID>> futures = requests.stream()
                .map(req -> CompletableFuture.supplyAsync(() -> {
                    processingService.process(req);
                    return req.paymentId();
                }, paymentExecutor))
                .toList();

        try {
            List<String> ids = futures.stream()
                    .map(CompletableFuture::join)
                    .map(UUID::toString)
                    .collect(Collectors.toList());

            return ResponseEntity.accepted().body(Map.of(
                    "acceptedCount", ids.size(),
                    "paymentIds", ids
            ));

        } catch (CompletionException ex) {
            if (ex.getCause() instanceof ResponseStatusException rse) {
                throw rse;
            }
            throw ex;
        }
    }

    @GetMapping("/accounts/{accountId}")
    public ResponseEntity<AccountEntity> getAccount(
            @PathVariable String accountId) {

        return accountRepo.findById(accountId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(
                        404,
                        "Account not found: " + accountId, null
                ));
    }
}