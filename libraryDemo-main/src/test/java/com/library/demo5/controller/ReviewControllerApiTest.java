package com.library.demo5.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.demo5.common.ApiException;
import com.library.demo5.config.GlobalExceptionHandler;
import com.library.demo5.service.ReviewService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReviewController.class)
@Import(GlobalExceptionHandler.class)
class ReviewControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReviewService reviewService;

    @Test
    void listReviewsReturnsPayload() throws Exception {
        ReviewService.ReviewResponse item = new ReviewService.ReviewResponse(
                1L,
                9L,
                "Clean Architecture",
                "tom",
                5,
                "great",
                LocalDateTime.of(2026, 4, 3, 11, 0)
        );
        when(reviewService.listReviews()).thenReturn(List.of(item));

        mockMvc.perform(get("/api/reviews"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bookTitle").value("Clean Architecture"))
                .andExpect(jsonPath("$[0].rating").value(5));
    }

    @Test
    void createReviewReturnsCreated() throws Exception {
        ReviewService.ReviewResponse saved = new ReviewService.ReviewResponse(
                2L,
                9L,
                "Clean Architecture",
                "tom",
                4,
                "nice",
                LocalDateTime.of(2026, 4, 3, 12, 0)
        );
        when(reviewService.createReview(eq("tom"), eq(9L), eq(4), eq("nice"))).thenReturn(saved);

        String body = "{\"username\":\"tom\",\"bookId\":9,\"rating\":4,\"comment\":\"nice\"}";

        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.username").value("tom"));
    }

    @Test
    void createReviewWhenInvalidReturns400() throws Exception {
        when(reviewService.createReview(anyString(), anyLong(), eq(0), anyString()))
                .thenThrow(new ApiException(HttpStatus.BAD_REQUEST, "评分不合法"));

        String body = "{\"username\":\"tom\",\"bookId\":9,\"rating\":0,\"comment\":\"x\"}";

        mockMvc.perform(post("/api/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("评分不合法"));
    }
}
