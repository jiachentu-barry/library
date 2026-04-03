package com.library.demo5.repository;

import java.util.List;

import com.library.demo5.entity.Announcement;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    List<Announcement> findAllByOrderByPublishedAtDesc();
}
