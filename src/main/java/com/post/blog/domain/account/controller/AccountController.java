package com.post.blog.domain.account.controller;


import com.post.blog.domain.account.dto.AccountDto;
import com.post.blog.domain.account.service.AccountService;
import com.post.blog.global.utils.UriCreator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1/accounts")
@RestController
public class AccountController {
    private static final String ACCOUNT_DEFAULT_URL = "/v1/accounts";

    private final AccountService accountService;

    @PostMapping("/sign-up")
    public ResponseEntity<HttpStatus> signUp(@Valid @RequestBody AccountDto.SignUp requestDto) {
        AccountDto.Response responseDto = accountService.createAccount(requestDto);
        URI location = UriCreator.createUri(ACCOUNT_DEFAULT_URL, responseDto.getAccountId());

        return ResponseEntity.created(location).build();
    }

}
