package com.seProject.stockTrading.domain.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.seProject.stockTrading.domain.enums.MemberRole;
import com.seProject.stockTrading.domain.member.Member;
import com.seProject.stockTrading.domain.member.MemberRepository;
import com.seProject.stockTrading.global.jwt.JwtService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Base64;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class JwtServiceTest {
    @Autowired
    JwtService jwtService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    EntityManager em;

    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.access.header}")
    private String accessHeader;
    @Value("${jwt.refresh.header}")
    private String refreshHeader;

    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final String USERNAME_CLAIM = "userName";
    private static final String BEARER = "Bearer ";

    private String userName = "userName";

    @BeforeEach
    public void setUp() {
        Member member = Member.builder().username(userName)
                .name("Member1").nickname("NickName1").role(MemberRole.USER).build();
    }

    private void clear() {
        em.flush();
        em.clear();
    }


    private DecodedJWT getVerify(String token) {
        return JWT.require(HMAC512(secret)).build().verify(token);
    }

    @Test
    public void createAccessToken_발급() throws Exception {

        String accessToken = jwtService.createAccessToken(userName);
        DecodedJWT verify = getVerify(accessToken);
        String subject = verify.getSubject();
        String findByUserName = verify.getClaim(USERNAME_CLAIM).asString();

        assertThat(findByUserName).isEqualTo(userName);
        assertThat(subject).isEqualTo(ACCESS_TOKEN_SUBJECT);
    }
    @Test
    public void createRefreshToken_RefreshToken_발급() throws Exception {
        //given, when
        String refreshToken = jwtService.createRefreshToken();
        DecodedJWT verify = getVerify(refreshToken);
        String subject = verify.getSubject();
        String username = verify.getClaim(USERNAME_CLAIM).asString();

        //then
        assertThat(subject).isEqualTo(REFRESH_TOKEN_SUBJECT);
        assertThat(username).isNull();
    }
}