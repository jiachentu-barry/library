package com.library.demo5.dto;

import java.time.LocalDateTime;

import com.library.demo5.enums.BorrowStatus;
import com.library.demo5.service.BorrowService;

public record BorrowRecordDTO(
        Long id,
        String username,
        Long bookId,
        String bookTitle,
        String bookAuthor,
        LocalDateTime borrowedAt,
        LocalDateTime dueDate,
        LocalDateTime returnedAt,
        BorrowStatus status,
        boolean overdue) {

    public static BorrowRecordDTO from(BorrowService.BorrowRecordResponse record) {
        return new BorrowRecordDTO(
                record.getId(),
                record.getUsername(),
                record.getBookId(),
                record.getBookTitle(),
                record.getBookAuthor(),
                record.getBorrowedAt(),
                record.getDueDate(),
                record.getReturnedAt(),
                record.getStatus(),
                record.isOverdue());
    }
}