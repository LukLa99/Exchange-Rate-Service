package com.example.currencyexchange.integration;

import com.example.currencyexchange.Exceptions.NoBankDaysException;
import com.example.currencyexchange.enums.CurrencyCode;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.stream.StreamSupport;

@Component
@Data
public class RiksBankenApiClient {
    private WebClient webClient;

    @Value("${external-api.exchange-rate-api}")
    String riksBankApi;

    @Value("${external-api.exchange-rate-key}")
    String riksBankApiKey;

    @PostConstruct
    public void init() {
        this.webClient =
                WebClient.builder()
                        .baseUrl(riksBankApi)
                        .defaultHeader("Ocp-Apim-Subscription-Key", riksBankApiKey)
                        .build();
    }

    //Hämtar de sju senaste bankdagarna för att hitta den senaste bank dagen
    public LocalDate getLatestBankDay() {
        LocalDate fromDate = LocalDate.now().minusDays(7);
        JsonNode jsonNodes = webClient.get().uri(uriBuilder -> uriBuilder
                        .path("/CalendarDays/{date}")
                        .build(fromDate))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        if (jsonNodes.isNull() || !jsonNodes.isArray() || jsonNodes.isEmpty()) {
            throw new NoBankDaysException("Inga Bankdagar kuna hämtas.");
        }

        return StreamSupport.stream(jsonNodes.spliterator(), false)
                .filter(node -> node.get("swedishBankday").asBoolean(false))
                .map(node -> LocalDate.parse(node.get("calendarDate").asText()))
                .max(Comparator.naturalOrder())
                .orElseThrow(() -> new NoBankDaysException("Inga bankdagar återfanns i listan"));
    }


    public BigDecimal getExchangeRateForDate(final CurrencyCode fromCurrency, final CurrencyCode toCurrency, final LocalDate toDate) {
        sleepIFApiKeyNotAvailable();
        JsonNode jsonNode = webClient.get().uri(uriBuilder -> uriBuilder
                        .path("/CrossRates/{from}/{to}/{date}")
                        .build(fromCurrency.getCode(),
                                toCurrency.getCode(),
                                toDate))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        return new BigDecimal(jsonNode.get(0).get("value").asText());
    }

    /**
     * Om användaren inte definierar en API nyckel kommer applicationen rate-limitas,
     * För att undgå detta sätter vi en Thread.sleep ifall att användaren inte matat in en nyckel
     * Detta kommer att låta oss hämta dagens kurser utan att nå vårt maxtak av anrop per minut.
     */
    private void sleepIFApiKeyNotAvailable() {
        if (riksBankApiKey.isEmpty()) {
            try {
                Thread.sleep(25000);
            } catch (InterruptedException e) {
                throw new RuntimeException("Try again");
            }
        }
    }
}