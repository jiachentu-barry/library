package com.library.demo5.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import com.library.demo5.common.ApiException;
import com.library.demo5.entity.Announcement;
import com.library.demo5.enums.AnnouncementStatus;
import com.library.demo5.repository.AnnouncementRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final AuthService authService;

    public AnnouncementServiceImpl(AnnouncementRepository announcementRepository, AuthService authService) {
        this.announcementRepository = announcementRepository;
        this.authService = authService;
    }

    @Override
    public Announcement createAnnouncement(String authUsername, AnnouncementCommand command) {
        authService.requireAdmin(authUsername);
        AnnouncementStatus status = parseStatus(command.status());
        validate(command.title(), command.content(), status);

        Announcement announcement = new Announcement();
        announcement.setTitle(trim(command.title()));
        announcement.setContent(trim(command.content()));
        announcement.setStatus(status);
        announcement.setPublishedAt(LocalDateTime.now());

        return announcementRepository.save(announcement);
    }

    @Override
    public Announcement updateAnnouncement(String authUsername, Long id, AnnouncementCommand command) {
        authService.requireAdmin(authUsername);
        Announcement announcement = announcementRepository.findById(id).orElse(null);
        if (announcement == null) {
            throw new ApiException(HttpStatus.NOT_FOUND, "公告不存在");
        }

        AnnouncementStatus status = parseStatus(command.status());
        validate(command.title(), command.content(), status);

        announcement.setTitle(trim(command.title()));
        announcement.setContent(trim(command.content()));
        announcement.setStatus(status);
        return announcementRepository.save(announcement);
    }

    @Override
    public List<Announcement> listAnnouncementsForAdmin(String authUsername) {
        authService.requireAdmin(authUsername);
        return announcementRepository.findAllByOrderByPublishedAtDesc();
    }

    @Override
    public List<Announcement> listPublicAnnouncements() {
        return announcementRepository.findAllByOrderByPublishedAtDesc()
                .stream()
                .filter(item -> item.getStatus() == AnnouncementStatus.PUBLISHED)
                .toList();
    }

    private void validate(String rawTitle, String rawContent, AnnouncementStatus status) {
        String title = trim(rawTitle);
        String content = trim(rawContent);
        if (title.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "标题不能为空");
        }
        if (content.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "内容不能为空");
        }
        if (status == null) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "状态不合法");
        }
    }

    private AnnouncementStatus parseStatus(String rawStatus) {
        if (rawStatus == null || rawStatus.isBlank()) {
            return AnnouncementStatus.PUBLISHED;
        }
        try {
            return AnnouncementStatus.valueOf(rawStatus.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
