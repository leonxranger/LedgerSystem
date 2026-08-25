    --SEQUENCE GENERATOR
CREATE SEQUENCE transaction_seq START WITH 1 INCREMENT BY 50;
CREATE SEQUENCE transaction_items_seq START WITH 1 INCREMENT BY 50;


--table schema
CREATE TABLE Transactions(
    id BIGINT PRIMARY KEY DEFAULT nextval('transaction_seq'),
    date TIMESTAMP NOT NULL,
    description VARCHAR(255)
);

CREATE TABLE transaction_items(
    id BIGINT PRIMARY KEY DEFAULT nextval('transaction_items_seq'),
    transaction_id BIGINT NOT NULL,
    account_id BIGINT NOT NULL,
    amount NUMERIC NOT NULL,

    CONSTRAINT fk_transaction FOREIGN KEY (transaction_id) REFERENCES Transactions(id),
    CONSTRAINT fk_account FOREIGN KEY (account_id) REFERENCES accounts(id)
)