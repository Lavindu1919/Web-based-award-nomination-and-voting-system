package com.sliit.awardvote.common.config;

import com.sliit.awardvote.award.model.AwardProgramme;
import com.sliit.awardvote.award.model.AwardStatus;
import com.sliit.awardvote.award.model.Category;
import com.sliit.awardvote.award.service.AwardService;
import com.sliit.awardvote.award.service.CategoryService;
import com.sliit.awardvote.content.model.Announcement;
import com.sliit.awardvote.content.model.Faq;
import com.sliit.awardvote.content.service.AnnouncementService;
import com.sliit.awardvote.content.service.FaqService;
import com.sliit.awardvote.sponsor.model.Sponsor;
import com.sliit.awardvote.sponsor.model.SponsorTier;
import com.sliit.awardvote.sponsor.service.SponsorService;
import com.sliit.awardvote.user.model.Permission;
import com.sliit.awardvote.user.model.Role;
import com.sliit.awardvote.user.model.User;
import com.sliit.awardvote.user.model.UserRole;
import com.sliit.awardvote.user.service.RoleService;
import com.sliit.awardvote.user.service.UserService;
import com.sliit.awardvote.user.util.UserFactory;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Seeds a default System Administrator account plus a small amount of demo
 * data so the application is immediately explorable after first startup.
 * Runs once - it checks for existing data before inserting anything.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final UserService userService;
    private final RoleService roleService;
    private final AwardService awardService;
    private final CategoryService categoryService;
    private final SponsorService sponsorService;
    private final AnnouncementService announcementService;
    private final FaqService faqService;
    private final UserFactory userFactory;

    public DataSeeder(UserService userService, RoleService roleService, AwardService awardService, CategoryService categoryService,
                       SponsorService sponsorService, AnnouncementService announcementService, FaqService faqService,
                       UserFactory userFactory) {
        this.userService = userService;
        this.roleService = roleService;
        this.awardService = awardService;
        this.categoryService = categoryService;
        this.sponsorService = sponsorService;
        this.announcementService = announcementService;
        this.faqService = faqService;
        this.userFactory = userFactory;
    }

    @Override
    public void run(String... args) {
        if (userService.count() > 0) {
            return; // already seeded
        }

        User admin = userFactory.createUser(UserRole.SYSTEM_ADMIN, "System Administrator", "admin@awardvotelanka.lk", "admin", "admin123");
        userService.save(admin);

        User staff = userFactory.createUser(UserRole.AWARDS_STAFF, "Awards Staff", "staff@awardvotelanka.lk", "staff", "staff123");
        userService.save(staff);

        User judge = userFactory.createUser(UserRole.JUDGE, "Judge One", "judge@awardvotelanka.lk", "judge", "judge123");
        userService.save(judge);

        User publicUser = userFactory.createUser(UserRole.PUBLIC_USER, "Demo Voter", "voter@awardvotelanka.lk", "voter", "voter123");
        userService.save(publicUser);

        // Demo custom role: a Public User assigned a role that replaces their base-role
        // defaults with REVIEW_NOMINATIONS + HANDLE_FEEDBACK + VOTE explicitly (custom roles
        // are exclusive, not additive - see User#hasPermission) — shows off Roles & Permissions.
        Role coordinatorRole = new Role();
        coordinatorRole.setName("Regional Coordinator");
        coordinatorRole.setDescription("A trusted public volunteer given nomination-review rights for their region, on top of normal voting.");
        // Custom roles are exclusive (they replace base-role defaults entirely), so VOTE must be
        // listed explicitly here too - otherwise this Public User would lose their voting ability
        // the moment this role is assigned, since the base PUBLIC_USER defaults no longer apply.
        coordinatorRole.setPermissions(Set.of(Permission.REVIEW_NOMINATIONS, Permission.HANDLE_FEEDBACK, Permission.VOTE));
        roleService.save(coordinatorRole);

        User coordinator = userFactory.createUser(UserRole.PUBLIC_USER, "Regional Coordinator Demo", "coordinator@awardvotelanka.lk", "coordinator", "coordinator123");
        coordinator.setCustomRole(coordinatorRole);
        userService.save(coordinator);

        AwardProgramme programme = new AwardProgramme();
        programme.setName("Lanka Excellence Awards 2026");
        programme.setDescription("The annual flagship awards programme celebrating outstanding achievement across Sri Lanka.");
        programme.setYear(2026);
        programme.setStatus(AwardStatus.OPEN);
        awardService.save(programme);

        Category category = new Category();
        category.setName("Innovator of the Year");
        category.setDescription("Awarded to an individual or team driving impactful innovation.");
        category.setEligibilityCriteria("Must have launched a qualifying initiative within the last 12 months.");
        category.setVotingStart(LocalDateTime.now().minusDays(1));
        category.setVotingEnd(LocalDateTime.now().plusMonths(2));
        category.setJudgingEnabled(true);
        category.setAwardProgramme(programme);
        categoryService.save(category);

        Sponsor sponsor = new Sponsor();
        sponsor.setName("Ceylon Tech Holdings");
        sponsor.setContactPerson("Ms. Perera");
        sponsor.setEmail("partnerships@ceylontech.lk");
        sponsor.setTier(SponsorTier.PLATINUM);
        sponsor.setDescription("Platinum sponsor supporting the 2026 awards season.");
        sponsorService.save(sponsor);

        Announcement announcement = new Announcement();
        announcement.setTitle("Nominations are now open!");
        announcement.setContent("Submit your nominations for the Lanka Excellence Awards 2026 today.");
        announcement.setActive(true);
        announcement.setPublishDate(LocalDateTime.now());
        announcement.setPublishedBy(admin);
        announcementService.save(announcement);

        Faq faq = new Faq();
        faq.setQuestion("Who can submit a nomination?");
        faq.setAnswer("Any registered public user can submit a nomination during an open voting window.");
        faq.setCategory("General");
        faqService.save(faq);

        System.out.println("========================================================");
        System.out.println(" Award Vote Lanka - demo data seeded.");
        System.out.println(" Login as admin: username='admin' password='admin123'");
        System.out.println(" Login as staff: username='staff' password='staff123'");
        System.out.println(" Login as judge: username='judge' password='judge123'");
        System.out.println(" Login as voter: username='voter' password='voter123'");
        System.out.println(" Login as a custom-role demo (Public User + Regional Coordinator role):");
        System.out.println("   username='coordinator' password='coordinator123'");
        System.out.println("========================================================");
    }
}
