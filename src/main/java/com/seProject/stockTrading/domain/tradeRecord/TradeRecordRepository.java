package com.seProject.stockTrading.domain.tradeRecord;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TradeRecordRepository extends JpaRepository<TradeRecord, Long> {
    List<TradeRecord> findAllByMemberIdOrderByTimestampDesc(Long memberId);

    List<TradeRecord> findByMemberId(Long memberId);
    List<TradeRecord> findAllByMemberIdAndStockIdOrderByTimestampDesc(Long memberId, Long stockId);
    // 추가: stockId로 TradeRecord 삭제
    List<TradeRecord> findAllByStockId(Long stockId);
}
