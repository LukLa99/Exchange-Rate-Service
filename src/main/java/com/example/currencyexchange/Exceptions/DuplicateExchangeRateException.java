package com.example.currencyexchange.Exceptions;

public class DuplicateExchangeRateException extends RuntimeException {
    public DuplicateExchangeRateException(String message) {
        super(message);
    }
}
