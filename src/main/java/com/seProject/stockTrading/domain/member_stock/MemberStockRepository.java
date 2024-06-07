package com.seProject.stockTrading.domain.member_stock;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
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

}
