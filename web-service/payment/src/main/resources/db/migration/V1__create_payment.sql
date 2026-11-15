CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,

    order_id BIGINT NOT NULL UNIQUE,

    amount NUMERIC(12, 2) NOT NULL,

    status VARCHAR(20) NOT NULL
);