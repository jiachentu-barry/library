package com.library.demo5.service;

import java.util.List;

import com.library.demo5.common.ApiException;
import com.library.demo5.entity.Book;
import com.library.demo5.entity.BookReview;
import com.library.demo5.repository.BookRepository;
import com.library.demo5.repository.BookReviewRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final BookReviewRepository bookReviewRepository;
    private final BookRepository bookRepository;
    private final AuthService authService;

    public ReviewServiceImpl(BookReviewRepository bookReviewRepository, BookRepository bookRepository, AuthService authService) {
        this.bookReviewRepository = bookReviewRepository;
        this.bookRepository = bookRepository;
        this.authService = authService;
    }

    @Override
    public List<ReviewResponse> listReviews() {
        return bookReviewRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public ReviewResponse createReview(String rawUsername, Long bookId, Integer rating, String rawComment) {
        String username = trim(rawUsername);
        String comment = trim(rawComment);

        if (username.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "用户名不能为空");
        }
        if (bookId == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "请选择图书");
        }
        if (rating == null || rating < 1 || rating > 5) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "评分需为 1-5 分");
        }
        if (comment.length() > 400) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "评价内容不能超过 400 字");
        }

        Book book = bookRepository.findById(bookId).orElse(null);
        if (book == null) {
            throw new ApiException(HttpStatus.NOT_FOUND, "图书不存在");
        }

        BookReview review = new BookReview();
        review.setUser(authService.findByUsernameRequired(username));
        review.setBook(book);
        review.setRating(rating);
        review.setComment(comment);
        BookReview saved = bookReviewRepository.save(review);

        return toResponse(saved);
    }

    private ReviewResponse toResponse(BookReview r) {
        return new ReviewResponse(
                r.getId(),
                r.getBook().getId(),
                r.getBook().getTitle(),
                r.getUser().getUsername(),
                r.getRating(),
                r.getComment(),
                r.getCreatedAt());
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
