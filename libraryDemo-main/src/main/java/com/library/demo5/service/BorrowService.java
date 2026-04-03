package com.library.demo5.service;

import java.time.LocalDateTime;
import java.util.List;
import com.library.demo5.enums.BorrowStatus;
public interface BorrowService {

    BorrowRecordResponse borrowBook(String rawUsername, Long bookId);

    void returnBook(Long id, String rawUsername);

    List<BorrowRecordResponse> getUserBorrows(String username);

    List<BorrowRecordResponse> adminListBorrows(String authUsername);

    void adminReturnBook(String authUsername, Long id);

    BorrowStatsResponse borrowStats();

        public static final class BorrowRecordResponse {
                private final Long id;
                private final String username;
                private final Long bookId;
                private final String bookTitle;
                private final String bookAuthor;
                private final LocalDateTime borrowedAt;
                private final LocalDateTime dueDate;
                private final LocalDateTime returnedAt;
                private final BorrowStatus status;
                private final boolean overdue;

                public BorrowRecordResponse(
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
                        this.id = id;
                        this.username = username;
                        this.bookId = bookId;
                        this.bookTitle = bookTitle;
                        this.bookAuthor = bookAuthor;
                        this.borrowedAt = borrowedAt;
                        this.dueDate = dueDate;
                        this.returnedAt = returnedAt;
                        this.status = status;
                        this.overdue = overdue;
                }

                public Long getId() {
                        return id;
                }

                public String getUsername() {
                        return username;
                }

                public Long getBookId() {
                        return bookId;
                }

                public String getBookTitle() {
                        return bookTitle;
                }

                public String getBookAuthor() {
                        return bookAuthor;
                }

                public LocalDateTime getBorrowedAt() {
                        return borrowedAt;
                }

                public LocalDateTime getDueDate() {
                        return dueDate;
                }

                public LocalDateTime getReturnedAt() {
                        return returnedAt;
                }

                public BorrowStatus getStatus() {
                        return status;
                }

                public boolean isOverdue() {
                        return overdue;
                }
    }

        public static final class BorrowStatsResponse {
                private final long yesterdayBorrowCount;
                private final long todayBorrowCount;
                private final String mostBorrowedBookName;
                private final long mostBorrowedBookCount;

                public BorrowStatsResponse(
                                long yesterdayBorrowCount,
                                long todayBorrowCount,
                                String mostBorrowedBookName,
                                long mostBorrowedBookCount
                ) {
                        this.yesterdayBorrowCount = yesterdayBorrowCount;
                        this.todayBorrowCount = todayBorrowCount;
                        this.mostBorrowedBookName = mostBorrowedBookName;
                        this.mostBorrowedBookCount = mostBorrowedBookCount;
                }

                public long getYesterdayBorrowCount() {
                        return yesterdayBorrowCount;
                }

                public long getTodayBorrowCount() {
                        return todayBorrowCount;
                }

                public String getMostBorrowedBookName() {
                        return mostBorrowedBookName;
                }

                public long getMostBorrowedBookCount() {
                        return mostBorrowedBookCount;
                }
    }
}
