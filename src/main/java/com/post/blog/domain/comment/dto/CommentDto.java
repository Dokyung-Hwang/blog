package com.post.blog.domain.comment.dto;

import com.post.blog.domain.account.entity.Account;
import com.post.blog.domain.board.entity.Board;
import com.post.blog.domain.comment.entity.Comment;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CommentDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Post {
        private String content;

        @Builder
        public Post(String content) {
            this.content = content;
        }

        public Comment toEntity(Account account, Board board) {
            return Comment.builder()
                    .account(account)
                    .board(board)
                    .content(content)
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Update {
        private String content;

        @Builder
        public Update(String content) {
            this.content = content;
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class Response {
        private Long accountId;
        private Long commentId;
        private String content;

        @Builder
        public Response(Long accountId, Long commentId, String content) {
            this.accountId = accountId;
            this.commentId = commentId;
            this.content = content;
        }

        public static Response from(Comment comment) {
            return new Response(comment.getAccount().getAccountId(), comment.getCommentId(), comment.getContent());
        }
    }
}
