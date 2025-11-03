# Valutaväxlingstjänsten

En enkel tjänst som hjälper användare att konvertera valutor baserat på dagliga växelkurser.
Applikationen uppdaterar dagens växlingskurser automatiskt vid klockan 16:15!

---

## Funktioner

- Konvertera mellan valutor som SEK, EUR och USD.
- Växelkurser hämtas dagligen från Riksbankens API.
- Kurserna cachas för att minska onödiga anrop till Riksbanken.
---

## Installation & Uppstart

För att starta tjänsten för optimal användning bör användaren ha en API nyckel till Riksbankens API annars kommer svaren vara avsevärt långsammare.

För att starta applicationen ska följande kommando köras:
```bash
mvn spring-boot:run
```

För att köra applicationens tester ska följande kommando köras:
```bash
mvn test
```
---
## Länkar

När applicationen körs kommer användaren komma in på en Swagger via URLen 
 
För att se och testa alla endpoints, öppna [Swagger UI](http://localhost:8080/swagger-ui/index.html) 

För att komma åt tjänstens H2 Databas, öppna [H2 konsolen](http://localhost:8080/h2-console)

Skriv sedan in följande information:
- JDBC URL: jdbc:h2:mem:testdb
- User name: sa


