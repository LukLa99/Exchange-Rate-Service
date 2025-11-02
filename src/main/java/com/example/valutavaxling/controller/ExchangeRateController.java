package com.example.valutavaxling.controller;

import com.example.valutavaxling.enums.CurrencyCode;
import com.example.valutavaxling.service.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("valuta")
@RequiredArgsConstructor
@Slf4j
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    @GetMapping("/get/{amount}/{fromCurrency}/{toCurrency}")
    public Double getExchangeRates(@PathVariable final Double amount,
                                   @PathVariable final CurrencyCode fromCurrency,
                                   @PathVariable final CurrencyCode toCurrency) throws InterruptedException {
        log.info("Request received to convert amount {} from {} to {} ", amount, fromCurrency, toCurrency);
        return exchangeRateService.getExchangeRate(amount, fromCurrency, toCurrency);
    }

    @GetMapping("/update")
    public ResponseEntity<String> getExchangeRates() throws InterruptedException {
        log.info("Request Received to collect latest Exchange Rates");
        exchangeRateService.updateDailyExchangeRates();
        return ResponseEntity.ok("Daily exchange rates updated");
    }
}
