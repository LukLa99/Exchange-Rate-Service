package com.example.currencyexchange;

import com.example.currencyexchange.Exceptions.ConversionNotFoundException;
import com.example.currencyexchange.Exceptions.DuplicateExchangeRateException;
import com.example.currencyexchange.entity.ExchangeRate;
import com.example.currencyexchange.enums.CurrencyCode;
import com.example.currencyexchange.integration.RiksBankenApiClient;
import com.example.currencyexchange.repository.ExchangeRepository;
import com.example.currencyexchange.service.ExchangeRateService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest
class ExchangeRateServiceTests {

    @Mock
    private RiksBankenApiClient riksBankenApiClient;

    @Mock
    private ExchangeRepository exchangeRepository;

    @InjectMocks
    private ExchangeRateService exchangeRateService;

    @Test
    void testGetExchangeRate_sameCurrency_returnsSameAmount() throws InterruptedException {
        BigDecimal amount = BigDecimal.valueOf(100);
        String result = exchangeRateService.getExchangeRate(amount, CurrencyCode.SEKETT, CurrencyCode.SEKETT);
        assertEquals("100 SEKETT", result);
    }

    @Test
    void testGetExchangeRate_validConversion_returnsConvertedAmount() throws InterruptedException {
        BigDecimal amount = BigDecimal.valueOf(100);
        BigDecimal rate = BigDecimal.valueOf(10);
        ExchangeRate mockRate = ExchangeRate.builder()
                .fromCurrency(CurrencyCode.SEKETT)
                .toCurrency(CurrencyCode.SEKUSDPMI)
                .amount(rate)
                .localDate(LocalDate.now())
                .build();

        when(exchangeRepository.findByFromCurrencyAndToCurrencyAndLocalDate(
                any(), any(), any())).thenReturn(mockRate);

        String result = exchangeRateService.getExchangeRate(amount, CurrencyCode.SEKETT, CurrencyCode.SEKUSDPMI);
        assertEquals("1000 SEKUSDPMI", result);
    }

    @Test
    void testGetExchangeRate_conversionNotFound_throwsException() {
        when(exchangeRepository.findByFromCurrencyAndToCurrencyAndLocalDate(any(), any(), any()))
                .thenThrow(new ConversionNotFoundException("Not found"));

        assertThrows(ConversionNotFoundException.class, () ->
                exchangeRateService.getExchangeRate(BigDecimal.ONE, CurrencyCode.SEKETT, CurrencyCode.SEKUSDPMI)
        );
    }

    @Test
    void testUpdateDailyExchangeRates_savesExchangeRatesSuccessfully() throws InterruptedException {
        when(riksBankenApiClient.getLatestBankDay()).thenReturn(LocalDate.now());
        when(riksBankenApiClient.getExchangeRateForDate(any(), any(), any())).thenReturn(BigDecimal.valueOf(10));
        when(exchangeRepository.findByFromCurrencyAndToCurrencyAndLocalDate(any(), any(), any())).thenReturn(null);

        exchangeRateService.updateDailyExchangeRates();

        verify(exchangeRepository, atLeastOnce()).save(any(ExchangeRate.class));
    }

    @Test
    void testUpdateDailyExchangeRates_duplicateKey_throwsDuplicateExchangeRateException() throws InterruptedException {
        when(riksBankenApiClient.getLatestBankDay()).thenReturn(LocalDate.now());
        when(riksBankenApiClient.getExchangeRateForDate(any(), any(), any())).thenReturn(BigDecimal.valueOf(10));
        when(exchangeRepository.findByFromCurrencyAndToCurrencyAndLocalDate(any(), any(), any())).thenReturn(null);
        doThrow(new DuplicateKeyException("")).when(exchangeRepository).save(any(ExchangeRate.class));

        assertThrows(DuplicateExchangeRateException.class,
                () -> exchangeRateService.updateDailyExchangeRates());
    }

    @Test
    void testGetLatestBankDay_returnsDateFromApi() {
        LocalDate mockDate = LocalDate.of(2025, 11, 2);
        when(riksBankenApiClient.getLatestBankDay()).thenReturn(mockDate);
        assertEquals(mockDate, exchangeRateService.getLatestBankDay());
    }
}