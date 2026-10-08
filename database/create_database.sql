/* =========================================================================
   Award Vote Lanka — Database Creation Script (Microsoft SQL Server / T-SQL)
   SE2030 Software Engineering — Group 02, Batch 01
   SLIIT — Faculty of Computing

   This script creates every table exactly as Hibernate/JPA would generate
   them from the entity classes in src/main/java/com/sliit/awardvote/, so it
   can be run manually (e.g. for your report, or on a server where you don't
   want to rely on spring.jpa.hibernate.ddl-auto=update) and the application
   will work against it without any schema drift.

   Run this whole script in SQL Server Management Studio (SSMS) or via
   sqlcmd. It is safe to re-run: it drops and recreates every table.
   ========================================================================= */

-- ---------------------------------------------------------------------
-- 1. Database
-- ---------------------------------------------------------------------
IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'awardvote_lanka')
BEGIN
    CREATE DATABASE awardvote_lanka;
END
GO

USE awardvote_lanka;
GO

-- ---------------------------------------------------------------------
-- 2. Drop existing tables (children first, respecting foreign keys)
-- ---------------------------------------------------------------------
IF OBJECT_ID('dbo.otp_codes', 'U') IS NOT NULL DROP TABLE dbo.otp_codes;
IF OBJECT_ID('dbo.faqs', 'U') IS NOT NULL DROP TABLE dbo.faqs;
IF OBJECT_ID('dbo.banners', 'U') IS NOT NULL DROP TABLE dbo.banners;
IF OBJECT_ID('dbo.announcements', 'U') IS NOT NULL DROP TABLE dbo.announcements;
IF OBJECT_ID('dbo.award_feedback', 'U') IS NOT NULL DROP TABLE dbo.award_feedback;
IF OBJECT_ID('dbo.feedback', 'U') IS NOT NULL DROP TABLE dbo.feedback;
IF OBJECT_ID('dbo.notifications', 'U') IS NOT NULL DROP TABLE dbo.notifications;
IF OBJECT_ID('dbo.scores', 'U') IS NOT NULL DROP TABLE dbo.scores;
IF OBJECT_ID('dbo.votes', 'U') IS NOT NULL DROP TABLE dbo.votes;
IF OBJECT_ID('dbo.nominations', 'U') IS NOT NULL DROP TABLE dbo.nominations;
IF OBJECT_ID('dbo.programme_sponsors', 'U') IS NOT NULL DROP TABLE dbo.programme_sponsors;
IF OBJECT_ID('dbo.sponsors', 'U') IS NOT NULL DROP TABLE dbo.sponsors;
IF OBJECT_ID('dbo.categories', 'U') IS NOT NULL DROP TABLE dbo.categories;
IF OBJECT_ID('dbo.award_programmes', 'U') IS NOT NULL DROP TABLE dbo.award_programmes;
IF OBJECT_ID('dbo.role_permissions', 'U') IS NOT NULL DROP TABLE dbo.role_permissions;
IF OBJECT_ID('dbo.users', 'U') IS NOT NULL DROP TABLE dbo.users;
IF OBJECT_ID('dbo.roles', 'U') IS NOT NULL DROP TABLE dbo.roles;
GO

-- ---------------------------------------------------------------------
-- 3. MODULE 1 — User Management (roles, permissions, users)
-- ---------------------------------------------------------------------

-- Admin-created custom roles (Role.java)
CREATE TABLE dbo.roles (
    id              BIGINT IDENTITY(1,1) PRIMARY KEY,
    created_at      DATETIME2 NULL,
    updated_at      DATETIME2 NULL,
    name            NVARCHAR(255) NOT NULL,
    description     NVARCHAR(500) NULL,
    CONSTRAINT uk_roles_name UNIQUE (name)
);
GO

