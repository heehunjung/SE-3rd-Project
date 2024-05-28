package com.seProject.stockTrading;

import com.seProject.stockTrading.domain.stock.Stock;
import com.seProject.stockTrading.domain.stock.StockRepository;
import com.seProject.stockTrading.domain.stock.StockService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class StockTradingApplicationTests {

	@Autowired
	private StockService stockService;

	@Autowired
	private StockRepository stockRepository;

	@Test
	public void testFetchAndSaveStockData() throws Exception {
		String stockCode = "005930";

		// 데이터 크롤링 및 저장
		List<Stock> stockData = stockService.fetchStockData(stockCode);
		for (Stock stock : stockData) {
			stockService.saveStock(stock);
		}

		// 데이터베이스에서 데이터 확인
		List<Stock> stocks = stockRepository.findByStockSymbol(stockCode);
		assertThat(stocks).isNotEmpty();

		// 테스트 결과 출력
		System.out.println("Fetched and saved stock data:");
		for (Stock stock : stocks) {
			System.out.println(stock);
		}
	}
}
