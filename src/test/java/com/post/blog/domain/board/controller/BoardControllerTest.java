package com.post.blog.domain.board.controller;

import com.post.blog.domain.board.dto.BoardDto;
import com.post.blog.domain.board.service.BoardService;
import com.post.blog.global.ControllerTestSetUpUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;


@WebMvcTest(BoardController.class)
class BoardControllerTest extends ControllerTestSetUpUtil {

    @MockBean
    private BoardService boardService;

    @WithMockUser
    @DisplayName("게시글 생성 API 테스트")
    @Test
    void postBoard() throws Exception {
        // given

        BoardDto.Post postDto = BoardDto.Post.builder()
                .title("게시판 제목")
                .content("게시판 본문")
                .build();
        BoardDto.Response responseDto = BoardDto.Response.builder()
                .boardId(1L)
                .title("게시판 제목")
                .content("게시판 본문")
                .build();

        // when
        given(boardService.createBoard(any(BoardDto.Post.class)))
                .willReturn(responseDto);

        // then
        ResultActions actions = mockMvc.perform(post("/v1/boards", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postDto)));

        actions.andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @Test
    void getBoard() {
    }

    @Test
    void getBoards() {
    }

    @WithMockUser
    @DisplayName("게시판 수정 API 테스트")
    @Test
    void patchBoard() throws Exception{
        // given
//        Long boardId = 1L;
        BoardDto.Patch patchDto = BoardDto.Patch.builder()
                .title("수정된 게시판 제목")
                .content("수정된 게시판 본문")
                .build();

        // when
        doNothing().when(boardService).updateBoard(anyLong(), any(BoardDto.Patch.class));

        // then
        ResultActions actions = mockMvc.perform(patch("/v1/boards/{board-id}", 100L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patchDto)));

        actions.andExpect(status().isNoContent());
    }

    @WithMockUser
    @DisplayName("게시판 삭제 API 테스트")
    @Test
    void deleteBoard() throws Exception {
        // given
        doNothing().when(boardService).deleteBoard(anyLong());

        ResultActions actions = mockMvc.perform(delete("/v1/boards/{board-id}", 100L));

        actions.andExpect(status().isNoContent());
    }
}