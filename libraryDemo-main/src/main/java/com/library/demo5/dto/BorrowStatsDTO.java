package com.library.demo5.dto;

import com.library.demo5.service.BorrowService;

public record BorrowStatsDTO(
        long yesterdayBorrowCount,
        long todayBorrowCount,
        String mostBorrowedBookName,
        long mostBorrowedBookCount) {

    public static BorrowStatsDTO from(BorrowService.BorrowStatsResponse stats) {
        return new BorrowStatsDTO(
                stats.getYesterdayBorrowCount(),
                stats.getTodayBorrowCount(),
                stats.getMostBorrowedBookName(),
                stats.getMostBorrowedBookCount());
    }
}