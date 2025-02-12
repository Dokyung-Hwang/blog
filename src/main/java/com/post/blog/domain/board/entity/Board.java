package com.post.blog.domain.board.entity;

import com.post.blog.domain.account.entity.Account;
import com.post.blog.domain.comment.entity.Comment;
import com.post.blog.global.audit.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Board extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardId;

    @Column(nullable = false, length = 50)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne
    @JoinColumn(name = "ACCOUNT_ID")
    private Account account;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @Builder
    public Board(Long boardId, String title, String content, Account account, List<Comment> comments) {
        this.boardId = boardId;
        this.title = title;
        this.content = content;
        this.account = account;
        this.comments = comments;
    }

    @Builder
    public Board(Long boardId, String title, String content, Account account) {
        this.boardId = boardId;
        this.title = title;
        this.content = content;
        this.account = account;
    }

    public void updateBoard(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
    }
}
