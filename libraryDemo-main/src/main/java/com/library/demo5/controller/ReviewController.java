package com.library.demo5.controller;

import java.util.List;

import com.library.demo5.dto.ReviewDTO;
import com.library.demo5.service.ReviewService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/reviews")
    public List<ReviewDTO> listReviews() {
        return reviewService.listReviews().stream().map(ReviewDTO::from).toList();
    }

    @PostMapping("/reviews")
    public ResponseEntity<?> createReview(@RequestBody CreateReviewRequest request) {
        ReviewService.ReviewResponse saved = reviewService.createReview(
                request.username(),
                request.bookId(),
                request.rating(),
                request.comment());
        return ResponseEntity.status(HttpStatus.CREATED).body(ReviewDTO.from(saved));
    }

    record CreateReviewRequest(String username, Long bookId, Integer rating, String comment) {
    }

}
