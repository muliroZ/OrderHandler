CREATE TABLE IF NOT EXISTS payments (
    id UUID PRIMARY KEY,
    event_id UUID UNIQUE NOT NULL,
    order_id UUID NOT NULL,
    amount DECIMAL(11, 2) NOT NULL,
    method VARCHAR(30) NOT NULL,
    customer_email VARCHAR(255) NOT NULL,
    card_token VARCHAR(255) NULL,
    installments INTEGER NULL DEFAULT 1,
    status VARCHAR(30) NOT NULL,
    gateway_payment_id BIGINT NULL,
    failure_reason TEXT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP WITH TIME ZONE NULL
);

CREATE TABLE IF NOT EXISTS outbox (
    id UUID PRIMARY KEY,
    aggregate_type VARCHAR(25) NOT NULL,
    aggregate_id UUID NOT NULL,
    topic VARCHAR(30) NOT NULL,
    payload TEXT NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_payments_order_id ON payments(order_id);