-- Accounts (User.java, extends Person extends BaseEntity)
CREATE TABLE dbo.users (
    id              BIGINT IDENTITY(1,1) PRIMARY KEY,
    created_at      DATETIME2 NULL,
    updated_at      DATETIME2 NULL,
    full_name       NVARCHAR(255) NOT NULL,
    email           NVARCHAR(255) NOT NULL,
    phone           NVARCHAR(255) NULL,
    username        NVARCHAR(255) NOT NULL,
    password        NVARCHAR(255) NOT NULL,       -- SHA-256 hash, 64 hex chars, never plain text
    role            NVARCHAR(255) NOT NULL,       -- UserRole enum: SYSTEM_ADMIN, AWARDS_STAFF, JUDGE,
                                                    -- MARKETING_OFFICER, SPONSOR_COORDINATOR, NOMINEE, PUBLIC_USER
    active          BIT NOT NULL,
    custom_role_id  BIGINT NULL,                  -- optional admin-assigned Role, layered on top of `role`
    CONSTRAINT uk_users_username UNIQUE (username),
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT fk_users_custom_role FOREIGN KEY (custom_role_id) REFERENCES dbo.roles (id)
);
GO

-- Permissions granted by each custom role (Role.permissions, @ElementCollection)
CREATE TABLE dbo.role_permissions (
    role_id     BIGINT NOT NULL,
    permission  NVARCHAR(255) NOT NULL,           -- Permission enum: MANAGE_USERS, MANAGE_ROLES, MANAGE_AWARDS,
                                                    -- REVIEW_NOMINATIONS, JUDGE_NOMINATIONS, MANAGE_SPONSORS,
                                                    -- MANAGE_CONTENT, MANAGE_NOTIFICATIONS, HANDLE_FEEDBACK, VOTE
    CONSTRAINT pk_role_permissions PRIMARY KEY (role_id, permission),
    CONSTRAINT fk_role_permissions_role FOREIGN KEY (role_id) REFERENCES dbo.roles (id) ON DELETE CASCADE
);
GO

-- ---------------------------------------------------------------------
-- 4. MODULE 2 — Award Management (programmes, categories)
-- ---------------------------------------------------------------------

CREATE TABLE dbo.award_programmes (
    id              BIGINT IDENTITY(1,1) PRIMARY KEY,
    created_at      DATETIME2 NULL,
    updated_at      DATETIME2 NULL,
    name            NVARCHAR(255) NOT NULL,
    description     NVARCHAR(2000) NULL,
    year            INT NOT NULL,
    status          NVARCHAR(255) NULL            -- AwardStatus enum: DRAFT, OPEN, CLOSED, COMPLETED
);
GO

CREATE TABLE dbo.categories (
    id                      BIGINT IDENTITY(1,1) PRIMARY KEY,
    created_at              DATETIME2 NULL,
    updated_at              DATETIME2 NULL,
    name                    NVARCHAR(255) NOT NULL,
    description             NVARCHAR(1000) NULL,
    eligibility_criteria    NVARCHAR(1000) NULL,
    voting_start            DATETIME2 NULL,
    voting_end              DATETIME2 NULL,
    judging_enabled         BIT NOT NULL,
    programme_id            BIGINT NULL,
    CONSTRAINT fk_categories_programme FOREIGN KEY (programme_id) REFERENCES dbo.award_programmes (id)
);
GO

-- ---------------------------------------------------------------------
-- 5. MODULE 5 — Sponsor & Partner Management
-- ---------------------------------------------------------------------

CREATE TABLE dbo.sponsors (
    id              BIGINT IDENTITY(1,1) PRIMARY KEY,
    created_at      DATETIME2 NULL,
    updated_at      DATETIME2 NULL,
    name            NVARCHAR(255) NOT NULL,
    contact_person  NVARCHAR(255) NULL,
    email           NVARCHAR(255) NULL,
    phone           NVARCHAR(255) NULL,
    logo_url        NVARCHAR(255) NULL,
    tier            NVARCHAR(255) NULL,           -- SponsorTier enum: PLATINUM, GOLD, SILVER, BRONZE
    description     NVARCHAR(1000) NULL
);
GO

-- Many-to-many: AwardProgramme <-> Sponsor
CREATE TABLE dbo.programme_sponsors (
    programme_id    BIGINT NOT NULL,
    sponsor_id      BIGINT NOT NULL,
    CONSTRAINT pk_programme_sponsors PRIMARY KEY (programme_id, sponsor_id),
    CONSTRAINT fk_progsponsors_programme FOREIGN KEY (programme_id) REFERENCES dbo.award_programmes (id),
    CONSTRAINT fk_progsponsors_sponsor FOREIGN KEY (sponsor_id) REFERENCES dbo.sponsors (id)
);
GO

-- ---------------------------------------------------------------------
-- 6. MODULE 3 — Nominee Management (nominations, votes, scores)
-- ---------------------------------------------------------------------

