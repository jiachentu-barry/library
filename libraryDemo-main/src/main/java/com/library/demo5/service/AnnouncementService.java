package com.library.demo5.service;

import java.util.List;
import com.library.demo5.entity.Announcement;
public interface AnnouncementService {

    Announcement createAnnouncement(String authUsername, AnnouncementCommand command);

    Announcement updateAnnouncement(String authUsername, Long id, AnnouncementCommand command);

    List<Announcement> listAnnouncementsForAdmin(String authUsername);

    List<Announcement> listPublicAnnouncements();

    public static final class AnnouncementCommand {
        private final String title;
        private final String content;
        private final String status;

        public AnnouncementCommand(String title, String content, String status) {
            this.title = title;
            this.content = content;
            this.status = status;
        }

        public String title() {
            return title;
        }

        public String content() {
            return content;
        }

        public String status() {
            return status;
        }
    }
}
