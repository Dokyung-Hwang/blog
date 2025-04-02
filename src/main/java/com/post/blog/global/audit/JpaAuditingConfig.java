package com.post.blog.global.audit;


// 컨트롤러 계층 테스트 코드 작성을 위한 클래스

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}
