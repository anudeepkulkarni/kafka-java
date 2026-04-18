package com.llyods.payments.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.llyods.payments.entity.PaymentOutcomeEntity;

public interface PaymentOutcomeRepository extends JpaRepository<PaymentOutcomeEntity, Long> {

	Page<PaymentOutcomeEntity> findByStatus(String status, Pageable pageable);

	List<PaymentOutcomeEntity> findByDebitAccountIdOrCreditAccountIdOrderByProcessedAtDesc(String debit, String credit);

	// ✅ count by status
	@Query("""
			SELECT p.status, COUNT(p)
			FROM PaymentOutcomeEntity p
			GROUP BY p.status
			""")
	List<Object[]> countByStatus();

	// ✅ total amount
	@Query("""
			SELECT COALESCE(SUM(p.amount), 0)
			FROM PaymentOutcomeEntity p
			""")
	BigDecimal totalAmount();

	@Query("""
		       SELECT MIN(p.processedAt), MAX(p.processedAt)
		       FROM PaymentOutcomeEntity p
		       """)
		Object[] dateRange();


}