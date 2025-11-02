package com.example.valutavaxling.repository;

import com.example.valutavaxling.entity.ExchangeRate;
import com.example.valutavaxling.enums.CurrencyCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface ExchangeRepository extends JpaRepository<ExchangeRate, Long> {
  ExchangeRate findByFromCurrencyAndToCurrencyAndLocalDate(CurrencyCode fromCurrency, CurrencyCode toCurrency, LocalDate localDate);

}

