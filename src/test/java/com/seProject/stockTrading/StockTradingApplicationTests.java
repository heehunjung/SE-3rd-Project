package com.seProject.stockTrading;

import com.seProject.stockTrading.domain.stock.Stock;
import com.seProject.stockTrading.domain.stockPrice.StockPrice;
import com.seProject.stockTrading.domain.stock.StockService;
import com.seProject.stockTrading.domain.stock.StockRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class StockTradingApplicationTests {

	@Autowired
	private StockService stockService;

	@Autowired
	private StockRepository stockRepository;

	@Test
	@Transactional
	public void testFetchAndSaveAllStocks() throws Exception {
		// 코스피 전체 주식 데이터를 가져와 저장하는 메서드 호출
		stockService.fetchAndSaveAllStocks();

		// 저장된 데이터 검증
		List<Stock> stocks = stockRepository.findAll();
		assertThat(stocks).isNotEmpty();

		// 각 주식의 가격 데이터도 검증
		for (Stock stock : stocks) {
			List<StockPrice> stockPrices = stock.getStockPrices();
			assertThat(stockPrices).isNotEmpty();
		}
	}
}
