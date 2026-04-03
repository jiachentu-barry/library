package com.library.demo5.dto;

import com.library.demo5.enums.UserRole;
import com.library.demo5.service.UserService;

public record UserProfileDTO(Long id, String username, UserRole role) {

    public static UserProfileDTO from(UserService.LoginUser user) {
        return new UserProfileDTO(user.id(), user.username(), user.role());
    }
}