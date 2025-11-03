package com.example.currencyexchange.service;

import com.example.currencyexchange.Exceptions.ConversionNotFoundException;
import com.example.currencyexchange.Exceptions.DuplicateExchangeRateException;
import com.example.currencyexchange.entity.ExchangeRate;
import com.example.currencyexchange.enums.CurrencyCode;
import com.example.currencyexchange.integration.RiksBankenApiClient;
import com.example.currencyexchange.repository.ExchangeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class ExchangeRateService {

    private final RiksBankenApiClient riksBankenApiClient;
    private final ExchangeRepository exchangeRepository;
    private final AtomicBoolean initialized = new AtomicBoolean(false);

    public String getExchangeRate(final BigDecimal amount,
                                  final CurrencyCode fromCurrency,
                                  final CurrencyCode toCurrency)  {
        if (!initialized.get()) {
            updateDailyExchangeRates();
        }

        if (fromCurrency.equals(toCurrency)) {
            return String.format("%s %s", amount, toCurrency);
        }

        try {
            return String.format("%s %s",
                    amount.multiply(exchangeRepository.findByFromCurrencyAndToCurrencyAndLocalDate(
                                    fromCurrency,
                                    toCurrency,
                                    LocalDate.now())
                            .amount()),
                    toCurrency);
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
                BigDecimal exchangeRate = riksBankenApiClient.getExchangeRateForDate(fromCurrency, toCurrency, getLatestBankDay());
                try {
                    exchangeRepository.save(ExchangeRate.builder()
                            .fromCurrency(fromCurrency)
                            .toCurrency(toCurrency)
                            .localDate(LocalDate.now())
                            .amount(exchangeRate)
                            .build());
                } catch (Exception e) {
                    throw new DuplicateExchangeRateException("The daily Exchange Rates are already collected.");
                }
            }
        }
        initialized.set(true);
    }
}

