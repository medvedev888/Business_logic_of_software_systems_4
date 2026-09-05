package me.ifmo.backend.repositories;

import me.ifmo.backend.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByEnrollmentId(Long enrollmentId);

    Optional<Payment> findByProviderPaymentId(String providerPaymentId);

}