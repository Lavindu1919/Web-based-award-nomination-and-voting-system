# Award Vote Lanka

Web-based Voting System for Award Nominations — SE2030 Software Engineering,
**Group 02, Batch 01**, Sri Lanka Institute of Information Technology.

Built for the fictional client **Lanka Excellence Awards Council**, covering
the full award lifecycle: nomination submission → staff review → public
voting → judge evaluation → confidential winner record.

## Tech stack

| Layer      | Technology |
|------------|------------|
| Backend    | Java 17, Spring Boot 3.2 (Spring MVC, JPA/Hibernate with the DAO pattern) |
| Frontend   | Thymeleaf + Bootstrap 5 + custom CSS (navy/gold theme) |
| Database   | Microsoft SQL Server |
| Build      | Maven |

## Module ownership (6 members)

| # | Module | Package | Owner |
|---|--------|---------|-------|
| 1 | User Management | `com.sliit.awardvote.user` — users, roles & permissions, profile, login, registration, OTP, password reset | Nawarathna H.M.L.C. (IT25100124) |
| 2 | Award Management | `com.sliit.awardvote.award` — award programmes and categories | Weerasinghe A.S.T.W. (IT25100130) |
| 3 | Nominee Management | `com.sliit.awardvote.nominee` — nominations, votes and judge scores | Jayalath S.V. (IT25100138) |
| 4 | Notification & Feedback Management | `com.sliit.awardvote.notification` — notifications (email/SMS), general feedback and award-programme feedback | Aman A.A.M.H. (IT25100135) |
| 5 | Sponsor & Partner Management | `com.sliit.awardvote.sponsor` — sponsors and partners | Chandrasooriya J.T. (IT25100146) |
| 6 | Website Content Management | `com.sliit.awardvote.content` — announcements, banners and FAQs | Gamage G.D.S.S. (IT25100147) |

Each module package contains its own `controller`, `service`, `dao` and
`model` sub-packages (one controller/service/DAO/entity per thing it
manages). Code shared by every module lives in `com.sliit.awardvote.common`.

## OOP concepts demonstrated

- **Abstraction** — `BaseEntity` (`@MappedSuperclass`) and `AbstractCrudService<T, ID>`
  hide shared plumbing (id/timestamps, generic save/find/delete) behind a
  simple contract every module implements.
- **Inheritance** — three-level chain `BaseEntity → Person → User`. Every
  JPA entity extends `BaseEntity`; `User` additionally extends the abstract
  `Person` class.
- **Polymorphism** — `Person#getRoleDescription()` is overridden by `User`
  and resolved at runtime per role. The **Strategy pattern**
  (`NotificationStrategy` → `EmailNotificationStrategy` / `SmsNotificationStrategy`)
  is a second, cleaner example: `NotificationService` calls `strategy.send(...)`
  without knowing which channel it will hit.
- **Encapsulation** — every entity field is private with controlled getters/
  setters; `Vote` and `Score` go further and expose **no setters at all**
  after construction, to protect vote/score audit integrity (a design
  decision carried over from the original system design work).
- **Design patterns** — Template Method (`AbstractCrudService`), Strategy
  (`NotificationStrategy`), Data Access Object (`GenericDao` → `AbstractJpaDao`
  → per-entity `XxxDao` / `XxxDaoImpl`; services never touch the
  `EntityManager` or JPQL directly).
- **Relationships** — One-to-many (`AwardProgramme`→`Category`,
  `Category`→`Nomination`, `Nomination`→`Vote`/`Score`), Many-to-one
  (`Nomination`→`User`, `Vote`→`User`), Many-to-many
  (`AwardProgramme`↔`Sponsor`).

## Voting rule

A **public user gets exactly one vote per award category** (not one vote
per nominee). Once you vote for a nominee in "Innovator of the Year", every
other nominee in that same category is locked for you — but you can still
cast a separate vote in every other category. This is enforced twice, on
purpose:
- **Application layer** — `NominationService.castVote()` checks for an
  existing vote in the nominee's category before allowing a new one.
- **Database layer** — `Vote` carries a denormalized `category` reference
  with a `UNIQUE(category_id, voter_id)` constraint, so the rule holds even
  if something bypasses the service layer.

## Judging rule

