package com.seProject.stockTrading.controller;

import com.seProject.stockTrading.domain.stock.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
@CrossOrigin
public class StockController {

    private final StockService stockService;

    @Autowired
    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @PostMapping("/fetchAll")
    public ResponseEntity<?> fetchAndSaveAllStocks(@RequestBody List<String> stockCodes) {
        try {
            stockService.fetchAndSaveAllStockData(stockCodes);
            return ResponseEntity.ok("All stock data fetched and saved successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while fetching stock data.");
        }
    }
}
