package com.library.demo5.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import com.library.demo5.common.ApiException;
import com.library.demo5.entity.Book;
import com.library.demo5.enums.BookStatus;
import com.library.demo5.repository.BookRepository;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final AuthService authService;

    public BookServiceImpl(BookRepository bookRepository, AuthService authService) {
        this.bookRepository = bookRepository;
        this.authService = authService;
    }

    @Override
    public List<Book> listBooks() {
        return bookRepository.findAllByOrderByCreatedAtDesc();
    }

    @Override
    public Book getBook(Long id) {
        Book book = bookRepository.findById(id).orElse(null);
        if (book == null) {
            throw new ApiException(HttpStatus.NOT_FOUND, "图书不存在");
        }
        return book;
    }

    @Override
    public String getBookImage(Long id) {
        return getBook(id).getImage();
    }

    @Override
    public Book createBook(String authUsername, BookCommand command) {
        authService.requireAdmin(authUsername);
        validate(command);

        Book book = new Book();
        book.setTitle(trim(command.title()));
        book.setAuthor(trim(command.author()));
        book.setCategory(trim(command.category()));
        book.setStock(command.stock());
        book.setRecommendationIndex(command.recommendationIndex());
        book.setImage(trim(command.image()));
        book.setStatus(parseStatus(command.status()));
        book.setCreatedAt(LocalDateTime.now());

        try {
            return bookRepository.save(book);
        } catch (DataAccessException ex) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "保存图书失败：数据库写入异常，请检查图片或字段长度");
        }
    }

    @Override
    public Book updateBook(String authUsername, Long id, BookCommand command) {
        authService.requireAdmin(authUsername);
        validate(command);

        Book book = getBook(id);
        book.setTitle(trim(command.title()));
        book.setAuthor(trim(command.author()));
        book.setCategory(trim(command.category()));
        book.setStock(command.stock());
        book.setRecommendationIndex(command.recommendationIndex());
        book.setImage(trim(command.image()));
        book.setStatus(parseStatus(command.status()));

        try {
            return bookRepository.save(book);
        } catch (DataAccessException ex) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "更新图书失败：数据库写入异常，请检查图片或字段长度");
        }
    }

    @Override
    public void deleteBook(String authUsername, Long id) {
        authService.requireAdmin(authUsername);
        if (!bookRepository.existsById(id)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "图书不存在");
        }
        bookRepository.deleteById(id);
    }

    private void validate(BookCommand command) {
        String title = trim(command.title());
        String author = trim(command.author());
        String category = trim(command.category());
        Integer stock = command.stock();
        Integer recommendationIndex = command.recommendationIndex();
        BookStatus status = parseStatus(command.status());

        if (title.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "书名不能为空");
        }
        if (author.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "作者不能为空");
        }
        if (category.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "分类不能为空");
        }
        if (stock == null || stock < 0) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "库存数必须大于等于 0");
        }
        if (recommendationIndex == null || recommendationIndex < 1 || recommendationIndex > 5) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "推荐指数必须在 1-5 之间");
        }
        if (status == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "状态不合法");
        }
    }

    private BookStatus parseStatus(String rawStatus) {
        if (rawStatus == null || rawStatus.isBlank()) {
            return BookStatus.NORMAL;
        }
        try {
            return BookStatus.valueOf(rawStatus.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
