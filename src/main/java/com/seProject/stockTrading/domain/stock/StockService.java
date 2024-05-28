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

@Service
public class StockService {

    private final StockRepository stockRepository;

    @Autowired
    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    public Stock saveStock(Stock stock) {
        return stockRepository.save(stock);
    }

    public List<Stock> getAllStocks() {
        return stockRepository.findAll();
    }

    public List<Stock> getStocksBySymbol(String stockSymbol) {
        return stockRepository.findByStockSymbol(stockSymbol);
    }

    // 네이버 금융에서 주식 데이터를 크롤링하는 메소드
    public List<Stock> fetchStockData(String stockCode) throws Exception {
        List<Stock> stockData = new ArrayList<>();
        String url = "https://finance.naver.com/item/sise_day.naver?code=" + stockCode;
        Document doc = Jsoup.connect(url).get();

        Elements rows = doc.select("table.type2 tr");

        for (Element row : rows) {
            Elements tds = row.select("td");
            if (tds.size() == 7) {
                String date = tds.get(0).text().trim();
                String closingPrice = tds.get(1).text().trim().replace(",", "");
                String openingPrice = tds.get(3).text().trim().replace(",", "");
                String highPrice = tds.get(4).text().trim().replace(",", "");
                String lowPrice = tds.get(5).text().trim().replace(",", "");
                String volume = tds.get(6).text().trim().replace(",", "");

                if (!date.isEmpty() && !closingPrice.isEmpty() && !openingPrice.isEmpty() && !highPrice.isEmpty() && !lowPrice.isEmpty() && !volume.isEmpty()) {
                    try {
                        Stock stock = new Stock();
                        stock.setStockSymbol(stockCode);
                        stock.setDate(LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy.MM.dd")));
                        stock.setOpenPrice(Float.parseFloat(openingPrice));
                        stock.setClosePrice(Float.parseFloat(closingPrice));
                        stock.setHighPrice(Float.parseFloat(highPrice));
                        stock.setLowPrice(Float.parseFloat(lowPrice));
                        stock.setVolume(Integer.parseInt(volume));

                        stockData.add(stock);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return stockData;
    }
}
