package com.post.blog.global.auth.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.post.blog.domain.account.repository.AccountRepository;
import com.post.blog.global.auth.jwt.filter.JwtAuthenticationProcessingFilter;
import com.post.blog.global.auth.jwt.service.JwtTokenProvider;
import com.post.blog.global.auth.oauth2.handler.OAuth2LoginFailureHandler;
import com.post.blog.global.auth.oauth2.handler.OAuth2LoginSuccessHandler;
import com.post.blog.global.auth.oauth2.service.CustomOAuth2UserService;
import com.post.blog.global.login.filter.CustomJsonUsernamePasswordAuthenticationFilter;
import com.post.blog.global.login.handler.LoginFailureHandler;
import com.post.blog.global.login.handler.LoginSuccessHandler;
import com.post.blog.global.login.service.AccountDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {
    private final AccountDetailsService accountDetailsService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AccountRepository accountRepository;
    private final ObjectMapper objectMapper;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;
    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)  // Rest API 는 stateless 하기때문에 인증정보를 세션에 보관하지 않아 csrf 에 대해 안전
                .formLogin(AbstractHttpConfigurer::disable) // formLogin 방식 사용 x
                .httpBasic(AbstractHttpConfigurer::disable) // httpBasic(request header 에 id와 password를 직접 날리는 방식) 사용 X, jwt 토큰 인증 방식
                .headers(httpSecurityHeadersConfigurer -> httpSecurityHeadersConfigurer
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)) // sameOrigin: 자기 자신 혹은 지정 도메인은 <Iframe(현재 페이지에 다른 페이지를 포함시키는 역할)> 허용. 그 외 나머지 사이트는 비허용, 클릭재킹 방지
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))    // Session 사용 x
//                .exceptionHandling(httpSecurityExceptionHandlingConfigurer -> httpSecurityExceptionHandlingConfigurer
//                        .authenticationEntryPoint(new JwtAuthenticationEntryPoint()) // 인증되지 않은 사용자가 인증이 필요한 요청 엔드포인트로 접근하려 할 때 발생하는 예외 처리
//                        .accessDeniedHandler(new JwtAccessDeniedHandler())) // 인증 완료된 사용자가 권한이 없을 때 발생하는 예외 처리
                // HTTP 요청에 대한 인가 설정(특정 경로나 URL Pattern)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
//                                new AntPathRequestMatcher("/**"),
                                new AntPathRequestMatcher("/"),
                                new AntPathRequestMatcher("/index.html"),
                                new AntPathRequestMatcher("/account/sign-up"),
                                new AntPathRequestMatcher("/h2-console/**"),
                                new AntPathRequestMatcher("/oauth2/**"),
                                new AntPathRequestMatcher("/login/**"),
                                new AntPathRequestMatcher("/**", "GET")
                        ).permitAll()
//                        .requestMatchers(
//                                new AntPathRequestMatcher("/v1/accounts/accessDeniedTest")
//                        ).hasRole(Role.ADMIN.name())
                        .anyRequest().authenticated())
                .oauth2Login(oauth -> oauth
                        .successHandler(oAuth2LoginSuccessHandler)
                        .failureHandler(oAuth2LoginFailureHandler)
                        .userInfoEndpoint(config -> config.userService(customOAuth2UserService)) // OAuth2 로그인 성공 후 동작
                )
                .addFilterAfter(customJsonUsernamePasswordAuthenticationFilter(), LogoutFilter.class)
                .addFilterBefore(jwtAuthenticationProcessingFilter(), CustomJsonUsernamePasswordAuthenticationFilter.class);
                // jwt 인증 필터를 usernamepassword 인증 필터 앞에 놓기 (/login 으로 오는 요청은 jwt 인증 필터 제외하고 바로 넘기기)
//                .addFilterBefore(authExceptionFilter(), JwtAuthenticationProcessingFilter.class); // 필터 내 exception handling

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(accountDetailsService);
        return new ProviderManager(provider);
    }

    // LoginSuccessHandler, LoginFailureHandler는 config파일에서 직접 빈 등록을 했으므로 두 파일에 @component 사용 X
    @Bean
    public LoginSuccessHandler loginSuccessHandler() {
        return new LoginSuccessHandler(jwtTokenProvider, accountRepository);
    }

    @Bean
    public LoginFailureHandler loginFailureHandler() {
        return new LoginFailureHandler();
    }

    @Bean
    public CustomJsonUsernamePasswordAuthenticationFilter customJsonUsernamePasswordAuthenticationFilter() {
        CustomJsonUsernamePasswordAuthenticationFilter customJsonUsernamePasswordLoginFilter
                = new CustomJsonUsernamePasswordAuthenticationFilter(objectMapper);
        customJsonUsernamePasswordLoginFilter.setAuthenticationManager(authenticationManager());
        customJsonUsernamePasswordLoginFilter.setAuthenticationSuccessHandler(loginSuccessHandler());
        customJsonUsernamePasswordLoginFilter.setAuthenticationFailureHandler(loginFailureHandler());
        return customJsonUsernamePasswordLoginFilter;
    }

    @Bean
    public JwtAuthenticationProcessingFilter jwtAuthenticationProcessingFilter() {
        return new JwtAuthenticationProcessingFilter(jwtTokenProvider, accountRepository);
    }

//    public AuthExceptionFilter authExceptionFilter() {
//        return new AuthExceptionFilter();
//    }
}
