--#sequence generator
CREATE SEQUENCE accounts_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE accounts (
    id BIGINT PRIMARY KEY DEFAULT nextval('accounts_seq'),
    code VARCHAR(255) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL
);