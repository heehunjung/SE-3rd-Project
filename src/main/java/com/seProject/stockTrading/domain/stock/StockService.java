package com.seProject.stockTrading.domain.stock;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StockService {

    private static final Logger logger = LoggerFactory.getLogger(StockService.class);

    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private StockPriceRepository stockPriceRepository;

    public List<StockPrice> fetchStockData(Stock stock, int page) throws Exception {
        List<StockPrice> stockData = new ArrayList<>();
        String url = "https://finance.naver.com/item/sise_day.naver?code=" + stock.getStockSymbol() + "&page=" + page;
        Document doc = Jsoup.connect(url).timeout(60000).get();

        Elements rows = doc.select("table.type2 tr");

        for (Element row : rows) {
            Elements tds = row.select("td");
            if (tds.size() >= 7) { // 최소한 7개의 <td> 요소가 있는지 확인
                String date = tds.get(0).text().trim();
                String closingPrice = tds.get(1).text().trim().replace(",", "");
                String openingPrice = tds.get(3).text().trim().replace(",", "");
                String highPrice = tds.get(4).text().trim().replace(",", "");
                String lowPrice = tds.get(5).text().trim().replace(",", "");
                String volume = tds.get(6).text().trim().replace(",", "");

                logger.info("Parsed data - Date: {}, Close: {}, Open: {}, High: {}, Low: {}, Volume: {}", date, closingPrice, openingPrice, highPrice, lowPrice, volume);

                if (!date.isEmpty() && !closingPrice.isEmpty() && !openingPrice.isEmpty() && !highPrice.isEmpty() && !lowPrice.isEmpty() && !volume.isEmpty()) {
                    StockPrice stockPrice = new StockPrice();
                    stockPrice.setDate(LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy.MM.dd")));
                    stockPrice.setOpenPrice(Float.parseFloat(openingPrice));
                    stockPrice.setClosingPrice(Float.parseFloat(closingPrice));
                    stockPrice.setHighPrice(Float.parseFloat(highPrice));
                    stockPrice.setLowPrice(Float.parseFloat(lowPrice));
                    stockPrice.setVolume(Integer.parseInt(volume));
                    stockPrice.setStock(stock);

                    stockData.add(stockPrice);
                }
            }
        }
        return stockData;
    }

    public void saveStockData(Stock stock, List<StockPrice> stockPrices) {
        for (StockPrice stockPrice : stockPrices) {
            stockPriceRepository.save(stockPrice);
        }
        logger.info("Saved stock data for symbol {}: {} entries", stock.getStockSymbol(), stockPrices.size());
    }

    public void fetchAndSaveAllStocks() throws Exception {
        List<Stock> stocks = stockRepository.findAll();
        for (Stock stock : stocks) {
            String symbol = stock.getStockSymbol();
            logger.info("Fetching data for symbol: {}", symbol);
            List<StockPrice> allStockPrices = new ArrayList<>();
            for (int page = 1; page <= 10; page++) {
                List<StockPrice> stockPrices = fetchStockData(stock, page);
                allStockPrices.addAll(stockPrices);
                logger.info("Fetched stock data for symbol {} from page {}: {} entries", symbol, page, stockPrices.size());
            }
            saveStockData(stock, allStockPrices);
        }
        logger.info("All stock data fetched and saved successfully.");
    }

    private String fetchStockName(String symbol) {
        try {
            String url = "https://finance.naver.com/item/main.naver?code=" + symbol;
            Document doc = Jsoup.connect(url).timeout(60000).get();
            String title = doc.title();
            if (title != null && title.contains(":")) {
                return title.split(":")[0].trim();
            } else {
                return "Unknown";
            }
        } catch (Exception e) {
            return "Unknown";
        }
    }
}
