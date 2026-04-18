package com.llyods.payments.controller;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.llyods.payments.entity.PaymentOutcomeEntity;
import com.llyods.payments.model.PaymentSummaryResponse;
import com.llyods.payments.repository.PaymentOutcomeRepository;

@RestController
@RequestMapping("/api/reports")
public class ReportsController {

	private final PaymentOutcomeRepository repo;

	public ReportsController(PaymentOutcomeRepository repo) {
		this.repo = repo;
	}

	@GetMapping("/activity")
	public Page<PaymentOutcomeEntity> activity(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size, @RequestParam(required = false) String status) {

		Pageable pageable = PageRequest.of(page, size, Sort.by("processedAt").descending());

		return status == null ? repo.findAll(pageable) : repo.findByStatus(status, pageable);
	}

	@GetMapping("/accounts/{accountId}/history")
	public List<PaymentOutcomeEntity> history(@PathVariable String accountId) {

		return repo.findByDebitAccountIdOrCreditAccountIdOrderByProcessedAtDesc(accountId, accountId);
	}

	@GetMapping("/summary")
	public PaymentSummaryResponse summary() {

	    // ✅ 1. Count by status
	    List<Object[]> rows = repo.countByStatus();
	    Map<String, Long> countByStatus = new HashMap<>();

	    for (Object[] row : rows) {
	        countByStatus.put((String) row[0], (Long) row[1]);
	    }

	    // ✅ 2. Total amount
	    BigDecimal totalAmount = repo.totalAmount();

	    // ✅ 3. Date range — FIXED
	    Object[] range = repo.dateRange();

	    Instant fromDate = null;
	    Instant toDate = null;

	    if (range != null && range.length == 2) {
	        fromDate = (Instant) range[0];
	        toDate   = (Instant) range[1];
	    }

	    return new PaymentSummaryResponse(
	            countByStatus,
	            totalAmount,
	            fromDate,
	            toDate
	    );
	}

}