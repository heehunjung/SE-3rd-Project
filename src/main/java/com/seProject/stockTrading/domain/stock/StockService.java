package com.seProject.stockTrading.domain.stock;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StockService {

    private final StockRepository stockRepository;
    private final StockPriceRepository stockPriceRepository;

    @Autowired
    public StockService(StockRepository stockRepository, StockPriceRepository stockPriceRepository) {
        this.stockRepository = stockRepository;
        this.stockPriceRepository = stockPriceRepository;
    }

    public Stock saveStock(Stock stock) {
        return stockRepository.save(stock);
    }

    public StockPrice saveStockPrice(StockPrice stockPrice) {
        return stockPriceRepository.save(stockPrice);
    }

    public List<Stock> getAllStocks() {
        return stockRepository.findAll();
    }

    public Optional<Stock> getStockBySymbol(String stockSymbol) {
        return stockRepository.findByStockSymbol(stockSymbol);
    }

    public void fetchAndSaveAllStockData(List<String> stockCodes) throws Exception {
        for (String stockCode : stockCodes) {
            Optional<Stock> stockOptional = stockRepository.findByStockSymbol(stockCode);
            Stock stock = stockOptional.orElseGet(() -> {
                Stock newStock = new Stock();
                newStock.setStockSymbol(stockCode);
                newStock.setStockName("Stock Name"); // 필요한 경우 주식 이름 설정
                newStock.setVolume(0); // 기본값 설정
                return stockRepository.save(newStock);
            });

            for (int page = 1; page <= 10; page++) { // 페이지 수를 조절하세요
                List<StockPrice> stockPrices = fetchStockData(stock, page);
                for (StockPrice stockPrice : stockPrices) {
                    stockPrice.setStock(stock);
                    stockPriceRepository.save(stockPrice);
                }
                Thread.sleep(1000); // 각 페이지 사이에 1초 대기
            }
        }
    }

    public List<StockPrice> fetchStockData(Stock stock, int page) throws Exception {
        List<StockPrice> stockData = new ArrayList<>();
        String url = "https://finance.naver.com/item/sise_day.naver?code=" + stock.getStockSymbol() + "&page=" + page;
        Document doc = Jsoup.connect(url).get();

        Elements rows = doc.select("table.type2 tr");

        for (Element row : rows) {
            Elements tds = row.select("td");
            if (tds.size() >= 7) { // 변경: 최소한 7개의 <td> 요소가 있는지 확인
                String date = tds.get(0).text().trim();
                String closingPrice = tds.get(1).text().trim().replace(",", "");
                String openingPrice = tds.get(3).text().trim().replace(",", "");
                String highPrice = tds.get(4).text().trim().replace(",", "");
                String lowPrice = tds.get(5).text().trim().replace(",", "");
                String volume = tds.get(6).text().trim().replace(",", "");

                if (!date.isEmpty() && !closingPrice.isEmpty() && !openingPrice.isEmpty() && !highPrice.isEmpty() && !lowPrice.isEmpty() && !volume.isEmpty()) {
                    StockPrice stockPrice = new StockPrice();
                    stockPrice.setDate(LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy.MM.dd")));
                    stockPrice.setOpenPrice(Float.parseFloat(openingPrice));
                    stockPrice.setClosePrice(Float.parseFloat(closingPrice));
                    stockPrice.setHighPrice(Float.parseFloat(highPrice));
                    stockPrice.setLowPrice(Float.parseFloat(lowPrice));
                    stockPrice.setVolume(Integer.parseInt(volume));

                    stockData.add(stockPrice);
                }
            }
        }

        return stockData;
    }
}
