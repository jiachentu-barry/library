package com.example.demo5.service;

import java.util.List;
import com.example.demo5.entity.Announcement;
public interface AnnouncementService {

    Announcement createAnnouncement(String authUsername, AnnouncementCommand command);

    Announcement updateAnnouncement(String authUsername, Long id, AnnouncementCommand command);

    List<Announcement> listAnnouncementsForAdmin(String authUsername);

    List<Announcement> listPublicAnnouncements();

    public record AnnouncementCommand(String title, String content, String status) {
    }
}
