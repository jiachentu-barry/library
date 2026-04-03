package com.library.demo5.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.demo5.common.ApiException;
import com.library.demo5.config.GlobalExceptionHandler;
import com.library.demo5.entity.Announcement;
import com.library.demo5.enums.AnnouncementStatus;
import com.library.demo5.service.AnnouncementService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AnnouncementController.class)
@Import(GlobalExceptionHandler.class)
class AnnouncementControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AnnouncementService announcementService;

    @Test
    void listPublicAnnouncementsReturnsData() throws Exception {
        Announcement a = createAnnouncement("Notice", "Hello", AnnouncementStatus.PUBLISHED);
        when(announcementService.listPublicAnnouncements()).thenReturn(List.of(a));

        mockMvc.perform(get("/api/announcements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Notice"))
                .andExpect(jsonPath("$[0].status").value("PUBLISHED"));
    }

    @Test
    void createAnnouncementReturnsCreated() throws Exception {
        Announcement saved = createAnnouncement("Title", "Content", AnnouncementStatus.PUBLISHED);
        when(announcementService.createAnnouncement(anyString(), any(AnnouncementService.AnnouncementCommand.class)))
                .thenReturn(saved);

        AnnouncementController.AnnouncementRequest body =
                new AnnouncementController.AnnouncementRequest("Title", "Content", "PUBLISHED");

        mockMvc.perform(post("/api/admin/announcements")
                        .header("X-Auth-Username", "admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Title"));
    }

    @Test
    void updateAnnouncementWhenForbiddenReturns403() throws Exception {
        when(announcementService.updateAnnouncement(anyString(), anyLong(), any(AnnouncementService.AnnouncementCommand.class)))
                .thenThrow(new ApiException(HttpStatus.FORBIDDEN, "仅管理员可访问后台管理"));

        AnnouncementController.AnnouncementRequest body =
                new AnnouncementController.AnnouncementRequest("T", "C", "DRAFT");

        mockMvc.perform(put("/api/admin/announcements/1")
                        .header("X-Auth-Username", "tom")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("仅管理员可访问后台管理"));
    }

    @Test
    void listAdminAnnouncementsReturnsData() throws Exception {
        Announcement a = createAnnouncement("Draft One", "xxx", AnnouncementStatus.DRAFT);
        when(announcementService.listAnnouncementsForAdmin("admin")).thenReturn(List.of(a));

        mockMvc.perform(get("/api/admin/announcements").header("X-Auth-Username", "admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("DRAFT"));
    }

    private Announcement createAnnouncement(String title, String content, AnnouncementStatus status) {
        Announcement announcement = new Announcement();
        announcement.setTitle(title);
        announcement.setContent(content);
        announcement.setStatus(status);
        return announcement;
    }
}
