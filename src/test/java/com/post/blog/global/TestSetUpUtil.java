package com.post.blog.global;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.post.blog.domain.account.repository.AccountRepository;
import com.post.blog.domain.board.controller.BoardController;
import com.post.blog.domain.comment.controller.CommentController;
import com.post.blog.global.auth.config.SecurityConfig;
import com.post.blog.global.auth.jwt.service.JwtTokenProvider;
import com.post.blog.global.auth.login.service.AccountDetailsService;
import com.post.blog.global.auth.oauth2.handler.OAuth2LoginFailureHandler;
import com.post.blog.global.auth.oauth2.handler.OAuth2LoginSuccessHandler;
import com.post.blog.global.auth.oauth2.service.CustomOAuth2UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@WebMvcTest    // CommentController 클래스만 로딩하여 컨트롤러 계층만 집중 테스트
@Import(SecurityConfig.class)
public class TestSetUpUtil {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    private AccountDetailsService accountDetailsService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private AccountRepository accountRepository;

    @MockBean
    private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    @MockBean
    private OAuth2LoginFailureHandler oAuth2LoginFailureHandler;

    @MockBean
    private CustomOAuth2UserService customOAuth2UserService;

    @BeforeEach
    public void setUp(final WebApplicationContext context) {
        this.mockMvc = webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .alwaysDo(print())
                .build();
    }
}
