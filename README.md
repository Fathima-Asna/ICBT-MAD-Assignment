# PrintXpress — Digital Printing Service App

Native Android (Java) mobile application for a digital printing service, powered by Supabase.

## Table of Contents

- [Introduction](#introduction)
- [Features](#features)
- [Project Structure](#project-structure)
- [Architecture](#architecture)
- [Technology Stack](#technology-stack)
- [Workflow](#workflow)
- [UML Diagrams](#uml-diagrams)
  - [Use Case Diagram](#use-case-diagram)
  - [System Architecture Diagram](#system-architecture-diagram)
  - [Class Diagram](#class-diagram)
  - [Sequence Diagrams](#sequence-diagrams)
  - [Activity Diagram](#activity-diagram)
  - [Data Model / ER Diagram](#data-model--er-diagram)
  - [Package Diagram](#package-diagram)
- [Supabase Setup](#supabase-setup)
- [Android Setup](#android-setup)
- [Screenshots](#screenshots)

---

## Introduction

**PrintXpress** is a mobile-first digital printing marketplace. Customers can browse print products (business cards, flyers, banners, posters, etc.), customize their order, choose a delivery method, and track order history. The Android app communicates directly with Supabase for both authentication and data storage, using Supabase Auth REST API for email/password and Google Sign-In, and the Supabase PostgREST API for CRUD operations on a PostgreSQL database.

---

## Features

- **Supabase Authentication**
  - Register with email and password
  - Login with email **or** username
  - Forgot / reset password
  - Google Sign-In via Supabase `id_token` grant
  - Session management with automatic token refresh
- **Product browsing**
  - View product list by category
  - View product details and specifications
- **Order placement**
  - Select product, quantity, design file, custom text
  - Choose delivery option
  - Calculate total amount automatically
- **Order history**
  - View past orders and their status
- **Validation**
  - Client-side validation in Activities / ViewModels
- **Architecture**
  - MVVM pattern on Android
  - Repository pattern for data access
  - LiveData / MutableLiveData for UI updates
  - Retrofit for Supabase REST API communication

---

## Project Structure

```
Asna_app/
├── android/                  # Native Android Java app
│   └── app/src/main/java/com/printxpress/android/
│       ├── ui/               # Activities and Adapters
│       ├── viewmodel/        # MVVM ViewModels
│       ├── data/
│       │   ├── model/        # POJOs
│       │   ├── repository/   # Data repositories
│       │   └── remote/       # Supabase API interfaces & client
│       ├── util/             # SessionManager, ValidationUtils
│       ├── PrintXpressApp.java
│       └── MainActivity.java
├── supabase_setup.sql        # Database schema & seed data
├── app-icon.jpg
├── screen.png
└── README.md
```

---

## Architecture

- **Android client**: Java, MVVM, Retrofit/Gson, LiveData, Supabase Auth
- **Backend**: Supabase (hosted PostgreSQL + Auth + PostgREST)
- **Database**: PostgreSQL via Supabase
- **Authentication**: Supabase Auth (email/password, Google Sign-In)

---

## Technology Stack

| Layer | Technology |
|-------|------------|
| Mobile OS | Android 7.0+ (API 24+) |
| Mobile Language | Java |
| Mobile Architecture | MVVM |
| Networking | Retrofit 2, OkHttp, Gson |
| UI Components | RecyclerView, ConstraintLayout, Material Design |
| Authentication | Supabase Auth, Google Sign-In |
| Database | PostgreSQL (Supabase) |
| API Layer | Supabase PostgREST |

---

## Workflow

1. **Launch** the app → `MainActivity` checks for an existing session.
2. **Unauthenticated user** is redirected to `LoginActivity`.
3. From `LoginActivity`, user can:
   - Sign in with email or username
   - Register a new account
   - Reset password
   - Use Google Sign-In
4. After login, user reaches `ProductListActivity`.
5. User selects a product → `ProductDetailActivity`.
6. User customizes the order and taps **Order** → `OrderSummaryActivity`.
7. User selects delivery option and confirms → order is saved via Supabase.
8. User can view all previous orders in `OrderHistoryActivity`.

---

## UML Diagrams

### Use Case Diagram

```mermaid
graph LR
    User((Customer))
    Admin((Admin))

    subgraph PrintXpress App
        UC1[Register]
        UC2[Login with email]
        UC3[Login with username]
        UC4[Reset password]
        UC5[Google Sign-In]
        UC6[Browse products]
        UC7[View product details]
        UC8[Place print order]
        UC9[Choose delivery]
        UC10[View order history]
        UC11[Manage products]
        UC12[Manage orders]
    end

    User --> UC1
    User --> UC2
    User --> UC3
    User --> UC4
    User --> UC5
    User --> UC6
    User --> UC7
    User --> UC8
    User --> UC9
    User --> UC10

    Admin --> UC11
    Admin --> UC12
```

### System Architecture Diagram

```mermaid
graph TB
    subgraph Client
        A[Android App Java/MVVM]
    end

    subgraph Supabase Cloud
        B[Supabase Auth]
        C[PostgREST API]
        D[PostgreSQL Database]
    end

    A -->|Retrofit HTTPS| B
    A -->|Retrofit HTTPS| C
    C -->|SQL| D
    B -->|Manages users| D
```

### Class Diagram

#### Android Client

```mermaid
classDiagram
    class MainActivity
    class LoginActivity
    class RegisterActivity
    class ForgotPasswordActivity
    class ProductListActivity
    class ProductDetailActivity
    class OrderSummaryActivity
    class OrderHistoryActivity

    class AuthViewModel {
        -AuthRepository authRepository
        +login(String identifier, String password)
        +register(User user, String password)
        +sendPasswordReset(String email)
        +createUserProfile(User user)
    }

    class ProductViewModel {
        -ProductRepository productRepository
        +loadProducts(String category)
        +loadProduct(String id)
    }

    class OrderViewModel {
        -OrderRepository orderRepository
        +createOrder(PrintOrder order)
        +loadUserOrders(String userId)
    }

    class AuthRepository {
        -SupabaseAuthApi authApi
        -SupabaseDataApi dataApi
        +loginWithEmail(...)
        +loginWithUsername(...)
        +register(...)
        +signInWithGoogleIdToken(...)
        +sendPasswordResetEmail(...)
        +getCurrentUser() AuthUser
    }

    class ProductRepository {
        -SupabaseDataApi dataApi
        +getProducts(...)
        +getProduct(...)
    }

    class OrderRepository {
        -SupabaseDataApi dataApi
        +createOrder(...)
        +getOrdersByUser(...)
    }

    class SupabaseAuthApi {
        <<interface>>
        +signUp(SignUpRequest)
        +signInWithPassword(String, PasswordGrantRequest)
        +signInWithIdToken(String, IdTokenGrantRequest)
        +refreshToken(String, RefreshGrantRequest)
        +recoverPassword(RecoverRequest)
    }

    class SupabaseDataApi {
        <<interface>>
        +findProfiles(...)
        +upsertProfile(...)
        +getProducts(...)
        +createOrder(...)
        +getOrders(...)
    }

    class SupabaseClient {
        +SUPABASE_URL String
        +SUPABASE_ANON_KEY String
        +getRetrofit() Retrofit
        +getAuthApi() SupabaseAuthApi
        +getDataApi() SupabaseDataApi
    }

    class SessionManager {
        +saveSession(...)
        +clearSession()
        +isLoggedIn() boolean
        +getAccessToken() String
        +getCurrentUser() AuthUser
    }

    MainActivity --> AuthViewModel
    LoginActivity --> AuthViewModel
    RegisterActivity --> AuthViewModel
    ForgotPasswordActivity --> AuthViewModel
    ProductListActivity --> ProductViewModel
    ProductDetailActivity --> ProductViewModel
    OrderSummaryActivity --> OrderViewModel
    OrderHistoryActivity --> OrderViewModel

    AuthViewModel --> AuthRepository
    ProductViewModel --> ProductRepository
    OrderViewModel --> OrderRepository

    AuthRepository --> SupabaseAuthApi
    AuthRepository --> SupabaseDataApi
    ProductRepository --> SupabaseDataApi
    OrderRepository --> SupabaseDataApi
    SupabaseAuthApi --> SupabaseClient
    SupabaseDataApi --> SupabaseClient
```

### Sequence Diagrams

#### 1. Login with Username

```mermaid
sequenceDiagram
    actor U as Customer
    participant A as LoginActivity
    participant VM as AuthViewModel
    participant R as AuthRepository
    participant DATA as Supabase PostgREST
    participant AUTH as Supabase Auth

    U->>A: Enter username + password
    A->>VM: login(username, password)
    VM->>R: loginWithUsername(username, password)
    R->>DATA: GET /rest/v1/profiles?username=eq.xxx
    DATA-->>R: email address
    R->>AUTH: POST /auth/v1/token?grant_type=password
    AUTH-->>R: TokenResponse (access_token, refresh_token)
    R-->>VM: success (AuthUser)
    VM-->>A: navigate to ProductListActivity
```

#### 2. Place an Order

```mermaid
sequenceDiagram
    actor U as Customer
    participant PDA as ProductDetailActivity
    participant OSA as OrderSummaryActivity
    participant VM as OrderViewModel
    participant R as OrderRepository
    participant DATA as Supabase PostgREST

    U->>PDA: Select product & options
    PDA->>OSA: Open order summary
    U->>OSA: Confirm order
    OSA->>VM: createOrder(order)
    VM->>R: createOrder(order)
    R->>DATA: POST /rest/v1/print_orders
    DATA-->>R: created PrintOrder
    R-->>VM: success
    VM-->>OSA: show confirmation
```

### Activity Diagram

```mermaid
flowchart TD
    Start([Start App]) --> CheckAuth{User logged in?}
    CheckAuth -- No --> Login[Show Login Screen]
    CheckAuth -- Yes --> Home[Show Product List]

    Login --> Action{Choose action}
    Action -- Register --> Register[Register new account]
    Action -- Login --> Validate{Valid credentials?}
    Action -- Forgot --> Reset[Send reset email]

    Register --> Validate
    Reset --> Login

    Validate -- No --> Login
    Validate -- Yes --> Home

    Home --> SelectProduct[Select Product]
    SelectProduct --> Customize[Customize order]
    Customize --> Delivery[Choose delivery]
    Delivery --> Confirm{Confirm?}
    Confirm -- No --> Customize
    Confirm -- Yes --> PlaceOrder[Place order via Supabase]
    PlaceOrder --> Success{Success?}
    Success -- No --> Customize
    Success -- Yes --> History[Show order history]
    History --> End([End])
```

### Data Model / ER Diagram

```mermaid
erDiagram
    PROFILES ||--o{ PRINT_ORDERS : places
    PRINT_ORDERS ||--|{ ORDER_ITEMS : "contains (jsonb)"
    PRODUCTS ||--o{ ORDER_ITEMS : referenced_by

    PROFILES {
        uuid id PK
        text username
        text profile
        text name
        text phone
        text email
        text address
    }

    PRODUCTS {
        uuid id PK
        text category
        text name
        numeric basePrice
        text specs
        text type
        text color
        text weight
    }

    ORDER_ITEMS {
        text productId
        text productName
        int quantity
        numeric price
        text designUrl
        text customText
    }

    PRINT_ORDERS {
        uuid id PK
        text name
        text type
        numeric totalAmount
        text status
        uuid userId FK
        text deliveryId
        jsonb orderItems
        timestamptz createdAt
    }
```

### Package Diagram

```mermaid
graph TD
    subgraph Android App
        A1[com.printxpress.android.ui]
        A2[com.printxpress.android.viewmodel]
        A3[com.printxpress.android.data.repository]
        A4[com.printxpress.android.data.remote]
        A5[com.printxpress.android.data.model]
        A6[com.printxpress.android.util]
    end

    subgraph Supabase
        C1[Supabase Auth]
        C2[Supabase PostgREST]
        C3[PostgreSQL]
    end

    A1 --> A2
    A2 --> A3
    A3 --> A4
    A4 --> A5
    A3 --> A5
    A6 --> A4

    A4 -->|Retrofit HTTPS| C1
    A4 -->|Retrofit HTTPS| C2
    C2 -->|SQL| C3
```

---

## Supabase Setup

1. Create a free project on [supabase.com](https://supabase.com).
2. In the Supabase Dashboard, go to **SQL Editor → New query**, paste the contents of
   [`supabase_setup.sql`](supabase_setup.sql), and click **Run**. This creates the
   `profiles`, `products`, and `print_orders` tables with Row Level Security (RLS) policies
   and seeds four sample products.
3. In **Authentication → Providers**, enable:
   - **Email** (with or without "Confirm email" — the app handles both flows)
   - **Google** (paste your Google OAuth Client ID and Secret)
4. Note your **Project URL** and **anon/public key** from **Settings → API**.

---

## Android Setup

1. Open the `android` folder in Android Studio.
2. Update `SupabaseClient.java` with your Supabase project URL and anon key:
   ```java
   public static final String SUPABASE_URL = "https://YOUR_PROJECT.supabase.co/";
   public static final String SUPABASE_ANON_KEY = "YOUR_ANON_KEY";
   ```
3. For Google Sign-In, add your **Web Client ID** (from Google Cloud Console) to
   `res/values/strings.xml`:
   ```xml
   <string name="default_web_client_id">YOUR_WEB_CLIENT_ID</string>
   ```
4. Build and run:
   ```bash
   cd android
   ./gradlew installDebug
   ```
   On Windows:
   ```powershell
   cd android
   .\gradlew.bat installDebug
   ```

---

## Screenshots

<p align="center">
  <img src="app-icon.jpg" alt="App Icon" width="120" />
  <img src="screen.png" alt="App Screenshot" width="240" />
</p>

---

## Author

**Fathima Asna** — ICBT MAD Assignment
