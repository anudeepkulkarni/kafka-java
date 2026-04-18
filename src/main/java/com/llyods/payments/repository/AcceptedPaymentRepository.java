package com.llyods.payments.repository;


import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.llyods.payments.entity.AcceptedPaymentEntity;

public interface AcceptedPaymentRepository
        extends JpaRepository<AcceptedPaymentEntity, UUID> {
}