# BU FINDER — Campus Lost and Found Web Application

A modern, full-stack, enterprise-grade Lost and Found web application engineered with **Java 17, Spring Boot 3, Spring Data JPA, Spring Security 6, Thymeleaf, Bootstrap 5, and MySQL**.

Originally based on the Android prototype "BU Finder", this project elevates the platform into a comprehensive, responsive web application that preserves the complete hierarchical categorization taxonomy, dynamic sub-types, brands, colors, and reporting workflows while adding multi-user authentication, ownership verification claims, file uploads, role-based administration, and persistent MySQL database storage.

---

## 🌟 Key Features

### 1. Public & Exploration Pages
- **Interactive Home Page:**
  - Modern hero section with quick links to report lost or found items.
  - Live campus recovery metrics (Total Reports, Active Lost, Active Found, Resolved Handover).
  - Integrated search bar with instant category filters.
  - Interactive category shortcut pills.
  - Real-time lists of recently reported Lost items and Found items.
  - 4-step workflow guide explaining reporting, matching, claim verification, and safe handover.
- **Browse & Filter Catalog:**
  - Search across titles, descriptions, brands, sub-types, and colors.
  - Granular filters: Report Type (`LOST` / `FOUND`), Category, Status (`ACTIVE`, `CLAIM_PENDING`, `RESOLVED`, `CLOSED`), and Location.
  - Pagination and sorting (newest/oldest).
- **Item Details:**
  - Full item presentation with classification specifications (Category, Sub-type, Brand, Color, Incident Date, Location).
  - High-resolution item photos.
  - Protected reporter contact information (masked for guests, revealed to authenticated members).
  - Similar items recommendations within the same category.
- **Campus Information & Help Desk:**
  - Dedicated **About** page explaining mission and architectural stack.
  - Dedicated **Contact & Help Desk** page with physical center hours and inquiry form.

### 2. False-Claim Protection & Ownership Verification Workflow
- **Confidential Distinguishing Details:**
  - When reporting an item, reporters can input secret identifying marks (e.g. serial numbers, engravings, phone wallpapers, unique keychain tags).
  - These identifying marks are **hidden from public view** and visible only to the reporter and system administrators.
- **Interactive Claim Submission:**
  - A user who lost or found an item can click **"This Is My Item / Submit Claim"**.
  - Claimants must provide proof of ownership matching the secret distinguishing details.
  - Claimants cannot submit claims on items they reported themselves.
- **Claim Review & Decision Workflow:**
  - The reporter receives the claim in their private dashboard under **Claims Received**.
  - The reporter compares the claimant's proof with their private notes.
  - **Approve Claim:** Automatically changes the item status to `RESOLVED` and notifies the claimant with reviewer remarks.
  - **Reject Claim:** Rejects the claim with explanation and resets the item status to `ACTIVE`.

### 3. Dynamic Cascading Taxonomy (BU Finder Domain Preservation)
- Category selection dynamically updates the available **Sub-types** and **Brands** using real-time JavaScript and REST API endpoints (`/api/categories/subtypes`, `/api/categories/brands`), directly mirroring the original BU Finder Android forms:
  - *Audio Devices* &rarr; Speakers, Headphones, Earphones &rarr; Bose, Apple, Boat, Noise, Sony, JBL, etc.
  - *Laptop & Accessories* &rarr; Laptop, Keyboard, Mouse, Charger, Sleeve &rarr; Apple, HP, Dell, Lenovo, Asus, etc.
  - *Wearables* &rarr; Spectacles, Watches, Jewellery &rarr; Casio, Titan, Oakley, Apple, G-Shock, etc.
  - *Footwear* &rarr; Shoes, Slippers, Sandals &rarr; Nike, Adidas, Puma, Sparx, Crocs, Decathlon, etc.
  - *Keys* &rarr; Car Keys, Room Keys, Locker Keys &rarr; Godrej, Link, Maruti, Hyundai, etc.
  - *Cards* &rarr; Student ID, Credit/Debit, Driver's License &rarr; Bennett University, HDFC, SBI, etc.
  - *Bottles* &rarr; Steel, Gym, Flask &rarr; Milton, Cello, Hydro Flask, etc.

### 4. User Dashboard & Profile Management
- **My Reported Items:** Manage user-created reports with direct links to view, edit, or delete.
- **Claims Received:** Review incoming ownership claims on reported items with one-click approve/reject actions.
- **Claims Sent:** Track status (`PENDING`, `APPROVED`, `REJECTED`) and reviewer notes on claims submitted by the user.
- **Profile Management:** Update personal contact details (full name, phone) and change account password securely.

