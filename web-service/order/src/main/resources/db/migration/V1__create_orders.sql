CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,

    product_id BIGINT NOT NULL,

    quantity INTEGER NOT NULL,

    amount NUMERIC(12, 2) NOT NULL,

    status VARCHAR(30) NOT NULL
);