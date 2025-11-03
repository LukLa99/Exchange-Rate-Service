package com.example.currencyexchange.utils;

import com.example.currencyexchange.service.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExchangeRateScheduler {
    private final ExchangeRateService exchangeRateService;

    @Scheduled(cron = "0 15 16 * * *", zone = "Europe/Stockholm")
    public void fetchDailyExchangeRates() {
        log.info("Fetching exchange rate data");
        exchangeRateService.updateDailyExchangeRates();
    }
}
