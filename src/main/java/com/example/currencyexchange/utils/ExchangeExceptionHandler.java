package com.example.currencyexchange.utils;

import com.example.currencyexchange.Exceptions.ConversionNotFoundException;
import com.example.currencyexchange.Exceptions.NoBankDaysException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExchangeExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.getMessage());
    }

    @ExceptionHandler(ConversionNotFoundException.class)
    public ResponseEntity<String> handleConversionNotFoundException(ConversionNotFoundException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }

    @ExceptionHandler(NoBankDaysException.class)
    public ResponseEntity<String> handleNoBankDaysException(NoBankDaysException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }
}
