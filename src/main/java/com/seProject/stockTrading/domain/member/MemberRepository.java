package com.seProject.stockTrading.domain.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member,Long>{
    //default : public
    Optional<Member> findByUsername(String username);
    List<Member> findByName(String name);
    Optional<Member> findByUsernameAndPassword(String username, String password);
    Optional<Member> findByNickname(String nickname);
    boolean existsByNickname(String nickname);
    Optional<Member> findByRefreshToken(String refreshToken);
}
