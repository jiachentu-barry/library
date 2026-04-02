package com.example.demo5.service;

import com.example.demo5.entity.AppUser;
import com.example.demo5.enums.UserRole;

public interface AuthService {

    AppUser requireAdmin(String authUsername);

    AppUser findByUsernameRequired(String username);

    UserRole effectiveRole(AppUser user);
}
