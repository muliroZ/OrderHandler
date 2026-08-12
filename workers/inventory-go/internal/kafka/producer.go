package kafka

import (
	"context"
	"dev/muliroz/inventory-worker/internal/domain"
	"encoding/json"

	"github.com/twmb/franz-go/pkg/kgo"
)

type EventProducer struct {
	client *kgo.Client
}

func NewEventProducer(client *kgo.Client) *EventProducer {
	return &EventProducer{client: client}
}

func (p *EventProducer) PublishInventoryReserved(
	ctx context.Context,
	event *domain.InventoryReservedEvent,
) error {
	payload, err := json.Marshal(event)
	if err != nil {
		return err
	}

	record := &kgo.Record{
		Topic: "inventory-reserved",
		Key: []byte(event.OrderID.String()),
		Value: payload,
	}

	results := p.client.ProduceSync(ctx, record)
	return results.FirstErr()
}

func (p *EventProducer) PublishInventoryRejected(
	ctx context.Context,
	event domain.InventoryRejectedEvent,
) error {
	payload, err := json.Marshal(event)
	if err != nil {
		return err
	}

	record := &kgo.Record{
		Topic: "inventory-rejected",
		Key: []byte(event.OrderID.String()),
		Value: payload,
	}

	results := p.client.ProduceSync(ctx, record)
	return results.FirstErr()
}

func (p *EventProducer) PublishInventoryRestored(
	ctx context.Context,
	event domain.InventoryRestoredEvent,
) error {
	payload, err := json.Marshal(event)
	if err != nil {
		return err
	}

	record := &kgo.Record{
		Topic: "inventory-restored",
		Key: []byte(event.OrderID.String()),
		Value: payload,
	}

	results := p.client.ProduceSync(ctx, record)
	return results.FirstErr()
}