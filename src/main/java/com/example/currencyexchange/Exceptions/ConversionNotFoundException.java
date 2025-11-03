package com.example.currencyexchange.Exceptions;

public class ConversionNotFoundException extends RuntimeException {
    public ConversionNotFoundException(String message) {
        super(message);
    }
}
