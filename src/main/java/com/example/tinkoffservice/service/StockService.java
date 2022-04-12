package com.example.tinkoffservice.service;

import com.example.tinkoffservice.model.Stock;
import dto.*;

public interface StockService {
    Stock getStockByTicker(String ticker);
    StocksDto getStocksByTickers();
    StocksDtoMocks getPrices();
    StocksDtoMocks sendStocks();
}
