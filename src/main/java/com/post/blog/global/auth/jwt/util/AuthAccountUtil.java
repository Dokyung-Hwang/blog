package com.post.blog.global.auth.jwt.util;

import com.post.blog.domain.account.entity.Account;
import com.post.blog.domain.account.repository.AccountRepository;
import com.post.blog.global.exception.BusinessLogicException;
import com.post.blog.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Map;

@RequiredArgsConstructor
@Component
public class AuthAccountUtil {

    private final AccountRepository accountRepository;

    public Account getAuthAccount() {
        // SecurityContextHolder 에서 검증된 Account 객체 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getName() == null || authentication.getName().equals("anonymousUser")) {
            throw new BusinessLogicException(ExceptionCode.ACCOUNT_UNAUTHORIZED);
        }

        Map<String, Object> principal = (Map<String, Object>) authentication.getPrincipal();

        return accountRepository
                .findById(Long.parseLong((String) principal.get("accountId"))).orElseThrow(() ->
                        new BusinessLogicException(ExceptionCode.ACCOUNT_NOT_FOUND));
    }
}