### 5. Administrator Portal
- Protected under `ROLE_ADMIN` (`/admin/**`).
- **Metrics Dashboard:** Real-time statistics across all users, reports, active searches, handovers, and claims.
- **Item Moderation (`/admin/items`):** Search, change status, or delete any campus report.
- **Claim Oversight (`/admin/claims`):** Administrative approval or rejection of contested claims.
- **User Directory (`/admin/users`):** View registered campus members, roles, and registration dates.

### 6. Security & Engineering Standards
- **Spring Security 6** with BCrypt password hashing.
- Role-Based Access Control (`ROLE_USER` vs. `ROLE_ADMIN`).
- CSRF protection enabled on all form submissions.
- Strict server-side validation using Jakarta Bean Validation (`@Valid`, `@NotBlank`, `@Size`, `@Email`).
- Secure file upload handling: validates MIME types, allowed extensions (`jpg`, `jpeg`, `png`, `webp`, `gif`), and 5MB size limit with unique UUID filenames.

---

## 🛠️ Technology Stack

| Layer | Technology |
|---|---|
| **Language & Runtime** | Java 17 (OpenJDK 17 LTS) |
| **Backend Framework** | Spring Boot 3.2.5 (Spring MVC, Spring Data JPA, Spring Security 6) |
| **Frontend Templates** | Thymeleaf 3 + Thymeleaf Spring Security 6 dialect |
| **UI & Styling** | Bootstrap 5.3, Bootstrap Icons, Google Fonts 'Inter', Custom CSS (Navy & Teal palette) |
| **Database** | MySQL Server 8.0 / 8.1 (Local and Cloud Railway MySQL) |
| **Persistence** | Hibernate ORM 6.4 with HikariCP connection pooling |
| **Build & Containerization** | Apache Maven 3.9.x, Docker multi-stage build |

---

## 🚢 Deploying to Railway (Step-by-Step Guide)

