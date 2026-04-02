package com.example.demo5.service;

import java.time.LocalDateTime;
import java.util.List;
import com.example.demo5.enums.BorrowStatus;
public interface BorrowService {

    BorrowRecordResponse borrowBook(String rawUsername, Long bookId);

    void returnBook(Long id, String rawUsername);

    List<BorrowRecordResponse> getUserBorrows(String username);

    List<BorrowRecordResponse> adminListBorrows(String authUsername);

    void adminReturnBook(String authUsername, Long id);

    BorrowStatsResponse borrowStats();

    public record BorrowRecordResponse(
            Long id,
            String username,
            Long bookId,
            String bookTitle,
            String bookAuthor,
            LocalDateTime borrowedAt,
            LocalDateTime dueDate,
            LocalDateTime returnedAt,
            BorrowStatus status,
            boolean overdue
    ) {
    }

    public record BorrowStatsResponse(
            long yesterdayBorrowCount,
            long todayBorrowCount,
            String mostBorrowedBookName,
            long mostBorrowedBookCount
    ) {
    }
}
