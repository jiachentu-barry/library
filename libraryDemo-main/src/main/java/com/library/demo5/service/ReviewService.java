package com.library.demo5.service;

import java.time.LocalDateTime;
import java.util.List;

public interface ReviewService {

    List<ReviewResponse> listReviews();

    ReviewResponse createReview(String rawUsername, Long bookId, Integer rating, String rawComment);

    public static final class ReviewResponse {
        private final Long id;
        private final Long bookId;
        private final String bookTitle;
        private final String username;
        private final Integer rating;
        private final String comment;
        private final LocalDateTime createdAt;

        public ReviewResponse(Long id, Long bookId, String bookTitle, String username, Integer rating, String comment,
                              LocalDateTime createdAt) {
            this.id = id;
            this.bookId = bookId;
            this.bookTitle = bookTitle;
            this.username = username;
            this.rating = rating;
            this.comment = comment;
            this.createdAt = createdAt;
        }

        public Long id() {
            return id;
        }

        public Long bookId() {
            return bookId;
        }

        public String bookTitle() {
            return bookTitle;
        }

        public String username() {
            return username;
        }

        public Integer rating() {
            return rating;
        }

        public String comment() {
            return comment;
        }

        public LocalDateTime createdAt() {
            return createdAt;
        }
    }
}
