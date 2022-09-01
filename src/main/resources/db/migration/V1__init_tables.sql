CREATE TABLE orders (
    id UUID NOT NULL PRIMARY KEY,
    client_id BIGINT NOt NULL,
    state VARCHAR (100),
    city VARCHAR,
    street VARCHAR,
    building_number VARCHAR,
    contact_phone_number VARCHAR,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
CREATE TABLE order_products (
    id BIGSERIAL NOT NULL PRIMARY KEY,
    product_id UUID NOT NULL,
    order_id UUID NOT NULL,
    amount INTEGER NOT NULL,
    measure_unit VARCHAR (100),
    price numeric(18,2),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

ALTER TABLE order_products ADD CONSTRAINT fk_order_products_orders  FOREIGN KEY (order_id) REFERENCES orders(id);

CREATE TABLE order_state_machine_context (
    machine_id VARCHAR (255) not null PRIMARY KEY,
    state VARCHAR (255),
    state_machine_context oid
);
