package com.post.blog.domain.comment.dto;

import com.post.blog.domain.account.entity.Account;
import com.post.blog.domain.comment.entity.Comment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.*;

// 검증할 내용
// Comment 엔티티를 ResponseDto로 변환할 때 필드가 올바르게 매핑되는 지 확인

public class CommentDtoResponseTest {

    @Test
    @DisplayName("Comment 엔티티를 ResponseDto로 변환")
    void from_ShouldConvertCommentToResponseDto() {
        // given
        Account account = Account.builder().accountId(1L).build();

        Comment comment = Comment.builder()
                .commentId(1L)
                .account(account)
                .content("응답 테스트 댓글")
                .build();

        // when
        CommentDto.Response responseDto = CommentDto.Response.from(comment);

        // then
        assertAll(
                () -> assertThat(responseDto.getAccountId()).isEqualTo(1L),
                () -> assertThat(responseDto.getCommentId()).isEqualTo(1L),
                () -> assertThat(responseDto.getContent()).isEqualTo("응답 테스트 댓글")
        );
    }
}
