package com.post.blog.domain.comment.service;

import com.post.blog.domain.account.entity.Account;
import com.post.blog.domain.board.entity.Board;
import com.post.blog.domain.board.repository.BoardRepository;
import com.post.blog.domain.comment.dto.CommentDto;
import com.post.blog.domain.comment.entity.Comment;
import com.post.blog.domain.comment.repository.CommentRepository;
import com.post.blog.global.utils.AuthUserUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Comment Service Layer Test")
@Slf4j
@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private AuthUserUtils authUserUtils;

    @Test
    @DisplayName("댓글 생성 성공 테스트")
    void createComment() {
        // given
        Long boardId = 1L;
        CommentDto.Post postDto = CommentDto.Post.builder().content("테스트 댓글").build();

        Account mockAccount = Account.builder()
                .accountId(1L)
                .nickname("testUser")
                .email("testUser@gmail.com")
                .build();

        Board mockBoard = Board.builder()
                .boardId(1L)
                .title("테스트 게시글")
                .account(mockAccount)
                .comments(new ArrayList<>())
                .build();

        Comment mockComment = Comment.builder()
                .commentId(1L)
                .content("테스트 댓글")
                .account(mockAccount)
                .board(mockBoard)
                .build();

        given(authUserUtils.getAuthUser()).willReturn(mockAccount);
        given(boardRepository.findById(boardId)).willReturn(Optional.of(mockBoard));
        given(commentRepository.save(any(Comment.class))).willReturn(mockComment);


        // when
        CommentDto.Response responseDto = commentService.createComment(boardId, postDto);

        // then
        assertNotNull(responseDto);
//        assertEquals(1L, responseDto.getCommentId());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void updateComment() {
    }

    @Test
    void deleteComment() {
    }

    @Test
    void getComments() {
    }
}