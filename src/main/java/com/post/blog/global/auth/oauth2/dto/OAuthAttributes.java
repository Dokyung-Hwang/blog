package com.post.blog.global.auth.oauth2.dto;


/*
* 각 소셜에서 받아오는 데이터가 다르므로
* 소셜별로 데이터를 받는 데이터를 분기 처리하는 DTO 클래스*/

import com.post.blog.domain.account.constants.Role;
import com.post.blog.domain.account.constants.SocialType;
import com.post.blog.domain.account.entity.Account;
import com.post.blog.global.auth.oauth2.userInfo.GoogleOAuth2UserInfo;
import com.post.blog.global.auth.oauth2.userInfo.KakaoOAuth2UserInfo;
import com.post.blog.global.auth.oauth2.userInfo.NaverOAuth2UserInfo;
import com.post.blog.global.auth.oauth2.userInfo.OAuth2UserInfo;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;
import java.util.UUID;

@Getter
public class OAuthAttributes {

    private String nameAttributeKey;    // OAuth2 로그인 진행 시 키가 되는 필드 값, PK와 같은 의미
    private OAuth2UserInfo oauth2UserInfo;

    @Builder
    public OAuthAttributes(String nameAttributeKey, OAuth2UserInfo oauth2UserInfo) {
        this.nameAttributeKey = nameAttributeKey;
        this.oauth2UserInfo = oauth2UserInfo;
    }

    public static OAuthAttributes of(SocialType socialType,
                                     String userNameAttributeName, Map<String, Object> attributes) {

        if (socialType == SocialType.NAVER) {
            return ofNaver(userNameAttributeName, attributes);
        }
        if (socialType == SocialType.KAKAO) {
            return ofKakao(userNameAttributeName, attributes);
        }
        return ofGoogle(userNameAttributeName, attributes);
    }

    private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .nameAttributeKey(userNameAttributeName)
                .oauth2UserInfo(new KakaoOAuth2UserInfo(attributes))
                .build();
    }

    public static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .nameAttributeKey(userNameAttributeName)
                .oauth2UserInfo(new GoogleOAuth2UserInfo(attributes))
                .build();
    }

    public static OAuthAttributes ofNaver(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .nameAttributeKey(userNameAttributeName)
                .oauth2UserInfo(new NaverOAuth2UserInfo(attributes))
                .build();
    }

    public Account toEntity(SocialType socialType, OAuth2UserInfo oauth2UserInfo) {
        return Account.builder()
                .socialType(socialType)
                .socialId(oauth2UserInfo.getId())
                .email(UUID.randomUUID() + "@socialUser.com")
                .password(UUID.randomUUID().toString())
                .nickname(oauth2UserInfo.getNickname())
                .profileImage(oauth2UserInfo.getImageUrl())
                .role(Role.MEMBER)
                .build();
    }
}
