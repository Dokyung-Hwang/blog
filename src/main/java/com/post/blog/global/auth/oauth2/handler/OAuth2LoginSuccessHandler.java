package com.post.blog.global.auth.oauth2.handler;

import com.post.blog.domain.account.constants.Role;
import com.post.blog.domain.account.entity.Account;
import com.post.blog.domain.account.repository.AccountRepository;
import com.post.blog.global.auth.jwt.service.JwtTokenProvider;
import com.post.blog.global.auth.oauth2.CustomOAuth2User;
import com.post.blog.global.exception.BusinessLogicException;
import com.post.blog.global.exception.ExceptionCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final AccountRepository accountRepository;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("OAuth2 Login 성공!");
        try {
            CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

            if (oAuth2User.getRole() == Role.GUEST) {
                String accessToken = jwtTokenProvider.createAccessToken(oAuth2User.getEmail());
                response.addHeader(jwtTokenProvider.getAccessHeader(), "Bearer " + accessToken);
                response.sendRedirect("oauth2/sign-up");

                jwtTokenProvider.sendAccessAndRefreshToken(response, accessToken, null);

//                Account findAccount = accountRepository.findByEmail(oAuth2User.getEmail())
//                        .orElseThrow(() -> new BusinessLogicException(ExceptionCode.ACCOUNT_NOT_FOUND));
//
//                findAccount.authorizeUser();
            } else {
                loginSuccess(response, oAuth2User);
            }

        } catch (Exception e) {
            throw e;
        }

    }

    private void loginSuccess(HttpServletResponse response, CustomOAuth2User oAuth2User) throws IOException {
        String accessToken = jwtTokenProvider.createAccessToken(oAuth2User.getEmail());
        String refreshToken = jwtTokenProvider.createRefreshToken();
        response.addHeader(jwtTokenProvider.getAccessHeader(), "Bearer " + accessToken);
        response.addHeader(jwtTokenProvider.getRefreshHeader(), "Bearer " + refreshToken);

        jwtTokenProvider.sendAccessAndRefreshToken(response, accessToken, refreshToken);
        jwtTokenProvider.updateRefreshToken(oAuth2User.getEmail(), refreshToken);
    }
}
