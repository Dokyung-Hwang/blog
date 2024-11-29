package com.post.blog.global.login.handler;

import com.post.blog.domain.account.repository.AccountRepository;
import com.post.blog.global.auth.jwt.service.JwtTokenProvider;
import com.post.blog.global.login.service.AccountDetailsService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final AccountRepository accountRepository;

    @Value("${jwt.access.expiration)")
    private String accessTokenExpiration;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) {
        String email = extractUsername(authentication);
        String accessToken = jwtTokenProvider.createAccessToken(email);
        String refreshToken = jwtTokenProvider.createRefreshToken();

        jwtTokenProvider.sendAccessAndRefreshToken(response, accessToken, refreshToken);

        accountRepository.findByEmail(email)
                .ifPresent(account -> {
            account.updateRefreshToken(refreshToken);
            accountRepository.saveAndFlush(account);
        });

        log.info("로그인에 성공하였습니다. 이메일: {}", email);
        log.info("로그인에 성공하였습니다. Access Token: {}", accessToken);
        log.info("발급된 AccessToken 만료 시간: {}", accessTokenExpiration);
    }

    private String extractUsername(Authentication authentication) {
        AccountDetailsService.AccountDetails accountDetails = (AccountDetailsService.AccountDetails) authentication.getPrincipal();
        return accountDetails.getUsername();
    }
}
