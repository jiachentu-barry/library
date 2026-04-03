package com.library.demo5.service;

import java.util.List;
import com.library.demo5.enums.UserRole;
public interface UserService {

    void register(String rawUsername, String rawPassword);

    LoginUser login(String rawUsername, String rawPassword, String loginRole);

    LoginUser profile(String username);

    List<UserAdminView> listUsersForAdmin(String authUsername);

    UserAdminView updateUserRole(String authUsername, Long id, String role);

    public static final class LoginUser {
        private final Long id;
        private final String username;
        private final UserRole role;

        public LoginUser(Long id, String username, UserRole role) {
            this.id = id;
            this.username = username;
            this.role = role;
        }

        public Long id() {
            return id;
        }

        public String username() {
            return username;
        }

        public UserRole role() {
            return role;
        }
    }

    public static final class UserAdminView {
        private final Long id;
        private final String username;
        private final UserRole role;
        private final java.time.LocalDateTime createdAt;

        public UserAdminView(Long id, String username, UserRole role, java.time.LocalDateTime createdAt) {
            this.id = id;
            this.username = username;
            this.role = role;
            this.createdAt = createdAt;
        }

        public Long id() {
            return id;
        }

        public String username() {
            return username;
        }

        public UserRole role() {
            return role;
        }

        public java.time.LocalDateTime createdAt() {
            return createdAt;
        }
    }
}
