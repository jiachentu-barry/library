package com.library.demo5.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.demo5.common.ApiException;
import com.library.demo5.config.GlobalExceptionHandler;
import com.library.demo5.entity.Book;
import com.library.demo5.enums.BookStatus;
import com.library.demo5.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
@Import(GlobalExceptionHandler.class)
class BookControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookService bookService;

    @Test
    void listBooksReturnsData() throws Exception {
        Book book = createBook("Clean Code", "Robert Martin", "Programming", 10, 5, BookStatus.RECOMMENDED);
        when(bookService.listBooks()).thenReturn(List.of(book));

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Clean Code"))
                .andExpect(jsonPath("$[0].status").value("RECOMMENDED"));
    }

    @Test
    void createBookReturnsCreated() throws Exception {
        Book saved = createBook("DDD", "Eric Evans", "Architecture", 6, 4, BookStatus.NORMAL);
        when(bookService.createBook(anyString(), any(BookService.BookCommand.class))).thenReturn(saved);

        BookController.BookRequest body = new BookController.BookRequest(
                "DDD", "Eric Evans", "Architecture", 6, 4, "img", "NORMAL"
        );

        mockMvc.perform(post("/api/books")
                        .header("X-Auth-Username", "admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("DDD"));
    }

    @Test
    void updateBookReturnsForbiddenWhenNotAdmin() throws Exception {
        when(bookService.updateBook(anyString(), anyLong(), any(BookService.BookCommand.class)))
                .thenThrow(new ApiException(HttpStatus.FORBIDDEN, "仅管理员可访问后台管理"));

        BookController.BookRequest body = new BookController.BookRequest(
                "DDD", "Eric Evans", "Architecture", 6, 4, "img", "NORMAL"
        );

        mockMvc.perform(put("/api/books/1")
                        .header("X-Auth-Username", "tom")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("仅管理员可访问后台管理"));
    }

    @Test
    void deleteBookReturnsMessage() throws Exception {
        doNothing().when(bookService).deleteBook("admin", 2L);

        mockMvc.perform(delete("/api/books/2").header("X-Auth-Username", "admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("删除成功"));
    }

    @Test
    void getBookImageReturnsImagePayload() throws Exception {
        when(bookService.getBookImage(9L)).thenReturn("http://img/test.png");

        mockMvc.perform(get("/api/books/9/image"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.image").value("http://img/test.png"));
    }

    private Book createBook(String title, String author, String category, int stock, int rec, BookStatus status) {
        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setCategory(category);
        book.setStock(stock);
        book.setRecommendationIndex(rec);
        book.setStatus(status);
        return book;
    }
}
