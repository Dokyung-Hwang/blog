package com.post.blog.domain.account.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class AccountRequestDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class SignUp {

        @Email
        @NotBlank(message = "이메일이 비어있습니다.")
        private String email;

        // 최소 6자리 이상, 적어도 하나의 영문자(대소문자 구분 없이)가 포함, 적어도 하나의 숫자 포함
        @NotBlank(message =  "비밀번호가 비어있습니다.")
        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[\\W_]).{6,}$\n" , message = "영문, 숫자, 특수기호(\"!@#$%^&*\") 포함 6글자 이상의 패스워드만 허용합니다.")
        private String password;

        @NotBlank(message = "닉네임이 비어있습니다.")
        private String nickname;
    }
}
