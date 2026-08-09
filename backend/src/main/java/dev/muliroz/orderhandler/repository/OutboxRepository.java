package dev.muliroz.orderhandler.repository;

import dev.muliroz.orderhandler.model.OutboxMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OutboxRepository extends JpaRepository<OutboxMessage, UUID> {
    List<OutboxMessage> findTop100ByStatusOrderByCreatedAtAsc(String status);
}
