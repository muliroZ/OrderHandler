package domain

import (
	"context"

	"github.com/google/uuid"
)

type Repository interface {
	SaveItem(ctx context.Context, item *StockItem) error
	FindItemById(ctx context.Context, itemID uuid.UUID) (*StockItem, error)
	FindItemsByIds(ctx context.Context, itemsIDs []uuid.UUID) ([]*StockItem, error)
	IsEventProcessed(ctx context.Context, eventID uuid.UUID) (bool, error)
	SaveMovement(ctx context.Context, movement *StockMovement) error
	SaveItemAndMovement(ctx context.Context, item *StockItem, movement *StockMovement) error
	UpdateStockAndSaveMovement(ctx context.Context, items []*StockItem, movement *StockMovement) error
}

type EventPublisher interface {
	PublishInventoryReserved(ctx context.Context, event *InventoryReservedEvent) error
	PublishInventoryRejected(ctx context.Context, event *InventoryRejectedEvent) error
	PublishInventoryRestored(ctx context.Context, event *InventoryRestoredEvent) error
}