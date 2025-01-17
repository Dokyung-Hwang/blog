package com.post.blog.domain.account.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    ADMIN("ROLE_ADMIN"),
    MEMBER("ROLE_MEMBER"),
    GUEST("ROLE_GUEST");

    private final String role;
}

