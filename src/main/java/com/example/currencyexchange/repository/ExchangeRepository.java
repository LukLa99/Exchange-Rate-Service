package com.example.currencyexchange.repository;

import com.example.currencyexchange.entity.ExchangeRate;
import com.example.currencyexchange.enums.CurrencyCode;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface ExchangeRepository extends CrudRepository<ExchangeRate, Integer> {
  ExchangeRate findByFromCurrencyAndToCurrencyAndLocalDate(CurrencyCode fromCurrency, CurrencyCode toCurrency, LocalDate localDate);

}

