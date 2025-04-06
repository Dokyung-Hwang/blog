package com.post.blog.domain.comment.service;

import com.post.blog.domain.account.entity.Account;
import com.post.blog.domain.board.entity.Board;
import com.post.blog.domain.board.repository.BoardRepository;
import com.post.blog.domain.comment.dto.CommentDto;
import com.post.blog.domain.comment.entity.Comment;
import com.post.blog.domain.comment.repository.CommentRepository;
import com.post.blog.global.exception.code.BusinessLogicException;
import com.post.blog.global.exception.code.ExceptionCode;
import com.post.blog.global.utils.AuthUserUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Comment Service Layer Test")
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
    void createComment_Success() {
        // given
        Long boardId = 1L;
        CommentDto.Post postDto = CommentDto.Post.builder().content("테스트 댓글").build();

        Account account = Account.builder()
                .accountId(1L)
                .build();
        Board board = Board.builder()
                .boardId(1L)
                .account(account)
                .comments(new ArrayList<>())
                .build();
        Comment comment = Comment.builder()
                .commentId(1L)
                .content("테스트 댓글")
                .account(account)
                .board(board)
                .build();

        given(authUserUtils.getAuthUser()).willReturn(account);
        given(boardRepository.findById(1L)).willReturn(Optional.of(board));
        given(commentRepository.save(any(Comment.class))).willReturn(comment);


        // when
        CommentDto.Response responseDto = commentService.createComment(boardId, postDto);

        // then
        assertNotNull(responseDto);
        assertEquals(1L, responseDto.getCommentId());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }


    @DisplayName("댓글 생성 시 존재하지 않는 게시판이면 예외 발생")
    @Test
    void createComment_BoardNotFound_ThrowsException() {
        // given
        Long boardId = 999L;
        CommentDto.Post postDto = CommentDto.Post.builder()
                .content("테스트 댓글")
                .build();

        given(authUserUtils.getAuthUser())
                .willReturn(
                        Account.builder()
                                .accountId(1L)
                                .nickname("testUser")
                                .build());

        given(boardRepository.findById(boardId)).willReturn(Optional.empty());

        // when
        BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> {
            commentService.createComment(boardId, postDto);
        });

        // then
        assertEquals(ExceptionCode.BOARD_NOT_FOUND, exception.getExceptionCode());
        assertEquals("Post not found", exception.getMessage());
    }

    @Test
    @DisplayName("댓글 수정 테스트")
    void updateComment() {
        // given
        Long commentId = 1L;
        CommentDto.Update updateDto = CommentDto.Update.builder().content("수정된 댓글").build();
        Account account = Account.builder()
                .accountId(1L)
                .build();
        Board board = Board.builder()
                .boardId(1L)
                .comments(new ArrayList<>())
                .build();
        Comment comment = Comment.builder()
                .commentId(commentId)
                .content("수정 전 댓글")
                .account(account)
                .board(board)
                .build();

        given(authUserUtils.getAuthUser()).willReturn(account);
        given(commentRepository.findById(any(Long.class))).willReturn(Optional.of(comment));

        // when
        commentService.updateComment(commentId, updateDto);

        // then
        assertEquals("수정된 댓글", comment.getContent());
    }

    @DisplayName("댓글 수정 시 다른 사용자의 댓글이면 예외 발생")
    @Test
    void updateComment_UnauthorizedUser_ThrowsException() {
        // given
        Long commentId = 1L;
        CommentDto.Update updateDto = new CommentDto.Update("수정된 댓글");

        Account author = Account.builder().accountId(1L).nickname("작성자").build();
        Account otherAccount = Account.builder().accountId(2L).nickname("testUser").build();

        Comment comment = Comment.builder()
                .commentId(commentId)
                .content("원본 댓글")
                .account(author)
                .board(Board.builder().build())
                .build();

        given(authUserUtils.getAuthUser()).willReturn(otherAccount);
        given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));

        // when
        BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> {
            commentService.updateComment(commentId, updateDto);
        });


        // then
        assertEquals(ExceptionCode.COMMENT_UNAUTHORIZED, exception.getExceptionCode());
        assertEquals("No Access to Resource", exception.getMessage());
    }

    @DisplayName("댓글 삭제 성공 테스트")
    @Test
    void deleteComment() {
        // given
        Long commentId = 1L;
        Account account = Account.builder()
                .accountId(1L)
                .build();
        Comment comment = Comment.builder()
                .content("삭제 전 댓글")
                .account(account)
                .board(Board.builder().comments(new ArrayList<>()).build())
                .build();

        given(authUserUtils.getAuthUser()).willReturn(account);
        given(commentRepository.findById(any(Long.class))).willReturn(Optional.of(comment));

        // when
        commentService.deleteComment(commentId);

        //then
        verify(commentRepository, times(1)).delete(any(Comment.class));
    }

    @DisplayName("댓글 삭제 시 다른 사용자의 댓글이면 예외 발생")
    @Test
    void deleteComment_UnauthorizedUser_ThrowsException() {
        // given
        Long commentId = 1L;
        Account author = Account.builder().accountId(1L).nickname("작성자").build();
        Account otherAccount = Account.builder().accountId(2L).nickname("testUser").build();
        Comment comment = Comment.builder()
                .commentId(commentId)
                .content("삭제할 댓글")
                .account(author)
                .board(Board.builder().comments(new ArrayList<>()).build())
                .build();

        given(authUserUtils.getAuthUser()).willReturn(otherAccount);
        given(commentRepository.findById(commentId)).willReturn(Optional.of(comment));

        // when
        BusinessLogicException exception = assertThrows(BusinessLogicException.class, () -> {
            commentService.deleteComment(commentId);
        });

        // then
        assertEquals(ExceptionCode.COMMENT_UNAUTHORIZED, exception.getExceptionCode());
        assertEquals("No Access to Resource", exception.getMessage());
    }
}