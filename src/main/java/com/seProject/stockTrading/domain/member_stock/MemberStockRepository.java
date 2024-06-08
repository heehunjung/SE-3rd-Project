package com.seProject.stockTrading.domain.member_stock;

import com.seProject.stockTrading.domain.dto.StockDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface MemberStockRepository extends JpaRepository<MemberStock, Long> {
    List<MemberStock> findAllByMemberId(Long memberId);
    Optional<MemberStock> findByStockId(Long stockId);
    Optional<MemberStock> findByMemberIdAndStockId(Long memberId, Long stockId);
    List<MemberStock> findByIsPreferredAndMemberId(int isPreferred, Long member_id);
    // 추가: stockId로 MemberStock 삭제
    // 추가된 메서드
    List<MemberStock> findAllByStockId(Long stockId);


    @Query("SELECT new com.seProject.stockTrading.domain.dto.StockDTO(ms.stock.id, ms.stock.stockName, ms.stock.stockSymbol, COUNT(ms),ms.stock.content) " +
            "FROM MemberStock ms " +
            "WHERE ms.isPreferred = 1 " +
            "GROUP BY ms.stock.id, ms.stock.stockName, ms.stock.stockSymbol " +
            "ORDER BY COUNT(ms) DESC")
    List<StockDTO> findTop5PreferredStocks(Pageable pageable);
}
