package com.example.demo5.controller;

import java.util.List;

import com.example.demo5.enums.UserRole;
import com.example.demo5.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        userService.register(request.username(), request.password());
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiMessage("注册成功"));
    }

    @PostMapping("/users/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        UserService.LoginUser user = userService.login(request.username(), request.password(), request.loginRole());

        return ResponseEntity.ok(new LoginResponse("登录成功", new UserProfileResponse(
                user.id(),
                user.username(),
                user.role()
        )));
    }

    @GetMapping("/users/{username}/profile")
    public ResponseEntity<?> profile(@PathVariable String username) {
        UserService.LoginUser user = userService.profile(username);
        return ResponseEntity.ok(new UserProfileResponse(
                user.id(),
                user.username(),
                user.role()
        ));
    }

    @GetMapping("/admin/users")
    public ResponseEntity<?> listUsersForAdmin(@RequestHeader(value = "X-Auth-Username", required = false) String authUsername) {
        List<UserAdminResponse> users = userService.listUsersForAdmin(authUsername).stream()
            .map(user -> new UserAdminResponse(
                user.id(),
                user.username(),
                user.role(),
                user.createdAt()))
                .toList();
        return ResponseEntity.ok(users);
    }

    @PutMapping("/admin/users/{id}/role")
    public ResponseEntity<?> updateUserRole(@RequestHeader(value = "X-Auth-Username", required = false) String authUsername,
                                            @PathVariable Long id,
                                            @RequestBody UpdateUserRoleRequest request) {
        UserService.UserAdminView saved = userService.updateUserRole(authUsername, id, request.role());
        return ResponseEntity.ok(new UserAdminResponse(
                saved.id(),
                saved.username(),
                saved.role(),
                saved.createdAt()
        ));
    }

    public record RegisterRequest(String username, String password) {
    }

    public record LoginRequest(String username, String password, String loginRole) {
    }

    public record ApiMessage(String message) {
    }

    public record UpdateUserRoleRequest(String role) {
    }

    public record UserProfileResponse(Long id, String username, UserRole role) {
    }

    public record UserAdminResponse(Long id, String username, UserRole role, java.time.LocalDateTime createdAt) {
    }

    public record LoginResponse(String message, UserProfileResponse user) {
    }
}
