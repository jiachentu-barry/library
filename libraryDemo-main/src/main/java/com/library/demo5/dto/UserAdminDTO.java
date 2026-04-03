package com.library.demo5.dto;

import java.time.LocalDateTime;

import com.library.demo5.enums.UserRole;
import com.library.demo5.service.UserService;

public record UserAdminDTO(Long id, String username, UserRole role, LocalDateTime createdAt) {

    public static UserAdminDTO from(UserService.UserAdminView user) {
        return new UserAdminDTO(user.id(), user.username(), user.role(), user.createdAt());
    }
}