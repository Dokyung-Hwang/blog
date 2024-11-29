package com.post.blog.domain.account.service;

import com.post.blog.domain.account.constants.Role;
import com.post.blog.domain.account.dto.AccountRequestDto;
import com.post.blog.domain.account.dto.AccountResponseDto;
import com.post.blog.domain.account.entity.Account;
import com.post.blog.domain.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private PasswordEncoder passwordEncoder;

    public AccountResponseDto.SignUp createAccount(AccountRequestDto.SignUp signUp) {

        // 예외 묶기
//        if (accountRepository.findByEmail(sighUpDto.getEmail()).isPresent()) {
//            throw new Exception("이미 존재하는 이메일입니다.");
//        }
//
//        if (accountRepository.findByNickName(sighUpDto.getNickname()).isPresent()) {
//            throw new Exception("이미 존재하는 닉네임입니다.");
//        }

        String encryptedPassword = passwordEncoder.encode(signUp.getPassword());

        Account savedAccount = accountRepository.save(Account.builder()
                .email(signUp.getEmail())
                .password(encryptedPassword)
                .nickname(signUp.getNickname())
                .role(Role.MEMBER)
                .build());

        return AccountResponseDto.SignUp.builder()
                .accountId(savedAccount.getAccountId())
                .build();
    }
}
