-- E-Commerce Order & Inventory Management System
-- MySQL 8 schema

CREATE DATABASE IF NOT EXISTS ecom_oims;
USE ecom_oims;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS stock_movement;
DROP TABLE IF EXISTS return_request;
DROP TABLE IF EXISTS order_item;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS cart_item;
DROP TABLE IF EXISTS wishlist;
DROP TABLE IF EXISTS product;
DROP TABLE IF EXISTS customer;
DROP TABLE IF EXISTS seller;
DROP TABLE IF EXISTS category;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE category (
    category_id   INT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(60)  NOT NULL UNIQUE,
    description   VARCHAR(255)
);

CREATE TABLE seller (
    seller_id     INT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    email         VARCHAR(120) NOT NULL UNIQUE,
    phone         VARCHAR(15)  NOT NULL,
    city          VARCHAR(60),
    gstin         VARCHAR(15)  UNIQUE,
    joined_on     DATE         NOT NULL
);

CREATE TABLE customer (
    customer_id   INT AUTO_INCREMENT PRIMARY KEY,
    first_name    VARCHAR(50)  NOT NULL,
    last_name     VARCHAR(50)  NOT NULL,
    email         VARCHAR(120) NOT NULL UNIQUE,
    phone         VARCHAR(15)  NOT NULL,
    address       VARCHAR(255) NOT NULL,
    city          VARCHAR(60)  NOT NULL,
    pincode       VARCHAR(6)   NOT NULL,
    registered_on DATE         NOT NULL
);

CREATE TABLE product (
    product_id    INT AUTO_INCREMENT PRIMARY KEY,
    sku           VARCHAR(30)   NOT NULL UNIQUE,
    name          VARCHAR(120)  NOT NULL,
    description   VARCHAR(500),
    price         DECIMAL(10,2) NOT NULL CHECK (price > 0),
    stock_qty     INT           NOT NULL DEFAULT 0 CHECK (stock_qty >= 0),
    reorder_level INT           NOT NULL DEFAULT 5,
    active        BOOLEAN       NOT NULL DEFAULT TRUE,
    category_id   INT           NOT NULL,
    seller_id     INT           NOT NULL,
    created_on    DATE          NOT NULL,
    CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES category(category_id),
    CONSTRAINT fk_product_seller   FOREIGN KEY (seller_id)   REFERENCES seller(seller_id)
);

-- many-to-many between customer and product
CREATE TABLE wishlist (
    customer_id   INT NOT NULL,
    product_id    INT NOT NULL,
    PRIMARY KEY (customer_id, product_id),
    CONSTRAINT fk_wish_customer FOREIGN KEY (customer_id) REFERENCES customer(customer_id) ON DELETE CASCADE,
    CONSTRAINT fk_wish_product  FOREIGN KEY (product_id)  REFERENCES product(product_id)  ON DELETE CASCADE
);

CREATE TABLE cart_item (
    cart_item_id  INT AUTO_INCREMENT PRIMARY KEY,
    customer_id   INT NOT NULL,
    product_id    INT NOT NULL,
    quantity      INT NOT NULL CHECK (quantity BETWEEN 1 AND 10),
    added_at      DATETIME NOT NULL,
    CONSTRAINT uq_cart UNIQUE (customer_id, product_id),
    CONSTRAINT fk_cart_customer FOREIGN KEY (customer_id) REFERENCES customer(customer_id) ON DELETE CASCADE,
    CONSTRAINT fk_cart_product  FOREIGN KEY (product_id)  REFERENCES product(product_id)
);

CREATE TABLE orders (
    order_id         INT AUTO_INCREMENT PRIMARY KEY,
    order_number     VARCHAR(20)   NOT NULL UNIQUE,
    customer_id      INT           NOT NULL,
    order_date       DATETIME      NOT NULL,
    status           VARCHAR(15)   NOT NULL,   -- PLACED, SHIPPED, DELIVERED, CANCELLED
    payment_mode     VARCHAR(10)   NOT NULL,   -- COD, UPI, CARD
    shipping_address VARCHAR(255)  NOT NULL,
    total_amount     DECIMAL(12,2) NOT NULL,
    delivered_on     DATE,
    CONSTRAINT fk_order_customer FOREIGN KEY (customer_id) REFERENCES customer(customer_id)
);

CREATE TABLE order_item (
    order_item_id INT AUTO_INCREMENT PRIMARY KEY,
    order_id      INT           NOT NULL,
    product_id    INT           NOT NULL,
    quantity      INT           NOT NULL CHECK (quantity > 0),
    unit_price    DECIMAL(10,2) NOT NULL,
    line_total    DECIMAL(12,2) NOT NULL,
    CONSTRAINT fk_item_order   FOREIGN KEY (order_id)   REFERENCES orders(order_id),
    CONSTRAINT fk_item_product FOREIGN KEY (product_id) REFERENCES product(product_id)
);

CREATE TABLE return_request (
    return_id     INT AUTO_INCREMENT PRIMARY KEY,
    order_item_id INT           NOT NULL,
    quantity      INT           NOT NULL CHECK (quantity > 0),
    reason        VARCHAR(255)  NOT NULL,
    status        VARCHAR(10)   NOT NULL,   -- REQUESTED, APPROVED, REJECTED
    refund_amount DECIMAL(12,2),
    requested_on  DATETIME      NOT NULL,
    processed_on  DATETIME,
    CONSTRAINT fk_return_item FOREIGN KEY (order_item_id) REFERENCES order_item(order_item_id)
);

-- every change to stock is logged here (used for recent activity + audit)
CREATE TABLE stock_movement (
    movement_id   INT AUTO_INCREMENT PRIMARY KEY,
    product_id    INT          NOT NULL,
    change_qty    INT          NOT NULL,
    movement_type VARCHAR(12)  NOT NULL,    -- SALE, CANCEL, RETURN, RESTOCK
    reference     VARCHAR(40),
    created_at    DATETIME     NOT NULL,
    CONSTRAINT fk_move_product FOREIGN KEY (product_id) REFERENCES product(product_id)
);

CREATE INDEX idx_orders_customer ON orders(customer_id, order_date);
CREATE INDEX idx_orders_status   ON orders(status);
CREATE INDEX idx_product_name    ON product(name);
