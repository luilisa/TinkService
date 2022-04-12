package com.example.tinkoffservice.model;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.Value;

@Value
@RequiredArgsConstructor
public class Stock {
    String ticker;
    String name;
    String figi;
    Currency currency;

}