A **judge gets exactly one judged pick per award category** too — the same
"one pick per category" rule as public voting, applied to `Score` instead of
`Vote`. A judge doesn't rate every nominee submitted to a category; they pick
their single top nominee, and that locks the rest of that category for them
(they can still judge a different category). Enforced the same way, twice:
- **Application layer** — `NominationService.submitScore()` checks for an
  existing score in the nominee's category before allowing a new one, and
  requires the category to have `judgingEnabled = true` (set per-category by
  Awards Staff/Admin, separate from the public voting window).
- **Database layer** — `Score` carries a denormalized `category` reference
  with a `UNIQUE(category_id, judge_id)` constraint.

The Nominations page shows judges an **"Open for Judging"** section (mirroring
"Open for Voting" for public users), and the nomination detail page shows one
of three states: your pick, a different pick already used for that category,
or the judging form.

> **If you already ran this app against SQL Server before:** the `scores`
> table just gained a required `category_id` column and a new unique
> constraint. `ddl-auto=update` cannot safely add a `NOT NULL` column to a
> table that already has rows, so drop the `scores` table (or the whole
> `awardvote_lanka` database) and let it get recreated on next startup.

## Roles & Permissions

Beyond the seven fixed account categories (`UserRole`), an administrator can
create **custom roles** under *User Management → Roles & Permissions* and
grant them any combination of ten fine-grained permissions (manage users,
manage roles, manage awards, review nominations, judge nominations, manage
sponsors, manage content, manage notifications, handle feedback, vote).

**Assigning a custom role to a user is exclusive, not additive.** The user
gets *exactly* the permissions checked on that role — their base role's
default permissions no longer apply at all once a custom role is attached.
For example, a Public User normally gets `VOTE` by default; hand them a
"Regional Coordinator" custom role that only has `REVIEW_NOMINATIONS`
checked, and they can review nominations but **can no longer vote**, unless
`VOTE` is checked on that role too. This is deliberate: a custom role fully
defines what that person can do, rather than quietly layering on top of
something else. The one exception is `SYSTEM_ADMIN`, which always has every
permission regardless of any custom role assigned — a safety net so an admin
account can never accidentally lock itself out.

Every built-in `UserRole` ships with sensible default permissions out of the
box (`DefaultRolePermissions`), used whenever a user has **no** custom role
assigned — so the system works immediately with zero configuration.
`User#hasPermission(Permission)` is the single source of truth — every
controller in the app (Awards, Nominations, Sponsors, Content, Feedback,
Users, Roles) authorizes against it instead of comparing role names
directly. This also fixed two pre-existing gaps where Sponsor and Content
management had no server-side authorization at all.

**Selecting a permission for a custom role grants exactly that page/action —
nothing hidden and nothing extra.** Every button, nav link, and dashboard
tile that depends on a permission is shown or hidden using the same
`hasPermission(...)` check the server enforces, so what a role can *see*
always matches what it can *do*. There are no hardcoded role-name exceptions
left anywhere in the templates or controllers (verified by grepping the
whole codebase) — for example, a custom role granted only `MANAGE_AWARDS`
gets full award-programme CRUD including delete, without needing
`SYSTEM_ADMIN`.

A demo account (`coordinator` / `coordinator123`) is seeded showing exactly
this pattern: a Public User with the "Regional Coordinator" custom role.

## Forgot Password (OTP via Email or SMS)

The login page has a **"Forgot password?"** link. The flow:

1. `/forgot-password` — enter your username or email, choose Email or SMS.
2. A 6-digit, single-use code valid for **10 minutes** is generated and sent
   through the same `NotificationStrategy` (Email/SMS) infrastructure used
   for review-decision and reminder notifications elsewhere in the app — no
   separate delivery mechanism was added. **Email is simulated** (logged to
   console — see "Real email delivery" below to wire up SMTP).
   **SMS sends for real via Twilio** once configured — see "Real SMS delivery
   via Twilio" below; until then it also just logs to console.
3. `/reset-password` — enter the code plus a new password. On success the
   OTP is marked used (it cannot be reused) and the password is hashed and
   saved exactly like any other password change.

Two things worth knowing:
- The confirmation screen after step 1 shows the **same message regardless
  of whether the identifier matched an account**, so the form can't be used
  to check who is registered.
- If SMS is chosen but the account has no phone number on file, delivery
  silently falls back to email rather than failing.


## Real email delivery (optional)

