package com.seProject.stockTrading;

import com.seProject.stockTrading.domain.stock.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StockTradingApplication/* implements CommandLineRunner*/ {
/*
	@Autowired
	private StockService stockService;
*/

	public static void main(String[] args) {
		SpringApplication.run(StockTradingApplication.class, args);
	}
/*	@Override
	public void run(String... args) {
		try {
			stockService.fetchTop100Stocks();
			System.out.println("Top 100 stocks fetched and saved successfully.");
			stockService.fetchAndSaveAllStocks();
			System.out.println("Stock data fetched and saved successfully.");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("An error occurred: " + e.getMessage());
		}
	}*/
}
