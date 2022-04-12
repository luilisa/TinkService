package com.example.tinkoffservice.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockMock {
    String ticker;
    String name;
    String figi;
    Currency currency;
    double price;

}
