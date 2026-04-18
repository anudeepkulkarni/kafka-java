package com.llyods.payments.entity;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "accepted_payments")
@Data
@NoArgsConstructor
public class AcceptedPaymentEntity {

    @Id
    private UUID paymentId;
}