CREATE TABLE dbo.nominations (
    id                  BIGINT IDENTITY(1,1) PRIMARY KEY,
    created_at          DATETIME2 NULL,
    updated_at          DATETIME2 NULL,
    nominee_name        NVARCHAR(255) NOT NULL,
    nominee_email       NVARCHAR(255) NOT NULL,
    description         NVARCHAR(2000) NULL,
    evidence_url        NVARCHAR(255) NULL,
    status              NVARCHAR(255) NULL,       -- NominationStatus enum: SUBMITTED, UNDER_REVIEW, APPROVED, REJECTED
    review_comment      NVARCHAR(1000) NULL,
    category_id         BIGINT NULL,
    submitted_by        BIGINT NULL,
    CONSTRAINT fk_nominations_category FOREIGN KEY (category_id) REFERENCES dbo.categories (id),
    CONSTRAINT fk_nominations_submitted_by FOREIGN KEY (submitted_by) REFERENCES dbo.users (id)
);
GO

-- One vote per user PER CATEGORY (not per nominee) — enforced by uk_vote_category_voter.
-- category_id is a denormalized copy of nominations.category_id purely so this constraint can exist.
CREATE TABLE dbo.votes (
    id              BIGINT IDENTITY(1,1) PRIMARY KEY,
    created_at      DATETIME2 NULL,
    updated_at      DATETIME2 NULL,
    nomination_id   BIGINT NOT NULL,
    voter_id        BIGINT NOT NULL,
    category_id     BIGINT NOT NULL,
    CONSTRAINT fk_votes_nomination FOREIGN KEY (nomination_id) REFERENCES dbo.nominations (id),
    CONSTRAINT fk_votes_voter FOREIGN KEY (voter_id) REFERENCES dbo.users (id),
    CONSTRAINT fk_votes_category FOREIGN KEY (category_id) REFERENCES dbo.categories (id),
    CONSTRAINT uk_vote_nomination_voter UNIQUE (nomination_id, voter_id),
    CONSTRAINT uk_vote_category_voter UNIQUE (category_id, voter_id)
);
GO

-- One judged pick per judge PER CATEGORY (not per nominee) — enforced by uk_score_category_judge.
-- Mirrors the votes table exactly, same reasoning.
CREATE TABLE dbo.scores (
    id              BIGINT IDENTITY(1,1) PRIMARY KEY,
    created_at      DATETIME2 NULL,
    updated_at      DATETIME2 NULL,
    nomination_id   BIGINT NOT NULL,
    judge_id        BIGINT NOT NULL,
    category_id     BIGINT NOT NULL,
    score_value     FLOAT NOT NULL,
    comments        NVARCHAR(1000) NULL,
    CONSTRAINT fk_scores_nomination FOREIGN KEY (nomination_id) REFERENCES dbo.nominations (id),
    CONSTRAINT fk_scores_judge FOREIGN KEY (judge_id) REFERENCES dbo.users (id),
    CONSTRAINT fk_scores_category FOREIGN KEY (category_id) REFERENCES dbo.categories (id),
    CONSTRAINT uk_score_nomination_judge UNIQUE (nomination_id, judge_id),
    CONSTRAINT uk_score_category_judge UNIQUE (category_id, judge_id)
);
GO

-- ---------------------------------------------------------------------
-- 7. MODULE 4 — Notification & Feedback Management
-- ---------------------------------------------------------------------

CREATE TABLE dbo.notifications (
    id              BIGINT IDENTITY(1,1) PRIMARY KEY,
    created_at      DATETIME2 NULL,
    updated_at      DATETIME2 NULL,
    recipient_id    BIGINT NOT NULL,
    message         NVARCHAR(1000) NOT NULL,
    type            NVARCHAR(255) NULL,           -- NotificationType enum: EMAIL, SMS
    status          NVARCHAR(255) NULL,           -- NotificationStatus enum: PENDING, SENT, FAILED
    related_entity  NVARCHAR(255) NULL,
    CONSTRAINT fk_notifications_recipient FOREIGN KEY (recipient_id) REFERENCES dbo.users (id)
);
GO

