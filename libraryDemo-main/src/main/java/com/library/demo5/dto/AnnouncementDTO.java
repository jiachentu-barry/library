package com.library.demo5.dto;

import java.time.LocalDateTime;

import com.library.demo5.entity.Announcement;
import com.library.demo5.enums.AnnouncementStatus;

public record AnnouncementDTO(
        Long id,
        String title,
        String content,
        AnnouncementStatus status,
        LocalDateTime publishedAt) {

    public static AnnouncementDTO from(Announcement announcement) {
        return new AnnouncementDTO(
                announcement.getId(),
                announcement.getTitle(),
                announcement.getContent(),
                announcement.getStatus(),
                announcement.getPublishedAt());
    }
}