package com.sliit.awardvote.content.service;

import com.sliit.awardvote.common.dao.GenericDao;
import com.sliit.awardvote.common.service.AbstractCrudService;
import com.sliit.awardvote.content.model.Faq;
import com.sliit.awardvote.content.dao.FaqDao;

import org.springframework.stereotype.Service;

@Service
public class FaqService extends AbstractCrudService<Faq, Long> {

    private final FaqDao faqDao;

    public FaqService(FaqDao faqDao) {
        this.faqDao = faqDao;
    }

    @Override
    protected GenericDao<Faq, Long> getDao() {
        return faqDao;
    }
}
