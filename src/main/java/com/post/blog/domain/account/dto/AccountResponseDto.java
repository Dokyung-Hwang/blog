package com.post.blog.domain.account.dto;

import lombok.Builder;
import lombok.Getter;

public class AccountResponseDto {

    @Getter
    @Builder
    public static class SignUp {
        private Long accountId;
        private String email;
        private String nickname;
    }
}
