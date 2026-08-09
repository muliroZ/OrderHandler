-- name: FindItemByID :one
SELECT * FROM stock_items
WHERE item_id = ANY($1)
FOR UPDATE;