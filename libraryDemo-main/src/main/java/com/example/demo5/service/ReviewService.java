package com.example.demo5.service;

import java.time.LocalDateTime;
import java.util.List;

public interface ReviewService {

    List<ReviewResponse> listReviews();

    ReviewResponse createReview(String rawUsername, Long bookId, Integer rating, String rawComment);

    public record ReviewResponse(Long id, Long bookId, String bookTitle, String username, Integer rating, String comment,
                                 LocalDateTime createdAt) {
    }
}
