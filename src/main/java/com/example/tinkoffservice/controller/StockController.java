package com.example.tinkoffservice.controller;

import com.example.tinkoffservice.model.Stock;
import com.example.tinkoffservice.model.StockMock;
import com.example.tinkoffservice.service.StockService;
import dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class StockController {
    private final StockService stockService;
    @Autowired
    RestTemplate restTemplate;

    @GetMapping("/stocks/{ticker}")
    public Stock getStock(@PathVariable String ticker) {
        return stockService.getStockByTicker(ticker);
    }

    @GetMapping("/stocks/getStocksByTickers")
    public StocksDto getStocksByTickers() {
        return stockService.getStocksByTickers();
    }

    @GetMapping("/prices")
    public StocksDtoMocks getPrices() {
        return stockService.getPrices();
    }

    @PostMapping("/stocks")
    public String createStocks() {
        StocksDtoMocks stocksDtoMocks = stockService.getPrices();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<StocksDtoMocks> entity = new HttpEntity<StocksDtoMocks>(stocksDtoMocks, headers);

        return restTemplate.exchange(
                "http://localhost:8080/api/v1/stocks", HttpMethod.POST, entity, String.class).getBody();
    }
}
