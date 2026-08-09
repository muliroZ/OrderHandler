CREATE TABLE IF NOT EXISTS stock_items (
    item_id UUID PRIMARY KEY,
    quantity INTEGER NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS stock_movements (
    id UUID PRIMARY KEY,
    event_id UUID UNIQUE NOT NULL,
    order_id UUID NULL,
    type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_stock_movements_event_id ON stock_movements(event_id);