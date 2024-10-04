package com.seProject.stockTrading.domain.member;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired
    MemberServiceImpl memberServiceImpl;
    @Autowired
    EntityManager entityManager;

    @AfterEach
    void after() {
        entityManager.clear();
    }

    @Test
    void saveMember() throws Exception{
        Member member = Member.builder().
                username("admintestkkk").
                password("admin").
                balance(10000L).
                build();
        Member savedMember = memberRepository.save(member);
        Member findMember = memberRepository.findByUsername("admintestkkk")
                .orElseThrow(() -> new RuntimeException("저장된 회원이 없습니다"));
        assertThat(savedMember).isSameAs(member);
    }

    @Test
    void 아이디_없이_회원가입_오류() throws Exception{
        Member member = Member.builder().
                name("admintestkkk").
                password("admin").
                balance(10000L).
                build();
        boolean result = memberServiceImpl.checkUsername(member.getUsername());
        assertThat(result).isFalse();
    }
}