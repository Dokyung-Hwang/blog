package com.post.blog.global.auth.jwt.filter;

import com.post.blog.domain.account.entity.Account;
import com.post.blog.domain.account.repository.AccountRepository;
import com.post.blog.global.auth.jwt.service.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// JWT 인증 필터
// "/login" 이외의 URI 요청이 들어왔을 때 처리하는 필터
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {

    private static final String NO_CHECK_URL = "/login";

    private final JwtTokenProvider jwtTokenProvider;
    private final AccountRepository accountRepository;

    private GrantedAuthoritiesMapper authoritiesMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getRequestURI().equals(NO_CHECK_URL)) {
            filterChain.doFilter(request, response); // "/login" 요청이 들어오면, 다음 필터 호출
            return; // return 으로 이후 현재 필터 진행 막기 (안해주면 아래로 내려가서 계속 필터 진행시킴)
        }

        String refreshToken = jwtTokenProvider.extractRefreshToken(request)
                .filter(jwtTokenProvider::isTokenValid)
                .orElse(null);

        if (refreshToken == null) {
            checkAccessTokenAndAuthentication(request, response, filterChain);
        } else {
            checkRefreshTokenAndReIssueAccessToken(response, refreshToken);
        }
    }

    private void checkRefreshTokenAndReIssueAccessToken(HttpServletResponse response, String refreshToken) {
        accountRepository.findByRefreshToken(refreshToken)
                .ifPresent(account -> {
                    String reIssuedRefreshToken = reIssueRefreshToken(account);
                    jwtTokenProvider.sendAccessAndRefreshToken(response, jwtTokenProvider.createAccessToken(account.getEmail()), reIssuedRefreshToken);
                });
    }

    private String reIssueRefreshToken(Account account) {
        String reIssuedRefreshToken = jwtTokenProvider.createRefreshToken();
        account.updateRefreshToken(reIssuedRefreshToken);
        accountRepository.saveAndFlush(account);
        return reIssuedRefreshToken;
    }

    private void checkAccessTokenAndAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        jwtTokenProvider.extractAccessToken(request)
                .filter(jwtTokenProvider::isTokenValid)
                .flatMap(jwtTokenProvider::extractEmail)
                .flatMap(accountRepository::findByEmail)
                .ifPresent(this::saveAuthentication);


        filterChain.doFilter(request, response);
    }

    public void saveAuthentication(Account account) {
//        String password = account.getPassword();
//        if (password == null) { // 소셜 로그인 유저의 비밀번호 임의로 설정 하여 소셜 로그인 유저도 인증 되도록 설정
//            password = PasswordUtil.generateRandomPassword();
//        }

        UserDetails userDetailsUser = org.springframework.security.core.userdetails.User.builder()
                .username(account.getEmail())
                .password(account.getPassword())
                .roles(account.getRole().name())
                .build();

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userDetailsUser, null,
                        authoritiesMapper.mapAuthorities(userDetailsUser.getAuthorities()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
