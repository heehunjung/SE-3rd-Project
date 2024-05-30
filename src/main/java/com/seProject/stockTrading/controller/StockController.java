package com.seProject.stockTrading.controller;

import com.seProject.stockTrading.domain.stock.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stocks")
public class StockController {

    @Autowired
    private StockService stockService;

    @PostMapping("/fetchAll")
    public ResponseEntity<?> fetchAndSaveAllStocks() {
        try {
            stockService.fetchAndSaveAllStocks();
            return ResponseEntity.ok("All stock data fetched and saved successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error occurred while fetching stock data: " + e.getMessage());
        }
    }
}
