package com.seProject.stockTrading.global.config.login.handler;

import com.seProject.stockTrading.domain.member.MemberRepository;
import com.seProject.stockTrading.global.jwt.JwtService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class LoginSuccessJWTProvideHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    private final MemberRepository memberRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            String username = extractUsername(authentication);
            String accessToken = jwtService.createAccessToken(username);
            String refreshToken = jwtService.createRefreshToken();

            // 실제 로그인 요청에 대해서만 토큰 설정
            jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);

            memberRepository.findByUsername(username).ifPresent(
                    member -> member.updateRefreshToken(refreshToken)
            );

            log.info("로그인에 성공했습니다. username: {}", username);
            log.info("AccessToken 을 발급합니다. AccessToken: {}", accessToken);
            log.info("RefreshToken 을 발급합니다. RefreshToken: {}", refreshToken);
        } else {
            log.info("Preflight 요청입니다. 토큰을 발급하지 않습니다.");
        }
    }


    private String extractUsername(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }
}
