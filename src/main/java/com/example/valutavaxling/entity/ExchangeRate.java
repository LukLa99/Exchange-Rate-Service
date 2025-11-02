package com.example.valutavaxling.entity;

import com.example.valutavaxling.enums.CurrencyCode;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class ExchangeRate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Enumerated(EnumType.STRING)
    CurrencyCode fromCurrency;
    @Enumerated(EnumType.STRING)
    CurrencyCode toCurrency;
    Double amount;
    LocalDate localDate;
}