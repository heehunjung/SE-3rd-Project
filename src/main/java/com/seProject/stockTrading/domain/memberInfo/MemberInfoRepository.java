package com.seProject.stockTrading.domain.memberInfo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberInfoRepository extends JpaRepository<MemberInfo,Long> {
    List<MemberInfo> findAllByOrderByIdDesc();
    List<MemberInfo> findAllByOrderByMember_IdDesc();

    List<MemberInfo> findByMember_Id(Long memberId); // 수정된 메서드
}
