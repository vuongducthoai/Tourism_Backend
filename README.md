# Tourism Backend System — Spring Boot API

<p align="center">
  <img src="https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk" alt="Java" />
  <img src="https://img.shields.io/badge/Spring_Boot-3.3.0-6DB33F?style=for-the-badge&logo=springboot" alt="Spring Boot" />
  <img src="https://img.shields.io/badge/PostgreSQL-15-4169E1?style=for-the-badge&logo=postgresql" alt="PostgreSQL" />
  <img src="https://img.shields.io/badge/Spring_Security-JWT-6DB33F?style=for-the-badge&logo=springsecurity" alt="Spring Security" />
  <img src="https://img.shields.io/badge/Gemini_AI-2.0_Flash-4285F4?style=for-the-badge&logo=google" alt="Gemini AI" />
</p>

---

## Table of Contents

- [Overview](#overview)
- [Key Features](#key-features)
- [Tech Stack](#tech-stack)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
- [Project Structure](#project-structure)
- [Database Design](#database-design)
- [API Endpoints](#api-endpoints)
- [Security & Authentication](#security--authentication)
- [Payment Integration](#payment-integration)
- [AI & Chatbot](#ai--chatbot)
- [WebSocket & Real-time](#websocket--real-time)
- [Email Service](#email-service)
- [File Storage](#file-storage)
- [Environment Configuration](#environment-configuration)
- [Error Handling](#error-handling)

---

## Overview

**Tourism Backend System** is a RESTful API backend built with **Spring Boot 3.3.0** and **Java 17**, serving the **Future Travel** online tour booking platform. The system handles all business logic — from tour management and booking to multi-gateway payment processing and an intelligent AI chatbot.

### Highlights

- **Clean layered RESTful architecture**: Controller → Service → Repository
- **Stateless JWT Authentication** with a robust refresh token mechanism
- **Multi-gateway payments**: VNPay, PayOS, and bank transfer verification via SePay
- **AI-powered Chatbot** integrating Google Gemini 2.0 Flash and Pinecone Vector Database
- **Real-time notifications** over WebSocket STOMP
- **System-wide Soft Delete** via a shared `BaseEntity`
- **Automatic Audit Trail**: `createdAt`, `updatedAt`, `createdBy`, `updatedBy` on every entity

---

## Key Features

### Authentication & Users
- Email/password registration and login with BCrypt password hashing
- Email verification via Gmail SMTP
- Google OAuth 2.0 login
- Multi-device refresh token management with per-device or bulk revocation
- Three-tier role system: `ADMIN`, `CUSTOMER`, `TOUR_OWNER`
- Customer loyalty point system (`coinBalance`)

### Tour Management
- Full CRUD for tours, itinerary days, images, and videos
- Image and video upload to Cloudinary
- Advanced tour search with multiple filters
- Departure cloning for schedule reuse
- Per-passenger-type pricing (adult, child, infant)
- Per-departure transport management

### Booking & Payment
- Multi-passenger booking creation
- Coupon application (global or departure-specific)
- Loyalty point redemption for discounts
- Booking cancellation and refund requests
- Automated payment status checking and updates
- Booking status workflow: `PENDING_PAYMENT` → `PAID` → `PENDING_REVIEW` → `REVIEWED`

### Multi-gateway Payment
- **VNPay**: Sandbox payment gateway with HMAC-SHA512 signature validation
- **PayOS**: Java SDK integration with webhook and QR code support
- **SePay**: Bank transfer verification (BIDV), VietQR content generation

### AI & Chatbot
- Intelligent tour Q&A chatbot powered by Gemini 2.0 Flash
- Tour and location vector embeddings stored in Pinecone
- Admin-triggered data sync into the vector database
- AI-assisted business analytics for the admin dashboard

### Additional Features
- Tour reviews with photo attachments
- Favourite tour management
- Real-time notifications via WebSocket STOMP
- Coupon management with usage limits
- Branch contact and cancellation/refund policy template management
- Dashboard statistics for revenue, bookings, and users

---

## Tech Stack

### Core Framework

| Technology | Version | Purpose |
|------------|---------|---------|
| [Spring Boot](https://spring.io/projects/spring-boot) | 3.3.0 | Application framework |
| Java | 17 | Programming language |
| Maven | 4.0.0 | Build tool & dependency management |

### Data & Persistence

| Technology | Version | Purpose |
|------------|---------|---------|
| [Spring Data JPA](https://spring.io/projects/spring-data-jpa) | 3.3.0 | ORM & Repository pattern |
| [PostgreSQL](https://www.postgresql.org/) | Runtime | Primary relational database |
| [Flyway](https://flywaydb.org/) | Latest | Database migration (pre-configured) |
| [ModelMapper](http://modelmapper.org/) | 0.7.4 | DTO ↔ Entity mapping |

### Security

| Technology | Version | Purpose |
|------------|---------|---------|
| [Spring Security](https://spring.io/projects/spring-security) | 3.3.0 | Authentication & authorisation |
| [JJWT](https://github.com/jwtk/jjwt) | 0.11.5 | JWT generation & validation |
| [Spring OAuth2 Client](https://spring.io/projects/spring-security-oauth) | 3.3.0 | Google OAuth 2.0 |

### Communication & Real-time

| Technology | Version | Purpose |
|------------|---------|---------|
| [Spring WebSocket](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#websocket) | 3.3.0 | STOMP WebSocket server |
| [Spring WebFlux](https://spring.io/projects/spring-webflux) | 3.3.0 | Reactive HTTP client |
| [Spring Mail](https://docs.spring.io/spring-framework/docs/current/reference/html/integration.html#mail) | 3.3.0 | SMTP email sending |
| [Thymeleaf](https://www.thymeleaf.org/) | 3.3.0 | Email template engine |
| [OkHttp](https://square.github.io/okhttp/) | 4.12.0 | HTTP client for external APIs |

### Payment Gateways

| Technology | Version | Purpose |
|------------|---------|---------|
| [PayOS Java SDK](https://payos.vn/) | 2.0.1 | PayOS payment gateway integration |
| [Commons Codec](https://commons.apache.org/proper/commons-codec/) | 1.15 | HMAC-SHA512 for VNPay |

### AI & Cloud

| Technology | Version | Purpose |
|------------|---------|---------|
| [Pinecone Client](https://www.pinecone.io/) | 2.0.0 | Vector database for AI chatbot |
| [Google API Client](https://github.com/googleapis/google-api-java-client) | 2.2.0 | Google Gemini AI & OAuth |
| [Cloudinary](https://cloudinary.com/) | 1.36.0 | Image and video storage & optimisation |

### Utilities

| Technology | Version | Purpose |
|------------|---------|---------|
| [Lombok](https://projectlombok.org/) | Latest | Boilerplate code reduction |
| [Gson](https://github.com/google/gson) | 2.10.1 | JSON serialisation/deserialisation |
| [SpringDoc OpenAPI](https://springdoc.org/) | 2.8.9 | Auto-generated API documentation (Swagger UI) |
| [DataFaker](https://github.com/datafaker-net/datafaker) | 2.0.2 | Fake data generation for testing |
| [Apache Commons Text](https://commons.apache.org/proper/commons-text/) | 1.10.0 | Advanced string processing |
| [Logback](https://logback.qos.ch/) | 1.4.11 | Logging framework |

---

## Prerequisites

- **Java** 17+
- **Maven** 3.8+
- **PostgreSQL** 13+
- **Internet connection** (required for Cloudinary, Pinecone, Gemini AI, Gmail SMTP)

---

## Getting Started

### 1. Clone the repository

```bash
git clone <repository-url>
cd Tourism_Backend
```

### 2. Create the PostgreSQL database

```sql
CREATE DATABASE tourism;
```

### 3. Configure `application.yaml`

Edit `src/main/resources/application.yaml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/tourism
    username: your_postgres_user
    password: your_postgres_password

jwt:
  secret: your-secret-key

# Update API keys for Cloudinary, Pinecone, Gemini, PayOS, VNPay, ...
```

### 4. Build & Run

```bash
# Run directly with Maven
mvn spring-boot:run

# Or build a JAR and run it
mvn clean package -DskipTests
java -jar target/tourism-system-0.0.1-SNAPSHOT.jar
```

The server will start at: **http://localhost:8080**

### 5. Explore the API

Swagger UI: **http://localhost:8080/swagger-ui.html**

---

## Project Structure

```
Tourism_Backend/
├── pom.xml                              # Maven dependencies
├── src/
│   ├── main/
│   │   ├── resources/
│   │   │   └── application.yaml         # Application configuration
│   │   └── java/com/tourism/backend/
│   │       ├── TourismBackendApplication.java  # Entry point
│   │       │
│   │       ├── config/                  # Spring configuration
│   │       │   ├── SecurityConfig.java  # Spring Security & CORS
│   │       │   ├── WebSocketConfig.java # STOMP WebSocket
│   │       │   ├── PayOSConfig.java     # PayOS SDK config
│   │       │   ├── VNPayConfig.java     # VNPay config
│   │       │   └── CloudinaryConfig.java
│   │       │
│   │       ├── controller/              # REST Controllers
│   │       │   ├── AuthController.java
│   │       │   ├── TourController.java
│   │       │   ├── BookingController.java
│   │       │   ├── PaymentController.java
│   │       │   ├── UserController.java
│   │       │   ├── ReviewController.java
│   │       │   ├── LocationController.java
│   │       │   ├── NotificationController.java
│   │       │   ├── FavoriteTourController.java
│   │       │   ├── ChatbotController.java
│   │       │   └── admin/              # Admin-only controllers
│   │       │       ├── AdminAuthController.java
│   │       │       ├── AdminTourController.java
│   │       │       ├── AdminDepartureController.java
│   │       │       ├── AdminBookingController.java
│   │       │       ├── AdminUserController.java
│   │       │       ├── AdminCouponController.java
│   │       │       ├── AdminLocationController.java
│   │       │       ├── AdminDashboardController.java
│   │       │       ├── AdminBranchController.java
│   │       │       ├── AdminPolicyController.java
│   │       │       └── AdminProfileController.java
│   │       │
│   │       ├── service/                 # Business logic layer
│   │       │   ├── AuthService.java
│   │       │   ├── UserService.java
│   │       │   ├── TourService.java
│   │       │   ├── BookingService.java
│   │       │   ├── PaymentService.java
│   │       │   ├── ReviewService.java
│   │       │   ├── LocationService.java
│   │       │   ├── NotificationService.java
│   │       │   ├── FavoriteTourService.java
│   │       │   ├── CouponService.java
│   │       │   ├── DashboardService.java
│   │       │   ├── EmailService.java
│   │       │   ├── CloudinaryService.java
│   │       │   ├── SepayService.java
│   │       │   ├── GoogleAuthService.java
│   │       │   ├── GeminiAIService.java
│   │       │   ├── ChatbotService.java
│   │       │   ├── VectorService.java
│   │       │   ├── VectorSyncService.java
│   │       │   └── admin/
│   │       │       ├── TourManagementService.java
│   │       │       ├── TourDepartureService.java
│   │       │       ├── PolicyTemplateService.java
│   │       │       └── BranchContactService.java
│   │       │
│   │       ├── entity/                  # JPA entities
│   │       │   ├── BaseEntity.java      # Abstract audit superclass
│   │       │   ├── User.java
│   │       │   ├── Tour.java
│   │       │   ├── Location.java
│   │       │   ├── TourDeparture.java
│   │       │   ├── DeparturePricing.java
│   │       │   ├── DepartureTransport.java
│   │       │   ├── Booking.java
│   │       │   ├── BookingPassenger.java
│   │       │   ├── Payment.java
│   │       │   ├── Review.java
│   │       │   ├── RefundInformation.java
│   │       │   ├── Coupon.java
│   │       │   ├── Notification.java
│   │       │   ├── FavoriteTour.java
│   │       │   ├── PolicyTemplate.java
│   │       │   ├── BranchContact.java
│   │       │   ├── TourImage.java
│   │       │   ├── TourMedia.java
│   │       │   ├── ItineraryDay.java
│   │       │   ├── ImageReview.java
│   │       │   └── RefreshToken.java
│   │       │
│   │       ├── repository/              # Spring Data JPA repositories
│   │       │   ├── custom/              # Complex custom queries
│   │       │   └── [Entity]Repository.java (20+ repositories)
│   │       │
│   │       ├── dto/                     # Data Transfer Objects
│   │       │   ├── request/             # Incoming request DTOs
│   │       │   └── response/            # Outgoing response DTOs
│   │       │
│   │       ├── security/               # JWT & security components
│   │       │   ├── JwtTokenProvider.java
│   │       │   ├── JwtAuthenticationFilter.java
│   │       │   └── JwtAuthenticationEntryPoint.java
│   │       │
│   │       ├── exception/              # Custom exceptions & global handler
│   │       │   ├── GlobalExceptionHandler.java
│   │       │   ├── BadRequestException.java
│   │       │   ├── NotFoundException.java
│   │       │   └── ...
│   │       │
│   │       ├── enums/                  # Enumerations
│   │       │   ├── BookingStatus.java
│   │       │   ├── PaymentMethod.java
│   │       │   ├── Role.java
│   │       │   └── ...
│   │       │
│   │       └── utils/                  # Utility classes
│   │           ├── VNPayUtil.java
│   │           └── BankUntil.java
│   │
│   └── test/                           # Unit & integration tests
│
└── target/                             # Build output
```

---

## Database Design

### Entity Relationship Overview (ERD)

```
User ──────────────────────────┐
 │                             │
 ├──< Booking >──┬─────< Payment
 │               ├─────< BookingPassenger
 │               ├─────< RefundInformation
 │               └──< TourDeparture >──┬─< Tour >──┬─< TourImage
 │                                     ├─< DeparturePricing   ├─< TourMedia
 │                                     ├─< DepartureTransport ├─< ItineraryDay
 │                                     └─ PolicyTemplate      ├─< FavoriteTour
 │                                                            ├─ Location (start)
 ├──< Review >──< ImageReview                                 └─ Location (end)
 ├──< FavoriteTour
 ├──< Notification
 └──< RefreshToken

Coupon ──────── TourDeparture
PolicyTemplate ─ TourDeparture
BranchContact ── PolicyTemplate
```

### Core Tables

#### `users`
| Column | Type | Description |
|--------|------|-------------|
| `user_id` | UUID (PK) | Primary key |
| `full_name` | VARCHAR | Full name |
| `email` | VARCHAR (UNIQUE) | Login email |
| `password` | VARCHAR | BCrypt-hashed password |
| `role` | ENUM | ADMIN / CUSTOMER / TOUR_OWNER |
| `coin_balance` | DECIMAL | Loyalty points balance |
| `is_email_verified` | BOOLEAN | Email verification status |
| `avatar` | VARCHAR | Avatar URL (Cloudinary) |
| `status` | VARCHAR | Account status |
| `last_active_at` | TIMESTAMP | Last activity timestamp |

#### `tours`
| Column | Type | Description |
|--------|------|-------------|
| `tour_id` | UUID (PK) | Primary key |
| `tour_code` | VARCHAR (UNIQUE) | Unique tour code |
| `tour_name` | VARCHAR | Tour name |
| `duration` | VARCHAR | Duration (e.g. 3D2N) |
| `transportation` | VARCHAR | Primary transport type |
| `start_location_id` | FK → locations | Departure location |
| `end_location_id` | FK → locations | Destination |
| `status` | VARCHAR | Tour status |

#### `bookings`
| Column | Type | Description |
|--------|------|-------------|
| `booking_id` | UUID (PK) | Primary key |
| `booking_code` | VARCHAR (UNIQUE) | Booking code (BK + UUID) |
| `booking_status` | ENUM | Current booking status |
| `total_price` | DECIMAL | Total amount payable |
| `paid_by_coin` | DECIMAL | Amount paid with loyalty points |
| `coupon_discount` | DECIMAL | Discount from coupon |
| `user_id` | FK → users | Customer who booked |
| `departure_id` | FK → departures | Booked departure |

#### `payments`
| Column | Type | Description |
|--------|------|-------------|
| `payment_id` | UUID (PK) | Primary key |
| `payment_method` | ENUM | VNPAY / PAYOS |
| `amount` | DECIMAL | Payment amount |
| `status` | ENUM | Payment status |
| `transaction_id` | VARCHAR | Gateway transaction reference |
| `time_limit` | TIMESTAMP | Payment deadline |
| `booking_id` | FK (UNIQUE) → bookings | Associated booking |

### Booking Status Workflow

```
PENDING_PAYMENT ──(payment success)──► PAID
                                         │
    OVERDUE_PAYMENT ◄─(deadline passed)──┤
                                         ▼
CANCELLED ◄──(admin/customer cancel)── PENDING_CONFIRMATION
                                         │
                                  (admin confirms)
                                         ▼
                                       PAID
                                         │
                                  (tour completed)
                                         ▼
                                  PENDING_REVIEW
                                         │
                                  (customer reviews)
                                         ▼
                                      REVIEWED

PENDING_REFUND ◄──(cancelled after PAID)
```

---

## API Endpoints

**Base URL:** `http://localhost:8080/api`

### Authentication `/api/auth`

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/auth/register` | Public | Register a new account |
| POST | `/auth/login` | Public | Login with email/password |
| POST | `/auth/google/login` | Public | Login via Google OAuth |
| POST | `/auth/refresh-token` | Public | Refresh the access token |
| POST | `/auth/logout` | JWT | Logout (revoke refresh token) |
| POST | `/auth/logout-all` | JWT | Logout from all devices |
| GET | `/auth/profile` | JWT | Get current user profile |
| GET | `/auth/verify-email` | Public | Verify email with token |
| POST | `/auth/resend-verification` | Public | Resend verification email |

### Tours `/api/tours`

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/tours` | Public | List all tours (paginated) |
| GET | `/tours/display` | Public | All active tours for display |
| GET | `/tours/deepest-discount` | Public | Top 10 deepest-discounted tours |
| GET | `/tours/search` | Public | Search tours with filters |
| GET | `/tours/{tourCode}` | Public | Tour details by code |
| GET | `/tours/{id}/departures` | Public | Available departures for a tour |
| GET | `/tours/related/{tourCode}` | Public | Related tours |

### Bookings `/api/bookings`

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/bookings/order` | JWT | Get booking initialisation info |
| POST | `/bookings/create` | JWT | Create a new booking |
| GET | `/bookings/payment/{bookingCode}` | JWT | Get booking details |
| GET | `/bookings/user/{userID}` | JWT | Get all bookings for a user |
| POST | `/bookings/cancel` | JWT | Cancel a booking |
| POST | `/bookings/refund-request/{bookingID}` | JWT | Submit a refund request |

### Payments `/api/payment`

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/payment/vnpay/create` | JWT | Create a VNPay payment link |
| GET | `/payment/vnpay-callback` | Public | VNPay payment callback |
| POST | `/payment/payos/create` | JWT | Create a PayOS payment |
| POST | `/payment/payos/webhook` | Public | PayOS webhook handler |
| GET | `/payment/payos/return` | Public | PayOS return URL |
| GET | `/payment/payos/status/{orderCode}` | JWT | Check PayOS payment status |
| GET | `/payment/check-status/{orderCode}` | JWT | General payment status check |

### Reviews `/api/reviews`

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/reviews` | JWT | Submit a review (multipart, with photos) |
| GET | `/reviews/{bookingID}` | JWT | Get review by booking |
| GET | `/reviews/tour/{tourCode}` | Public | Get tour reviews (paginated) |
| GET | `/reviews/tour/{tourCode}/statistics` | Public | Get review statistics for a tour |

### Notifications `/api/notifications`

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/notifications` | JWT | Get notifications (paginated) |
| GET | `/notifications/unread-count` | JWT | Get unread notification count |
| PUT | `/notifications/{id}/read` | JWT | Mark a notification as read |
| PUT | `/notifications/read-all` | JWT | Mark all notifications as read |

### Chatbot `/api/chatbot`

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/chatbot/chat` | Public | Send a message to the AI chatbot |
| POST | `/chatbot/sync` | ADMIN | Trigger data sync to Pinecone |
| DELETE | `/chatbot/admin/clear-data` | ADMIN | Clear all Pinecone data |

### Admin Endpoints `/api/admin/**`

<details>
<summary>View full Admin API (click to expand)</summary>

#### Admin Auth `/api/admin/auth`
```
POST   /admin/auth/login            - Admin login
POST   /admin/auth/refresh-token    - Refresh admin token
POST   /admin/auth/logout           - Admin logout
GET    /admin/auth/profile          - Get admin profile
```

#### Admin Tours `/api/admin/tours`
```
GET    /admin/tours                        - List all tours (paginated)
GET    /admin/tours/{tourId}               - Get tour by ID
POST   /admin/tours                        - Create new tour
PUT    /admin/tours/{tourId}               - Update tour
DELETE /admin/tours/{tourId}               - Delete tour
PUT    /admin/tours/{tourId}/general-info  - Update general info
PUT    /admin/tours/{tourId}/itinerary     - Update itinerary
POST   /admin/tours/{tourId}/upload-image  - Upload tour image
POST   /admin/tours/{tourId}/upload-video  - Upload tour video
GET    /admin/tours/check-code             - Check if tour code exists
```

#### Admin Departures `/api/admin/departures`
```
GET    /admin/departures                      - List all departures
GET    /admin/departures/{id}                 - Get departure details
POST   /admin/departures                      - Create departure
PUT    /admin/departures/{id}                 - Update departure
DELETE /admin/departures/{id}                 - Delete departure
PUT    /admin/departures/{id}/pricing         - Update pricing
PUT    /admin/departures/{id}/transport       - Update transport
POST   /admin/departures/{id}/clone           - Clone a departure
GET    /admin/departures/by-tour/{tourId}     - Get departures by tour
```

#### Admin Users `/api/users`
```
GET    /users/{userID}               - Get user profile
PUT    /users/{userID}               - Update user profile
POST   /users/admin/search           - Search users
POST   /users/admin/update-status    - Update user status
```

#### Admin Coupons `/api/admin/coupons`
```
GET    /admin/coupons                - List all coupons
GET    /admin/coupons/{id}           - Get coupon by ID
POST   /admin/coupons                - Create coupon
PUT    /admin/coupons/{id}           - Update coupon
DELETE /admin/coupons/{id}           - Delete coupon
GET    /admin/coupons/global         - Get global coupons
GET    /admin/coupons/departure      - Get departure-specific coupons
```

#### Admin Locations `/api/admin/locations`
```
GET    /admin/locations              - List all locations
GET    /admin/locations/{id}         - Get location by ID
POST   /admin/locations              - Create location
PUT    /admin/locations/{id}         - Update location
DELETE /admin/locations/{id}         - Delete location
POST   /admin/locations/{id}/image   - Upload location image
```

#### Admin Dashboard `/api/admin/dashboard`
```
GET    /admin/dashboard/statistics   - Get dashboard statistics
GET    /admin/dashboard/analysis     - Get AI-powered analysis
```

#### Admin Branches & Policies
```
GET/POST/PUT/DELETE  /admin/branches           - Branch management
GET/POST/PUT/DELETE  /admin/policy-templates   - Policy template management
```

</details>

---

## Security & Authentication

### Spring Security Configuration

```
CSRF:             Disabled (JWT stateless)
CORS:             Enabled — localhost:3000
Session:          Stateless
```

### JWT Authentication Flow

```
Client                             Server
  │                                  │
  ├──── POST /auth/login ───────────►│
  │                                  ├── Validate credentials
  │                                  ├── Generate accessToken  (15 min)
  │◄─── { accessToken,              ├── Generate refreshToken (7 days)
  │       refreshToken } ────────────┤── Persist refreshToken in DB
  │                                  │
  ├──── Request + Bearer token ─────►│
  │                                  ├── JwtAuthenticationFilter
  │                                  ├── Validate token signature & expiry
  │◄─── Response ────────────────────┤
  │                                  │
  ├──── (401 received) ──────────────│
  ├──── POST /auth/refresh-token ───►│
  │                                  ├── Validate refreshToken in DB
  │◄─── { newAccessToken } ──────────┤
  │                                  │
```

### Role-based Access Control

| Role | Access |
|------|--------|
| `ADMIN` | Full access: `/api/admin/**` and all public endpoints |
| `CUSTOMER` | Booking, payment, reviews, personal profile |
| `TOUR_OWNER` | Own tour management (ready to extend) |
| Anonymous | Browse tours, locations, chatbot |

---

## Payment Integration

### VNPay

- **Environment:** Sandbox (`https://sandbox.vnpayment.vn/paymentv2/vpcpay.html`)
- **Signature:** HMAC-SHA512
- **Payment deadline:** 15 minutes
- **Callback:** `GET /api/payment/vnpay-callback`

```
Create payment → Redirect to VNPay → Customer pays
                                          │
                   ◄─── Callback ─────────┘
                   Verify HMAC-SHA512
                   Update booking status
```

### PayOS

- **SDK:** payos-java 2.0.1
- **Webhook:** `POST /api/payment/payos/webhook`
- **Supports:** QR code, bank transfer, card payment

### SePay (Bank Transfer Verification)

- **Bank:** BIDV
- **Features:** Verify real transactions, generate VietQR transfer content
- **API:** `https://my.sepay.vn/userapi`

---

## AI & Chatbot

### Architecture

```
User
 │
 ▼
POST /api/chatbot/chat
 │
 ▼
ChatbotService
 │
 ├── VectorService (Pinecone)
 │   ├── Encode question → vector embedding (text-embedding-004)
 │   └── Similarity search in "tourism-chatbot" index
 │
 └── GeminiAIService
     ├── Build prompt with tour context from vector DB
     └── Call Gemini 2.0 Flash → Generate response
         Temperature: 0.7 | Max tokens: 1000
```

### Data Synchronisation

```bash
# Admin triggers tour/location sync into Pinecone
POST /api/chatbot/sync

# Clear all Pinecone data (dev/test)
DELETE /api/chatbot/admin/clear-data
```

### Gemini Configuration

| Parameter | Value |
|-----------|-------|
| Model | `gemini-2.0-flash` |
| Embedding Model | `text-embedding-004` |
| Embedding Dimension | 768 |
| Temperature | 0.7 |
| Max Output Tokens | 1000 |
| Vector DB Index | `tourism-chatbot` (Pinecone) |

---

## WebSocket & Real-time

### STOMP Configuration

| Parameter | Value |
|-----------|-------|
| WebSocket Endpoint | `/ws` |
| Broker Prefix | `/topic`, `/topic/user` |
| App Destination Prefix | `/app` |
| Fallback | SockJS |

### Subscribe Topics

```javascript
// User notifications
/topic/user/{userId}

// Booking status updates
/topic/booking/{bookingCode}
```

### Real-time Events

| Event | Topic | Description |
|-------|-------|-------------|
| Payment successful | `/topic/booking/{code}` | Push booking status update |
| New notification | `/topic/user/{userId}` | Deliver system notification |
| Booking confirmed | `/topic/booking/{code}` | Admin confirms a booking |

---

## Email Service

**Provider:** Gmail SMTP (`smtp.gmail.com:587`)

### Email Types

| Email | Trigger | Description |
|-------|---------|-------------|
| **Email Verification** | Account registration | Contains a time-limited verification link |
| **Welcome** | Email verified | Welcome message to the platform |
| **Booking Confirmation** | Booking status → PAID | Full booking details |
| **Payment Reminder** | Booking → PENDING_PAYMENT | Remind customer to complete payment |

Templates are rendered with **Thymeleaf**.

---

## File Storage

### Cloudinary

- **Upload limit:** 10 MB per file
- **Supported types:** Images (jpg, png, webp) and Videos (mp4)
- **Automatic processing:** Compression, resizing optimisation, CDN delivery

**Resource types:**

| Type | Description |
|------|-------------|
| Tour Images | Tour gallery photos |
| Tour Videos | Tour introduction videos |
| Location Images | Destination photos |
| User Avatars | Profile pictures |
| Review Images | Photos attached to reviews |

---

## Environment Configuration

File: `src/main/resources/application.yaml`

```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/tourism
    username: postgres
    password: postgres
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
  mail:
    host: smtp.gmail.com
    port: 587
    username: your-email@gmail.com
    password: your-app-password

jwt:
  secret: your-jwt-secret-key-min-32-chars
  access-token-expiration: 900000      # 15 minutes (ms)
  refresh-token-expiration: 604800000  # 7 days (ms)

cloudinary:
  cloud-name: your-cloud-name
  api-key: your-api-key
  api-secret: your-api-secret

vnpay:
  tmn-code: your-tmn-code
  hash-secret: your-hash-secret
  api-url: https://sandbox.vnpayment.vn/paymentv2/vpcpay.html

payos:
  client-id: your-client-id
  api-key: your-api-key
  checksum-key: your-checksum-key
  return-url: http://localhost:3000/payment-success
  cancel-url: http://localhost:3000/payment-cancel

sepay:
  api-url: https://my.sepay.vn/userapi
  api-token: your-sepay-token

ai:
  pinecone:
    api-key: your-pinecone-api-key
  gemini:
    api-key: your-gemini-api-key
    generation-model: gemini-2.0-flash
    embedding-model: text-embedding-004

spring:
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: your-google-client-id
            client-secret: your-google-client-secret
```

> **Security:** Never commit `application.yaml` containing real credentials to version control. Use environment variable substitution (`${ENV_VAR}`) or Spring Cloud Config for production deployments.

---

## Error Handling

### Custom Exceptions

| Exception | HTTP Status | Description |
|-----------|-------------|-------------|
| `BadRequestException` | 400 | Invalid input data |
| `UnauthorizedException` | 401 | Not authenticated or token expired |
| `ForbiddenException` | 403 | Insufficient permissions |
| `NotFoundException` | 404 | Resource not found |
| `ConflictException` | 409 | Data conflict |
| `DuplicateResourceException` | 409 | Resource already exists |
| `ResourceInUseException` | 409 | Resource is currently in use |
| `FileSizeExceededException` | 413 | File exceeds the 10 MB limit |
| `InvalidFileException` | 415 | Unsupported file format |
| `VideoUploadException` | 500 | Video upload failure |

### Standard Response Format

```json
{
  "success": true,
  "message": "Operation completed successfully",
  "data": { ... }
}
```

```json
{
  "success": false,
  "message": "Tour with code TOUR001 not found",
  "error": "NotFoundException",
  "timestamp": "2026-04-25T10:30:00Z"
}
```

---

## Enums

| Enum | Values |
|------|--------|
| `BookingStatus` | PENDING_PAYMENT, OVERDUE_PAYMENT, PENDING_CONFIRMATION, PAID, CANCELLED, PENDING_REVIEW, REVIEWED, PENDING_REFUND |
| `PaymentMethod` | VNPAY, PAYOS |
| `Role` | ADMIN, CUSTOMER, TOUR_OWNER |
| `Region` | Vietnamese tourism regions |
| `PassengerType` | ADULT, CHILD, INFANT |
| `VehicleType` | BUS, CAR, AIRPLANE, TRAIN |
| `CouponType` | GLOBAL, DEPARTURE_SPECIFIC |

---

## Contact

For any inquiries, support, or feedback regarding the Future Travel system, please reach out to the development team:

* **Tran Anh Thu** 
    * Email: [trananhthu270904@gmail.com](mailto:trananhthu270904@gmail.com)
    * GitHub: [https://github.com/ThuHoiBao](https://github.com/ThuHoiBao)
* **Vuong Duc Thoai** 
    * Email: [thoai12309@gmail.com](mailto:thoai12309@gmail.com)
    * GitHub: [https://github.com/vuongducthoai](https://github.com/vuongducthoai)

---

<p align="center"> Future Travel </p>

