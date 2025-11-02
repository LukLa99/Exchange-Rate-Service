package com.example.valutavaxling.service;

import com.example.valutavaxling.Exceptions.ConversionNotFoundException;
import com.example.valutavaxling.entity.ExchangeRate;
import com.example.valutavaxling.enums.CurrencyCode;
import com.example.valutavaxling.integration.RiksBankenApiClient;
import com.example.valutavaxling.repository.ExchangeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class ExchangeRateService {

    private final RiksBankenApiClient riksBankenApiClient;
    private final ExchangeRepository exchangeRepository;
    private final AtomicBoolean initialized = new AtomicBoolean(false);

    public Double getExchangeRate(final Double amount,
                                  final CurrencyCode fromCurrency,
                                  final CurrencyCode toCurrency) {
        if (!initialized.get()) {
            updateDailyExchangeRates();
        }

        try {
            return amount * exchangeRepository.findByFromCurrencyAndToCurrencyAndLocalDate(
                            fromCurrency,
                            toCurrency,
                            LocalDate.now())
                    .getAmount();
        } catch (ConversionNotFoundException e) {
            throw new ConversionNotFoundException("Conversion not found");
        }
    }

    public LocalDate getLatestBankDay() {
        return riksBankenApiClient.getLatestBankDay();
    }

    public void updateDailyExchangeRates() {
        for (CurrencyCode fromCurrency : CurrencyCode.values()) {
            for (CurrencyCode toCurrency : CurrencyCode.values()) {
                if (fromCurrency.equals(toCurrency)) continue;
                Double exchangeRate = riksBankenApiClient.getExchangeRateForDate(fromCurrency, toCurrency, getLatestBankDay());
                exchangeRepository.save(ExchangeRate.builder()
                        .fromCurrency(fromCurrency)
                        .toCurrency(toCurrency)
                        .localDate(LocalDate.now())
                        .amount(exchangeRate)
                        .build());
                initialized.set(true);
            }
        }
    }
}
