package com.example.demo5.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;

import com.example.demo5.common.ApiException;
import com.example.demo5.entity.AppUser;
import com.example.demo5.enums.UserRole;
import com.example.demo5.repository.AppUserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final AppUserRepository appUserRepository;
    private final AuthService authService;

    public UserServiceImpl(AppUserRepository appUserRepository, AuthService authService) {
        this.appUserRepository = appUserRepository;
        this.authService = authService;
    }

    @Override
    public void register(String rawUsername, String rawPassword) {
        String username = trim(rawUsername);
        String password = trim(rawPassword);

        String validation = validate(username, password);
        if (validation != null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, validation);
        }

        if (appUserRepository.existsByUsernameIgnoreCase(username)) {
            throw new ApiException(HttpStatus.CONFLICT, "用户名已存在");
        }

        AppUser user = new AppUser();
        user.setUsername(username);
        user.setPasswordHash(sha256(password));
        AppUser saved = appUserRepository.save(user);
        ensureFirstUserIsAdmin(saved);
    }

    @Override
    public LoginUser login(String rawUsername, String rawPassword, String loginRole) {
        String username = trim(rawUsername);
        String password = trim(rawPassword);

        if (username.isEmpty() || password.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "用户名和密码不能为空");
        }

        AppUser user = appUserRepository.findByUsernameIgnoreCase(username).orElse(null);
        if (user == null) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "用户名或密码错误");
        }

        String inputHash = sha256(password);
        if (!inputHash.equals(user.getPasswordHash())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "用户名或密码错误");
        }

        user = ensureFirstUserIsAdmin(user);
        UserRole actualRole = authService.effectiveRole(user);
        UserRole requestedRole = parseRole(loginRole);
        if (requestedRole == UserRole.ADMIN && actualRole != UserRole.ADMIN) {
            throw new ApiException(HttpStatus.FORBIDDEN, "该账号不是管理员，无法使用管理员登录");
        }

        return new LoginUser(user.getId(), user.getUsername(), actualRole);
    }

    @Override
    public LoginUser profile(String username) {
        AppUser user = authService.findByUsernameRequired(username);
        user = ensureFirstUserIsAdmin(user);
        return new LoginUser(user.getId(), user.getUsername(), authService.effectiveRole(user));
    }

    @Override
    public List<UserAdminView> listUsersForAdmin(String authUsername) {
        authService.requireAdmin(authUsername);
        return appUserRepository.findAll().stream()
                .sorted((a, b) -> Long.compare(a.getId(), b.getId()))
                .map(user -> new UserAdminView(
                        user.getId(),
                        user.getUsername(),
                        authService.effectiveRole(user),
                        user.getCreatedAt()))
                .toList();
    }

    @Override
    public UserAdminView updateUserRole(String authUsername, Long id, String role) {
        authService.requireAdmin(authUsername);

        AppUser target = appUserRepository.findById(id).orElse(null);
        if (target == null) {
            throw new ApiException(HttpStatus.NOT_FOUND, "用户不存在");
        }

        UserRole targetRole = parseRole(role);
        if (targetRole == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "角色不合法");
        }

        if (target.getId() != null && target.getId() == 1L && targetRole != UserRole.ADMIN) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "id=1 必须保持管理员身份");
        }

        target.setRole(targetRole);
        AppUser saved = appUserRepository.save(target);
        return new UserAdminView(
                saved.getId(),
                saved.getUsername(),
                authService.effectiveRole(saved),
                saved.getCreatedAt());
    }

    private AppUser ensureFirstUserIsAdmin(AppUser user) {
        if (user.getId() != null && user.getId() == 1L && user.getRole() != UserRole.ADMIN) {
            user.setRole(UserRole.ADMIN);
            return appUserRepository.save(user);
        }
        return user;
    }

    private String validate(String username, String password) {
        if (username.isEmpty()) {
            return "用户名不能为空";
        }
        if (!username.matches("^[a-zA-Z0-9_]{3,20}$")) {
            return "用户名需为 3-20 位字母、数字或下划线";
        }
        if (password.length() < 6 || password.length() > 50) {
            return "密码长度需为 6-50 位";
        }
        return null;
    }

    private String sha256(String value) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 not supported", ex);
        }
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private UserRole parseRole(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return UserRole.valueOf(value.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