The application is completely configured for seamless deployment to [Railway.app](https://railway.app).

### Step 1: Push Code to GitHub
Ensure all your files are committed to a GitHub repository (note that local passwords and upload directories are safely ignored by `.gitignore`):
```powershell
git init
git add .
git commit -m "Prepare Lost and Found application for Railway deployment"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/YOUR_REPOSITORY.git
git push -u origin main
```

### Step 2: Create a Project on Railway
1. Log in to [Railway](https://railway.app).
2. Click **New Project** &rarr; select **Deploy from GitHub repo**.
3. Choose your repository.

### Step 3: Add MySQL Database on Railway
1. In your Railway project canvas, click **Create** &rarr; **Database** &rarr; **Add MySQL**.
2. Railway creates a managed MySQL service.
3. Click on the MySQL card, open the **Variables** tab, and note that Railway automatically generates:
   - `MYSQLHOST`
   - `MYSQLPORT`
   - `MYSQLDATABASE`
   - `MYSQLUSER`
   - `MYSQLPASSWORD`
   - `MYSQL_URL`

### Step 4: Link MySQL to the Web Service
1. Click on your application web service card in the Railway canvas.
2. Go to the **Variables** tab.
3. Click **+ New Variable** &rarr; **Add Reference**:
   - Reference `MYSQLHOST` from the MySQL service.
   - Reference `MYSQLPORT` from the MySQL service.
   - Reference `MYSQLDATABASE` from the MySQL service.
   - Reference `MYSQLUSER` from the MySQL service.
   - Reference `MYSQLPASSWORD` from the MySQL service.
   *(Or simply reference `MYSQL_URL`; our built-in `RailwayDataSourcePostProcessor` automatically translates `mysql://` into JDBC format!)*

### Step 5: Configure Persistent Volume for Image Uploads
*Important: Containers on Railway have ephemeral filesystems. To keep uploaded item photos across redeployments:*
1. In your application service on Railway, click on the **Volumes** tab.
2. Click **+ Add Volume**.
3. Set the **Mount Path** to:
   ```
   /app/uploads
   ```
4. In the **Variables** tab, set:
   ```
   UPLOAD_DIR=/app/uploads
   ```

### Step 6: Verify Deployment Variables
Ensure your application service has these variables set (under **Variables**):

| Variable Name | Value / Reference | Purpose |
|---|---|---|
| `PORT` | Auto-provided by Railway | Railway routes web traffic to this port |
| `SPRING_PROFILES_ACTIVE` | `prod` | Activates production profile |
| `UPLOAD_DIR` | `/app/uploads` | Path to persistent volume for item images |
| `MYSQLHOST` | `${{MySQL.MYSQLHOST}}` | Railway MySQL host |
| `MYSQLPORT` | `${{MySQL.MYSQLPORT}}` | Railway MySQL port |
| `MYSQLDATABASE` | `${{MySQL.MYSQLDATABASE}}` | Railway MySQL database name |
| `MYSQLUSER` | `${{MySQL.MYSQLUSER}}` | Railway MySQL user |
| `MYSQLPASSWORD` | `${{MySQL.MYSQLPASSWORD}}` | Railway MySQL password |
| `APP_SEED_DATA` | `true` (or `false` for blank DB) | Seeds initial admin/demo items on first boot |

### Step 7: Deploy and Access
1. Railway automatically detects the root `Dockerfile` and builds the container.
2. Go to the **Settings** tab of your service &rarr; **Networking** &rarr; click **Generate Domain** to get a public URL (e.g. `https://lost-and-found-production.up.railway.app`).
3. Open the generated domain in your browser!

---

## 💻 Local Development Setup

### 1. Prerequisites
- **Java 17 LTS (OpenJDK 17):** `java -version`
- **Apache Maven 3.8+:** `mvn -version`
- **MySQL 8.0 or 8.1:** Running locally on port `3306`

### 2. Local Database Setup
Log into MySQL:
```powershell
mysql -u root -p
```
Create the database:
```sql
CREATE DATABASE IF NOT EXISTS lost_and_found_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
```

### 3. Local Credentials Configuration
Copy `application-local.properties.example` to `application-local.properties`:
```powershell
cp src/main/resources/application-local.properties.example src/main/resources/application-local.properties
```
Edit `application-local.properties` with your local MySQL password. This file is excluded from Git by `.gitignore`.

### 4. Running Locally
Run with Maven:
```powershell
mvn spring-boot:run
```
Or run the packaged JAR:
```powershell
mvn clean package -DskipTests
java -jar target/lost-and-found-web-1.0.0.jar
```
Access the application at: **http://localhost:8080**

---

## 🔑 Pre-Seeded Demo Accounts

When starting with an empty database and `app.seed-data=true`, the system seeds initial accounts:

| Role | Email Address | Password | Permissions |
|---|---|---|---|
| **Administrator** | `admin@bufinder.com` | `Admin@123` | Full access: Admin Portal (`/admin`), manage items, claims, and users |
| **Student / User** | `demo@bufinder.com` | `User@123` | Regular access: Report items, submit claims, dashboard (`/dashboard`) |
| **Student / User** | `ananya@bufinder.com` | `User@123` | Regular member with active found item reports |

---

## 🧪 Running Automated Tests

Run the complete test suite (20 automated unit & integration tests):
```powershell
mvn test
```

Test coverage includes:
- `LostAndFoundApplicationTests`: Spring Boot context boot test.
- `UserServiceTest`: User registration, duplicate checks, password validation.
- `ItemServiceTest`: Item creation, updating, ownership verification, search.
- `ClaimServiceTest`: Claim submission, self-claim prevention, automatic status transitions.
- `WebSecurityIntegrationTest`: Public page accessibility, anonymous redirects, role-based protection for `/dashboard` and `/admin`, CSRF-protected form submission.

---

## 🗺️ Important Application URLs

| URL | Access Level | Description |
|---|---|---|
| `/` | Public | Home page with hero, live counters, recent lost/found items |
| `/items` | Public | Browse items catalog with search, filters, pagination |
| `/items/{id}` | Public | Detailed item page, specs, photo, and claim modal |
| `/about` | Public | About the platform and technology stack |
| `/contact` | Public | Help desk hours, physical center location, inquiry form |
| `/login` | Public | User authentication page |
| `/register` | Public | New user registration |
| `/dashboard` | User / Admin | User dashboard (My Items, Claims Received, Claims Sent) |
| `/profile` | User / Admin | Profile management and password updates |
| `/items/report-lost` | User / Admin | Report lost item form with dynamic dropdowns |
| `/items/report-found` | User / Admin | Report found item form with dynamic dropdowns |
| `/items/{id}/edit` | Owner / Admin | Edit item details or modify recovery status |
| `/admin` | Admin only | Administrator overview metrics |
| `/admin/items` | Admin only | Moderation table for all campus items |
| `/admin/claims` | Admin only | Moderation table for ownership claims |
| `/admin/users` | Admin only | Directory of registered campus accounts |
| `/api/categories/all` | Public REST | JSON taxonomy for categories, sub-types, and brands |

---

## 📁 Project Architecture & Structure

```
Lost and found application/
├── Dockerfile                                # Multi-stage Dockerfile for Railway deployment
├── .dockerignore                             # Files excluded from Docker build context
├── .gitignore                                # Secret and artifact protection for Git
├── pom.xml                                   # Maven configuration with Spring Boot 3.2.5
├── README.md                                 # Full system documentation & Railway deployment guide
├── uploads/                                  # Uploaded item photos (git-ignored)
├── src/
│   ├── main/
│   │   ├── java/com/bufinder/web/
│   │   │   ├── LostAndFoundApplication.java  # Main application entry point
│   │   │   ├── config/
│   │   │   │   ├── SecurityConfig.java       # Spring Security 6 RBAC & CSRF
│   │   │   │   ├── MvcConfig.java            # Static uploads resource handler
│   │   │   │   └── RailwayDataSourcePostProcessor.java # Converts mysql:// to jdbc:mysql://
│   │   │   ├── model/
│   │   │   │   ├── User.java                 # User entity with Role enum
│   │   │   │   ├── Item.java                 # Lost & Found report entity
│   │   │   │   ├── Claim.java                # Ownership claim verification entity
│   │   │   │   ├── Role.java                 # ROLE_USER, ROLE_ADMIN
│   │   │   │   ├── ItemType.java             # LOST, FOUND
│   │   │   │   ├── ItemStatus.java           # ACTIVE, CLAIM_PENDING, RESOLVED, CLOSED
│   │   │   │   └── ClaimStatus.java          # PENDING, APPROVED, REJECTED
│   │   │   ├── repository/
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── ItemRepository.java       # Search query with multi-field filters
│   │   │   │   └── ClaimRepository.java
│   │   │   ├── service/
│   │   │   │   ├── UserService.java & Impl
│   │   │   │   ├── ItemService.java & Impl
│   │   │   │   ├── ClaimService.java & Impl
│   │   │   │   ├── CategoryDataService.java  # Preserves BU Finder original taxonomy
│   │   │   │   ├── FileUploadService.java    # Secure image upload handler
│   │   │   │   └── DataInitializer.java      # Seeds admin, demo users, sample items
│   │   │   ├── dto/
│   │   │   │   ├── UserRegistrationDto.java
│   │   │   │   ├── ItemFormDto.java
│   │   │   │   ├── ClaimFormDto.java
│   │   │   │   └── UserProfileDto.java
│   │   │   └── controller/
│   │   │       ├── HomeController.java       # /, /about, /contact
│   │   │       ├── AuthController.java       # /login, /register
│   │   │       ├── ItemController.java       # Browse, Details, Report Lost/Found, Edit, Claim
│   │   │       ├── DashboardController.java  # User dashboard, profile, claim approval
│   │   │       ├── AdminController.java      # /admin metrics, items, claims, users
│   │   │       └── ApiController.java        # REST taxonomy APIs for cascading UI
│   │   └── resources/
│   │       ├── application.properties        # Railway and Production configuration (no secrets)
│   │       ├── application-local.properties  # Local configuration with password (git-ignored)
│   │       ├── application-local.properties.example # Template for other developers
│   │       ├── sql/
│   │       │   └── schema.sql                # Complete MySQL DDL schema
│   │       ├── static/
│   │       │   ├── css/style.css             # Navy, white & subtle teal styling
│   │       │   └── js/app.js                 # Dynamic cascading dropdowns & previews
│   │       └── templates/                    # Thymeleaf templates
│   └── test/
│       └── java/com/bufinder/web/
│           ├── LostAndFoundApplicationTests.java
│           ├── service/
│           │   ├── UserServiceTest.java
│           │   ├── ItemServiceTest.java
│           │   └── ClaimServiceTest.java
│           └── controller/
│               └── WebSecurityIntegrationTest.java
└── app/                                      # Original Android application (preserved intact)
```

---

## 🔒 Security Best Practices

1. **Zero Hardcoded Secrets:** No database passwords or sensitive tokens are stored in the codebase or committed to Git.
2. **Environment Variables:** All sensitive credentials (`DB_PASSWORD`, `MYSQLPASSWORD`, `MYSQL_URL`) are injected via Railway environment variables at runtime.
3. **Data Retention:** `spring.jpa.hibernate.ddl-auto=update` guarantees that table schemas are updated safely without dropping or erasing tables, user records, item reports, or claim verifications.
4. **Persistent Volume Mounting:** Always mount a Railway Volume to `/app/uploads` and set `UPLOAD_DIR=/app/uploads` so uploaded photos survive redeployments.
