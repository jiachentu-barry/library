package com.library.demo5.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.library.demo5.common.ApiException;
import com.library.demo5.entity.AppUser;
import com.library.demo5.entity.Book;
import com.library.demo5.entity.BorrowRecord;
import com.library.demo5.enums.BorrowStatus;
import com.library.demo5.repository.BookRepository;
import com.library.demo5.repository.BorrowRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BorrowServiceImpl implements BorrowService {

    private final BorrowRepository borrowRepository;
    private final BookRepository bookRepository;
    private final AuthService authService;

    public BorrowServiceImpl(BorrowRepository borrowRepository, BookRepository bookRepository, AuthService authService) {
        this.borrowRepository = borrowRepository;
        this.bookRepository = bookRepository;
        this.authService = authService;
    }

    @Override
    @Transactional
    public BorrowRecordResponse borrowBook(String rawUsername, Long bookId) {
        String username = trim(rawUsername);
        if (username.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "用户名不能为空");
        }
        if (bookId == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "请选择要借阅的图书");
        }

        AppUser user = authService.findByUsernameRequired(username);
        Book book = bookRepository.findById(bookId).orElse(null);
        if (book == null) {
            throw new ApiException(HttpStatus.NOT_FOUND, "图书不存在");
        }

        if (book.getStock() == null || book.getStock() <= 0) {
            throw new ApiException(HttpStatus.CONFLICT, "该图书暂无库存，无法借阅");
        }

        if (borrowRepository.existsByUserAndBookAndStatus(user, book, BorrowStatus.BORROWED)) {
            throw new ApiException(HttpStatus.CONFLICT, "您已借阅该图书，请归还后再借");
        }

        book.setStock(book.getStock() - 1);
        bookRepository.save(book);

        BorrowRecord record = new BorrowRecord();
        record.setUser(user);
        record.setBook(book);
        BorrowRecord saved = borrowRepository.save(record);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public void returnBook(Long id, String rawUsername) {
        String username = trim(rawUsername);
        BorrowRecord record = getRecord(id);

        if (!record.getUser().getUsername().equalsIgnoreCase(username)) {
            throw new ApiException(HttpStatus.FORBIDDEN, "您无权归还此借阅记录");
        }

        doReturn(record);
    }

    @Override
    public List<BorrowRecordResponse> getUserBorrows(String username) {
        AppUser user = authService.findByUsernameRequired(username);
        return borrowRepository.findByUserOrderByBorrowedAtDesc(user)
                .stream().map(this::toResponse).toList();
    }

    @Override
    public List<BorrowRecordResponse> adminListBorrows(String authUsername) {
        authService.requireAdmin(authUsername);
        return borrowRepository.findAllByOrderByBorrowedAtDesc().stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public void adminReturnBook(String authUsername, Long id) {
        authService.requireAdmin(authUsername);
        BorrowRecord record = getRecord(id);
        doReturn(record);
    }

    @Override
    public BorrowStatsResponse borrowStats() {
        LocalDate today = LocalDate.now();
        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime tomorrowStart = today.plusDays(1).atStartOfDay();
        LocalDateTime yesterdayStart = today.minusDays(1).atStartOfDay();

        long yesterdayBorrowCount = borrowRepository
                .findByBorrowedAtGreaterThanEqualAndBorrowedAtLessThanOrderByBorrowedAtAsc(yesterdayStart, todayStart)
                .size();

        long todayBorrowCount = borrowRepository
                .findByBorrowedAtGreaterThanEqualAndBorrowedAtLessThanOrderByBorrowedAtAsc(todayStart, tomorrowStart)
                .size();

        Map<String, Long> grouped = borrowRepository.findAllByOrderByBorrowedAtDesc().stream()
                .filter(r -> r.getBook() != null && r.getBook().getTitle() != null)
                .map(r -> r.getBook().getTitle().trim())
                .filter(title -> !title.isEmpty())
                .collect(Collectors.groupingBy(title -> title, Collectors.counting()));

        Map.Entry<String, Long> top = grouped.entrySet().stream()
                .max(Comparator.comparingLong(Map.Entry::getValue))
                .orElse(null);

        String mostBorrowedBookName = top == null ? "暂无数据" : top.getKey();
        long mostBorrowedBookCount = top == null ? 0L : top.getValue();

        return new BorrowStatsResponse(
                yesterdayBorrowCount,
                todayBorrowCount,
                mostBorrowedBookName,
                mostBorrowedBookCount);
    }

    private BorrowRecord getRecord(Long id) {
        BorrowRecord record = borrowRepository.findById(id).orElse(null);
        if (record == null) {
            throw new ApiException(HttpStatus.NOT_FOUND, "借阅记录不存在");
        }
        return record;
    }

    private void doReturn(BorrowRecord record) {
        if (record.getStatus() == BorrowStatus.RETURNED) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "该图书已归还");
        }

        record.setStatus(BorrowStatus.RETURNED);
        record.setReturnedAt(LocalDateTime.now());
        Book book = record.getBook();
        book.setStock(book.getStock() + 1);
        bookRepository.save(book);
        borrowRepository.save(record);
    }

    private BorrowRecordResponse toResponse(BorrowRecord r) {
        boolean overdue = r.getStatus() == BorrowStatus.BORROWED
                && r.getDueDate() != null
                && LocalDateTime.now().isAfter(r.getDueDate());
        BorrowStatus displayStatus = overdue ? BorrowStatus.OVERDUE : r.getStatus();
        return new BorrowRecordResponse(
                r.getId(),
                r.getUser().getUsername(),
                r.getBook().getId(),
                r.getBook().getTitle(),
                r.getBook().getAuthor(),
                r.getBorrowedAt(),
                r.getDueDate(),
                r.getReturnedAt(),
                displayStatus,
                overdue);
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
