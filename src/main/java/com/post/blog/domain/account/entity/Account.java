package com.post.blog.domain.account.entity;

import com.post.blog.domain.account.constants.Role;
import com.post.blog.domain.account.constants.SocialType;
import com.post.blog.global.audit.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Account extends BaseTimeEntity {

    @Column(name = "ACCOUNT_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private long accountId;

    @Column(name = "NICKNAME", nullable = false, length = 50)
    private String nickname;

    @Column(name = "EMAIL", unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "PASSWORD", length = 100, nullable = false)
    private String password;

    @Column(name = "PROFILE_IMAGE")
    private String profileImage;


    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private SocialType socialType; // GOOGLE, KAKAO, NAVER

    private String socialId; // 로그인한 소셜 타입의 식별자 값 (일반 로그인인 경우 null)

    private String refreshToken; // 리프레시 토큰

    @Builder(toBuilder = true)
    public Account(long accountId, String nickname, String email, String password, String profileImage, Role role, SocialType socialType, String socialId, String refreshToken) {
        this.accountId = accountId;
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.profileImage = profileImage;
        this.role = role;
        this.socialType = socialType;
        this.socialId = socialId;
        this.refreshToken = refreshToken;
    }


    // 유저 권한 설정 메소드
    public void authorizeUser() {
        this.role = Role.MEMBER;
    }

    // 비밀번호 암호화 메소드
    public void passwordEncode(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }

    public void updateRefreshToken(String updateRefreshToken) {
        this.refreshToken = updateRefreshToken;
    }
}
