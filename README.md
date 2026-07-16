# PrintXpress — Digital Printing Service App

Native Android (Java) client + Java Spring Boot backend for a digital printing service.

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
- [API Endpoints](#api-endpoints)
- [Backend Setup](#backend-setup)
- [Backend Deployment](#backend-deployment)
- [Android Setup](#android-setup)
- [Screenshots](#screenshots)
- [Required Firebase / Google Cloud Info](#required-firebase--google-cloud-info)

---

## Introduction

**PrintXpress** is a mobile-first digital printing marketplace. Customers can browse print products (business cards, flyers, banners, mugs, etc.), customize their order, choose a delivery method, and track order history. The Android app communicates with a Spring Boot REST API backed by Google Cloud Firestore. Authentication is handled by Firebase Authentication, supporting email/password, username login, and Google Sign-In.

---

## Features

- **Firebase Authentication**
  - Register with email and password
  - Login with email **or** username
  - Forgot / reset password
  - Google Sign-In
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
  - Server-side validation in Spring Boot controllers and services
- **Architecture**
  - MVVM pattern on Android
  - Repository pattern for data access
  - LiveData / MutableLiveData for UI updates
  - Retrofit with Gson for REST communication

---

## Project Structure

```
Asna_app/
├── backend/                  # Spring Boot REST API
│   └── src/main/java/com/printxpress/backend/
│       ├── controller/       # REST controllers
│       ├── service/          # Business logic
│       ├── model/            # Firestore entities
│       ├── dto/              # Request/response objects
│       └── config/           # Firestore / security config
├── android/                  # Native Android Java app
│   └── app/src/main/java/com/printxpress/android/
│       ├── ui/               # Activities and Adapters
│       ├── viewmodel/        # MVVM ViewModels
│       ├── data/
│       │   ├── model/        # POJOs
│       │   ├── repository/   # Data repositories
│       │   └── remote/       # Retrofit API service
│       └── MainActivity.java
├── app-icon.jpg
├── screen.png
└── README.md
```

---

## Architecture

- **Android client**: Java, MVVM, Retrofit/Gson, LiveData, Firebase Authentication
- **Backend**: Java Spring Boot, REST APIs
- **Database**: Google Cloud Firestore (NoSQL document store)
- **Authentication**: Firebase Authentication
- **Hosting**: Firebase App Hosting / Google Cloud Run

---

## Technology Stack

| Layer | Technology |
|-------|------------|
| Mobile OS | Android 7.0+ (API 24+) |
| Mobile Language | Java |
| Mobile Architecture | MVVM |
| Networking | Retrofit 2, OkHttp, Gson |
| UI Components | RecyclerView, ConstraintLayout, Material Design |
| Authentication | Firebase Auth, Google Sign-In |
| Backend Framework | Spring Boot 3 |
| Backend Language | Java 17+ |
| Database | Google Cloud Firestore |
| Containerization | Docker |
| Cloud Hosting | Firebase App Hosting / Google Cloud Run |

---

## Workflow

1. **Launch** the app → `MainActivity` checks the current Firebase user.
2. **Unauthenticated user** is redirected to `LoginActivity`.
3. From `LoginActivity`, user can:
   - Sign in with email or username
   - Register a new account
   - Reset password
   - Use Google Sign-In
4. After login, user reaches `ProductListActivity`.
5. User selects a product → `ProductDetailActivity`.
6. User customizes the order and taps **Order** → `OrderSummaryActivity`.
7. User selects delivery option and confirms → order is sent to backend.
8. Backend stores the order in Firestore and returns the created order.
9. User can view all previous orders in `OrderHistoryActivity`.

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

    subgraph Google Cloud
        B[Firebase Authentication]
        C[Firebase App Hosting]
    end

    subgraph Backend
        D[Spring Boot REST API]
        E[Docker Container]
    end

    subgraph Data
        F[Google Cloud Firestore]
    end

    A -->|HTTPS / Retrofit| D
    A -->|Firebase SDK| B
    D -->|Firestore SDK| F
    D --> E
    C -->|Reverse proxy| D
```

### Class Diagram

#### Backend (Spring Boot)

```mermaid
classDiagram
    class ProductController {
        -ProductService productService
        +getAll(String category) ResponseEntity~ApiResponse~List~Product~~
        +getById(String id) ResponseEntity~ApiResponse~Product~
        +create(Product product) ResponseEntity~ApiResponse~Product~
    }

    class OrderController {
        -OrderService orderService
        +getAll() ResponseEntity~ApiResponse~List~PrintOrder~~
        +getByUser(String userId) ResponseEntity~ApiResponse~List~PrintOrder~~
        +getById(String id) ResponseEntity~ApiResponse~PrintOrder~
        +create(CreateOrderRequest request) ResponseEntity~ApiResponse~PrintOrder~
    }

    class UserController {
        -UserService userService
        +getById(String id) ResponseEntity~ApiResponse~User~
        +create(User user) ResponseEntity~ApiResponse~User~
        +lookupEmail(UsernameLookupRequest request) ResponseEntity~ApiResponse~String~
    }

    class ProductService {
        -CollectionReference products
        +findAll() List~Product~
        +findByCategory(String category) List~Product~
        +findById(String id) Optional~Product~
        +save(Product product) Product
    }

    class OrderService {
        -CollectionReference orders
        -ProductService productService
        +createOrder(CreateOrderRequest request) PrintOrder
        +findByUserId(String userId) List~PrintOrder~
        +findById(String id) Optional~PrintOrder~
        +findAll() List~PrintOrder~
    }

    class UserService {
        -Firestore firestore
        -CollectionReference users
        +save(User user) User
        +findById(String id) Optional~User~
        +findByEmail(String email) Optional~User~
        +findByUsername(String username) Optional~User~
        +existsByEmail(String email) boolean
        +existsByUsername(String username) boolean
    }

    class Product {
        -String id
        -String category
        -String name
        -Double basePrice
        -String specs
        -String type
        -String color
        -String weight
    }

    class PrintOrder {
        -String id
        -String name
        -String type
        -Double totalAmount
        -String status
        -String userId
        -String deliveryId
        -List~OrderItem~ orderItems
    }

    class User {
        -String id
        -String username
        -String email
        -String phone
        -String role
    }

    class OrderItem {
        -String productId
        -String productName
        -Integer quantity
        -Double price
        -String designUrl
        -String customText
    }

    class ApiResponse~T~ {
        -boolean success
        -String message
        -T data
        +success(T data) ApiResponse~T~
        +error(String message) ApiResponse~T~
    }

    ProductController --> ProductService
    OrderController --> OrderService
    UserController --> UserService
    ProductService --> Product
    OrderService --> PrintOrder
    OrderService --> OrderItem
    OrderService --> ProductService
    UserService --> User
```

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
        +createOrder(CreateOrderRequest request)
        +loadUserOrders(String userId)
    }

    class AuthRepository {
        -ApiService apiService
        -FirebaseAuth firebaseAuth
        +loginWithEmail(...)
        +loginWithUsername(...)
        +register(...)
        +sendPasswordResetEmail(...)
        +getCurrentUser() FirebaseUser
    }

    class ProductRepository {
        -ApiService apiService
        +getProducts(...)
        +getProduct(...)
    }

    class OrderRepository {
        -ApiService apiService
        +createOrder(...)
        +getOrdersByUser(...)
    }

    class ApiService {
        <<interface>>
        +getProducts(String category)
        +getProduct(String id)
        +createOrder(CreateOrderRequest request)
        +getOrdersByUser(String userId)
        +createUserProfile(User user)
        +lookupEmail(Map request)
    }

    class RetrofitClient {
        +BASE_URL String
        +getApiService() ApiService
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

    AuthRepository --> ApiService
    ProductRepository --> ApiService
    OrderRepository --> ApiService
    ApiService --> RetrofitClient
```

### Sequence Diagrams

#### 1. Login with Username

```mermaid
sequenceDiagram
    actor U as Customer
    participant A as LoginActivity
    participant VM as AuthViewModel
    participant R as AuthRepository
    participant API as Spring Boot API
    participant FB as Firebase Auth

    U->>A: Enter username + password
    A->>VM: login(username, password)
    VM->>R: loginWithUsername(username, password)
    R->>API: POST /api/users/lookup-email
    API->>API: resolve username to email
    API-->>R: email address
    R->>FB: signInWithEmailAndPassword(email, password)
    FB-->>R: FirebaseUser
    R-->>VM: success
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
    participant API as Spring Boot API
    participant FS as Google Cloud Firestore

    U->>PDA: Select product & options
    PDA->>OSA: Open order summary
    U->>OSA: Confirm order
    OSA->>VM: createOrder(request)
    VM->>R: createOrder(request)
    R->>API: POST /api/orders
    API->>API: validate items, calculate total
    API->>FS: save PrintOrder document
    FS-->>API: saved order
    API-->>R: ApiResponse~PrintOrder~
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
    Confirm -- Yes --> PlaceOrder[Place order via API]
    PlaceOrder --> Success{Success?}
    Success -- No --> Customize
    Success -- Yes --> History[Show order history]
    History --> End([End])
```

### Data Model / ER Diagram

```mermaid
erDiagram
    USER ||--o{ PRINT_ORDER : places
    PRINT_ORDER ||--|{ ORDER_ITEM : contains
    PRODUCT ||--o{ ORDER_ITEM : referenced_by
    DELIVERY ||--o{ PRINT_ORDER : selected_for
    SAMPLE ||--o{ PRODUCT : showcases
    ADMIN ||--o{ PRODUCT : manages
    ADMIN ||--o{ PRINT_ORDER : manages

    USER {
        string id PK
        string username
        string email
        string phone
        string role
    }

    PRODUCT {
        string id PK
        string category
        string name
        double basePrice
        string specs
        string type
        string color
        string weight
    }

    ORDER_ITEM {
        string productId
        string productName
        int quantity
        double price
        string designUrl
        string customText
    }

    PRINT_ORDER {
        string id PK
        string name
        string type
        double totalAmount
        string status
        string userId FK
        string deliveryId FK
        array orderItems
    }

    DELIVERY {
        string id PK
        string method
        string duration
        double fee
    }

    SAMPLE {
        string id PK
        string title
        string imageUrl
        string category
    }

    ADMIN {
        string id PK
        string email
        string name
        string role
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
    end

    subgraph Spring Boot API
        B1[com.printxpress.backend.controller]
        B2[com.printxpress.backend.service]
        B3[com.printxpress.backend.model]
        B4[com.printxpress.backend.dto]
        B5[com.printxpress.backend.config]
    end

    subgraph External Services
        C1[Firebase Authentication]
        C2[Google Cloud Firestore]
    end

    A1 --> A2
    A2 --> A3
    A3 --> A4
    A4 --> A5
    A3 --> A5

    B1 --> B2
    B2 --> B3
    B2 --> B4
    B5 --> C2

    A4 -->|Retrofit HTTPS| B1
    A2 -->|Firebase SDK| C1
    B1 -->|Firebase SDK| C1
```

---

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/products?category={category}` | List all products or filter by category |
| GET | `/api/products/{id}` | Get product details |
| POST | `/api/products` | Create a new product (admin) |
| POST | `/api/orders` | Create a new print order |
| GET | `/api/orders/user/{userId}` | Get orders for a specific user |
| GET | `/api/orders/{id}` | Get order by ID |
| GET | `/api/deliveries` | List delivery options |
| GET | `/api/samples` | List sample designs |
| POST | `/api/users` | Create or update user profile |
| GET | `/api/users/{id}` | Get user profile by ID |
| POST | `/api/users/lookup-email` | Resolve username to email |

---

## Backend Setup

1. Create a Firebase project and enable Firestore.
2. Download your service account key (Firebase Console → Project Settings → Service Accounts →
   Generate new private key) and set the environment variable:
   ```bash
   export GOOGLE_APPLICATION_CREDENTIALS="/path/to/serviceAccountKey.json"
   ```
   On Cloud Run / App Hosting the default service account is used automatically.
   **Never commit this file** — it's already covered by `.gitignore`.
3. Run (uses the bundled Gradle wrapper, no local Gradle install needed):
   ```bash
   cd backend
   ./gradlew bootRun
   ```
   On Windows:
   ```powershell
   cd backend
   .\gradlew.bat bootRun
   ```
4. Optional: to populate a few sample products on first run (when Firestore has none yet), set
   `SEED_DATA=true`:
   ```bash
   SEED_DATA=true ./gradlew bootRun
   ```

---

## Backend Deployment

1. Build the Docker image:
   ```bash
   cd backend
   docker build -t printxpress-backend .
   ```
2. Push to Google Artifact Registry and deploy to Cloud Run / Firebase App Hosting.
3. Note the HTTPS URL and update `RetrofitClient.BASE_URL` in the Android app.

---

## Android Setup

1. In the Firebase console, add an Android app and download `google-services.json`.
2. Add your SHA-1 fingerprint to the Firebase Android app (see SHA-1 section below).
3. In Firebase Console → Authentication → Sign-in method, enable **Email/Password** and **Google**.
4. Place `google-services.json` in `android/app/`.
5. `BASE_URL` in `android/app/src/main/java/com/printxpress/android/data/remote/RetrofitClient.java`
   defaults to `http://10.0.2.2:8080/`, the Android emulator's alias for your machine's localhost —
   this works out of the box against a locally running backend (`./gradlew bootRun` in `backend/`).
   For a physical device or a deployed backend, change it to your Cloud Run / Firebase App Hosting
   HTTPS URL (must end in `/`).
6. Open the `android` folder in Android Studio and run the app, or use Gradle from the command line:
   ```bash
   cd android
   ./gradlew installDebug
   ```
   On Windows:
   ```powershell
   cd android
   .\gradlew.bat installDebug
   ```

### Get your SHA-1 fingerprint (Windows)

Open PowerShell or Command Prompt and run:

```bash
cd %USERPROFILE%\.android
keytool -list -v -keystore debug.keystore -alias androiddebugkey -storepass android -keypass android
```

Or in Android Studio: open the Gradle panel, run:

```
android > Tasks > android > signingReport
```

Copy the **SHA-1** value (not SHA-256) and paste it into your Firebase Android app settings.

---

## Screenshots

<p align="center">
  <img src="app-icon.jpg" alt="App Icon" width="120" />
  <img src="screen.png" alt="App Screenshot" width="240" />
</p>

---

## Required Firebase / Google Cloud Info

Before running you need:
- Firebase project ID
- `google-services.json` for Android
- Firestore database created in Native mode
- Cloud Run / Firebase App Hosting URL for the backend

---

## Author

**Fathima Asna** — ICBT MAD Assignment
