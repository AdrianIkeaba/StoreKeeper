# StoreKeeper HNG

A modern, lightweight Android app to manage a small store’s inventory. Built with Kotlin and Jetpack Compose, StoreKeeper lets you add, edit, search, and delete products with images, quantities, and pricing — all stored locally using Room.


## Table of Contents
- [Features](#features)
- [Screens](#screens)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Permissions](#permissions)
- [Setup](#setup)
- [Build & Run](#build--run)
- [Project Structure](#project-structure)
- [Notable Implementation Details](#notable-implementation-details)
- [Troubleshooting](#troubleshooting)
- [Links](#links)


## Features
- Inventory management
  - Create, read, update, and delete products
  - Track name, SKU, quantity, price, category, description, and image
- Product images
  - Pick from Gallery or capture from Camera
  - Rotate image clockwise and persist the rotation to disk
  - Lightweight on-device compression for saved images
- Search
  - Fast text search with debounced querying
  - Smooth typing with immediate UI reflection
- Delightful UX
  - Splash screen with subtle animations
  - Graceful loading states (prevents empty-state flashes)
  - Delete confirmation dialogs on Home and Detail screens
  - Snackbars on permission denial for Camera/Gallery access


## Screens
- Splash: Branding + animated loading
- Home: Stats, search bar, product list, and global FAB to add products
- Add/Edit: Form for creating or updating a product, with image picker/camera & rotate
- Detail: Product details with edit and delete actions


## Tech Stack
- Language: Kotlin (JDK 11)
- UI: Jetpack Compose + Material 3
- Navigation: androidx.navigation-compose
- DI: Koin
- Persistence: Room
- Images: Coil
- Permissions: Accompanist Permissions
- Camera: Activity Result API (preview capture) + CameraX dependencies available for future extensions
- Concurrency: Kotlin Coroutines/Flow

Minimum SDK: 28
Target SDK: 36


## Architecture
- Clean-ish MVVM with use cases wrapping the repository
- Layers:
  - data: Room database (Dao + Entity) and repository implementation
  - domain: Model, repository interface, and use cases
  - presentation: ViewModels (StateFlow-based) and Compose screens
- Unidirectional data flow
- State holders (UiState) drive the UI


## Permissions
Declared in AndroidManifest.xml:
- Camera: `android.permission.CAMERA`
- Read External Storage (API ≤ 32): `android.permission.READ_EXTERNAL_STORAGE`
- Read Media Images (API 33+): `android.permission.READ_MEDIA_IMAGES`

Runtime handling in Add/Edit screen:
- Tapping Camera or Gallery requests the corresponding permission if needed
- On grant: automatically proceeds to the intended action
- On denial: shows a Snackbar explaining the permission is required


## Setup
1. Prerequisites
   - Android Studio Iguana or later
   - Android Gradle Plugin compatible with Iguana
   - JDK 11
   - Android SDK 28–36 installed
2. Clone the repository
   ```bash
   git clone https://github.com/your-org/StoreKeeperHNG.git
   cd StoreKeeperHNG
   ```
3. Open the project in Android Studio and let it sync


## Build & Run
- From Android Studio: Run ▶ on an emulator or a device (Android 9 / API 28 or higher)
- From terminal (Gradle Wrapper):
  ```bash
  ./gradlew assembleDebug
  ```


## Project Structure
```
StoreKeeperHNG/
├─ app/
│  ├─ src/main/java/com/ghostdev/storekeeperhng/
│  │  ├─ data/           # Room + repository impl
│  │  ├─ domain/         # Models + repository interfaces + use cases
│  │  ├─ presentation/   # ViewModels + Compose screens + navigation
│  │  └─ util/           # Helpers (e.g., ImageUtils)
│  ├─ src/main/res/      # Resources (drawables, themes, strings)
│  └─ build.gradle.kts
├─ build.gradle.kts
└─ settings.gradle.kts
```

Key files:
- `MainActivity.kt` and `StoreKeeperApp.kt`: App entry and theme
- `presentation/navigation/NavGraph.kt`: Routes and navigation setup
- `presentation/screens/*`: Compose screens (Home, AddEdit, Detail, Splash)
- `presentation/*ViewModel.kt`: ViewModels using StateFlow
- `data/local/*`: Room database, DAO, and entities
- `domain/*`: Repository interface and use cases
- `util/ImageUtils.kt`: Image compression and rotation utilities


## Notable Implementation Details
- Image rotation persistence
  - Rotates and overwrites the original image file using a Matrix
  - Coil cache-busting via a timestamp key ensures the rotated image refreshes immediately in UI
- Search typing fix
  - TextField is bound to a UI `query` StateFlow to reflect keystrokes immediately
  - Debounced search still applies for efficient queries
- Loading UX improvements
  - Grace period keeps a spinner visible during initial load and query changes
  - Prevents brief flashes of the empty state when data is still incoming
- Delete safety
  - Material 3 AlertDialog confirms deletion on Home and Detail screens
- Permissions UX
  - Accompanist Permissions requests at point-of-use; snackbar on denial


## Troubleshooting
- Camera opens but image not saved
  - Ensure app has Camera permission and there is sufficient storage space
- Gallery picker not opening on Android 13+
  - `READ_MEDIA_IMAGES` must be granted; the app will request it as needed
- Images not updating after rotation
  - The app cache-busts Coil requests; if you still see stale images, force-stop and reopen
- Build errors on older toolchains
  - Ensure Android Studio and AGP match the versions defined in `gradle/libs.versions.toml`


## Links
- Release build
  - (Check release tab)
- Demo video
  - 
