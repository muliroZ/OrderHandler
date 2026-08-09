CREATE TABLE outbox (
    id UUID PRIMARY KEY,
    aggregate_type VARCHAR(25) NOT NULL,
    aggregate_id UUID NOT NULL,
    topic VARCHAR(30) NOT NULL,
    payload TEXT NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
)