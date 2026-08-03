package dev.muliroz.orderhandler.repository;

import dev.muliroz.orderhandler.model.ItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SpringDataItemRepository extends JpaRepository<ItemEntity, UUID>, JpaSpecificationExecutor<ItemEntity> {

    @Query("""
        select exists (
            select 1
            from OrderItemEntity
            where item.id = :itemId
        )
    """)
    boolean isUsed(@Param("itemId") UUID itemId);
}
