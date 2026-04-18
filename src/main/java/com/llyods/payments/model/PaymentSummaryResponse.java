package com.llyods.payments.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentSummaryResponse {

    /**
     * Counts grouped by status:
     * PROCESSED, HELD, REJECTED
     */
    private Map<String, Long> countByStatus;

    /**
     * Total amount of all payments (regardless of status)
     */
    private BigDecimal totalAmount;

    /**
     * Earliest processedAt timestamp
     */
    private Instant fromDate;

    /**
     * Latest processedAt timestamp
     */
    private Instant toDate;
}