package com.example.currencyexchange.entity;

import com.example.currencyexchange.enums.CurrencyCode;
import lombok.Builder;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record ExchangeRate(
        @Id
        Integer id,
        CurrencyCode fromCurrency,
        CurrencyCode toCurrency,
        BigDecimal amount,
        LocalDate localDate) {
}