package com.seProject.stockTrading.controller;

import com.seProject.stockTrading.domain.stock.Stock;
import com.seProject.stockTrading.domain.stock.StockService;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping("/fetch")
    public ResponseEntity<String> fetchAndSaveStockData(@RequestParam String stockCode) {
        try {
            List<Stock> stockData = stockService.fetchStockData(stockCode);
            for (Stock stock : stockData) {
                stockService.saveStock(stock);
                System.out.println("Saved stock: " + stock);
            }
            return ResponseEntity.ok("Stock data saved successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Stock>> getAllStocks() {
        List<Stock> stocks = stockService.getAllStocks();
        return ResponseEntity.ok(stocks);
    }

    @GetMapping("/{symbol}")
    public ResponseEntity<List<Stock>> getStocksBySymbol(@PathVariable String symbol) {
        List<Stock> stocks = stockService.getStocksBySymbol(symbol);
        return ResponseEntity.ok(stocks);
    }
}
