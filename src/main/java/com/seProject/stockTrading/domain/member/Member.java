package com.seProject.stockTrading.domain.member;

import com.seProject.stockTrading.domain.enums.MemberRole;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

import static com.seProject.stockTrading.domain.enums.MemberRole.USER;

@Entity
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor  // 기본 생성자 추가
public class Member implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String password;
    private String username;
    private String nickname;
    private String name;
    private Long number;
    @Column(nullable = false)
    private Long balance = 0L;
    @Enumerated(EnumType.STRING)
    private MemberRole role = USER;

    // UserDetails 메서드들 구현
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

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // 생성자 (빌더를 사용할 때 필요)
    public Member(Long id, String password, String username, String nickname, String name, Long number, Long balance, MemberRole role) {
        this.id = id;
        this.password = password;
        this.username = username;
        this.nickname = nickname;
        this.name = name;
        this.number = number;
        this.balance = balance;
        this.role = role;
    }
}
