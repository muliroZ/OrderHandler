-- name: SaveItem :exec
INSERT INTO stock_items (id, quantity, updated_at)
VALUES ($1, $2, $3)
ON CONFLICT (id) DO UPDATE
SET quantity = EXCLUDED.quantity, updated_at = EXCLUDED.updated_at;

-- name: FindItemByID :one
SELECT * FROM stock_items
WHERE id = $1;

-- name: FindItemsByIDs :many
SELECT * FROM stock_items
WHERE id = ANY($1::uuid[])
FOR UPDATE;

-- name: IsEventProcessed :one
SELECT EXISTS (SELECT 1 FROM stock_movements WHERE event_id = $1);

-- name: SaveMovement :exec
INSERT INTO stock_movements (event_id, order_id, operation_type, status)
VALUES ($1, $2, $3, $4);

-- name: UpdateStockItemQuantity :exec
UPDATE stock_items 
SET quantity = $1, updated_at = $2 
WHERE id = $3;