# 🥗 FoodLabel Scanner - AI Nutrition Analysis App

<div align="center">

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-0095D5?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)
![Google Gemini](https://img.shields.io/badge/Google%20Gemini-8E75B2?style=for-the-badge&logo=google&logoColor=white)

**A modern Android app that scans nutrition labels and provides instant AI-powered health insights**

[Features](#-features) • [Quick Start](#-quick-start) • [Setup](#-setup) • [Architecture](#-architecture) • [Documentation](#-documentation)

</div>

---

## 📱 About

FoodLabel Scanner is a cutting-edge Android application that leverages AI to help users make
informed dietary choices. Simply point your camera at any nutrition label, and get instant, detailed
nutritional analysis with allergen detection and health insights.

### 🎯 Key Highlights

- 🔍 **Smart OCR** - Google ML Kit extracts text from nutrition labels in under 1 second
- 🤖 **AI Analysis** - Google Gemini provides accurate nutritional data extraction
- 🎨 **Modern UI** - Beautiful Material Design 3 interface built with Jetpack Compose
- 🔒 **Secure** - Firebase Authentication with email/password
- 📊 **History** - Cloud-synced scan history across devices
- ⚡ **Fast** - Complete analysis in 2-3 seconds
- 🛡️ **Privacy-First** - OCR processed on-device

---

## ✨ Features

### Core Features

- 📸 **Camera Integration** - Professional camera interface with CameraX
- 🔤 **On-Device OCR** - ML Kit Text Recognition (works offline)
- 🧠 **AI-Powered Analysis** - Google Gemini extracts structured nutrition data
- 📝 **Detailed Nutrition Info** - Calories, fats, sugars, sodium, protein, fiber
- 🚨 **Allergen Detection** - Identifies common allergens (milk, eggs, peanuts, tree nuts, soy,
  wheat, fish, shellfish)
- ⚠️ **Watchlist Ingredients** - Flags concerning ingredients (Aspartame, Red 40)
- 📱 **Scan History** - View and track all your previous scans
- 🔐 **User Authentication** - Secure login with Firebase Auth
- ☁️ **Cloud Sync** - Scan history synced via Firebase Firestore

### UI/UX

- 🎨 Material Design 3 theming
- 🌙 Beautiful gradient backgrounds
- 📊 Interactive nutrition cards
- 🎯 Health score indicators
- ✅ Smooth animations and transitions
- 📱 Responsive design

---

## 🚀 Quick Start

### Prerequisites

- Android Studio Hedgehog | 2023.1.1 or newer
- Android SDK 24+ (Android 7.0+)
- JDK 17
- Firebase account (free)
- Google Gemini API key (free tier available)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/YOUR_USERNAME/foodlabel-scanner.git
   cd foodlabel-scanner
   ```

2. **Open in Android Studio**
    - Open Android Studio
    - Select "Open an Existing Project"
    - Navigate to the cloned directory

3. **Configure Firebase** (see [Firebase Setup](#firebase-setup) below)

4. **Add Gemini API Key**
    - Open `app/src/main/java/com/runanywhere/startup_hackathon20/AIModels.kt`
    - Replace line 136 with your API key:
      ```kotlin
      apiKey = "YOUR_GEMINI_API_KEY_HERE"
      ```

5. **Build and Run**
   ```bash
   ./gradlew assembleDebug
   ```
   Or click the green ▶️ button in Android Studio

---

## 🔧 Setup

### Firebase Setup

1. **Create Firebase Project**
    - Go to [Firebase Console](https://console.firebase.google.com/)
    - Click "Add project"
    - Follow the setup wizard

2. **Add Android App**
    - In Firebase Console, click "Add app" → Android
    - Package name: `com.runanywhere.startup_hackathon20`
    - Download `google-services.json`

3. **Configure Firebase**
    - Place `google-services.json` in `app/` directory
    - Enable Authentication (Email/Password)
    - Create Firestore database (start in test mode)

4. **Firestore Collections**

   The app will automatically create these collections:
    - `users` - User profiles
    - `scans` - Scan history

### Google Gemini API Setup

1. **Get API Key**
    - Visit [Google AI Studio](https://makersuite.google.com/app/apikey)
    - Click "Create API Key"
    - Copy the key

2. **Add to Project**
    - Open `AIModels.kt`
    - Line 136: Replace with your key
   ```kotlin
   apiKey = "AIzaSy_YOUR_ACTUAL_KEY_HERE"
   ```

3. **Free Tier Limits**
    - 60 requests per minute
    - 1,500 requests per day
    - Perfect for personal use!

---

## 🏗️ Architecture

### Tech Stack

| Component | Technology |
|-----------|-----------|
| **UI Framework** | Jetpack Compose |
| **Language** | Kotlin |
| **Architecture** | MVVM (Model-View-ViewModel) |
| **Dependency Injection** | Manual (lightweight) |
| **Navigation** | Jetpack Navigation Compose |
| **Camera** | CameraX |
| **OCR** | Google ML Kit Text Recognition |
| **AI Analysis** | Google Gemini API |
| **Authentication** | Firebase Auth |
| **Database** | Firebase Firestore |
| **Networking** | Ktor (for Gemini SDK) |

### Project Structure

```
app/
├── src/main/java/com/runanywhere/startup_hackathon20/
│   ├── data/
│   │   ├── AuthRepository.kt         # Firebase Auth
│   │   └── ScanHistoryRepository.kt  # Firestore operations
│   ├── models/
│   │   ├── ScanResult.kt            # Nutrition data model
│   │   └── User.kt                  # User model
│   ├── screens/
│   │   ├── LoginScreen.kt           # Authentication UI
│   │   ├── HomeScreen.kt            # Main screen with scan history
│   │   ├── CameraScreen.kt          # Camera capture
│   │   ├── ScanScreen.kt            # Processing/loading
│   │   └── ResultsScreen.kt         # Nutrition display
│   ├── viewmodels/
│   │   ├── AuthViewModel.kt         # Auth state management
│   │   ├── HomeViewModel.kt         # Home screen logic
│   │   └── ScanViewModel.kt         # Scan processing
│   ├── ui/theme/                    # Material Design theming
│   ├── AIModels.kt                  # OCR + AI analysis logic
│   ├── MyApplication.kt             # App initialization
│   └── MainActivity.kt              # Main activity + navigation
```

### Data Flow

```
User Captures Image
    ↓
CameraScreen → Bitmap
    ↓
ScanViewModel.processImage()
    ↓
AIModels.performOcrInference()
    ↓ (ML Kit OCR - 1 sec)
Extracted Text
    ↓
AIModels.performLlmInference()
    ↓ (Google Gemini - 2 sec)
Structured JSON
    ↓
ScanResult Model
    ↓
Save to Firestore
    ↓
ResultsScreen → Display
```

---

## 📚 Documentation

Comprehensive guides are available in the repository:

- **[QUICK_START.md](QUICK_START.md)** - 5-minute setup guide
- **[GEMINI_SOLUTION.md](GEMINI_SOLUTION.md)** - Gemini API integration details
- **[FIREBASE_DATABASE_SETUP.md](FIREBASE_DATABASE_SETUP.md)** - Firebase configuration
- **[DESIGN_SYSTEM.md](DESIGN_SYSTEM.md)** - UI/UX design guidelines
- **[FINAL_FIX_KTOR_ENGINE.md](FINAL_FIX_KTOR_ENGINE.md)** - Technical implementation notes

---

## 🎨 Screenshots

<div align="center">
<table>
  <tr>
    <td><b>Login</b></td>
    <td><b>Home</b></td>
    <td><b>Camera</b></td>
    <td><b>Results</b></td>
  </tr>
  <tr>
    <td><i>Beautiful gradient login</i></td>
    <td><i>Scan history cards</i></td>
    <td><i>Professional camera UI</i></td>
    <td><i>Detailed nutrition info</i></td>
  </tr>
</table>
</div>

*(Add screenshots here after deployment)*

---

## 🔒 Privacy & Security

- ✅ **On-Device OCR** - Image processing happens locally
- ✅ **Secure Authentication** - Firebase Auth with industry standards
- ✅ **Cloud Storage** - Only text data stored (not images)
- ✅ **No Tracking** - No analytics or third-party trackers
- ✅ **User Control** - Users can delete their scan history

---

## 🛠️ Development

### Build Variants

```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Run tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest
```

### Dependencies

Key dependencies (see `app/build.gradle.kts` for complete list):

```kotlin
// Jetpack Compose
implementation("androidx.compose.ui:ui:1.5.4")
implementation("androidx.compose.material3:material3:1.1.2")

// CameraX
implementation("androidx.camera:camera-camera2:1.3.1")

// ML Kit
implementation("com.google.mlkit:text-recognition:16.0.0")

// Google Gemini
implementation("com.google.ai.client.generativeai:generativeai:0.1.2")

// Firebase
implementation("com.google.firebase:firebase-auth-ktx:22.3.1")
implementation("com.google.firebase:firebase-firestore-ktx:24.10.1")
```

---

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

---

## 📝 License

This project is open source and available under the [MIT License](LICENSE).

---

## 🙏 Acknowledgments

- [Google ML Kit](https://developers.google.com/ml-kit) - On-device text recognition
- [Google Gemini](https://ai.google.dev/) - AI-powered analysis
- [Firebase](https://firebase.google.com/) - Backend infrastructure
- [Jetpack Compose](https://developer.android.com/jetpack/compose) - Modern Android UI

---

## 📧 Contact

For questions or support, please open an issue on GitHub.

---

<div align="center">

**Made with ❤️ for healthier eating**

⭐ Star this repo if you find it useful!

</div>
