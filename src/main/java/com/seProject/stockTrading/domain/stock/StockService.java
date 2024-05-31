package com.seProject.stockTrading.domain.stock;

import com.seProject.stockTrading.domain.stockPrice.StockPrice;
import com.seProject.stockTrading.domain.stockPrice.StockPriceRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class StockService {

    private static final Logger logger = LoggerFactory.getLogger(StockService.class);

    private final StockRepository stockRepository;
    private final StockPriceRepository stockPriceRepository;

    @Autowired
    public StockService(StockRepository stockRepository, StockPriceRepository stockPriceRepository) {
        this.stockRepository = stockRepository;
        this.stockPriceRepository = stockPriceRepository;
    }

    // 주식 데이터를 가져오는 메서드
    public List<StockPrice> fetchStockData(Stock stock, int page) throws Exception {
        List<StockPrice> stockData = new ArrayList<>();
        String url = "https://finance.naver.com/item/sise_day.naver?code=" + stock.getStockSymbol() + "&page=" + page;
        logger.info("Fetching URL: {}", url);
        Document doc = Jsoup.connect(url).timeout(60000).get();

        Elements rows = doc.select("table.type2 tr");

        for (Element row : rows) {
            Elements tds = row.select("td");
            if (tds.size() >= 7) {
                String date = tds.get(0).text().trim();
                String closingPrice = tds.get(1).text().trim().replace(",", "");
                String openingPrice = tds.get(3).text().trim().replace(",", "");
                String highPrice = tds.get(4).text().trim().replace(",", "");
                String lowPrice = tds.get(5).text().trim().replace(",", "");
                String volume = tds.get(6).text().trim().replace(",", "");

                logger.info("Parsed data - Date: {}, Close: {}, Open: {}, High: {}, Low: {}, Volume: {}", date, closingPrice, openingPrice, highPrice, lowPrice, volume);

                if (!date.isEmpty() && !closingPrice.isEmpty() && !openingPrice.isEmpty() && !highPrice.isEmpty() && !lowPrice.isEmpty() && !volume.isEmpty()) {
                    try {
                        StockPrice stockPrice = new StockPrice();
                        stockPrice.setDate(LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy.MM.dd")));
                        stockPrice.setOpenPrice(Float.parseFloat(openingPrice));
                        stockPrice.setClosingPrice(Float.parseFloat(closingPrice));
                        stockPrice.setHighPrice(Float.parseFloat(highPrice));
                        stockPrice.setLowPrice(Float.parseFloat(lowPrice));
                        stockPrice.setVolume(Integer.parseInt(volume));
                        stockPrice.setStock(stock);

                        stockData.add(stockPrice);
                        logger.info("Added stock price: {}", stockPrice);
                    } catch (Exception e) {
                        logger.error("Error parsing stock data: {}", e.getMessage());
                    }
                }
            } else {
                logger.warn("Row does not contain enough columns: {}", row);
            }
        }
        logger.info("Fetched {} stock prices from page {}", stockData.size(), page);
        return stockData;
    }

    // 주식 데이터 저장 메서드
    @Transactional
    public void saveStockData(Stock stock, List<StockPrice> stockPrices) {
        if (stock.getId() == null) {
            stock = stockRepository.save(stock);
            logger.info("Saved stock: {}", stock);
        }

        int batchSize = 50; // 트랜잭션당 저장할 데이터 크기 설정
        for (int i = 0; i < stockPrices.size(); i += batchSize) {
            int end = Math.min(i + batchSize, stockPrices.size());
            List<StockPrice> batchList = stockPrices.subList(i, end);
            for (StockPrice stockPrice : batchList) {
                try {
                    stockPrice.setStock(stock); // Ensure stock is set
                    stockPriceRepository.save(stockPrice);
                    logger.info("Saved stock price: {}", stockPrice);
                } catch (Exception e) {
                    logger.error("Error saving stock price: {}", e.getMessage());
                }
            }
            logger.info("Saved stock data batch from {} to {}", i, end);
        }
        logger.info("Saved stock data for symbol {}: {} entries", stock.getStockSymbol(), stockPrices.size());
    }

    // 모든 주식 데이터를 가져와 저장하는 메서드
    @Transactional
    public void fetchAndSaveAllStocks() throws Exception {
        List<Stock> stocks = stockRepository.findAll();
        for (Stock stock : stocks) {
            String symbol = stock.getStockSymbol();
            logger.info("Fetching data for symbol: {}", symbol);
            List<StockPrice> allStockPrices = new ArrayList<>();
            for (int page = 1; page <= 25; page++) { // 25 페이지까지 데이터를 가져오도록 수정
                List<StockPrice> stockPrices = fetchStockData(stock, page);
                allStockPrices.addAll(stockPrices);
                logger.info("Fetched stock data for symbol {} from page {}: {} entries", symbol, page, stockPrices.size());
            }
            saveStockData(stock, allStockPrices);
        }
        logger.info("All stock data fetched and saved successfully.");
    }

    // 시가총액 상위 100개의 주식 번호와 이름을 가져와 저장하는 메서드
    @Transactional
    public void fetchTop100Stocks() throws Exception {
        String baseUrl = "https://finance.naver.com/sise/sise_market_sum.naver?sosok=0&page=";
        String detailBaseUrl = "https://finance.naver.com/item/main.naver?code=";
        List<Stock> top100Stocks = new ArrayList<>();

        int page = 1;
        while (top100Stocks.size() < 100) {
            String url = baseUrl + page;
            logger.info("Fetching top stocks from URL: {}", url);
            Document doc = Jsoup.connect(url).timeout(60000).get();

            Elements stockElements = doc.select(".type_2 tbody tr");

            for (Element stockElement : stockElements) {
                Elements tds = stockElement.select("td");
                if (tds.size() > 1) {
                    Element linkElement = tds.get(1).selectFirst("a");
                    if (linkElement != null) {
                        String href = linkElement.attr("href");
                        String[] hrefParts = href.split("code=");
                        if (hrefParts.length > 1) {
                            String stockSymbol = hrefParts[1];
                            String stockName = linkElement.text().trim();
                            String currentPriceText = tds.get(2).text().trim().replace(",", "");
                            logger.info("Parsed stock - Symbol: {}, Name: {}, Current Price: {}", stockSymbol, stockName, currentPriceText);

                            try {
                                float currentPrice = Float.parseFloat(currentPriceText);

                                // 상세 정보 크롤링
                                String detailUrl = detailBaseUrl + stockSymbol;
                                Document detailDoc = Jsoup.connect(detailUrl).timeout(60000).get();
                                Elements pElements = detailDoc.select(".summary_info p"); // p 태그가 있는 요소 선택

                                StringBuilder contentBuilder = new StringBuilder();
                                for (Element pElement : pElements) {
                                    contentBuilder.append(pElement.text()).append("\n");
                                }
                                String content = contentBuilder.toString().trim();

                                if (content.length() > 65535) {
                                    content = content.substring(0, 65535); // 최대 길이 제한 (TEXT 타입의 경우)
                                }

                                Stock stock = new Stock();
                                stock.setStockSymbol(stockSymbol);
                                stock.setStockName(stockName);
                                stock.setCurrentPrice(currentPrice);
                                stock.setContent(content); // 상세 정보 설정

                                top100Stocks.add(stock);

                                if (top100Stocks.size() >= 100) {
                                    break;
                                }
                            } catch (NumberFormatException e) {
                                logger.error("Error parsing current price: {}", e.getMessage());
                            } catch (Exception e) {
                                logger.error("Error fetching detail information: {}", e.getMessage());
                            }
                        }
                    }
                }
            }
            page++;
        }

        for (Stock stock : top100Stocks) {
            try {
                stockRepository.save(stock);
                logger.info("Saved top stock: {}", stock);
            } catch (Exception e) {
                logger.error("Error saving top stock: {}", e.getMessage());
            }
        }
        logger.info("Top 100 stocks fetched and saved successfully.");
    }

}
