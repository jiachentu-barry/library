package com.library.demo5.service;

import com.library.demo5.common.ApiException;
import com.library.demo5.entity.AppUser;
import com.library.demo5.enums.UserRole;
import com.library.demo5.repository.AppUserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final AppUserRepository appUserRepository;

    public AuthServiceImpl(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @Override
    public AppUser requireAdmin(String authUsername) {
        String username = trim(authUsername);
        if (username.isEmpty()) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "请先登录管理员账号");
        }

        AppUser user = appUserRepository.findByUsernameIgnoreCase(username).orElse(null);
        if (user == null) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "用户不存在");
        }

        if (effectiveRole(user) != UserRole.ADMIN) {
            throw new ApiException(HttpStatus.FORBIDDEN, "仅管理员可访问后台管理");
        }
        return user;
    }

    @Override
    public AppUser findByUsernameRequired(String username) {
        AppUser user = appUserRepository.findByUsernameIgnoreCase(trim(username)).orElse(null);
        if (user == null) {
            throw new ApiException(HttpStatus.NOT_FOUND, "用户不存在");
        }
        return user;
    }

    @Override
    public UserRole effectiveRole(AppUser user) {
        return user.getRole() == null ? UserRole.USER : user.getRole();
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
