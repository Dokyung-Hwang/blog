package com.post.blog.domain.board.entity;

import com.post.blog.domain.account.entity.Account;
import com.post.blog.global.audit.BaseTimeEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Board extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardId;

    @Column(nullable = false, length = 50)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "ACCOUNT_ID")
    private Account account;

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
}
