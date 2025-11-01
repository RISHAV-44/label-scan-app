# 🥗 FoodLabel Scanner App

A modern Android nutrition label scanner app built with Jetpack Compose and on-device AI.

## ✨ Features

- 📸 **Smart Camera** - Professional nutrition label scanning
- 🤖 **Two-Step AI Pipeline** - ML Kit OCR + Local LLM for complete privacy
- 🔒 **100% Private** - All processing happens on your device
- 📊 **Detailed Nutrition** - Calories, macros, allergens, watchlist ingredients
- 📱 **Beautiful Modern UI** - Material Design 3 with gradients and animations
- 🔥 **Firebase Integration** - User authentication and scan history
- 🌙 **Offline Capable** - Works without internet after initial setup

## 🤖 How AI Analysis Works

### Two-Step Pipeline

1. **Step 1: OCR (Image → Text)**
    - **Google ML Kit** extracts text from photos
    - ✅ On-device, free, fast (< 1 second)
    - ✅ No API keys needed
    - ✅ Works offline immediately

2. **Step 2: LLM (Text → JSON)**
    - **Local AI model** structures text into nutrition data
    - ✅ Complete privacy (data never leaves device)
    - ✅ Works offline after model download
    - ⚠️ Requires one-time model download (374 MB)

**See [HOW_IT_WORKS.md](HOW_IT_WORKS.md) for detailed explanation**

## 🚀 Quick Start

### Prerequisites

- Android Studio Hedgehog or newer
- Android device/emulator with API 24+ (Android 7.0+)
- 2+ GB RAM recommended for AI models
- 500+ MB free storage for model download

### Setup

1. **Clone the repository**
   ```bash
   git clone <your-repo-url>
   cd Hackss
   ```

2. **Open in Android Studio**
    - File → Open → Select the project folder
    - Wait for Gradle sync to complete

3. **Configure Firebase** (for authentication and scan history)
    - Create a Firebase project at https://console.firebase.google.com
    - Download `google-services.json`
    - Place it in `app/` directory
    - Enable Email/Password authentication in Firebase Console

4. **Build and run**
   ```bash
   ./gradlew assembleDebug
   adb install app/build/outputs/apk/debug/app-debug.apk
   ```

### First Launch

On first launch, the app will automatically download the AI model (Qwen 2.5 0.5B - 374 MB) in the
background.

**Watch progress:**

```bash
adb logcat | grep "MyApp"
```

**Look for:**

- `🚀 Starting auto-download of AI model...`
- `📥 Download progress: 10%, 20%, ...`
- `✅ SUCCESS! Model loaded and ready to use!`

## 🎯 Current Status

### ✅ What's Working

- [x] Beautiful modern UI with gradients and animations
- [x] Firebase authentication (login/signup)
- [x] Camera with professional viewfinder
- [x] **ML Kit OCR** - Real text extraction from images
- [x] Local LLM integration with RunAnywhere SDK
- [x] Auto-download configuration for Qwen 2.5 0.5B
- [x] Scan history with Firebase Firestore
- [x] Nutrition display with color-coded badges
- [x] Error handling and fallbacks

### ⚠️ What Needs Testing

- [ ] Verify auto-download works on first launch
- [ ] Test real nutrition label scanning
- [ ] Verify LLM accuracy with various labels
- [ ] Test offline capability after model download

## 📱 App Screens

1. **Login/Signup** - Firebase authentication with beautiful gradient UI
2. **Home** - Welcome card + prominent scan button + scan history
3. **Camera** - Professional viewfinder with capture button
4. **Loading** - Animated screen with rotating tips
5. **Results** - Detailed nutrition info with color-coded badges

## 🎨 Design System

