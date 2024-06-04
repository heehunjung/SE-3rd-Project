package com.seProject.stockTrading.domain.trade;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TradeRecordRepository extends JpaRepository<TradeRecord, Long> {
    List<TradeRecord> findAllByMemberIdOrderByTimestampDesc(Long memberId);

    List<TradeRecord> findByMemberId(Long memberId);
}
