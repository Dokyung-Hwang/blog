package com.post.blog.domain.board.dto;

import com.post.blog.domain.account.entity.Account;
import com.post.blog.domain.board.entity.Board;
import com.post.blog.domain.comment.dto.CommentDto;
import com.post.blog.domain.comment.entity.Comment;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class BoardDto {

    @NoArgsConstructor(access = AccessLevel.PROTECTED )
    @Getter
    public static class Post {
        @NotBlank
        private String title;

        @NotBlank
        private String content;


        @Builder
        public Post(String title, String content) {
            this.title = title;
            this.content = content;
        }

        public Board toEntity(Account account) {
            return Board.builder()
                    .title(title)
                    .content(content)
                    .account(account)
                    .build();
        }
    }

    @NoArgsConstructor(access = AccessLevel.PROTECTED )
    @Getter
    public static class Patch {
        private String title;
        private String content;

        @Builder
        public Patch(String title, String content) {
            this.title = title;
            this.content = content;
        }
    }

    @Getter
    public static class Response {
        // Boards
        private final Long boardId;
        private final String title;
        private final String content;
        private List<CommentDto.Response> comments;

        @Builder
        public Response(Long boardId, String title, String content) {
            this.boardId = boardId;
            this.title = title;
            this.content = content;
        }

        @Builder
        public Response(Long boardId, String title, String content, List<CommentDto.Response> comments) {
            this.boardId = boardId;
            this.title = title;
            this.content = content;
            this.comments = comments;
        }

        public static Response from(Board board) {
            List<CommentDto.Response> commentDtoList = board.getComments().stream()
                    .map(CommentDto.Response::from)
                    .collect(Collectors.toList());

            return new Response(board.getBoardId(), board.getTitle(), board.getContent(), commentDtoList);
        }

    }
}
