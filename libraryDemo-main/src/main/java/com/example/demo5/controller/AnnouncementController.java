package com.example.demo5.controller;

import java.util.List;

import com.example.demo5.entity.Announcement;
import com.example.demo5.service.AnnouncementService;

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
public class AnnouncementController {

    private final AnnouncementService announcementService;

    public AnnouncementController(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    @PostMapping("/admin/announcements")
    public ResponseEntity<?> createAnnouncement(@RequestHeader(value = "X-Auth-Username", required = false) String authUsername,
                                                @RequestBody AnnouncementRequest request) {
        Announcement saved = announcementService.createAnnouncement(authUsername,
                new AnnouncementService.AnnouncementCommand(request.title(), request.content(), request.status()));
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/admin/announcements/{id}")
    public ResponseEntity<?> updateAnnouncement(@RequestHeader(value = "X-Auth-Username", required = false) String authUsername,
                                                @PathVariable Long id,
                                                @RequestBody AnnouncementRequest request) {
        Announcement saved = announcementService.updateAnnouncement(authUsername, id,
                new AnnouncementService.AnnouncementCommand(request.title(), request.content(), request.status()));
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/admin/announcements")
    public ResponseEntity<?> listAnnouncementsForAdmin(@RequestHeader(value = "X-Auth-Username", required = false) String authUsername) {
        return ResponseEntity.ok(announcementService.listAnnouncementsForAdmin(authUsername));
    }

    @GetMapping("/announcements")
    public List<Announcement> listPublicAnnouncements() {
        return announcementService.listPublicAnnouncements();
    }

    public record AnnouncementRequest(String title, String content, String status) {
    }

}
