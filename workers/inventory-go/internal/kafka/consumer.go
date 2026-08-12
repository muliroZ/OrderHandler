package kafka

import (
	"context"
	"dev/muliroz/inventory-worker/internal/domain"
	"encoding/json"
	"log"

	"github.com/google/uuid"
	"github.com/twmb/franz-go/pkg/kgo"
)

type OrderCreatedMessageDTO struct {
    EventID uuid.UUID `json:"event_id"`
    OrderID uuid.UUID `json:"order_id"`
    Items   []struct {
        ItemID   uuid.UUID `json:"item_id"`
        Quantity int       `json:"quantity"`
    } `json:"items"`
}

type OrderCancelledMessageDTO struct {
	EventID uuid.UUID `json:"event_id"`
	OrderID uuid.UUID `json:"order_id"`
	Items   []struct {
        ItemID   uuid.UUID `json:"item_id"`
        Quantity int       `json:"quantity"`
    } `json:"items"`
}

type ItemCreatedMessageDTO struct {
	EventID uuid.UUID `json:"event_id"`
	ItemID uuid.UUID `json:"item_id"`
	InitialQuantity int `json:"initial_quantity"`
}

type OrderConsumer struct {
	service *domain.InventoryService
	client *kgo.Client
}

func NewOrderConsumer(service *domain.InventoryService, client *kgo.Client) *OrderConsumer {
	return &OrderConsumer{
		service: service,
		client: client,
	}
}

func (c *OrderConsumer) Start(
	ctx context.Context,
) error {
	for {
		fetches := c.client.PollFetches(ctx)
		if fetches.IsClientClosed() {
			return nil
		}

		if errs := fetches.Errors(); len(errs) > 0 {
			log.Printf("Erros ao buscar mensagens: %v", errs)
		}

		iter := fetches.RecordIter()
		for !iter.Done() {
			record := iter.Next()

			err := c.processRecord(ctx, record)
			if err != nil {
				log.Printf("Erro ao processar mensagem offset %d: %v", record.Offset, err)
				continue
			}

			c.client.MarkCommitRecords(record)
		}

		if err := c.client.CommitUncommittedOffsets(ctx); err != nil {
			log.Printf("Erro ao commitar offsets: %v", err)
		}
	}
}

func (c *OrderConsumer) processRecord(ctx context.Context, record *kgo.Record) error {
	switch record.Topic {
	case "orders-created":
		return c.handleOrderCreated(ctx, record.Value)
	case "orders-cancelled":
		return c.handleOrderCancelled(ctx, record.Value)
	case "items-created":
		return c.handleItemCreated(ctx, record.Value)
	default:
		log.Printf("Tópico não reconhecido: %v", record.Topic)
		return nil
	}
}

func (c *OrderConsumer) handleOrderCreated(ctx context.Context, payload []byte) error {
	var dto OrderCreatedMessageDTO
	if err := json.Unmarshal(payload, &dto); err != nil {
		log.Printf("Erro ao desserializar orders-created: %v", err)
		return nil
	}

	params := c.toStockManagementParams(dto.EventID, dto.OrderID, dto.Items)
	return c.service.ReserveStock(ctx, params)
}

func (c *OrderConsumer) handleOrderCancelled(ctx context.Context, payload []byte) error {
	var dto OrderCancelledMessageDTO
	if err := json.Unmarshal(payload, &dto); err != nil {
		log.Printf("Erro ao desserializar orders-cancelled: %v", err)
		return nil
	}

	params := c.toStockManagementParams(dto.EventID, dto.OrderID, dto.Items)
	return c.service.RestoreStock(ctx, params)
}

func (c *OrderConsumer) handleItemCreated(ctx context.Context, payload []byte) error {
	var dto ItemCreatedMessageDTO
	if err := json.Unmarshal(payload, &dto); err != nil {
		log.Printf("Erro ao desserializar items-created: %v", err)
		return nil
	}

	return c.service.RegisterItem(
		ctx,
		dto.EventID,
		dto.ItemID,
		dto.InitialQuantity,
	)
}

func (c *OrderConsumer) toStockManagementParams(
	eventID uuid.UUID, 
	orderID uuid.UUID, 
	items []struct {
		ItemID   uuid.UUID `json:"item_id"`
		Quantity int       `json:"quantity"`
	},
) domain.StockManagementParams {
	var orderItems []domain.OrderItemParam
	for _, item := range items {
		orderItems = append(orderItems, domain.OrderItemParam{
			ItemID:   item.ItemID,
			Quantity: item.Quantity,
		})
	}

	return domain.StockManagementParams{
		EventID:    eventID,
		OrderID:    orderID,
		OrderItems: orderItems,
	}
}