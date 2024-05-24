package com.seProject.stockTrading.domain.memberInfo;
import com.seProject.stockTrading.domain.member.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class MemberInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // 사용자 이름
    private String name;
    // 사용자 닉네임
    private String nickname;
    // 사용자 id
    private String username;
    // 사용자 password
    private String password;
    // 사용자 잔고
    private Long balance;
    // 사용자가 소유한 주식 정보 & 나중에 추가 될 portfolio 와 연결 될 예정
    private Long portfolio;
    // 사용자 권한(일반 사용자, 관리자)
    private Long role;
    //Member table 와 연결
    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
    // Member ID를 설정하는 메서드 추가
    public void setMemberId(Long memberId) {
        Member member = new Member();
        member.setId(memberId);
        this.member = member;
    }
}
