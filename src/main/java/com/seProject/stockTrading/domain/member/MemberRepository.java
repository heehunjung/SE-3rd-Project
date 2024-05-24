package com.seProject.stockTrading.domain.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member,Long>{
    List<Member> findAllByOrderByIdDesc();
    Optional<Member> findByUsername(String username);
    public List<Member> findByName(String name);
    Optional<Member> findByUsernameAndPassword(String username, Long password);

}
