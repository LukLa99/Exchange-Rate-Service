package com.example.valutavaxling.integration;

import com.example.valutavaxling.enums.CurrencyCode;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

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

    //Hämtar de sju senaste bankdagarna för att hitta den senaste BankDagen
    public LocalDate getLatestBankDay() {
        LocalDate fromDate = LocalDate.now().minusDays(7);
        JsonNode jsonNodes = webClient.get().uri(uriBuilder -> uriBuilder
                        .path("/CalendarDays/{date}")
                        .build(fromDate))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        if (jsonNodes.isNull() || !jsonNodes.isArray() || jsonNodes.isEmpty()) {
            throw new IllegalStateException("Inga Bankdagar kuna hämtas.");
        }

        return StreamSupport.stream(jsonNodes.spliterator(), false)
                .filter(node -> node.get("swedishBankday").asBoolean(false))
                .map(node -> LocalDate.parse(node.get("calendarDate").asText()))
                .max(Comparator.naturalOrder())
                .orElseThrow(() -> new IllegalStateException("Inga bankdagar återfanns i listan"));
    }

    public Double getExchangeRateForDate(CurrencyCode fromCurrency, CurrencyCode toCurrency, LocalDate toDate) {
        JsonNode jsonNode = webClient.get().uri(uriBuilder -> uriBuilder
                        .path("/CrossRates/{from}/{to}/{date}")
                        .build(fromCurrency,
                                toCurrency,
                                toDate))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

        return jsonNode.get(0).get("value").asDouble();
    }
}
