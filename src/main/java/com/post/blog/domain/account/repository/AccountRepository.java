package com.post.blog.domain.account.repository;

import com.post.blog.domain.account.constants.SocialType;
import com.post.blog.domain.account.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByEmail(String email);
    Optional<Account> findByNickname(String nickname);
    Optional<Account> findByRefreshToken(String refreshToken);


    Optional<Account> findBySocialTypeAndSocialId(SocialType socialType, String socialId);
}
