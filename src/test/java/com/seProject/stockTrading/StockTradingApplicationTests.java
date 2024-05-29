package com.seProject.stockTrading;

import com.seProject.stockTrading.domain.stock.Stock;
import com.seProject.stockTrading.domain.stock.StockPrice;
import com.seProject.stockTrading.domain.stock.StockService;
import com.seProject.stockTrading.domain.stock.StockRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class StockTradingApplicationTests {

	@Autowired
	private StockService stockService;

	@Autowired
	private StockRepository stockRepository;

	@Test
	@Transactional // 트랜잭션 범위 내에서 수행하여 LazyInitializationException 방지
	public void testFetchAndSaveAllStockData() throws Exception {
		List<String> stockCodes = Arrays.asList("005930", "000660"); // 삼성전자, SK하이닉스 예시

		// StockService의 메소드 직접 호출
		stockService.fetchAndSaveAllStockData(stockCodes);

		// 데이터베이스에서 데이터 확인
		for (String stockCode : stockCodes) {
			Optional<Stock> stockOptional = stockRepository.findByStockSymbol(stockCode);
			assertThat(stockOptional).isPresent();
			Stock stock = stockOptional.get();

			// 트랜잭션 범위 내에서 stockPrices 초기화
			List<StockPrice> stockPrices = stock.getStockPrices();
			assertThat(stockPrices).isNotEmpty();

			// 테스트 결과 출력
			System.out.println("Fetched and saved stock data for stock symbol: " + stockCode);
			for (StockPrice stockPrice : stockPrices) {
				System.out.println(stockPrice);
			}
		}
	}
}
