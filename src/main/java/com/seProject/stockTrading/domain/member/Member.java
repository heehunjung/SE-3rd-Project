package com.seProject.stockTrading.domain.member;

import com.seProject.stockTrading.domain.enums.MemberRole;
import com.seProject.stockTrading.domain.post.Post;
import com.seProject.stockTrading.domain.tradeRecord.TradeRecord;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import static com.seProject.stockTrading.domain.enums.MemberRole.USER;

@Entity
@Getter
@Setter
public class Member implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //비밀번호
    private String password;
    //아이디
    private String username;
    //닉네임
    private String nickname;
    //사용자 이름
    private String name;
    //사용자 전화번호
    private Long number;
    //사용자 잔고
    @Column(nullable = false)
    private Long balance = 0L;
    //사용자 권한
    @Enumerated(EnumType.STRING)
    private MemberRole role = USER;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    //계정 만료 여부 반환
    @Override
    public boolean isAccountNonExpired() {
        return true; // true : 만료 x
    }

    //계정 잠금 여부 반환
    @Override
    public boolean isAccountNonLocked() {
        return true; // true : 잠금 x
    }

    // 패스워드 만료 여부 반환
    @Override
    public boolean isCredentialsNonExpired() {
        return true; // true : 만료 x
    }

    // 계정 사용 가능 여부 반환
    @Override
    public boolean isEnabled() {
        return true; // true : 사용 가능
    }
}


