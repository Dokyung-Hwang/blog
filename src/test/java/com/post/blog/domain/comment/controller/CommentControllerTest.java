package com.post.blog.domain.comment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.post.blog.domain.comment.dto.CommentDto;
import com.post.blog.domain.comment.service.CommentService;
import com.post.blog.global.TestSetUpUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/*
* 테스트 목표
* (post/update/delete) API가 정상적으로 동작하는지 검증
* MockMvc 를 이용하여 실제 HTTP 요청 및 응답 검증
* CommentService를 MockBean으로 모킹하여 서비스 계층 분리                     */

@WebMvcTest(CommentController.class)    // CommentController 클래스만 로딩하여 컨트롤러 계층만 집중 테스트
class CommentControllerTest extends TestSetUpUtil {

    // 실제 서비스 대신 가짜(Mock) 객체를 사용하여 CommentService 동작을 목킹
    @MockBean
    private CommentService commentService;


    @WithMockUser
    @DisplayName("댓글 생성 API 테스트")
    @Test
    void postComment() throws Exception {
        // given
        CommentDto.Post postDto = CommentDto.Post.builder().content("테스트 댓글").build();
        CommentDto.Response responseDto = CommentDto.Response.builder().accountId(1L).commentId(1L).content("테스트 댓글").build();

        // when
        given(commentService.createComment(anyLong(), any(CommentDto.Post.class)))
                .willReturn(responseDto);

        // then
        ResultActions actions = mockMvc.perform(post("/v1/comments/boards/{board-id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postDto)));

        actions
               .andExpect(status().isCreated())
               .andExpect(header().exists("Location"));
    }

    @WithMockUser
    @DisplayName("댓글 수정 API 테스트")
    @Test
    void updateComment() throws Exception {
        // given
        CommentDto.Update updateDto = CommentDto.Update.builder().content("수정된 댓글").build();

        // when
        doNothing().when(commentService).updateComment(anyLong(), any(CommentDto.Update.class));

        // then
        ResultActions actions = mockMvc.perform(patch("/v1/comments/{comment-id}", 100L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)));

        actions
                .andExpect(status().isNoContent());
    }

    @WithMockUser
    @DisplayName("댓글 수정 API 테스트")
    @Test
    void deleteComment() throws Exception {
        // given
        doNothing().when(commentService).deleteComment(anyLong());

        // when & then
        ResultActions actions = mockMvc.perform(delete("/v1/comments/{comment-id}", 100L));

        actions
                .andExpect(status().isNoContent());
    }
}