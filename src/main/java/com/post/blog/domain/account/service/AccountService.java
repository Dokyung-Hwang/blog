package com.post.blog.domain.account.service;

import com.post.blog.domain.account.constants.Role;
import com.post.blog.domain.account.dto.AccountDto;
import com.post.blog.domain.account.entity.Account;
import com.post.blog.domain.account.repository.AccountRepository;
import com.post.blog.global.exception.code.BusinessLogicException;
import com.post.blog.global.exception.code.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public AccountDto.Response createAccount(AccountDto.SignUp signUp) {

        verifyExistsEmail(signUp.getEmail());
        verifyExistsNickname(signUp.getNickname());

        Account account = Account.builder()
                .email(signUp.getEmail())
                .password(signUp.getPassword())
                .nickname(signUp.getNickname())
                .role(Role.MEMBER)
                .build();

        account.passwordEncode(passwordEncoder);
        accountRepository.save(account);

        return AccountDto.Response.builder()
                .accountId(account.getAccountId())
                .build();
    }


    private void verifyExistsEmail(String email) {
        if (accountRepository.findByEmail(email).isPresent()) {
            throw new BusinessLogicException(ExceptionCode.EMAIL_ALREADY_EXISTS);
        }
    }

    private void verifyExistsNickname(String nickname) {
        if (accountRepository.findByNickname(nickname).isPresent()) {
            throw new BusinessLogicException(ExceptionCode.NICKNAME_ALREADY_EXISTS);
        }
    }

    private void verifyNotValidPassword(String password) {
        if (!password.matches("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[\\W_]).{6,}$")) {
            throw new BusinessLogicException(ExceptionCode.PASSWORD_NOT_VALID);
        }
    }
}
