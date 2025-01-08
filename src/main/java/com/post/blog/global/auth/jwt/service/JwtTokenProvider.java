package com.post.blog.global.auth.jwt.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.post.blog.domain.account.entity.Account;
import com.post.blog.domain.account.repository.AccountRepository;
import com.post.blog.global.exception.BusinessLogicException;
import com.post.blog.global.exception.ExceptionCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Getter
@Slf4j
public class JwtTokenProvider {
    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access.expiration}")
    private Long accessTokenExpirationPeriod;

    @Value("${jwt.access.header}")
    private String accessHeader;

    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenExpirationPeriod;

    @Value("${jwt.refresh.header}")
    private String refreshHeader;

    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String REFRESH_TOKEN_SUBJECT = "RefreshToken";
    private static final String EMAIL_CLAIM = "email";
    private static final String BEARER = "Bearer";

    private final AccountRepository accountRepository;

    public String createAccessToken(String email) {

        Date now = new Date();
        return JWT.create()
                .withSubject(ACCESS_TOKEN_SUBJECT)
                .withExpiresAt(new Date(now.getTime() + accessTokenExpirationPeriod))   // 토큰 만료 시간
                .withClaim(EMAIL_CLAIM, email)
                .sign(Algorithm.HMAC512(secretKey));
    }

    public String createRefreshToken() {
        Date now = new Date();
        return JWT.create()
                .withSubject(REFRESH_TOKEN_SUBJECT)
                .withExpiresAt(new Date(now.getTime() + refreshTokenExpirationPeriod))
                .sign(Algorithm.HMAC512(secretKey));
    }

    public void sendAccessToken(HttpServletResponse response, String accessToken) {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setHeader(accessHeader, accessToken);
        log.info("재발급된 Access Token : {}", accessToken);
    }

    public void sendAccessAndRefreshToken(HttpServletResponse response, String accessToken, String refreshToken) {
        response.setStatus(HttpServletResponse.SC_OK);

        setAccessTokenHeader(response, accessToken);
        setRefreshTokenHeader(response, refreshToken);
        log.info("Access Token, Refresh Token 헤더 설정 완료");
    }

    public Optional<String> extractAccessToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(accessHeader))
                .filter(refreshToken -> refreshToken.startsWith(BEARER))
                .map(refreshToken -> refreshToken.replace(BEARER, ""));
    }

    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(refreshHeader))
                .filter(refreshToken -> refreshToken.startsWith(BEARER))
                .map(refreshToken -> refreshToken.replace(BEARER, ""));
    }

    public Optional<String> extractEmail(String accessToken) {
        return Optional.ofNullable(JWT.require(Algorithm.HMAC512(secretKey))
                .build() // 반환된 빌더로 JWT verifier 생성
                .verify(accessToken) // accessToken을 검증하고 유효하지 않다면 예외 발생
                .getClaim(EMAIL_CLAIM) // claim(Emial) 가져오기
                .asString());
    }

    // Set Access Token Header
    private void setAccessTokenHeader(HttpServletResponse response, String accessToken) {
        response.setHeader(accessHeader, accessToken);
    }

    // Set Refresh Token Header
    public void setRefreshTokenHeader(HttpServletResponse response, String refreshToken) {
        response.setHeader(refreshHeader, refreshToken);
    }

    public void updateRefreshToken(String email, String refreshToken) {
        Account findAccount = accountRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.ACCOUNT_NOT_FOUND));
        findAccount.updateRefreshToken(refreshToken);
    }

    // 매 인증 시(Client 에서 토큰을 헤더에 담아 요청 할 때마다) (Access, Refresh) Token 검증
//    public void verifyToken(String token) {
//        try {
//            JWT.require(Algorithm.HMAC512(secretKey))
//                    .build()
//                    .verify(token);
//        } catch (TokenExpiredException e) {
//            throw new TokenExpiredException("만료된 토큰", Instant.now());
//        } catch (JWTVerificationException e) {
//            throw new JWTVerificationException("유효하지 않은 토큰");
//        }
//    }

    public boolean isTokenValid(String token) {
        try {
            JWT.require(Algorithm.HMAC512(secretKey)).build().verify(token);
            return true;
        } catch (Exception e) {
            log.error("유효하지 않은 토큰입니다. {}", e.getMessage());
            return false;
        }
    }
}
