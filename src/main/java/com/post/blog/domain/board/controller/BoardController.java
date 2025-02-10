package com.post.blog.domain.board.controller;

import com.post.blog.domain.board.dto.BoardDto;
import com.post.blog.domain.board.service.BoardService;
import com.post.blog.global.response.PaginatedResponse;
import com.post.blog.global.response.SingleApiResponse;
import com.post.blog.global.utils.UriCreator;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Validated
@RequiredArgsConstructor
@RequestMapping("/v1/boards")
@RestController
public class BoardController {
    private static final String BOARD_DEFAULT_URL = "/v1/boards";

    private final BoardService boardService;

    @PostMapping
    public ResponseEntity<HttpStatus> postBoard(@Valid @RequestBody BoardDto.Post requestDto) {
        BoardDto.Response responseDto = boardService.createBoard(requestDto);

        URI location = UriCreator.createUri(BOARD_DEFAULT_URL, responseDto.getBoardId());

        return ResponseEntity.created(location).build();
    }

    // 게시판 상세 조회
    @GetMapping("/{board-id}")
    public ResponseEntity<SingleApiResponse<BoardDto.Response>> getBoard(@Positive @PathVariable("board-id") Long boardId) {
        BoardDto.Response responseDto = boardService.readBoard(boardId);

        return ResponseEntity.ok(SingleApiResponse.success(responseDto));
    }

    // 게시판 페이지 조회
    @GetMapping
    public ResponseEntity<PaginatedResponse<BoardDto.Response>> getBoards(@Positive @RequestParam(name = "page", defaultValue = "1") int page,
                                                                          @Positive @RequestParam(name = "size", defaultValue = "5") int size) {
        Page<BoardDto.Response> responseDto = boardService.readBoards(page - 1, size);

        return ResponseEntity.ok(PaginatedResponse.success(responseDto.getContent(), responseDto));
    }

    @PatchMapping("/{board-id}")
    public ResponseEntity<HttpStatus> patchBoard(@Positive @PathVariable("board-id") Long boardId,
                                                 @Valid @RequestBody BoardDto.Patch requestDto) {
        boardService.updateBoard(boardId, requestDto);
        return ResponseEntity.noContent().build();
    }

    // 게시물 삭제
    @DeleteMapping("/{board-id}")
    public ResponseEntity<HttpStatus> deleteBoard(@Positive @PathVariable("board-id") Long boardId) {
        boardService.deleteBoard(boardId);

        return ResponseEntity.noContent().build();
    }
}
