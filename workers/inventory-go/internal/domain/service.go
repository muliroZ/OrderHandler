package domain

import (
	"context"
	"errors"

	"github.com/google/uuid"
)

// service init
type InventoryService struct {
	repo Repository
	publisher EventPublisher
}

func NewInventoryService(r Repository, ep EventPublisher) *InventoryService {
	return &InventoryService{
		repo: r,
		publisher: ep,
	}
}

// aux types
type StockManagementParams struct {
	EventID uuid.UUID
	OrderID uuid.UUID
	OrderItems []OrderItemParam
}

type OrderItemParam struct {
	ItemID uuid.UUID `json:"item_id"`
	Quantity int `json:"quantity"`
}

// functions
func (s *InventoryService) RegisterItem(
	ctx context.Context, 
	eventID uuid.UUID, 
	itemID uuid.UUID, 
	initialQuantity int,
) error {
	isProcessed, err := s.repo.IsEventProcessed(ctx, eventID)
	if err != nil {
		return err
	}
	
	if isProcessed {
		return nil
	}

	item := NewStockItem(itemID, initialQuantity)
	movement := NewStockMovement(eventID, nil, MovementTypeRegistration, MovementStatusSuccess)

	err = s.repo.SaveItemAndMovement(ctx, item, movement)
	if err != nil {
		return err
	}

	return nil
}

func (s *InventoryService) ReserveStock(
	ctx context.Context, 
	params StockManagementParams,
) error {
	isProcessed, err := s.repo.IsEventProcessed(ctx, params.EventID)
	if err != nil {
		return err
	}

	if isProcessed {
		return nil
	}

	var itemsIDs []uuid.UUID
	orderItems := make(map[uuid.UUID]int)

	for _, orderItem := range params.OrderItems {
		itemID := orderItem.ItemID
		itemsIDs = append(itemsIDs, itemID)

		orderItems[itemID] += orderItem.Quantity
	}

	movement := NewStockMovement(params.EventID, &params.OrderID, MovementTypeReservation, MovementStatusSuccess)
	reservedItems, err := s.repo.ReserveStockTx(ctx, itemsIDs, orderItems, movement)
	if err != nil {
		if errors.Is(err, ErrItemNotFound) || errors.Is(err, ErrInsufficientStock) {
			failMovement := NewStockMovement(params.EventID, &params.OrderID, MovementTypeReservation, err.Error())
			_ = s.repo.SaveMovement(ctx, failMovement)

			event := NewInventoryRejectedEvent(params.EventID, params.OrderID, err.Error())
			_ = s.publisher.PublishInventoryRejected(ctx, event)
		}
		return err
	}

	event := NewInventoryReservedEvent(params.EventID, params.OrderID, reservedItems)
	return s.publisher.PublishInventoryReserved(ctx, event)
}

func (s *InventoryService) RestoreStock(
	ctx context.Context, 
	params StockManagementParams,
) error {
	isProcessed, err := s.repo.IsEventProcessed(ctx, params.EventID)
	if err != nil {
		return err
	}

	if isProcessed {
		return nil
	}

	var itemsIDs []uuid.UUID
	orderItems := make(map[uuid.UUID]int)

	for _, orderItem := range params.OrderItems {
		itemID := orderItem.ItemID
		itemsIDs = append(itemsIDs, itemID)

		orderItems[itemID] += orderItem.Quantity
	}

	items, err := s.repo.FindItemsByIds(ctx, itemsIDs)
	if err != nil {
		return err
	}

	if len(items) != len(orderItems) {
		movement := NewStockMovement(params.EventID, &params.OrderID, MovementTypeRestoration, MovementStatusItemNotFound)
		event := NewInventoryRejectedEvent(params.EventID, params.OrderID, ErrItemNotFound.Error())

		err = s.repo.SaveMovement(ctx, movement)
		if err != nil {
			return err
		}

		err = s.publisher.PublishInventoryRejected(ctx, event)
		if err != nil {
			return err
		}

		return ErrItemNotFound
	}

	for _, item := range items {
		item.Credit(orderItems[item.ID])
	}

	movement := NewStockMovement(params.EventID, &params.OrderID, MovementTypeRestoration, MovementStatusSuccess)
	err = s.repo.UpdateStockAndSaveMovement(ctx, items, movement)
	if err != nil {
		return err
	}

	event := NewInventoryRestoredEvent(params.EventID, params.OrderID, params.OrderItems)
	err = s.publisher.PublishInventoryRestored(ctx, event)
	if err != nil {
		return err
	}

	return nil
}