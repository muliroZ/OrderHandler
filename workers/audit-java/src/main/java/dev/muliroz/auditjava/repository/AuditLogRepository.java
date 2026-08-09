package dev.muliroz.auditjava.repository;

import dev.muliroz.auditjava.model.AuditLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLogEntity, UUID> {
    boolean existsByEventId(UUID eventId);
}
