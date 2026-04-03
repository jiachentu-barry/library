package com.library.demo5.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.demo5.common.ApiException;
import com.library.demo5.config.GlobalExceptionHandler;
import com.library.demo5.enums.BorrowStatus;
import com.library.demo5.service.BorrowService;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BorrowController.class)
@Import(GlobalExceptionHandler.class)
class BorrowControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BorrowService borrowService;

    @Test
    void borrowBookReturnsCreated() throws Exception {
        BorrowService.BorrowRecordResponse response = new BorrowService.BorrowRecordResponse(
                10L,
                "tom",
                2L,
                "Clean Code",
                "Robert Martin",
                LocalDateTime.of(2026, 4, 3, 10, 0),
                LocalDateTime.of(2026, 4, 17, 10, 0),
                null,
                BorrowStatus.BORROWED,
                false
        );

        when(borrowService.borrowBook("tom", 2L)).thenReturn(response);

        BorrowController.BorrowRequest body = new BorrowController.BorrowRequest("tom", 2L);

        mockMvc.perform(post("/api/borrow")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.username").value("tom"))
                .andExpect(jsonPath("$.status").value("BORROWED"));
    }

    @Test
    void returnBookReturnsMessage() throws Exception {
        doNothing().when(borrowService).returnBook(10L, "tom");

        BorrowController.ReturnRequest body = new BorrowController.ReturnRequest("tom");

        mockMvc.perform(post("/api/borrow/10/return")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("归还成功"));
    }

    @Test
    void getUserBorrowsReturnsList() throws Exception {
        when(borrowService.getUserBorrows("tom"))
                .thenReturn(List.of(new BorrowService.BorrowRecordResponse(
                        11L,
                        "tom",
                        3L,
                        "Domain-Driven Design",
                        "Eric Evans",
                        LocalDateTime.of(2026, 4, 1, 9, 0),
                        LocalDateTime.of(2026, 4, 15, 9, 0),
                        null,
                        BorrowStatus.OVERDUE,
                        true
                )));

        mockMvc.perform(get("/api/users/tom/borrows"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].bookTitle").value("Domain-Driven Design"))
                .andExpect(jsonPath("$[0].overdue").value(true));
    }

    @Test
    void adminListWithoutPermissionReturns403() throws Exception {
        when(borrowService.adminListBorrows(anyString()))
                .thenThrow(new ApiException(HttpStatus.FORBIDDEN, "仅管理员可访问后台管理"));

        mockMvc.perform(get("/api/admin/borrows").header("X-Auth-Username", "tom"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("仅管理员可访问后台管理"));
    }

    @Test
    void borrowStatsReturnsPayload() throws Exception {
        when(borrowService.borrowStats())
                .thenReturn(new BorrowService.BorrowStatsResponse(8L, 5L, "Clean Code", 20L));

        mockMvc.perform(get("/api/borrow/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.yesterdayBorrowCount").value(8))
                .andExpect(jsonPath("$.todayBorrowCount").value(5))
                .andExpect(jsonPath("$.mostBorrowedBookName").value("Clean Code"))
                .andExpect(jsonPath("$.mostBorrowedBookCount").value(20));
    }
}
