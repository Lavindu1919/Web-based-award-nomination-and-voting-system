package com.sliit.awardvote.content.service;

import com.sliit.awardvote.common.dao.GenericDao;
import com.sliit.awardvote.common.service.AbstractCrudService;
import com.sliit.awardvote.content.dao.AnnouncementDao;
import com.sliit.awardvote.content.model.Announcement;
import com.sliit.awardvote.content.util.ContentSettings;

import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class AnnouncementService extends AbstractCrudService<Announcement, Long> {

    private final AnnouncementDao announcementDao;

    public AnnouncementService(AnnouncementDao announcementDao) {
        this.announcementDao = announcementDao;
    }

    @Override
    protected GenericDao<Announcement, Long> getDao() {
        return announcementDao;
    }

    /** Active announcements, newest first, limited by the shared ContentSettings singleton. */
    public List<Announcement> findLatestActive() {
        int max = ContentSettings.getInstance().getMaxHomeAnnouncements();
        return findAll().stream()
                .filter(Announcement::isActive)
                .sorted(Comparator.comparing(Announcement::getPublishDate,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(max)
                .toList();
    }
}
