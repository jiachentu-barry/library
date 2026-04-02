package com.example.demo5.controller;

import java.util.List;

import com.example.demo5.service.BorrowService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class BorrowController {

    private final BorrowService borrowService;

    public BorrowController(BorrowService borrowService) {
        this.borrowService = borrowService;
    }

    /** 借阅图书 */
    @PostMapping("/borrow")
    public ResponseEntity<?> borrowBook(@RequestBody BorrowRequest request) {
        return ResponseEntity.status(201).body(borrowService.borrowBook(request.username(), request.bookId()));
    }

    /** 用户归还图书 */
    @PostMapping("/borrow/{id}/return")
    public ResponseEntity<?> returnBook(@PathVariable Long id, @RequestBody ReturnRequest request) {
        borrowService.returnBook(id, request.username());
        return ResponseEntity.ok(new ApiMessage("归还成功"));
    }

    /** 查询用户借阅记录 */
    @GetMapping("/users/{username}/borrows")
    public ResponseEntity<?> getUserBorrows(@PathVariable String username) {
        List<BorrowService.BorrowRecordResponse> records = borrowService.getUserBorrows(username);
        return ResponseEntity.ok(records);
    }

    /** 管理员查看所有借阅记录 */
    @GetMapping("/admin/borrows")
    public ResponseEntity<?> adminListBorrows(@RequestHeader(value = "X-Auth-Username", required = false) String authUsername) {
        return ResponseEntity.ok(borrowService.adminListBorrows(authUsername));
    }

    /** 管理员强制归还 */
    @PostMapping("/admin/borrow/{id}/return")
    public ResponseEntity<?> adminReturnBook(@RequestHeader(value = "X-Auth-Username", required = false) String authUsername,
                                             @PathVariable Long id) {
        borrowService.adminReturnBook(authUsername, id);
        return ResponseEntity.ok(new ApiMessage("归还成功"));
    }

    /** 借阅统计（昨日、今日、借阅最多图书） */
    @GetMapping("/borrow/stats")
    public BorrowService.BorrowStatsResponse borrowStats() {
        return borrowService.borrowStats();
    }

    public record BorrowRequest(String username, Long bookId) {
    }

    public record ReturnRequest(String username) {
    }

    public record ApiMessage(String message) {
    }

}
