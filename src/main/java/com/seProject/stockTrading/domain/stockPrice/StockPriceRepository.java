package com.seProject.stockTrading.domain.stockPrice;

import com.seProject.stockTrading.domain.stock.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface StockPriceRepository extends JpaRepository<StockPrice, Long> {
    List<StockPrice> findByStock(Stock stock);
    List<StockPrice> findAllByStockId(Long id);
    Optional<StockPrice> findTop1ByStockIdOrderByDateDesc(Long stockId);
    Optional<StockPrice> findTop1ByStockIdAndDateLessThanOrderByDateDesc(Long stockId, LocalDate date);

}
