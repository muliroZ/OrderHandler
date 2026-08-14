package repository

import (
	"context"
	"dev/muliroz/inventory-worker/internal/database/generated"
	"dev/muliroz/inventory-worker/internal/domain"
	"errors"

	"github.com/google/uuid"
	"github.com/jackc/pgx/v5"
	"github.com/jackc/pgx/v5/pgxpool"
)

type PostgresRepository struct {
	pool *pgxpool.Pool
	queries *database.Queries
}

func NewPostgresRepository(pool *pgxpool.Pool) *PostgresRepository {
	return &PostgresRepository{
		pool: pool,
		queries: database.New(pool),
	}
}

func (r *PostgresRepository) SaveItem(ctx context.Context, item *domain.StockItem) error {
	return r.queries.SaveItem(ctx, database.SaveItemParams{
		ID: item.ID,
		Quantity: int32(item.Quantity),
		UpdatedAt: item.UpdatedAt,
	})
}

func (r *PostgresRepository) FindItemById(ctx context.Context, itemID uuid.UUID) (*domain.StockItem, error) {
	row, err := r.queries.FindItemByID(ctx, itemID)
	if err != nil {
		if errors.Is(err, pgx.ErrNoRows) {
			return nil, domain.ErrItemNotFound
		}
		return nil, err
	}

	return &domain.StockItem{
		ID: row.ID,
		Quantity: int(row.Quantity),
		UpdatedAt: row.UpdatedAt,
	}, nil
}

func (r *PostgresRepository) FindItemsByIds(ctx context.Context, itemsIDs []uuid.UUID) ([]*domain.StockItem, error) {
	rows, err := r.queries.FindItemsByIDs(ctx, itemsIDs)
	if err != nil {
		return nil, err
	}

	items := make([]*domain.StockItem, 0, len(rows))
	for _, row := range rows {
		items = append(items, &domain.StockItem{
			ID: row.ID,
			Quantity: int(row.Quantity),
			UpdatedAt: row.UpdatedAt,
		})
	}
	return items, nil
}

func (r *PostgresRepository) IsEventProcessed(ctx context.Context, eventID uuid.UUID) (bool, error) {
	exists, err := r.queries.IsEventProcessed(ctx, eventID)
	if err != nil {
		return false, err
	}
	return exists, nil
}

func (r *PostgresRepository) SaveMovement(ctx context.Context, movement *domain.StockMovement) error {
	return r.queries.SaveMovement(ctx, database.SaveMovementParams{
		EventID: movement.EventID,
		OrderID: movement.OrderID,
		OperationType: movement.OperationType,
		Status: movement.Status,
	})
}

func (r *PostgresRepository) SaveItemAndMovement(ctx context.Context, item *domain.StockItem, movement *domain.StockMovement) error {
	tx, err := r.pool.Begin(ctx)
	if err != nil {
		return err
	}
	defer tx.Rollback(ctx)

	qtx := r.queries.WithTx(tx)

	if err := qtx.SaveItem(ctx, database.SaveItemParams{
		ID: item.ID,
		Quantity: int32(item.Quantity),
		UpdatedAt: item.UpdatedAt,
	}); err != nil {
		return err
	}

	if err := qtx.SaveMovement(ctx, database.SaveMovementParams{
		EventID: movement.EventID,
		OrderID: movement.OrderID,
		OperationType: movement.OperationType,
		Status: movement.Status,
	}); err != nil {
		return err
	}

	return tx.Commit(ctx)
}

func (r *PostgresRepository) UpdateStockAndSaveMovement(ctx context.Context, items []*domain.StockItem, movement *domain.StockMovement) error {
	tx, err := r.pool.Begin(ctx)
	if err != nil {
		return err
	}
	defer tx.Rollback(ctx)

	qtx := r.queries.WithTx(tx)

	for _, item := range items {
		err := qtx.UpdateStockItemQuantity(ctx, database.UpdateStockItemQuantityParams{
			Quantity: int32(item.Quantity),
			UpdatedAt: item.UpdatedAt,
			ID: item.ID,
		})
		if err != nil {
			return err
		}
	}

	if err := qtx.SaveMovement(ctx, database.SaveMovementParams{
		EventID: movement.EventID,
		OrderID: movement.OrderID,
		OperationType: movement.OperationType,
		Status: movement.Status,
	}); err != nil {
		return err
	}

	return tx.Commit(ctx)
}