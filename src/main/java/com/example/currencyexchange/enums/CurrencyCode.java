package com.example.currencyexchange.enums;

/**
 * These are the available currency-codes for the project.
 */
public enum CurrencyCode {
    SEK("SEKETT"),
    EUR("SEKEURPMI"),
    USD("SEKUSDPMI");

    private final String code;

    CurrencyCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
