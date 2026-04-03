package com.library.demo5.dto;

import java.time.LocalDateTime;

import com.library.demo5.entity.Book;
import com.library.demo5.enums.BookStatus;

public record BookDTO(
        Long id,
        String title,
        String author,
        String category,
        Integer stock,
        Integer recommendationIndex,
        String image,
        BookStatus status,
        LocalDateTime createdAt) {

    public static BookDTO from(Book book) {
        return new BookDTO(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getCategory(),
                book.getStock(),
                book.getRecommendationIndex(),
                book.getImage(),
                book.getStatus(),
                book.getCreatedAt());
    }
}