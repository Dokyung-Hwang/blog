package com.post.blog.domain.board.service;

import com.post.blog.domain.account.entity.Account;
import com.post.blog.domain.board.dto.BoardDto;
import com.post.blog.domain.board.entity.Board;
import com.post.blog.domain.board.repository.BoardRepository;
import com.post.blog.domain.comment.dto.CommentDto;
import com.post.blog.domain.comment.entity.Comment;
import com.post.blog.domain.comment.service.CommentService;
import com.post.blog.global.exception.code.BusinessLogicException;
import com.post.blog.global.exception.code.ExceptionCode;
import com.post.blog.global.utils.AuthUserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@RequiredArgsConstructor
@Transactional
@Service
public class BoardService {
    private final BoardRepository boardRepository;
    private final AuthUserUtils authUserUtils;

    public BoardDto.Response createBoard(BoardDto.Post requestDto) {
        Account findAccount = authUserUtils.getAuthUser();

        Board board = requestDto.toEntity(findAccount);

        boardRepository.save(board);
        findAccount.addBoard(board);

        return BoardDto.Response.builder()
                .boardId(board.getBoardId())
                .build();
    }

    // TODO. 추후 게시판 상세 조회 때 Comment, tag 등 추가
    public BoardDto.Response readBoard(Long boardId) {
        Board board = boardRepository.findById(boardId).orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.BOARD_NOT_FOUND));

        return BoardDto.Response.from(board);
    }

    public Page<BoardDto.Response> readBoards(int page, int size) {
        Account findAccount = findVerifiedAccount();

        List<BoardDto.Response> responses = findAccount.getBoards().stream()
                .map(board -> BoardDto.Response.builder()
                        .boardId(board.getBoardId())
                        .title(board.getTitle())
                        .content(board.getContent())
                        .build()).toList();

        int start = page * size;
        int end = Math.min(responses.size(), (page + 1) * size);
        return new PageImpl<>(responses.subList(start, end), PageRequest.of(page, size), responses.size());
    }

    public void updateBoard(Long boardId, BoardDto.Patch requestDto) {
        Account findAccount = authUserUtils.getAuthUser();
        Board findBoard = findVerifiedBoard(boardId, findAccount);


        findBoard.updateBoard(requestDto.getTitle(), requestDto.getContent());
    }

    public void deleteBoard(Long boardId) {
        Account findAccount = findVerifiedAccount();
        Board findBoard = findVerifiedBoard(boardId, findAccount);
        findAccount.getBoards().remove(findBoard);

        boardRepository.delete(findBoard);
    }

    public Account findVerifiedAccount() {
        return authUserUtils.getAuthUser();
    }


    public Board findVerifiedBoard(Long boardId, Account findAccount) {
        Board findBoard = boardRepository.findById(boardId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.BOARD_NOT_FOUND));

        // 찾은 게시물이 로그인된 사용자의 게시물인지 확인
        if (findBoard.getAccount().getAccountId() != findAccount.getAccountId()) {
            throw new BusinessLogicException(ExceptionCode.BOARD_NOT_ALLOW);
        }

        return findBoard;
    }
}
