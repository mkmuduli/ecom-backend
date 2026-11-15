CREATE TABLE inventory (
    id BIGSERIAL PRIMARY KEY,

    product_id BIGINT NOT NULL UNIQUE,

    available_quantity INTEGER NOT NULL,

    reserved_quantity INTEGER NOT NULL DEFAULT 0,

    CONSTRAINT chk_available_quantity
        CHECK (available_quantity >= 0),

    CONSTRAINT chk_reserved_quantity
        CHECK (reserved_quantity >= 0)
);