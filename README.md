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

<p align="center">Made with ❤️ — Future Travel Team</p>

- [Giới thiệu](#giới-thiệu)
- [Tính năng chính](#tính-năng-chính)
- [Công nghệ sử dụng](#công-nghệ-sử-dụng)
- [Yêu cầu hệ thống](#yêu-cầu-hệ-thống)
- [Cài đặt & Chạy dự án](#cài-đặt--chạy-dự-án)
- [Cấu trúc dự án](#cấu-trúc-dự-án)
- [Thiết kế cơ sở dữ liệu](#thiết-kế-cơ-sở-dữ-liệu)
- [API Endpoints](#api-endpoints)
- [Bảo mật & Xác thực](#bảo-mật--xác-thực)
- [Tích hợp thanh toán](#tích-hợp-thanh-toán)
- [AI & Chatbot](#ai--chatbot)
- [WebSocket & Real-time](#websocket--real-time)
- [Email Service](#email-service)
- [Lưu trữ tệp](#lưu-trữ-tệp)
- [Cấu hình môi trường](#cấu-hình-môi-trường)
- [Xử lý lỗi](#xử-lý-lỗi)

---

## Giới thiệu

**Tourism Backend System** là hệ thống backend RESTful API được xây dựng bằng **Spring Boot 3.3.0** và **Java 17**, phục vụ nền tảng đặt tour du lịch trực tuyến **Future Travel**. Hệ thống xử lý toàn bộ nghiệp vụ từ quản lý tour, đặt chỗ, thanh toán đa cổng đến tích hợp chatbot AI thông minh.

### Điểm nổi bật

- **Kiến trúc RESTful** chuẩn, phân tầng rõ ràng (Controller → Service → Repository)
- **JWT Stateless Authentication** với cơ chế refresh token bền vững
- **Đa cổng thanh toán**: VNPay, PayOS, chuyển khoản ngân hàng qua SePay
- **AI Chatbot thông minh** tích hợp Gemini 2.0 Flash + Pinecone Vector Database
- **Real-time notifications** qua WebSocket STOMP
- **Soft Delete** toàn hệ thống với `BaseEntity`
- **Audit Trail** tự động: createdAt, updatedAt, createdBy, updatedBy

---

## Tính năng chính

### Xác thực & Người dùng
- Đăng ký/đăng nhập email và mật khẩu với mã hóa BCrypt
- Xác thực email qua Gmail SMTP
- Đăng nhập Google OAuth 2.0
- Quản lý refresh token: đa thiết bị, thu hồi từng thiết bị hoặc toàn bộ
- Phân quyền ba cấp: `ADMIN`, `CUSTOMER`, `TOUR_OWNER`
- Hệ thống điểm thưởng (`coinBalance`) cho khách hàng

### Quản lý Tour
- CRUD đầy đủ cho tour, lịch trình (itinerary), hình ảnh, video
- Upload hình ảnh/video lên Cloudinary
- Tìm kiếm tour nâng cao với nhiều bộ lọc
- Clone lịch khởi hành (departure) để tái sử dụng
- Quản lý giá theo loại hành khách (người lớn, trẻ em, em bé)
- Quản lý phương tiện vận chuyển theo từng chuyến

### Đặt tour & Thanh toán
- Tạo booking với nhiều hành khách
- Áp dụng mã giảm giá (global hoặc theo chuyến cụ thể)
- Đổi điểm thưởng để giảm giá
- Hủy booking và yêu cầu hoàn tiền
- Kiểm tra và cập nhật trạng thái thanh toán tự động
- Workflow trạng thái booking: `PENDING_PAYMENT` → `PAID` → `PENDING_REVIEW` → `REVIEWED`

### Thanh toán đa cổng
- **VNPay**: Thanh toán qua cổng sandbox VNPay, xác thực HMAC-SHA512
- **PayOS**: Tích hợp SDK PayOS Java, webhook, QR code
- **SePay**: Xác minh chuyển khoản ngân hàng BIDV, tạo nội dung VietQR

### AI & Chatbot
- Chatbot hỏi đáp tour thông minh với Gemini 2.0 Flash
- Lưu trữ vector embedding tour/location trên Pinecone
- Đồng bộ dữ liệu tour vào vector database
- Phân tích kinh doanh AI cho dashboard admin

### Tính năng khác
- Đánh giá & Review tour (kèm ảnh)
- Tour yêu thích
- Thông báo real-time (WebSocket STOMP)
- Quản lý mã giảm giá với giới hạn sử dụng
- Quản lý chi nhánh và mẫu chính sách hủy/hoàn tiền
- Dashboard thống kê doanh thu, đặt tour, người dùng

---

## Công nghệ sử dụng

### Core Framework

| Công nghệ | Phiên bản | Mục đích |
|-----------|-----------|----------|
| [Spring Boot](https://spring.io/projects/spring-boot) | 3.3.0 | Application framework |
| Java | 17 | Ngôn ngữ lập trình |
| Maven | 4.0.0 | Build tool & dependency management |

### Data & Persistence

| Công nghệ | Phiên bản | Mục đích |
|-----------|-----------|----------|
| [Spring Data JPA](https://spring.io/projects/spring-data-jpa) | 3.3.0 | ORM & Repository pattern |
| [PostgreSQL](https://www.postgresql.org/) | Runtime | Cơ sở dữ liệu chính |
| [Flyway](https://flywaydb.org/) | Latest | Database migration (cấu hình sẵn) |
| [ModelMapper](http://modelmapper.org/) | 0.7.4 | DTO ↔ Entity mapping |

### Security

| Công nghệ | Phiên bản | Mục đích |
|-----------|-----------|----------|
| [Spring Security](https://spring.io/projects/spring-security) | 3.3.0 | Authentication & Authorization |
| [JJWT](https://github.com/jwtk/jjwt) | 0.11.5 | JSON Web Token generation & validation |
| [Spring OAuth2 Client](https://spring.io/projects/spring-security-oauth) | 3.3.0 | Google OAuth 2.0 |

### Communication & Real-time

| Công nghệ | Phiên bản | Mục đích |
|-----------|-----------|----------|
| [Spring WebSocket](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#websocket) | 3.3.0 | WebSocket server (STOMP) |
| [Spring WebFlux](https://spring.io/projects/spring-webflux) | 3.3.0 | Reactive HTTP client |
| [Spring Mail](https://docs.spring.io/spring-framework/docs/current/reference/html/integration.html#mail) | 3.3.0 | Email gửi SMTP |
| [Thymeleaf](https://www.thymeleaf.org/) | 3.3.0 | Email template engine |
| [OkHttp](https://square.github.io/okhttp/) | 4.12.0 | HTTP client cho external APIs |

### Payment Gateways

| Công nghệ | Phiên bản | Mục đích |
|-----------|-----------|----------|
| [PayOS Java SDK](https://payos.vn/) | 2.0.1 | Tích hợp cổng thanh toán PayOS |
| [Commons Codec](https://commons.apache.org/proper/commons-codec/) | 1.15 | HMAC-SHA512 cho VNPay |

### AI & Cloud

| Công nghệ | Phiên bản | Mục đích |
|-----------|-----------|----------|
| [Pinecone Client](https://www.pinecone.io/) | 2.0.0 | Vector database cho AI chatbot |
| [Google API Client](https://github.com/googleapis/google-api-java-client) | 2.2.0 | Google Gemini AI & OAuth |
| [Cloudinary](https://cloudinary.com/) | 1.36.0 | Lưu trữ và tối ưu hình ảnh/video |

### Utilities

| Công nghệ | Phiên bản | Mục đích |
|-----------|-----------|----------|
| [Lombok](https://projectlombok.org/) | Latest | Giảm boilerplate code |
| [Gson](https://github.com/google/gson) | 2.10.1 | JSON serialization/deserialization |
| [SpringDoc OpenAPI](https://springdoc.org/) | 2.8.9 | Tự động tạo tài liệu API (Swagger UI) |
| [DataFaker](https://github.com/datafaker-net/datafaker) | 2.0.2 | Tạo dữ liệu giả để kiểm thử |
| [Apache Commons Text](https://commons.apache.org/proper/commons-text/) | 1.10.0 | Xử lý chuỗi nâng cao |
| [Logback](https://logback.qos.ch/) | 1.4.11 | Logging framework |

---

## Yêu cầu hệ thống

- **Java** 17+
- **Maven** 3.8+
- **PostgreSQL** 13+
- **Internet connection** (cho Cloudinary, Pinecone, Gemini AI, Gmail SMTP)

---

## Cài đặt & Chạy dự án

### 1. Clone repository

```bash
git clone <repository-url>
cd Tourism_Backend
```

### 2. Tạo cơ sở dữ liệu PostgreSQL

```sql
CREATE DATABASE tourism;
```

### 3. Cấu hình `application.yaml`

Chỉnh sửa file `src/main/resources/application.yaml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/tourism
    username: your_postgres_user
    password: your_postgres_password

jwt:
  secret: your-secret-key

# Cập nhật các API key cho Cloudinary, Pinecone, Gemini, PayOS, VNPay, ...
```

### 4. Build & Chạy

```bash
# Chạy trực tiếp với Maven
mvn spring-boot:run

# Hoặc build JAR rồi chạy
mvn clean package -DskipTests
java -jar target/tourism-system-0.0.1-SNAPSHOT.jar
```

Server sẽ khởi động tại: **http://localhost:8080**

### 5. Kiểm tra API

Swagger UI: **http://localhost:8080/swagger-ui.html**

---

## Cấu trúc dự án

```
Tourism_Backend/
├── pom.xml                              # Maven dependencies
├── src/
│   ├── main/
│   │   ├── resources/
│   │   │   └── application.yaml         # Cấu hình ứng dụng
│   │   └── java/com/tourism/backend/
│   │       ├── TourismBackendApplication.java  # Entry point
│   │       │
│   │       ├── config/                  # Cấu hình Spring
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
│   │       ├── service/                 # Business Logic Layer
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
│   │       ├── entity/                  # JPA Entities
│   │       │   ├── BaseEntity.java      # Abstract audit class
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
│   │       ├── repository/              # Spring Data JPA Repositories
│   │       │   ├── custom/              # Complex custom queries
│   │       │   └── [Entity]Repository.java (20+ repositories)
│   │       │
│   │       ├── dto/                     # Data Transfer Objects
│   │       │   ├── request/             # Incoming request DTOs
│   │       │   └── response/            # Outgoing response DTOs
│   │       │
│   │       ├── security/               # JWT & Security components
│   │       │   ├── JwtTokenProvider.java
│   │       │   ├── JwtAuthenticationFilter.java
│   │       │   └── JwtAuthenticationEntryPoint.java
│   │       │
│   │       ├── exception/              # Custom exceptions & handler
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
│   └── test/                           # Unit & Integration tests
│
└── target/                             # Build output
```

---

## Thiết kế cơ sở dữ liệu

### Sơ đồ quan hệ (ERD Overview)

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

### Bảng dữ liệu chính

#### `users`
| Cột | Kiểu | Mô tả |
|-----|------|-------|
| `user_id` | UUID (PK) | Khóa chính |
| `full_name` | VARCHAR | Họ tên |
| `email` | VARCHAR (UNIQUE) | Email đăng nhập |
| `password` | VARCHAR | Mật khẩu mã hóa BCrypt |
| `role` | ENUM | ADMIN / CUSTOMER / TOUR_OWNER |
| `coin_balance` | DECIMAL | Điểm thưởng |
| `is_email_verified` | BOOLEAN | Trạng thái xác thực email |
| `avatar` | VARCHAR | URL ảnh đại diện (Cloudinary) |
| `status` | VARCHAR | Trạng thái tài khoản |
| `last_active_at` | TIMESTAMP | Lần hoạt động cuối |

#### `tours`
| Cột | Kiểu | Mô tả |
|-----|------|-------|
| `tour_id` | UUID (PK) | Khóa chính |
| `tour_code` | VARCHAR (UNIQUE) | Mã tour duy nhất |
| `tour_name` | VARCHAR | Tên tour |
| `duration` | VARCHAR | Thời lượng (vd: 3N2Đ) |
| `transportation` | VARCHAR | Phương tiện chính |
| `start_location_id` | FK → locations | Điểm xuất phát |
| `end_location_id` | FK → locations | Điểm đến |
| `status` | VARCHAR | Trạng thái tour |

#### `bookings`
| Cột | Kiểu | Mô tả |
|-----|------|-------|
| `booking_id` | UUID (PK) | Khóa chính |
| `booking_code` | VARCHAR (UNIQUE) | Mã đặt tour (BK + UUID) |
| `booking_status` | ENUM | Trạng thái đặt tour |
| `total_price` | DECIMAL | Tổng tiền |
| `paid_by_coin` | DECIMAL | Số tiền thanh toán bằng điểm |
| `coupon_discount` | DECIMAL | Giảm giá từ coupon |
| `user_id` | FK → users | Khách hàng đặt |
| `departure_id` | FK → departures | Chuyến đã đặt |

#### `payments`
| Cột | Kiểu | Mô tả |
|-----|------|-------|
| `payment_id` | UUID (PK) | Khóa chính |
| `payment_method` | ENUM | VNPAY / PAYOS |
| `amount` | DECIMAL | Số tiền |
| `status` | ENUM | Trạng thái thanh toán |
| `transaction_id` | VARCHAR | Mã giao dịch từ cổng thanh toán |
| `time_limit` | TIMESTAMP | Hạn thanh toán |
| `booking_id` | FK (UNIQUE) → bookings | Booking tương ứng |

### Workflow trạng thái Booking

```
PENDING_PAYMENT ──(thanh toán thành công)──► PAID
                                              │
        OVERDUE_PAYMENT ◄─(hết hạn)──────────┤
                                              ▼
CANCELLED ◄──(admin hủy / khách hủy)── PENDING_CONFIRMATION
                                              │
                                     (admin xác nhận)
                                              ▼
                                           PAID
                                              │
                                      (tour kết thúc)
                                              ▼
                                     PENDING_REVIEW
                                              │
                                       (khách review)
                                              ▼
                                          REVIEWED

PENDING_REFUND ◄──(hủy sau khi PAID)
```

---

## API Endpoints

**Base URL:** `http://localhost:8080/api`

### Authentication `/api/auth`

| Method | Endpoint | Auth | Mô tả |
|--------|----------|------|-------|
| POST | `/auth/register` | Public | Đăng ký tài khoản mới |
| POST | `/auth/login` | Public | Đăng nhập email/mật khẩu |
| POST | `/auth/google/login` | Public | Đăng nhập Google OAuth |
| POST | `/auth/refresh-token` | Public | Làm mới access token |
| POST | `/auth/logout` | JWT | Đăng xuất (thu hồi refresh token) |
| POST | `/auth/logout-all` | JWT | Đăng xuất tất cả thiết bị |
| GET | `/auth/profile` | JWT | Lấy thông tin người dùng hiện tại |
| GET | `/auth/verify-email` | Public | Xác thực email với token |
| POST | `/auth/resend-verification` | Public | Gửi lại email xác thực |

### Tours `/api/tours`

| Method | Endpoint | Auth | Mô tả |
|--------|----------|------|-------|
| GET | `/tours` | Public | Danh sách tour (phân trang) |
| GET | `/tours/display` | Public | Tất cả tour hiển thị |
| GET | `/tours/deepest-discount` | Public | Top 10 tour giảm giá sâu nhất |
| GET | `/tours/search` | Public | Tìm kiếm tour với bộ lọc |
| GET | `/tours/{tourCode}` | Public | Chi tiết tour theo mã |
| GET | `/tours/{id}/departures` | Public | Danh sách lịch khởi hành |
| GET | `/tours/related/{tourCode}` | Public | Tour liên quan |

### Bookings `/api/bookings`

| Method | Endpoint | Auth | Mô tả |
|--------|----------|------|-------|
| GET | `/bookings/order` | JWT | Thông tin khởi tạo đặt tour |
| POST | `/bookings/create` | JWT | Tạo booking mới |
| GET | `/bookings/payment/{bookingCode}` | JWT | Chi tiết booking |
| GET | `/bookings/user/{userID}` | JWT | Danh sách booking của người dùng |
| POST | `/bookings/cancel` | JWT | Hủy booking |
| POST | `/bookings/refund-request/{bookingID}` | JWT | Yêu cầu hoàn tiền |

### Payments `/api/payment`

| Method | Endpoint | Auth | Mô tả |
|--------|----------|------|-------|
| POST | `/payment/vnpay/create` | JWT | Tạo link thanh toán VNPay |
| GET | `/payment/vnpay-callback` | Public | Callback từ VNPay |
| POST | `/payment/payos/create` | JWT | Tạo thanh toán PayOS |
| POST | `/payment/payos/webhook` | Public | Webhook PayOS |
| GET | `/payment/payos/return` | Public | URL return PayOS |
| GET | `/payment/payos/status/{orderCode}` | JWT | Kiểm tra trạng thái PayOS |
| GET | `/payment/check-status/{orderCode}` | JWT | Kiểm tra trạng thái thanh toán |

### Reviews `/api/reviews`

| Method | Endpoint | Auth | Mô tả |
|--------|----------|------|-------|
| POST | `/reviews` | JWT | Gửi đánh giá (multipart, kèm ảnh) |
| GET | `/reviews/{bookingID}` | JWT | Lấy review theo booking |
| GET | `/reviews/tour/{tourCode}` | Public | Review của tour (phân trang) |
| GET | `/reviews/tour/{tourCode}/statistics` | Public | Thống kê đánh giá tour |

### Notifications `/api/notifications`

| Method | Endpoint | Auth | Mô tả |
|--------|----------|------|-------|
| GET | `/notifications` | JWT | Danh sách thông báo (phân trang) |
| GET | `/notifications/unread-count` | JWT | Số thông báo chưa đọc |
| PUT | `/notifications/{id}/read` | JWT | Đánh dấu đã đọc |
| PUT | `/notifications/read-all` | JWT | Đánh dấu tất cả đã đọc |

### Chatbot `/api/chatbot`

| Method | Endpoint | Auth | Mô tả |
|--------|----------|------|-------|
| POST | `/chatbot/chat` | Public | Gửi tin nhắn đến chatbot AI |
| POST | `/chatbot/sync` | ADMIN | Đồng bộ dữ liệu vào Pinecone |
| DELETE | `/chatbot/admin/clear-data` | ADMIN | Xóa dữ liệu Pinecone |

### Admin Endpoints `/api/admin/**`

<details>
<summary>Xem đầy đủ Admin API (click để mở rộng)</summary>

#### Admin Auth `/api/admin/auth`
```
POST   /admin/auth/login            - Đăng nhập admin
POST   /admin/auth/refresh-token    - Refresh token admin
POST   /admin/auth/logout           - Đăng xuất
GET    /admin/auth/profile          - Hồ sơ admin
```

#### Admin Tours `/api/admin/tours`
```
GET    /admin/tours                  - Danh sách tour (phân trang)
GET    /admin/tours/{tourId}         - Chi tiết tour
POST   /admin/tours                  - Tạo tour mới
PUT    /admin/tours/{tourId}         - Cập nhật tour
DELETE /admin/tours/{tourId}         - Xóa tour
PUT    /admin/tours/{tourId}/general-info  - Cập nhật thông tin chung
PUT    /admin/tours/{tourId}/itinerary     - Cập nhật lịch trình
POST   /admin/tours/{tourId}/upload-image  - Upload ảnh tour
POST   /admin/tours/{tourId}/upload-video  - Upload video tour
GET    /admin/tours/check-code       - Kiểm tra mã tour
```

#### Admin Departures `/api/admin/departures`
```
GET    /admin/departures             - Danh sách chuyến
GET    /admin/departures/{id}        - Chi tiết chuyến
POST   /admin/departures             - Tạo chuyến mới
PUT    /admin/departures/{id}        - Cập nhật chuyến
DELETE /admin/departures/{id}        - Xóa chuyến
PUT    /admin/departures/{id}/pricing    - Cập nhật giá
PUT    /admin/departures/{id}/transport  - Cập nhật phương tiện
POST   /admin/departures/{id}/clone  - Clone chuyến
GET    /admin/departures/by-tour/{tourId} - Chuyến theo tour
```

#### Admin Users `/api/users`
```
GET    /users/{userID}               - Hồ sơ người dùng
PUT    /users/{userID}               - Cập nhật hồ sơ
POST   /users/admin/search           - Tìm kiếm người dùng
POST   /users/admin/update-status    - Cập nhật trạng thái
```

#### Admin Coupons `/api/admin/coupons`
```
GET    /admin/coupons                - Danh sách coupon
GET    /admin/coupons/{id}           - Chi tiết coupon
POST   /admin/coupons                - Tạo coupon
PUT    /admin/coupons/{id}           - Cập nhật coupon
DELETE /admin/coupons/{id}           - Xóa coupon
GET    /admin/coupons/global         - Coupon toàn hệ thống
GET    /admin/coupons/departure      - Coupon theo chuyến
```

#### Admin Locations `/api/admin/locations`
```
GET    /admin/locations              - Danh sách điểm đến
GET    /admin/locations/{id}         - Chi tiết điểm đến
POST   /admin/locations              - Tạo điểm đến
PUT    /admin/locations/{id}         - Cập nhật điểm đến
DELETE /admin/locations/{id}         - Xóa điểm đến
POST   /admin/locations/{id}/image   - Upload ảnh điểm đến
```

#### Admin Dashboard `/api/admin/dashboard`
```
GET    /admin/dashboard/statistics   - Thống kê tổng quan
GET    /admin/dashboard/analysis     - Phân tích AI
```

#### Admin Branches & Policies
```
GET/POST/PUT/DELETE  /admin/branches           - Quản lý chi nhánh
GET/POST/PUT/DELETE  /admin/policy-templates   - Quản lý mẫu chính sách
```

</details>

---

## Bảo mật & Xác thực

### Cấu hình Spring Security

```
CSRF:             Disabled (JWT stateless)
CORS:             Enabled — localhost:3000
Session:          Stateless
```

### JWT Authentication Flow

```
Client                          Server
  │                               │
  ├──── POST /auth/login ────────►│
  │                               ├── Xác thực credentials
  │                               ├── Tạo accessToken (15 phút)
  │◄─── { accessToken,           ├── Tạo refreshToken (7 ngày)
  │       refreshToken } ─────────┤── Lưu refreshToken vào DB
  │                               │
  ├──── Request + Bearer token ──►│
  │                               ├── JwtAuthenticationFilter
  │                               ├── Validate token
  │◄─── Response ─────────────────┤
  │                               │
  ├──── (401) ─────────────────── │
  ├──── POST /auth/refresh-token ►│
  │                               ├── Validate refreshToken
  │◄─── { newAccessToken } ───────┤
  │                               │
```

### Phân quyền

| Role | Quyền truy cập |
|------|----------------|
| `ADMIN` | Toàn quyền: `/api/admin/**` và tất cả endpoint public |
| `CUSTOMER` | Đặt tour, thanh toán, review, hồ sơ cá nhân |
| `TOUR_OWNER` | Xem và quản lý tour riêng (chuẩn bị mở rộng) |
| Anonymous | Browse tours, locations, chatbot |

---

## Tích hợp thanh toán

### VNPay

- **Môi trường:** Sandbox (`https://sandbox.vnpayment.vn/paymentv2/vpcpay.html`)
- **Xác thực:** HMAC-SHA512
- **Thời hạn thanh toán:** 15 phút
- **Callback:** `GET /api/payment/vnpay-callback`

```
Tạo thanh toán → Redirect sang VNPay → Khách thanh toán
                                          │
                    ◄─── Callback ────────┘
                    Verify HMAC-SHA512
                    Cập nhật trạng thái booking
```

### PayOS

- **SDK:** payos-java 2.0.1
- **Webhook:** `POST /api/payment/payos/webhook`
- **Hỗ trợ:** QR Code, chuyển khoản ngân hàng, thẻ

### SePay (Xác minh chuyển khoản ngân hàng)

- **Ngân hàng:** BIDV
- **Tính năng:** Xác minh giao dịch thực tế, tạo nội dung VietQR
- **API:** `https://my.sepay.vn/userapi`

---

## AI & Chatbot

### Kiến trúc

```
Người dùng
    │
    ▼
POST /api/chatbot/chat
    │
    ▼
ChatbotService
    │
    ├── VectorService (Pinecone)
    │   ├── Chuyển câu hỏi → vector embedding (text-embedding-004)
    │   └── Tìm kiếm similarity trong index "tourism-chatbot"
    │
    └── GeminiAIService
        ├── Xây dựng prompt với context tour từ vector DB
        └── Gọi Gemini 2.0 Flash → Tạo câu trả lời
            Temperature: 0.7 | Max tokens: 1000
```

### Đồng bộ dữ liệu

```bash
# Admin trigger đồng bộ tour/location vào Pinecone
POST /api/chatbot/sync

# Xóa toàn bộ dữ liệu Pinecone (dev/test)
DELETE /api/chatbot/admin/clear-data
```

### Cấu hình Gemini

| Tham số | Giá trị |
|---------|---------|
| Model | `gemini-2.0-flash` |
| Embedding Model | `text-embedding-004` |
| Embedding Dimension | 768 |
| Temperature | 0.7 |
| Max Output Tokens | 1000 |
| Vector DB Index | `tourism-chatbot` (Pinecone) |

---

## WebSocket & Real-time

### Cấu hình STOMP

| Tham số | Giá trị |
|---------|---------|
| WebSocket Endpoint | `/ws` |
| Broker Prefix | `/topic`, `/topic/user` |
| App Destination Prefix | `/app` |
| Fallback | SockJS |

### Subscribe Topics

```javascript
// Thông báo người dùng
/topic/user/{userId}

// Cập nhật trạng thái booking
/topic/booking/{bookingCode}
```

### Các sự kiện real-time

| Sự kiện | Topic | Mô tả |
|---------|-------|-------|
| Thanh toán thành công | `/topic/booking/{code}` | Cập nhật trạng thái booking |
| Thông báo mới | `/topic/user/{userId}` | Thông báo hệ thống cho user |
| Xác nhận đặt tour | `/topic/booking/{code}` | Admin xác nhận booking |

---

## Email Service

**Provider:** Gmail SMTP (`smtp.gmail.com:587`)

### Các loại email

| Email | Trigger | Mô tả |
|-------|---------|-------|
| **Xác thực email** | Đăng ký tài khoản | Chứa link xác thực có thời hạn |
| **Chào mừng** | Xác thực email thành công | Email welcome |
| **Xác nhận đặt tour** | Booking PAID | Thông tin chi tiết booking |
| **Nhắc nhở thanh toán** | Booking PENDING_PAYMENT | Nhắc khách hoàn thành thanh toán |

Template được render bằng **Thymeleaf**.

---

## Lưu trữ tệp

### Cloudinary

- **Giới hạn upload:** 10MB/file
- **Hỗ trợ:** Hình ảnh (jpg, png, webp) và Video (mp4)
- **Xử lý tự động:** Nén, tối ưu kích thước, CDN delivery

**Các loại tài nguyên:**

| Loại | Mô tả |
|------|-------|
| Tour Images | Ảnh gallery của tour |
| Tour Videos | Video giới thiệu tour |
| Location Images | Ảnh điểm đến |
| User Avatars | Ảnh đại diện người dùng |
| Review Images | Ảnh đính kèm review |

---

## Cấu hình môi trường

File `src/main/resources/application.yaml`:

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
  access-token-expiration: 900000      # 15 phút (ms)
  refresh-token-expiration: 604800000  # 7 ngày (ms)

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

> **Bảo mật:** Không commit file `application.yaml` có chứa credentials thật lên git. Sử dụng biến môi trường (`${ENV_VAR}`) hoặc Spring Cloud Config cho môi trường production.

---

## Xử lý lỗi

### Custom Exceptions

| Exception | HTTP Status | Mô tả |
|-----------|-------------|-------|
| `BadRequestException` | 400 | Dữ liệu đầu vào không hợp lệ |
| `UnauthorizedException` | 401 | Chưa xác thực hoặc token hết hạn |
| `ForbiddenException` | 403 | Không có quyền truy cập |
| `NotFoundException` | 404 | Tài nguyên không tìm thấy |
| `ConflictException` | 409 | Xung đột dữ liệu |
| `DuplicateResourceException` | 409 | Tài nguyên đã tồn tại |
| `ResourceInUseException` | 409 | Tài nguyên đang được sử dụng |
| `FileSizeExceededException` | 413 | File vượt quá giới hạn 10MB |
| `InvalidFileException` | 415 | Định dạng file không hợp lệ |
| `VideoUploadException` | 500 | Lỗi upload video |

### Response Format chuẩn

```json
{
  "success": true,
  "message": "Thao tác thành công",
  "data": { ... }
}
```

```json
{
  "success": false,
  "message": "Không tìm thấy tour với mã TOUR001",
  "error": "NotFoundException",
  "timestamp": "2026-04-25T10:30:00Z"
}
```

---

## Enums

| Enum | Giá trị |
|------|---------|
| `BookingStatus` | PENDING_PAYMENT, OVERDUE_PAYMENT, PENDING_CONFIRMATION, PAID, CANCELLED, PENDING_REVIEW, REVIEWED, PENDING_REFUND |
| `PaymentMethod` | VNPAY, PAYOS |
| `Role` | ADMIN, CUSTOMER, TOUR_OWNER |
| `Region` | Các vùng du lịch Việt Nam |
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

