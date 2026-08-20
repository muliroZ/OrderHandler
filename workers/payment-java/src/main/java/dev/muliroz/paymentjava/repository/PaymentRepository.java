package dev.muliroz.paymentjava.repository;

import dev.muliroz.paymentjava.model.PaymentEntity;
import dev.muliroz.paymentjava.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<PaymentEntity, UUID> {
    boolean existsByEventId(UUID eventId);
    Optional<PaymentEntity> findByOrderId(UUID orderId);
    boolean existsByOrderIdAndStatus(UUID uuid, PaymentStatus paymentStatus);
}
