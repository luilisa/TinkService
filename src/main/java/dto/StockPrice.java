package dto;

import com.example.tinkoffservice.model.Currency;
import lombok.AllArgsConstructor;
import lombok.Value;

@AllArgsConstructor
@Value
public class StockPrice {
    private String figi;
    private Double price;
//    private String ticker;
//    private Currency currency;
}
