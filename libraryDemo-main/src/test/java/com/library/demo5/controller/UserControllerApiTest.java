package com.library.demo5.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.demo5.common.ApiException;
import com.library.demo5.config.GlobalExceptionHandler;
import com.library.demo5.enums.UserRole;
import com.library.demo5.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(GlobalExceptionHandler.class)
class UserControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void registerReturnsCreated() throws Exception {
        doNothing().when(userService).register("tom", "123456");

        UserController.RegisterRequest body = new UserController.RegisterRequest("tom", "123456");

        mockMvc.perform(post("/api/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("注册成功"));
    }

    @Test
    void loginReturnsUserProfile() throws Exception {
        when(userService.login("admin", "secret", "ADMIN"))
                .thenReturn(new UserService.LoginUser(1L, "admin", UserRole.ADMIN));

        UserController.LoginRequest body = new UserController.LoginRequest("admin", "secret", "ADMIN");

        mockMvc.perform(post("/api/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("登录成功"))
                .andExpect(jsonPath("$.user.id").value(1))
                .andExpect(jsonPath("$.user.username").value("admin"))
                .andExpect(jsonPath("$.user.role").value("ADMIN"));
    }

    @Test
    void profileWhenNotFoundReturns404() throws Exception {
        when(userService.profile(anyString()))
                .thenThrow(new ApiException(HttpStatus.NOT_FOUND, "用户不存在"));

        mockMvc.perform(get("/api/users/ghost/profile"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("用户不存在"));
    }

    @Test
    void adminListUsersReturnsData() throws Exception {
        when(userService.listUsersForAdmin("admin"))
                .thenReturn(List.of(new UserService.UserAdminView(
                        1L,
                        "admin",
                        UserRole.ADMIN,
                        LocalDateTime.of(2026, 4, 3, 10, 0)
                )));

        mockMvc.perform(get("/api/admin/users").header("X-Auth-Username", "admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].username").value("admin"))
                .andExpect(jsonPath("$[0].role").value("ADMIN"));
    }

    @Test
    void updateRoleWhenForbiddenReturns403() throws Exception {
        when(userService.updateUserRole("tom", 2L, "ADMIN"))
                .thenThrow(new ApiException(HttpStatus.FORBIDDEN, "仅管理员可访问后台管理"));

        UserController.UpdateUserRoleRequest body = new UserController.UpdateUserRoleRequest("ADMIN");

        mockMvc.perform(put("/api/admin/users/2/role")
                        .header("X-Auth-Username", "tom")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("仅管理员可访问后台管理"));
    }
}
