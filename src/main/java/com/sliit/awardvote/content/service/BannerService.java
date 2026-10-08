package com.sliit.awardvote.content.service;

import com.sliit.awardvote.common.dao.GenericDao;
import com.sliit.awardvote.common.service.AbstractCrudService;
import com.sliit.awardvote.content.model.Banner;
import com.sliit.awardvote.content.dao.BannerDao;

import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BannerService extends AbstractCrudService<Banner, Long> {

    private final BannerDao bannerDao;

    public BannerService(BannerDao bannerDao) {
        this.bannerDao = bannerDao;
    }

    @Override
    protected GenericDao<Banner, Long> getDao() {
        return bannerDao;
    }

    /**
     * Only banners the admin has marked active, in the order they set,
     * so a still-in-progress upload (no imageUrl yet, or intentionally
     * disabled) never appears on the public site or the dashboard.
     */
    public List<Banner> findActive() {
        return bannerDao.findAll().stream()
                .filter(Banner::isActive)
                .sorted(Comparator.comparingInt(Banner::getDisplayOrder))
                .collect(Collectors.toList());
    }
}
