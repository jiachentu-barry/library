package com.example.demo5.service;

import java.util.List;
import com.example.demo5.enums.UserRole;
public interface UserService {

    void register(String rawUsername, String rawPassword);

    LoginUser login(String rawUsername, String rawPassword, String loginRole);

    LoginUser profile(String username);

    List<UserAdminView> listUsersForAdmin(String authUsername);

    UserAdminView updateUserRole(String authUsername, Long id, String role);

    public record LoginUser(Long id, String username, UserRole role) {
    }

    public record UserAdminView(Long id, String username, UserRole role, java.time.LocalDateTime createdAt) {
    }
}
