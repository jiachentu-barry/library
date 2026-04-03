package com.library.demo5.controller;

import java.util.List;
import java.util.stream.Collectors;

import com.library.demo5.dto.BookDTO;
import com.library.demo5.entity.Book;
import com.library.demo5.service.BookService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/books")
    public List<BookDTO> listBooks() {
        return bookService.listBooks().stream().map(BookDTO::from).collect(Collectors.toList());
    }

    @GetMapping("/books/{id}")
    public ResponseEntity<?> getBook(@PathVariable Long id) {
        return ResponseEntity.ok(BookDTO.from(bookService.getBook(id)));
    }

    @GetMapping("/books/{id}/image")
    public ResponseEntity<?> getBookImage(@PathVariable Long id) {
        return ResponseEntity.ok(new ImageResponse(bookService.getBookImage(id)));
    }

    @PostMapping("/books")
    public ResponseEntity<?> createBook(@RequestHeader(value = "X-Auth-Username", required = false) String authUsername,
                                        @RequestBody BookRequest request) {
        Book saved = bookService.createBook(authUsername,
                new BookService.BookCommand(
                        request.title(),
                        request.author(),
                        request.category(),
                        request.stock(),
                        request.recommendationIndex(),
                        request.image(),
                        request.status()));
        return ResponseEntity.status(201).body(BookDTO.from(saved));
    }

    @PutMapping("/books/{id}")
    public ResponseEntity<?> updateBook(@RequestHeader(value = "X-Auth-Username", required = false) String authUsername,
                                        @PathVariable Long id,
                                        @RequestBody BookRequest request) {
        Book saved = bookService.updateBook(authUsername, id,
                new BookService.BookCommand(
                        request.title(),
                        request.author(),
                        request.category(),
                        request.stock(),
                        request.recommendationIndex(),
                        request.image(),
                        request.status()));
        return ResponseEntity.ok(BookDTO.from(saved));
    }

    @DeleteMapping("/books/{id}")
    public ResponseEntity<?> deleteBook(@RequestHeader(value = "X-Auth-Username", required = false) String authUsername,
                                        @PathVariable Long id) {
        bookService.deleteBook(authUsername, id);
        return ResponseEntity.ok(new ApiMessage("删除成功"));
    }

    public record BookRequest(String title, String author, String category, Integer stock, Integer recommendationIndex, String image, String status) {
    }

    public record ApiMessage(String message) {
    }

    public record ImageResponse(String image) {
    }
}
