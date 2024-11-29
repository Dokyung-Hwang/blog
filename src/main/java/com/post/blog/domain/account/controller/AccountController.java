package com.post.blog.domain.account.controller;


import com.post.blog.domain.account.dto.AccountRequestDto;
import com.post.blog.domain.account.dto.AccountResponseDto;
import com.post.blog.domain.account.service.AccountService;
import com.post.blog.global.utils.UriCreator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RequiredArgsConstructor
@RestController
public class AccountController {
    private final String ACCOUNT_DEFAULT_URL = "v1/accounts";

    private final AccountService accountService;

    @PostMapping("/sign-up")
    public ResponseEntity<HttpStatus> signUp(@Valid @RequestBody AccountRequestDto.SignUp requestDto) {
        AccountResponseDto.SignUp responseDto = accountService.createAccount(requestDto);
        URI location = UriCreator.createUri(ACCOUNT_DEFAULT_URL, responseDto.getAccountId());

        return ResponseEntity.created(location).build();
    }

}
