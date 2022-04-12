package com.example.tinkoffservice.service;

import com.example.tinkoffservice.config.ApiConfig;
import com.example.tinkoffservice.exception.StockNotFoundException;
import com.example.tinkoffservice.model.Currency;
import com.example.tinkoffservice.model.Stock;
import com.example.tinkoffservice.model.StockMock;
import dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;
import ru.tinkoff.invest.openapi.OpenApi;
import ru.tinkoff.invest.openapi.model.rest.MarketInstrumentList;
import ru.tinkoff.invest.openapi.model.rest.Orderbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@EnableConfigurationProperties(ApiConfig.class)
@RequiredArgsConstructor
public class TinkoffStockService implements StockService{
    private final OpenApi openApi;
    private final ApiConfig stocks;


    @Async
    public CompletableFuture<MarketInstrumentList> getMarketInstrumentTicker(String ticker) {
        var context = openApi.getMarketContext();
        return context.searchMarketInstrumentsByTicker(ticker);
    }


    @Override
    public Stock getStockByTicker(String ticker) {
       var cf = getMarketInstrumentTicker(ticker);
        var list = cf.join().getInstruments();
        if (list.isEmpty()) {
            throw new StockNotFoundException(String.format("Stock %S not found.", ticker));
        }

        var item = list.get(0);
        return new Stock(
                item.getTicker(),
                item.getName(),
                item.getFigi(),
                Currency.valueOf(item.getCurrency().getValue()));
    }

    @Override
    public StocksDto getStocksByTickers() {
        List<CompletableFuture<MarketInstrumentList>> marketInstrument = new ArrayList<>();
        stocks.getStocks().forEach(ticker -> marketInstrument.add(getMarketInstrumentTicker(ticker)));
         List<Stock> stocks = marketInstrument.stream()
                 .map(CompletableFuture::join)
                 .map(mi -> {
                     if(!mi.getInstruments().isEmpty()) {
                         return mi.getInstruments().get(0);
                     }
                     return null;
                 })
                 .filter(el -> Objects.nonNull(el))
                 .map(mi -> new Stock(
                         mi.getTicker(),
                         mi.getName(),
                         mi.getFigi(),
                         Currency.valueOf(mi.getCurrency().getValue())
                 ))
                 .collect(Collectors.toList());
         return new StocksDto(stocks);
    }

    @Async
    public CompletableFuture<Optional<Orderbook>> getOrderBookByFigi(String figi) {
        var orderBook = openApi.getMarketContext().getMarketOrderbook(figi, 0);
        return orderBook;
    }

    public static Double findByFigi(List<StockPrice> listStockPrice, String figi) {
        return listStockPrice.stream().filter(stockPrice -> figi.equals(stockPrice.getFigi())).findFirst().orElse(null).getPrice();
    }
    @Override
    public StocksDtoMocks getPrices() {
        StocksDto stocksDto = this.getStocksByTickers();

        List<CompletableFuture<Optional<Orderbook>>> orderBooks = new ArrayList<>();
        stocksDto.getStocks().forEach(figi -> orderBooks.add(getOrderBookByFigi(figi.getFigi())));
        var listPrices = orderBooks.stream()
                .map(CompletableFuture::join)
                .map(oo -> oo.orElseThrow(() -> new StockNotFoundException("Stock not found.")))
                .map(orderbook -> new StockPrice(
                        orderbook.getFigi(),
                        orderbook.getLastPrice().doubleValue()
                ))
                .collect(Collectors.toList());

        List<StockMock> stockMocks = new ArrayList<>();
        StocksPricesDto stocksPricesDto = new StocksPricesDto(listPrices);
        for (int i=0; i<stocksDto.getStocks().size(); i++)
        {
            Stock stock = stocksDto.getStocks().get(i);
            StockMock stockMock = new StockMock(
                    stock.getTicker(),
                    stock.getName(),
                    stock.getFigi(),
                    stock.getCurrency(),
                    findByFigi(stocksPricesDto.getPrices(), stock.getFigi())
                    );
            stockMocks.add(i,stockMock);
        }
        return new StocksDtoMocks(stockMocks);
    }

    @Override
    public StocksDtoMocks sendStocks() {
        return getPrices();
    }

}
