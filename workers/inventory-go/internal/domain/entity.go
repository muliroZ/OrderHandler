package domain

import (
	"time"

	"github.com/google/uuid"
)

// constantes
const (
	MovementTypeRegistration = "REGISTRO"
	MovementTypeReservation = "RESERVA"
	MovementTypeRestoration = "DEVOLUCAO"
	MovementStatusSuccess = "SUCESSO"
	MovementStatusInsufficientStock = "ESTOQUE_INSUFICIENTE"
	MovementStatusItemNotFound = "ITEM_NAO_ENCONTRADO"
)

// eventos
type InventoryReservedEvent struct {
	ID uuid.UUID
	OriginEventID uuid.UUID 
	OrderID uuid.UUID 
	OccurredAt time.Time 
	Items []OrderItemParam
}

type InventoryRejectedEvent struct {
	ID uuid.UUID
	OriginEventID uuid.UUID
	OrderID uuid.UUID
	Reason string
	OccurredAt time.Time
}

type InventoryRestoredEvent struct {
	ID uuid.UUID
	OriginEventID uuid.UUID
	OrderID uuid.UUID
	OccurredAt time.Time
	Items []OrderItemParam
}

// entidades
type StockItem struct {
	ID uuid.UUID `json:"id"`
	Quantity int `json:"quantity"`
	UpdatedAt time.Time `json:"updated_at"`
}

type StockMovement struct {
	EventID uuid.UUID `json:"event_id"`
	OrderID *uuid.UUID `json:"order_id"`
	OperationType string `json:"operation_type"`
	Status string `json:"status"`
}


// factory functions
func NewInventoryReservedEvent(
	originEventID uuid.UUID, 
	orderID uuid.UUID, 
	items []OrderItemParam,
) *InventoryReservedEvent {
	return &InventoryReservedEvent{
		ID: uuid.New(),
		OriginEventID: originEventID,
		OrderID: orderID,
		OccurredAt: time.Now(),
		Items: items,
	}
}
	
func NewInventoryRejectedEvent(
	originEventID uuid.UUID,
	orderID uuid.UUID,
	reason string,
) *InventoryRejectedEvent {
	return &InventoryRejectedEvent{
		ID: uuid.New(),
		OriginEventID: originEventID,
		OrderID: orderID,
		Reason: reason,
		OccurredAt: time.Now(),
	}
}

func NewInventoryRestoredEvent(
	originEventID uuid.UUID,
	orderID uuid.UUID,
	items []OrderItemParam,
) *InventoryRestoredEvent {
	return &InventoryRestoredEvent{
		ID: uuid.New(),
		OriginEventID: originEventID,
		OrderID: orderID,
		OccurredAt: time.Now(),
		Items: items,
	}
}

func NewStockItem(id uuid.UUID, quantity int) *StockItem {
	return &StockItem{
		ID: id,
		Quantity: quantity,
		UpdatedAt: time.Now(),
	}
}

func NewStockMovement(eventID uuid.UUID, orderID *uuid.UUID, operationType string, status string) *StockMovement {
	return &StockMovement{
		EventID: eventID,
		OrderID: orderID,
		OperationType: operationType,
		Status: status,
	}
}

// funções auxiliares
func (s *StockItem) HasStock(qty int) bool {
	return s.Quantity >= qty
}

func (s *StockItem) Debit(qty int) error {
	if s.Quantity < qty {
		return ErrInsufficientStock
	}

	s.Quantity -= qty
	return nil
}

func (s *StockItem) Credit(qty int) {
	s.Quantity += qty
}