//This service manages sponsor data and applies tier‑specific display rules before sending it to the UI
package com.sliit.awardvote.sponsor.service;

import com.sliit.awardvote.common.dao.GenericDao;
import com.sliit.awardvote.common.service.AbstractCrudService;
import com.sliit.awardvote.sponsor.model.Sponsor;
import com.sliit.awardvote.sponsor.model.SponsorTier;
import com.sliit.awardvote.sponsor.dao.SponsorDao;
import com.sliit.awardvote.sponsor.strategy.SponsorTierStrategy;

import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

// Marks this class as a Spring Service (business logic layer)
@Service
// Extends AbstractCrudService (generic CRUD operations for entities)
public class SponsorService extends AbstractCrudService<Sponsor, Long> {

    // DAO for accessing Sponsor data from the database
    private final SponsorDao sponsorDao;

    /** Strategy pattern: one display strategy per tier. Spring injects every SponsorTierStrategy bean. */
    // Map that links each SponsorTier (Bronze, Silver, etc.) to its display strategy
    private final Map<SponsorTier, SponsorTierStrategy> tierStrategies = new EnumMap<>(SponsorTier.class);

    // Constructor: Spring injects SponsorDao and all SponsorTierStrategy beans
    // Each strategy is stored in the map by its tier
    public SponsorService(SponsorDao sponsorDao, List<SponsorTierStrategy> strategies) {
        this.sponsorDao = sponsorDao;
        for (SponsorTierStrategy strategy : strategies) {
            tierStrategies.put(strategy.tier(), strategy);
        }
    }

    // Provides the DAO to the parent AbstractCrudService (so CRUD methods work)
    @Override
    protected GenericDao<Sponsor, Long> getDao() {
        return sponsorDao;
    }

    // Returns the correct strategy for a given tier
    // If tier is null or missing, defaults to Bronze strategy
    /** Picks the strategy for a tier (falls back to Bronze if the tier is missing). */
    public SponsorTierStrategy strategyFor(SponsorTier tier) {
        SponsorTierStrategy strategy = tier == null ? null : tierStrategies.get(tier);
        return strategy != null ? strategy : tierStrategies.get(SponsorTier.BRONZE);
    }

    /** All sponsors ordered by their tier's display priority, each with its tier's display rules. */
    // Gets all sponsors from DB
    // Sorts them by tier priority (e.g., Platinum first, then Gold, etc.)
    // If names are equal, sorts alphabetically
    // Wraps each sponsor in a SponsorDisplay with its tier’s icon and benefits
    public List<SponsorDisplay> findAllForDisplay() {
        return findAll().stream()
                .sorted(Comparator
                        .comparingInt((Sponsor s) -> strategyFor(s.getTier()).displayPriority())
                        .thenComparing(s -> s.getName() == null ? "" : s.getName(), String.CASE_INSENSITIVE_ORDER))
                .map(s -> {
                    SponsorTierStrategy strategy = strategyFor(s.getTier());
                    return new SponsorDisplay(s, strategy.iconClass(), strategy.benefits());
                })
                .toList();
    }
}