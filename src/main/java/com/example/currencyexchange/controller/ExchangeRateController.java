package com.example.currencyexchange.controller;

import com.example.currencyexchange.enums.CurrencyCode;
import com.example.currencyexchange.service.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("valuta")
@RequiredArgsConstructor
@Slf4j
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    @GetMapping("/get/{amount}/{fromCurrency}/{toCurrency}")
    public ResponseEntity<String> getExchangeRates(@PathVariable final BigDecimal amount,
                                           @PathVariable final CurrencyCode fromCurrency,
                                           @PathVariable final CurrencyCode toCurrency) {
        log.info("Request received to convert amount {} from {} to {} ", amount, fromCurrency, toCurrency);
        return ResponseEntity.ok(exchangeRateService.getExchangeRate(amount, fromCurrency, toCurrency));
    }


    /**
     * This API is used to collect the daily Exchange Rates from the external API that Riksbanken provide.
     *
     * @return a ResponsEntity detailing whether the request was successful.
     */
    @PostMapping("/update")
    public ResponseEntity<String> getExchangeRates() {
        log.info("Request Received to collect latest Exchange Rates");
        exchangeRateService.updateDailyExchangeRates();
        return ResponseEntity.ok("Daily exchange rates updated");
    }
}
