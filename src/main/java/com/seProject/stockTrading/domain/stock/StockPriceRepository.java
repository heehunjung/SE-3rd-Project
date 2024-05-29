package com.seProject.stockTrading.domain.stock;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StockPriceRepository extends JpaRepository<StockPrice, Long> {
    List<StockPrice> findByStock(Stock stock);
}
