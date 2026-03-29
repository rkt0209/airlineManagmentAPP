# ✈️ Airline Booking — Android App

A premium Android client for the Airline Booking microservices platform, built entirely with **Kotlin** and **Jetpack Compose (Material 3)**. The app supports two distinct roles — **Passenger** and **Admin** — each with their own dedicated UI, navigation, and feature set. All data is live from the Node.js backend via REST API.

---

## 📲 Download (Static UI Preview)

> Try the full UI/UX without needing a running backend.

**[⬇️ Download APK (Google Drive)](https://drive.google.com/file/d/1mizDEc4hqTUytJFRnaJZeA0Q22mcGWJ1/view?usp=drivesdk)**

---

## 📋 Table of Contents

- [Features](#-features)
- [Architecture](#-architecture)
- [Project Structure](#-project-structure)
- [Tech Stack & Dependencies](#-tech-stack--dependencies)
- [Screens & UI Deep-Dive](#-screens--ui-deep-dive)
- [Network Layer](#-network-layer)
- [Authentication & JWT](#-authentication--jwt)
- [State Management](#-state-management)
- [Navigation](#-navigation)
- [Theme & Design System](#-theme--design-system)
- [How to Run](#-how-to-run)

---

## ✨ Features

### Passenger Features
- **Login & Signup** with role selection (Passenger / Admin)
- **Flight Search** — select departure airport, arrival airport, and travel date from real API data
- **Flight Results** — premium ticket cards with airline branding, duration, seat count, and price; filtered by airport IDs and date (device local timezone)
- **Flight Detail** — seat selector, price summary, and one-tap booking
- **Booking Confirmation** — real API call; triggers confirmation email + RabbitMQ ticket on backend
- **My Bookings** — boarding-pass style cards split into **Upcoming** and **Previous** tabs, sorted by departure time, enriched with route and flight details
- **User Profile** — JWT-decoded real email and role; settings, support, and logout

### Admin Features
- **Admin Dashboard** — overview of all managed entities
- **Cities Management** — create, view, update, delete cities
- **Airports Management** — create, view, update, delete airports linked to cities
- **Airplanes Management** — manage airplane inventory (model, capacity)
- **Flights Management** — full flight schedule CRUD (flight number, airplane, route, times, price, boarding gate); all times shown in device local timezone
- **Admin Profile** — distinct visual identity with system administration links

---

## 🏗️ Architecture

The app follows **Clean Architecture with MVVM** across three layers:

```
┌─────────────────────────────────────────┐
│         Presentation Layer              │
│  Composable Screens + ViewModels        │
│  StateFlow → collectAsState() → UI      │
├─────────────────────────────────────────┤
│           Domain Layer                  │
│  Repository interfaces + Result<T>      │
│  Error extraction from HTTP responses   │
├─────────────────────────────────────────┤
│            Data Layer                   │
│  Retrofit API interfaces + DTOs         │
│  OkHttp + AuthInterceptor + Logging     │
├─────────────────────────────────────────┤
│         Dependency Injection            │
│  Dagger Hilt — NetworkModule            │
│  @HiltViewModel, @Singleton, @Inject    │
└─────────────────────────────────────────┘
```

**Key architectural decisions:**

| Decision | Choice | Why |
| :--- | :--- | :--- |
| UI Framework | Jetpack Compose | Declarative, less boilerplate, better state integration |
| DI | Dagger Hilt | Compile-time safety, standard Android DI |
| State | StateFlow | Lifecycle-aware, works naturally with `collectAsState()` |
| Error propagation | `Result<T>` | Type-safe, avoids try/catch at every call site |
| Flight filtering | Client-side | Backend has no query params; fetch all and filter locally |
| Booking enrichment | Client-side join | `BookingItem` only has `flightId`; join with flights + airports in ViewModel |
| Navigation | Compose Navigation | Single-activity, type-safe args, back-stack management |

---

## 📂 Project Structure

```
app/src/main/java/com/example/airline/
│
├── AirlineApplication.kt          # @HiltAndroidApp entry point
├── MainActivity.kt                # Single Activity — hosts AppNavGraph
│
├── core/
│   └── network/
│       ├── NetworkModule.kt       # Hilt module: Retrofit, OkHttp, all APIs
│       ├── AuthInterceptor.kt     # Injects x-access-token header on every request
│       └── TokenManager.kt       # Save/get/clear/decode JWT (SharedPreferences)
│
├── data/
│   ├── remote/
│   │   ├── AuthApi.kt             # POST signup, POST signin
│   │   ├── AuthModels.kt          # SignUpRequest, SignInRequest, responses
│   │   ├── AdminApi.kt            # CRUD endpoints: cities, airports, airplanes, flights
│   │   ├── AdminModels.kt         # All DTOs + ApiListResponse<T>, ApiObjectResponse<T>
│   │   ├── BookingApi.kt          # POST create booking, GET bookings by user
│   │   └── BookingModels.kt       # CreateBookingRequest, BookingItem
│   │
│   └── repository/
│       ├── AuthRepository.kt      # signIn() + signUp() → saves token
│       ├── AdminRepository.kt     # All admin entity CRUD
│       └── BookingRepository.kt   # createBooking() + getBookingsByUser()
│
├── navigation/
│   └── AppNavGraph.kt             # Full nav graph: auth → user/admin → search → booking
│
└── ui/
    ├── theme/
    │   ├── Color.kt               # Airline navy/sky/aqua palette
    │   ├── Type.kt                # Material 3 typography
    │   └── Theme.kt               # AirlineTheme composable
    │
    └── screens/
        ├── auth/
        │   ├── AuthViewModel.kt
        │   ├── LoginScreen.kt
        │   └── SignupScreen.kt
        │
        ├── booking/
        │   ├── HomeScreen.kt          # Search form
        │   ├── HomeViewModel.kt       # Loads airports from API
        │   ├── FlightResultsScreen.kt # Results list with FlightUi cards
        │   ├── FlightResultsViewModel.kt  # Fetches + filters flights
        │   ├── FlightDetailScreen.kt  # Seat selector + booking trigger
        │   ├── BookingViewModel.kt    # createBooking() + fetchMyBookings()
        │   └── MyBookingsScreen.kt    # Upcoming/Previous tabs
        │
        ├── admin/
        │   ├── AdminDashboardScreen.kt
        │   ├── AdminCitiesScreen.kt + AdminCitiesViewModel.kt
        │   ├── AdminAirportsScreen.kt + AdminAirportsViewModel.kt
        │   ├── AdminAirplanesScreen.kt + AdminAirplanesViewModel.kt
        │   └── AdminFlightsScreen.kt + AdminFlightsViewModel.kt
        │
        ├── main/
        │   ├── UserMainScreen.kt      # Curved bottom nav + 3 tabs
        │   └── AdminMainScreen.kt     # Admin dashboard host
        │
        └── profile/
            ├── ProfileViewModel.kt
            ├── UserProfileScreen.kt
            └── AdminProfileScreen.kt
```

---

## 🛠️ Tech Stack & Dependencies

| Category | Library | Version |
| :--- | :--- | :--- |
| Language | Kotlin | — |
| UI | Jetpack Compose BOM | 2025.02.00 |
| UI Components | Material 3 | via BOM |
| Material Icons | Icons Extended | via BOM |
| Navigation | Compose Navigation | 2.9.7 |
| DI | Dagger Hilt | 2.56 |
| DI + Compose | Hilt Navigation Compose | 1.2.0 |
| ViewModel | Lifecycle ViewModel Compose | 2.10.0 |
| Networking | Retrofit | 2.11.0 |
| HTTP Client | OkHttp + Logging Interceptor | 4.12.0 |
| Serialization | Gson | 2.11.0 |
| Date/Time | `java.time` (API 26+) | built-in |
| Desugaring | Android Desugar JDK | enabled |
| Min SDK | Android 7.0 | API 24 |
| Target SDK | — | API 36 |
| Compile SDK | — | API 36 |

---

## 📱 Screens & UI Deep-Dive

### Auth Screens

**LoginScreen / SignupScreen**
- Custom hardcoded auth palette (navy `#0B1B3A`, accent `#1E88E5`) — immune to system dynamic color so branding is always consistent
- Status bar color pinned to auth navy
- Role selector chip (Passenger / Admin) drives both UI and the API role field
- Password show/hide toggle
- Server errors surface as an `AlertDialog` with the exact backend message
- Signup flow automatically signs in after registration — no second manual step

### Home Screen (Flight Search)

- Airport dropdowns populated from live API via `HomeViewModel`; show `CircularProgressIndicator` while loading and a retry button on error
- Each airport mapped to a 3-character display code (first 3 chars of name, uppercase)
- Material 3 `DatePickerDialog` for travel date; defaults to tomorrow
- Date stored as ISO `LocalDate.toString()` (`yyyy-MM-dd`) — consistent with timezone filtering downstream

### Flight Results Screen

**Header**
- Full-width gradient banner (`#071226` → `#0B1B3A` → `#1565C0`) displaying route and date
- Dynamic chip: "Searching…" / "N flights found" / "No flights found" / "Error"

**Flight cards** (`FlightTicketCard`)
- Rotating airline branding (IndiAir, SkyLink, BlueStar, VeloAir, AirDesh) with color-coded circle avatars
- HH:mm times converted to **device local timezone** using `Instant.parse().atZone(ZoneId.systemDefault())`
- Flight duration calculated from the raw ISO timestamps (falls back to HH:mm string arithmetic)
- Seat availability badge: orange warning when ≤ 6 seats left, green otherwise
- Dashed separator line drawn with `Canvas`
- "Select →" button and card itself are both tappable

**Filtering logic** (inside `FlightResultsViewModel`)
```kotlin
all.filter {
    it.departureAirportId == departureAirportId &&
    it.arrivalAirportId   == arrivalAirportId &&
    matchesDate(it.departureTime, date)       // local date comparison
}
```
`matchesDate()` parses the ISO timestamp to a `LocalDate` in the device timezone and compares with the selected date string.

### Flight Detail Screen

- Displays flight number, route, date, departure/arrival times, price per seat, boarding gate
- Stepper to select number of seats (1–9), live total price update
- "Confirm Booking" button calls `BookingViewModel.createBooking(flightId, seats)`
- Success dialog: "Booking Confirmed! ✓" → navigates to My Bookings tab
- Error dialog: shows exact server error message with retry option
- Real-time loading state disables the button and shows a spinner during API call

### My Bookings Screen

**Boarding-pass cards** (`BoardingPassCard`)
- Horizontal gradient header (`#071226` → `#1565C0`) with origin/destination airport codes in `displaySmall` typography
- Tear-off notch effect: two circles cut into the card edges using `background(screenBackground, CircleShape)`
- Dashed perforation line drawn with `Canvas`
- Simulated barcode strip generated deterministically from `bookingId.hashCode()`
- Status chip: green (Booked), red (Cancelled), amber (anything else)

**Tab system** (`TabRow`)
- **Upcoming** tab: flights with `departureTime` after `Instant.now()`, sorted ascending (soonest first)
- **Previous** tab: flights with `departureTime` before `Instant.now()`, sorted descending (most recent first)
- Each tab shows its count: "Upcoming (3)" / "Previous (2)"
- Empty state per tab with icon and contextual message

**Data enrichment** (inside `BookingViewModel.fetchMyBookings()`)
```
BookingItem (from API)      →  join with FlightItem  →  join with AirportItem
    flightId                       flightNumber              depCode (3-char)
    noOfSeats                      departureTime             arrCode (3-char)
    totalCost                      arrivalAirportId
    status
```
All three API calls happen concurrently; joined in-memory to build `BookingUi`.

### User Profile Screen

- Gradient hero header with account icon, real email from JWT, role badge
- List sections: Account (settings, payments, loyalty, preferences, notifications) and Support (help, privacy, about)
- Logout clears token and pops entire back stack back to Login

### Admin Screens

**Admin Main** hosts a bottom navigation with tabs for each entity.

**Each admin entity screen follows the same pattern:**
1. `LaunchedEffect(Unit)` triggers ViewModel load on entry
2. `LazyColumn` of entity cards with Edit / Delete icon buttons
3. Floating Action Button opens a `BottomSheet` or `Dialog` for create/edit
4. Form fields validated before API call
5. Loading/Error/Success states managed by sealed `UiState` classes

**AdminFlightsScreen** additionally:
- Shows departure/arrival times converted to local timezone via `extractTime()` using `Instant.parse().atZone(ZoneId.systemDefault())`
- Dropdown selectors for airplaneId, departureAirportId, arrivalAirportId during flight creation

### Custom Bottom Navigation (UserMainScreen)

Entirely custom — no `BottomNavigationBar` component used:

```
Box (90dp tall)
 ├── Canvas — draws the wave-shaped bar with a concave notch at center
 │           using cubic Bézier curves for smooth S-curve transition
 ├── Left Column  — "Bookings" tab (My Bookings icon)
 ├── FloatingActionButton (60dp) — raised at TopCenter, "Flights" (Home)
 └── Right Column — "Profile" tab
```

The notch is drawn purely with `Path` + `cubicTo()`. A subtle `Color.Black.copy(alpha=0.08f)` shadow pass is drawn before the solid fill for depth.

---

## 🌐 Network Layer

### NetworkModule (Hilt `@Module`)

```
OkHttpClient
  ├── AuthInterceptor   — adds x-access-token header
  ├── HttpLoggingInterceptor — logs requests/responses in DEBUG
  ├── connectTimeout(30s)
  └── readTimeout(30s)

Retrofit
  ├── baseUrl: http://10.0.2.2:3005/   (emulator → host machine gateway)
  ├── GsonConverterFactory
  └── OkHttpClient (above)

Provides:
  ├── AuthApi
  ├── AdminApi
  └── BookingApi
```

### AuthInterceptor

Reads the stored JWT from `TokenManager` and adds it to every outgoing request as:
```
x-access-token: <jwt>
```
Requests proceed normally even if no token is stored (login/signup don't need one).

### Repository Error Handling

All repositories use the same `mapHttpError()` pattern:
```kotlin
fun mapHttpError(response: Response<*>): String {
    return try {
        val json = response.errorBody()?.string()
        JSONObject(json!!).getString("message")
    } catch (_: Exception) {
        "HTTP ${response.code()}: ${response.message()}"
    }
}
```
This ensures the exact backend validation message (e.g., "Flight not found", "Insufficient seats") surfaces in the UI rather than a generic HTTP error.

### Generic API Response Wrappers

```kotlin
data class ApiListResponse<T>(
    val data:    List<T>,
    val success: Boolean,
    val message: String
)
data class ApiObjectResponse<T>(
    val data:    T,
    val success: Boolean,
    val message: String
)
```

---

## 🔐 Authentication & JWT

### Flow

```
User enters credentials
        │
        ▼
AuthRepository.signIn()
        │
        ▼
POST authservice/api/v1/signin
        │
   JWT returned in response.data
        │
        ▼
TokenManager.saveToken(jwt)    ← stored in SharedPreferences
        │
        ▼
AuthInterceptor picks it up automatically on all future requests
```

### TokenManager — JWT Decoding

The JWT payload is decoded **without** signature verification (client-side display only):
```kotlin
fun decodePayload(): JwtPayload? {
    val token = getToken() ?: return null
    val payloadBase64 = token.split(".").getOrNull(1) ?: return null
    val json = Base64.decode(payloadBase64, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
    // Gson deserialize → JwtPayload(id, email, role)
}
```

`JwtPayload` fields used across the app:
- `id` — passed as `userId` in booking creation
- `email` — displayed in profile screens
- `role` — determines which main screen to navigate to after login

### Signup Auto-Login

After a successful signup, `AuthViewModel` immediately calls `signIn()` with the same credentials so the user never has to log in manually after registering.

---

## 🔄 State Management

Every screen follows the same reactive pattern:

```
ViewModel (StateFlow)  →  collectAsState()  →  Composable re-composition
```

### Sealed State Classes

```kotlin
// Auth
sealed class AuthUiState {
    object Idle    : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val role: String) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

// Flight search
sealed class FlightSearchState {
    object Idle, Searching, Empty : FlightSearchState()
    data class Success(val flights: List<FlightItem>) : FlightSearchState()
    data class Error(val message: String) : FlightSearchState()
}

// My bookings
sealed class MyBookingsState {
    object Loading, Empty : MyBookingsState()
    data class Success(val upcoming: List<BookingUi>, val past: List<BookingUi>) : MyBookingsState()
    data class Error(val message: String) : MyBookingsState()
}

// Booking creation
sealed class BookingCreateState {
    object Idle, Loading, Success : BookingCreateState()
    data class Error(val message: String) : BookingCreateState()
}
```

### LaunchedEffect Usage

- `LaunchedEffect(Unit)` — fetch once when screen enters composition (profile, my bookings)
- `LaunchedEffect(departureAirportId, arrivalAirportId, selectedDate)` — re-fetch if any search parameter changes
- `LaunchedEffect(createState)` — react to booking result (show dialog, navigate)

---

## 🧭 Navigation

Single-activity with Compose Navigation. All routes use query-parameter style to support optional args and default values.

### Route Map

```
login
  └── signup?role={role}

login ──(success)──► user-main?tab={tab}
                         ├── tab=0  HomeScreen
                         │     └── flight-results?departureId={}&arrivalId={}&departure={}&arrival={}&date={}
                         │               └── flight-detail?flightId={}&departure={}&arrival={}&date={}
                         │                         &flightNumber={}&departureTime={}&arrivalTime={}&pricePerSeat={}
                         │                               └── (onBookingConfirmed) ──► user-main?tab=1
                         ├── tab=1  MyBookingsScreen
                         └── tab=2  UserProfileScreen

login ──(Admin)──► admin-main
```

### Type Safety

Integer IDs (`flightId`, `departureId`, `arrivalId`, `pricePerSeat`) use `NavType.IntType` to avoid string-to-int conversion bugs. Display strings (`departure`, `arrival`, `flightNumber`, `date`) use `NavType.StringType` with `Uri.encode()` to safely pass special characters through the route URL.

### Back Stack Management

- After login/signup: `popUpTo(Routes.Login) { inclusive = true }` — prevents back-navigating to auth
- After booking confirmed: `popUpTo(Routes.UserMain) { inclusive = false }` — lands on My Bookings without stacking a new UserMain

---

## 🎨 Theme & Design System

### Color Palette

| Token | Hex | Used for |
| :--- | :--- | :--- |
| Navy Dark | `#071226` | Screen backgrounds, gradient starts |
| Navy Mid | `#0B1B3A` | Card headers, gradient mid |
| Sky Blue | `#1E88E5` | Primary actions, active tab, labels |
| Aqua | `#00B3FF` | Tertiary accent |
| Light bg | `#F5F9FF` | Light mode background |
| Auth accent | `#1565C0` | Gradient end, flight card header |
| Admin indigo | `#1A237E` | Admin header gradient end |

### Design Principles Applied

- **Hardcoded palette on key screens** — Auth, FlightResults header, BoardingPass header, and AdminProfile use hardcoded colors so the airline branding is never overridden by Material You dynamic color
- **Elevation via `shadowElevation`** — flight cards (8dp), boarding pass cards (10dp) for physical depth
- **Canvas drawing** — dashed dividers, wave nav bar notch, barcode strip, flight path lines all drawn with `Canvas` for pixel-perfect control
- **`WindowInsets(0)`** on Scaffold where manual padding handling is needed

---

## 🚀 How to Run

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17+
- Android device or emulator with API 24+
- All 5 backend microservices running locally (see the [main project README]([../README.md](https://github.com/rkt0209/Airline-Booking-System-Distributed/blob/main/README.md)))

### Steps

**1. Clone the Android repository**
```bash
git clone https://github.com/rkt0209/airlineManagmentAPP
```

**2. Open in Android Studio**
`File → Open` → select the `airlineManagment/` folder → wait for Gradle sync

**3. Configure the backend URL**

Open `app/src/main/java/com/example/airline/core/network/NetworkModule.kt`:

```kotlin
// Default — works for Android Emulator connecting to your local machine
private const val BASE_URL = "http://10.0.2.2:3005/"

// For a physical device — replace with your machine's local IP:
private const val BASE_URL = "http://192.168.1.X:3005/"
```

> If using a physical device, ensure it is on the **same Wi-Fi network** as your development machine.

**4. Run the backend** (from the main project — all 5 services must be running before launching the app)

**5. Build & Run**
- Select emulator or connected device (API 24+)
- Press **Run ▶** (`Shift + F10`)

### Build a Shareable APK

```
Android Studio → Build → Build Bundle(s) / APK(s) → Build APK(s)
```
Output path:
```
app/build/outputs/apk/debug/app-debug.apk
```

---

## 📄 License

This project is for educational and portfolio purposes.
