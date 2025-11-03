CREATE TABLE exchange_rate
(
    id            INT PRIMARY KEY AUTO_INCREMENT,
    from_currency VARCHAR(10)    NOT NULL,
    to_currency   VARCHAR(10)    NOT NULL,
    amount        DECIMAL(10, 4) NOT NULL,
    local_date    DATE           NOT NULL,
    CONSTRAINT unique_currency_date UNIQUE (from_currency, to_currency, local_date));

