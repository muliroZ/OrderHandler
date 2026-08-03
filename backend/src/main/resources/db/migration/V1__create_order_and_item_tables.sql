CREATE TABLE IF NOT EXISTS items (
    id UUID PRIMARY KEY,
    name VARCHAR(75) NOT NULL,
    description TEXT,
    price DECIMAL(11,2) NOT NULL,
    category VARCHAR DEFAULT 'OUTROS',
    stock INTEGER NOT NULL,
    active BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS orders (
    id UUID PRIMARY KEY,
    client_id UUID NOT NULL,
    subtotal DECIMAL(11,2) NOT NULL,
    status VARCHAR DEFAULT 'PENDENTE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS order_items (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL,
    item_id UUID NOT NULL,
    quantity INTEGER NOT NULL,
    unit_price DECIMAL(11,2) NOT NULL,

    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (item_id) REFERENCES items(id)
);