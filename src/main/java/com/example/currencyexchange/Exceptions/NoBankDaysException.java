package com.example.currencyexchange.Exceptions;

public class NoBankDaysException extends RuntimeException {
    public NoBankDaysException(String message) {
        super(message);
    }
}
