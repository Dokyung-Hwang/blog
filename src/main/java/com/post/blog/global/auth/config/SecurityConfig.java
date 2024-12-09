package com.post.blog.global.auth.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.post.blog.domain.account.constants.Role;
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
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .headers(httpSecurityHeadersConfigurer -> httpSecurityHeadersConfigurer
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)) // disable이지만 h2 사용끝날때까지 임시로 sameorigin
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .exceptionHandling(httpSecurityExceptionHandlingConfigurer -> httpSecurityExceptionHandlingConfigurer
//                        .authenticationEntryPoint(new JwtAuthenticationEntryPoint()) // 인증되지 않은 사용자가 인증이 필요한 요청 엔드포인트로 접근하려 할 때 발생하는 예외 처리
//                        .accessDeniedHandler(new JwtAccessDeniedHandler())) // 인증 완료된 사용자가 권한이 없을 때 발생하는 예외 처리
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
//                                new AntPathRequestMatcher("/**"),
                                new AntPathRequestMatcher("/"),
                                new AntPathRequestMatcher("/index.html"),
                                new AntPathRequestMatcher("/v1/accounts/sign-up"),
                                new AntPathRequestMatcher("/h2-console/**")
                        ).permitAll()
//                        .requestMatchers(
//                                new AntPathRequestMatcher("/account/accessDeniedTest")
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
