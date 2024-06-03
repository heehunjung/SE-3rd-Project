package com.seProject.stockTrading.domain.member_stock;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberStockRepository extends JpaRepository<MemberStock, Long> {
    Optional<MemberStock> findByMemberId(Long memberId);
    Optional<MemberStock> findByStockId(Long stockId);
    Optional<MemberStock> findByMemberIdAndStockId(Long memberId, Long stockId);
}