Email OTPs are currently simulated the same way SMS was before Twilio was
wired up — logged to console via `EmailNotificationStrategy`, not actually
emailed. To send real emails, the quickest route is Gmail SMTP: add
`spring-boot-starter-mail` to `pom.xml`, generate a Google **App Password**
for your account, configure `spring.mail.*` properties, and replace the
`System.out.println` in `EmailNotificationStrategy` with a `JavaMailSender`
call. Happy to wire this up the same way if you want real emails too.

## Account Registration (OTP-verified)

Self-registration (`/register`) also requires OTP verification before the
account can log in:

1. Fill in the form, choose Email or SMS, submit.
2. The account is created immediately but starts **inactive** — it exists in
   the database (visible to admins under *User Management*, shown as
   "Disabled") but cannot log in yet.
3. A 6-digit code valid for 10 minutes is sent to the chosen channel (real
   SMS if Twilio is configured, as above; email is simulated).
4. `/verify-account` — enter the code. On success the account is activated
   and you're logged straight in (no separate login step needed). A
   **"Resend Code"** button is available if the first one doesn't arrive.

Both the registration flow and the forgot-password flow share one underlying
mechanism (`OtpCode` + `OtpService`, distinguished by an `OtpPurpose` enum),
rather than each maintaining separate OTP logic — a code issued to verify a
new account can never be reused to reset a password, or vice versa.

> If you ran the app before this feature was added, you may have an old,
> now-unused `password_reset_otps` table left over (it's been replaced by
> the more general `otp_codes` table). It's harmless to leave in place, or
> you can drop it manually.

## Notification & Feedback Management (full CRUD)

Anyone holding `MANAGE_NOTIFICATIONS` (Marketing Officer by default, or a
custom role granted it) gets, on the `/notifications` page:

- **My Notifications** — same as every user: their own messages.
- **All Notifications (Every User)** — every notification ever sent, across
  every recipient, with **Compose**, **Edit**, and **Delete** actions.
  - *Compose* (`/notifications/new`) sends a brand-new notification to any
    user through the existing Email/SMS `NotificationStrategy` infrastructure
    — it's a real send, same as a system-triggered one.
  - *Edit* only corrects the stored record (message, recipient, channel,
    status, related-entity label) — it deliberately does **not** re-send the
    message. `NotificationService.updateRecord()` bypasses the dispatch hook
    that `notify()` goes through, so fixing a typo doesn't spam the
    recipient a second time.
  - *Delete* removes the record outright.

Anyone holding `HANDLE_FEEDBACK` (Marketing Officer or Awards Staff by
default) gets, on the `/feedback` page:

- The existing **Respond** action (sets a reply + marks Resolved).
- **Edit** and **Delete** on any feedback entry.
- **Log Feedback** (`/feedback/new`) — records an inquiry received outside
  the public form (phone call, in person), on someone else's behalf.

Both permissions are independent — a custom role can be granted one, the
other, or both.

## Self-Service Profile

Every logged-in user gets a **My Profile** page (`/profile`, linked from the
account dropdown) where they can update their own name, email, phone,
username and password — no `MANAGE_USERS` permission needed, since it only
ever touches their own record.

**Role changes are exclusively an admin action.** `ProfileController` is a
completely separate controller from the admin `UserController`, and its form
has no `role`, `custom role`, or `active` fields at all — they're never read
from the request, never written to the database from here. The only way to
change a user's role or custom role is the admin *Manage Users* screen
(`MANAGE_USERS` permission).

**Delete My Account** is also self-service, gated behind re-entering your
current password. It follows the same delete-or-deactivate fallback as the
admin delete flow: a hard delete is attempted first, and if the account has
related records (nominations, votes, notifications, etc.) that would be
orphaned, it's deactivated instead — either way you're logged out and can no
longer sign back in.

## Deleting Users and Sponsors

`users` and `sponsors` are both referenced by foreign keys from several
other tables — `users` from nominations, votes, scores, notifications,
feedback, announcements and OTP codes; `sponsors` from the
`programme_sponsors` join table whenever it's linked to an award programme.
Deleting a row the database would otherwise orphan is rejected at the
constraint level, so both delete actions now catch that failure and show a
clear message instead of crashing:
- **User** with any activity → delete is blocked; use **Toggle** to
  deactivate the account instead, which preserves their history.