- **Primary Color**: Fresh Green (#4CAF50)
- **Secondary Color**: Warm Orange (#FF9800)
- **Nutrition Colors**: Yellow (calories), Purple (protein), Pink (sugar), Blue (sodium)
- **Typography**: Poppins font family
- **Animations**: Smooth transitions, pulsing effects, card animations
- **Spacing**: Consistent 4dp grid system

**See [DESIGN_SYSTEM.md](DESIGN_SYSTEM.md) for complete design documentation**

## 🔧 Technology Stack

### Frontend
- **Jetpack Compose** - Modern declarative UI
- **Material Design 3** - Latest design system
- **Navigation Compose** - Type-safe navigation
- **CameraX** - Modern camera API

### AI/ML

- **Google ML Kit** - On-device OCR (text recognition)
- **RunAnywhere SDK** - Local LLM inference
- **Qwen 2.5 0.5B** - Lightweight but capable language model

### Backend

- **Firebase Authentication** - User management
- **Firebase Firestore** - Scan history database
- **Local Storage** - Model caching and offline support

## 📁 Project Structure

```
app/
├── src/main/java/com/runanywhere/startup_hackathon20/
│   ├── screens/          # Composable screens
│   │   ├── LoginScreen.kt
│   │   ├── HomeScreen.kt
│   │   ├── CameraScreen.kt
│   │   ├── LoadingScreen.kt
│   │   └── ResultsScreen.kt
│   ├── viewmodels/       # Business logic
│   │   ├── ScanViewModel.kt
│   │   └── AuthViewModel.kt
│   ├── data/             # Data layer
│   │   ├── ScanResult.kt
│   │   └── ScanHistoryRepository.kt
│   ├── ui/theme/         # Design system
│   │   ├── Color.kt
│   │   ├── Theme.kt
│   │   └── Type.kt
│   ├── AIModels.kt       # Two-step AI pipeline
│   ├── MyApplication.kt  # SDK initialization + auto-download
│   └── MainActivity.kt   # App entry point
```

## 🔍 Troubleshooting

### App shows "Sample Product - Please Download AI Model"

**Cause:** No LLM model is loaded yet

**Solutions:**

1. Wait for auto-download to complete (check logcat)
2. Restart the app to trigger auto-load
3. Check device has 500+ MB free storage
4. Verify internet connection for initial download

### "No text detected in image"

**Cause:** ML Kit couldn't extract text from the photo

**Solutions:**

- Ensure good lighting
- Hold camera steady (avoid blur)
- Frame the nutrition label clearly
- Avoid glare and shadows
- Clean camera lens

### Model downloaded but not analyzing

**Solutions:**

1. Check logcat for loading errors
2. Restart app to trigger auto-load
3. Verify device has 2-3 GB free RAM
4. Check model file isn't corrupted

**See [HOW_IT_WORKS.md](HOW_IT_WORKS.md) for detailed troubleshooting**

## 🎯 Model Options

| Model               | Size   | Speed     | Quality   | Recommended For    |
|---------------------|--------|-----------|-----------|--------------------|
| **Qwen 2.5 0.5B** ⭐ | 374 MB | Fast      | Good      | **Default choice** |
| SmolLM2 360M        | 119 MB | Very Fast | Fair      | Testing            |
| Llama 3.2 1B        | 815 MB | Medium    | Excellent | Better accuracy    |
| Nanonets OCR2 3B    | 4.2 GB | Slow      | Best      | High-end devices   |
| Meta Llama 3 8B     | 4.8 GB | Very Slow | Best      | High-end devices   |

The app is configured to auto-download **Qwen 2.5 0.5B** by default.

## 📖 Documentation

- **[HOW_IT_WORKS.md](HOW_IT_WORKS.md)** - Complete AI pipeline explanation
- **[DESIGN_SYSTEM.md](DESIGN_SYSTEM.md)** - Complete design documentation
- **[LOCAL_AI_SETUP_GUIDE.md](LOCAL_AI_SETUP_GUIDE.md)** - Advanced AI setup

## 🎉 What Makes This Special

Unlike commercial nutrition scanner apps:

- ✅ **Complete Privacy** - No data sent to cloud
- ✅ **Works Offline** - After initial model download
- ✅ **No Subscriptions** - Free forever
- ✅ **No API Keys** - No external services required
- ✅ **Open Source** - Full transparency
- ✅ **On-Device AI** - Fast and private

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🤝 Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## 📧 Contact

For questions or support, please open an issue in the repository.

---

**Built with ❤️ using Jetpack Compose and on-device AI**
