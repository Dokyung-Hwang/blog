package com.post.blog.domain.comment.controller;

import com.post.blog.domain.comment.dto.CommentDto;
import com.post.blog.domain.comment.service.CommentService;
import com.post.blog.global.utils.UriCreator;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;


@Validated
@RequiredArgsConstructor
@RequestMapping("/v1/comments")
@RestController
public class CommentController {
    private static final String COMMENT_DEFAULT_URL = "/v1/comments";

    private final CommentService commentService;

    @PostMapping("/boards/{board-id}")
    public ResponseEntity<HttpStatus> postComment(@Positive @PathVariable("board-id") Long boardId,
                                                  @Valid @RequestBody CommentDto.Post post) {

        CommentDto.Response responseDto = commentService.createComment(boardId, post);

        URI location = UriCreator.createUri(COMMENT_DEFAULT_URL, responseDto.getCommentId());

        return ResponseEntity.created(location).build();
    }

    @PatchMapping("/{comment-id}")
    public ResponseEntity<HttpStatus> updateComment(@Positive @PathVariable("comment-id") Long commentId,
                                                    @Valid @RequestBody CommentDto.Update update) {

        commentService.updateComment(commentId, update);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{comment-id}")
    public ResponseEntity<HttpStatus> deleteComment(@Positive @PathVariable("comment-id") Long commentId) {
        commentService.deleteComment(commentId);

        return ResponseEntity.noContent().build();
    }


}
