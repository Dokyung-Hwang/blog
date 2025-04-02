package com.post.blog.domain.comment.dto;

import com.post.blog.domain.account.entity.Account;
import com.post.blog.domain.board.entity.Board;
import com.post.blog.domain.comment.entity.Comment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;


/*
* 검증할 내용
* 1. PostDto를 Comment Entity로 변환할 때 올바르게 매핑 되는지 확인
* 2.account, board, content가 정상적으로 설정되는지 확인
* */

class CommentDtoPostTest {

    @Test
    @DisplayName("Post DTO를 Comment 엔티티로 변환")
    void toEntity_ShouldConvertPostDtoToComment() {
        // given
        Account account = Account.builder().accountId(1L).build();
        Board board = Board.builder().boardId(1L).build();

        CommentDto.Post postDto = CommentDto.Post.builder().content("Comment Test").build();

        // when
        Comment comment = postDto.toEntity(account, board);

        // then
        assertThat(comment.getContent()).isEqualTo("Comment Test");
    }
}