CREATE TABLE dbo.feedback (
    id              BIGINT IDENTITY(1,1) PRIMARY KEY,
    created_at      DATETIME2 NULL,
    updated_at      DATETIME2 NULL,
    name            NVARCHAR(255) NOT NULL,
    email           NVARCHAR(255) NOT NULL,
    subject         NVARCHAR(255) NULL,
    message         NVARCHAR(2000) NOT NULL,
    status          NVARCHAR(255) NULL,           -- FeedbackStatus enum: OPEN, IN_PROGRESS, RESOLVED
    response        NVARCHAR(2000) NULL,
    submitted_by    BIGINT NULL,
    handled_by      BIGINT NULL,
    CONSTRAINT fk_feedback_submitted_by FOREIGN KEY (submitted_by) REFERENCES dbo.users (id),
    CONSTRAINT fk_feedback_handled_by FOREIGN KEY (handled_by) REFERENCES dbo.users (id)
);
GO

-- Award programme feedback (comments left on an award programme page)
CREATE TABLE dbo.award_feedback (
    id                  BIGINT IDENTITY(1,1) PRIMARY KEY,
    created_at          DATETIME2 NULL,
    updated_at          DATETIME2 NULL,
    message             NVARCHAR(1000) NOT NULL,
    award_programme_id  BIGINT NOT NULL,
    user_id             BIGINT NOT NULL,
    CONSTRAINT fk_award_feedback_programme FOREIGN KEY (award_programme_id) REFERENCES dbo.award_programmes (id),
    CONSTRAINT fk_award_feedback_user FOREIGN KEY (user_id) REFERENCES dbo.users (id)
);
GO

-- ---------------------------------------------------------------------
-- 8. MODULE 6 — Website Content Management
-- ---------------------------------------------------------------------

CREATE TABLE dbo.announcements (
    id              BIGINT IDENTITY(1,1) PRIMARY KEY,
    created_at      DATETIME2 NULL,
    updated_at      DATETIME2 NULL,
    title           NVARCHAR(255) NOT NULL,
    content         NVARCHAR(3000) NOT NULL,
    active          BIT NOT NULL,
    publish_date    DATETIME2 NULL,
    published_by    BIGINT NULL,
    CONSTRAINT fk_announcements_published_by FOREIGN KEY (published_by) REFERENCES dbo.users (id)
);
GO

CREATE TABLE dbo.banners (
    id              BIGINT IDENTITY(1,1) PRIMARY KEY,
    created_at      DATETIME2 NULL,
    updated_at      DATETIME2 NULL,
    title           NVARCHAR(255) NOT NULL,
    image_url       NVARCHAR(255) NULL,
    link_url        NVARCHAR(255) NULL,
    active          BIT NOT NULL,
    display_order   INT NOT NULL
);
GO

CREATE TABLE dbo.faqs (
    id              BIGINT IDENTITY(1,1) PRIMARY KEY,
    created_at      DATETIME2 NULL,
    updated_at      DATETIME2 NULL,
    question        NVARCHAR(255) NOT NULL,
    answer          NVARCHAR(2000) NOT NULL,
    category        NVARCHAR(255) NULL,
    display_order   INT NOT NULL
);
GO

-- ---------------------------------------------------------------------
-- 9. Auth — OTP codes (account verification + forgot-password, shared mechanism)
-- ---------------------------------------------------------------------

CREATE TABLE dbo.otp_codes (
    id              BIGINT IDENTITY(1,1) PRIMARY KEY,
    created_at      DATETIME2 NULL,
    updated_at      DATETIME2 NULL,
    user_id         BIGINT NOT NULL,
    code            NVARCHAR(6) NOT NULL,
    purpose         NVARCHAR(255) NOT NULL,       -- OtpPurpose enum: ACCOUNT_VERIFICATION, PASSWORD_RESET
    expires_at      DATETIME2 NOT NULL,
    used            BIT NOT NULL,
    CONSTRAINT fk_otp_codes_user FOREIGN KEY (user_id) REFERENCES dbo.users (id)
);
GO

-- ---------------------------------------------------------------------
-- Done. 17 tables created:
--   roles, users, role_permissions                              (Module 1)
--   award_programmes, categories                                (Module 2)
--   nominations, votes, scores                                  (Module 3)
--   notifications, feedback, award_feedback                     (Module 4)
--   sponsors, programme_sponsors                                (Module 5)
--   announcements, banners, faqs                                (Module 6)
--   otp_codes                                                   (Auth)
-- ---------------------------------------------------------------------
