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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@Service
public class CommentService {
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final AuthUserUtils authUserUtils;

    public CommentDto.Response createComment(Long boardId, CommentDto.Post requestDto) {
        Account findAccount = authUserUtils.getAuthUser();
        Board findBoard = getBoardByIdOrThrow(boardId, findAccount);

        Comment comment = requestDto.toEntity(findAccount, findBoard);
//        Comment comment = Comment.builder()
//                .content(requestDto.getContent())
//                .account(Account.builder().build())
//                .board(Board.builder().build())
//                .build();

        comment = commentRepository.save(comment);

        findAccount.addComment(comment);
        findBoard.addComment(comment);

        return CommentDto.Response.builder()
                .commentId(comment.getCommentId())
                .build();
    }

    public void updateComment(Long commentId, CommentDto.Update requestDto) {
        Account findAccount = authUserUtils.getAuthUser();
        Comment findComment = getCommentByIdOrThrow(commentId, findAccount);

        findComment.updateComment(requestDto.getContent());
    }

    public void deleteComment(Long commentId) {
        Account findAccount = getAuthenticatedAccount();
        Comment findComment = getCommentByIdOrThrow(commentId, findAccount);

        commentRepository.delete(findComment);

        findAccount.getComments().remove(findComment);
        findComment.getBoard().getComments().remove(findComment);
//        findComment.getAccount().getComments().remove(findComment);
    }

    public List<CommentDto.Response> getComments(List<Comment> comments) {
        List<CommentDto.Response> responses = comments.stream()
                .map(comment -> CommentDto.Response.builder()
                        .commentId(comment.getCommentId())
                        .content(comment.getContent())
                        .build()).collect(Collectors.toList());

        return responses;
    }

    private Account getAuthenticatedAccount() {
        return authUserUtils.getAuthUser();
    }

    private Comment getCommentByIdOrThrow(Long commentId, Account account) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.COMMENT_NOT_FOUND));

        if (comment.getAccount().getAccountId() != account.getAccountId())
            throw new BusinessLogicException(ExceptionCode.COMMENT_UNAUTHORIZED);
        return comment;
    }

    private Board getBoardByIdOrThrow(Long boardId, Account account) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.BOARD_NOT_FOUND));

        if (board.getAccount().getAccountId() != account.getAccountId())
            throw new BusinessLogicException(ExceptionCode.BOARD_UNAUTHORIZED);

        return board;
    }
}
