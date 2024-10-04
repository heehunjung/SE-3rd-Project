package com.seProject.stockTrading.global.jwt;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface JwtService {

    String createAccessToken(String userName);
    String createRefreshToken();

    void updateRefreshToken(String userName, String refreshToken);

    void deleteRefreshToken(String userName);

    void sendToken(HttpServletResponse response, String accessToken, String refreshToken) throws IOException;

    String extractAccessToken(HttpServletRequest request) throws IOException, ServletException;

    String extractRefreshToken(HttpServletRequest request) throws IOException, ServletException;

    String extractUserName(String accessToken);

    void setAccessTokenHeader(HttpServletResponse response, String accessToken);
    void setRefreshTokenHeader(HttpServletResponse response, String refreshToken);
}
