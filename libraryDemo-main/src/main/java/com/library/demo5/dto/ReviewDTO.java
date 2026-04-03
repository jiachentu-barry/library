package com.library.demo5.dto;

import java.time.LocalDateTime;

import com.library.demo5.service.ReviewService;

public record ReviewDTO(
        Long id,
        Long bookId,
        String bookTitle,
        String username,
        Integer rating,
        String comment,
        LocalDateTime createdAt) {

    public static ReviewDTO from(ReviewService.ReviewResponse review) {
        return new ReviewDTO(
                review.id(),
                review.bookId(),
                review.bookTitle(),
                review.username(),
                review.rating(),
                review.comment(),
                review.createdAt());
    }
}