- **Sponsor** linked to a programme → delete is blocked; remove it from
  that programme first (edit the programme's sponsor list), then delete.

A brand-new user or an unlinked sponsor deletes normally with no related
records to protect.

## Getting started

### 1. Install prerequisites
- JDK 17+
- Maven 3.9+
- Microsoft SQL Server (Developer/Express edition is fine) + SQL Server
  Management Studio (optional, for inspecting data)

### 2. Create the database

**Option A — run the provided script (recommended for a clean, documented schema):**
Open `database/create_database.sql` in SQL Server Management Studio (or run
it via `sqlcmd`) and execute it. It creates the database and all 16 tables
with every column, foreign key, and unique constraint exactly matching the
JPA entities — including the one-vote-per-category and one-judge-pick-per-
category rules. Safe to re-run; it drops and recreates every table.

**Option B — let Hibernate create it automatically:**
Just create an empty database and let `spring.jpa.hibernate.ddl-auto=update`
(already set in `application.properties`) generate the schema on first run:
```sql
CREATE DATABASE awardvote_lanka;
```
Either option works — the app doesn't care which one created the tables, as
long as the schema matches (and Option A guarantees that, since it's
generated directly from the entity classes).

### 3. Configure the connection
Edit `src/main/resources/application.properties` and update:
```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=awardvote_lanka;encrypt=true;trustServerCertificate=true
spring.datasource.username=sa
spring.datasource.password=YourStrong@Passw0rd
```
If SQL Server Authentication (`sa`) is disabled on your instance, enable it
in SSMS → Server Properties → Security → "SQL Server and Windows
Authentication mode", then restart the SQL Server service.

### 4. Run the application
```bash
mvn spring-boot:run
```
`spring.jpa.hibernate.ddl-auto=update` means Hibernate creates/updates all
tables automatically on first run — no manual schema scripts needed.

### 5. Open the site
Visit **http://localhost:8080**

On first startup, `DataSeeder` creates demo accounts so you can explore
every role immediately:

| Username | Password  | Role |
|----------|-----------|------|
| `admin`  | `admin123`| System Administrator |
| `staff`  | `staff123`| Awards Staff |
| `judge`  | `judge123`| Judge |
| `voter`  | `voter123`| Public User |
| `coordinator` | `coordinator123` | Public User + "Regional Coordinator" custom role |

It also seeds one award programme, one category, one sponsor, one
announcement and one FAQ so the homepage isn't empty.

## Suggested demo flow

1. Log in as **voter** → *Nominations* → *Submit Nomination* against the
   seeded category.
2. Log in as **staff** → *Nominations* → open the new nomination → **Approve** it.
3. Log in as **voter** again → open the nomination → **Vote**.
4. Log in as **judge** → open the nomination → submit a **score**.
5. Log in as **admin** → *Manage Users*, *Roles & Permissions*, *Site
   Content*, *Sponsors* to see the remaining modules — and try *Compose
   Notification* / *Log Feedback* from the Notifications and Feedback pages.

## Project structure

```
src/main/java/com/sliit/awardvote/
  AwardVoteLankaApplication.java
  common/        Shared code used by every module
    config/        WebConfig, AuthInterceptor, DataSeeder
    controller/    HomeController, DashboardController
    dao/           GenericDao, AbstractJpaDao
    dto/           DashboardStats
    model/         BaseEntity, Person
    service/       AbstractCrudService, DashboardService
    util/          PasswordUtil, SessionUtil
  user/          Module 1 — User Management (users, roles & permissions, profile,
                 login, registration, OTP, password reset)
  award/         Module 2 — Award Management (programmes, categories)
  nominee/       Module 3 — Nominee Management (nominations, votes, scores)
  notification/  Module 4 — Notification & Feedback Management (notifications,
                 feedback & inquiries, award-programme feedback)
  sponsor/       Module 5 — Sponsor & Partner Management
  content/       Module 6 — Website Content Management (announcements, banners, FAQs)
                 Every module has: controller/  service/  dao/  model/
                 (user/ also has util/ for DefaultRolePermissions)
src/main/resources/
  application.properties
  templates/     Thymeleaf views (Bootstrap 5, dark navy/gold theme)
  static/css/    style.css — design tokens, dark theme, animations
  static/js/     app.js — scroll reveal, count-up, ripple effects
database/
  create_database.sql   Full T-SQL schema — see "Create the database" above
```
