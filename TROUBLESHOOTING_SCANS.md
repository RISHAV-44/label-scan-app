# Troubleshooting: Recent Scans Not Showing Up

## ✅ What I Fixed

I've added comprehensive debug logging and Firebase initialization to help diagnose and fix the
issue.

### **Changes Made:**

1. **Firebase Initialization** (`MyApplication.kt`)
    - Added explicit `FirebaseApp.initializeApp()` call
    - Ensures Firebase is ready before the app tries to use it

2. **Debug Logging** (Added to all key files)
    - `ScanViewModel`: Tracks scan processing and saving
    - `ScanHistoryRepository`: Tracks Firestore operations
    - `HomeViewModel`: Tracks scan loading

---

## 🔍 How to Debug

### **Step 1: Check Logcat**

After rebuilding and running the app, open **Logcat** in Android Studio and filter by these tags:

```
Tag: MyApp
Tag: ScanViewModel
Tag: ScanHistoryRepo
Tag: HomeViewModel
```

### **Step 2: Expected Log Flow**

**When App Starts:**

```
MyApp: Firebase initialized successfully
MyApp: SDK initialized successfully
```

**When User Logs In:**

```
HomeViewModel: Loading scans for userId: abc123xyz
ScanHistoryRepo: Fetching scans for user: abc123xyz
ScanHistoryRepo: Found X scans
HomeViewModel: Loaded X scans successfully
```

**When User Scans a Label:**

```
ScanViewModel: Starting scan processing for userId: abc123xyz
ScanViewModel: OCR extracted text length: 1234
ScanViewModel: LLM result: {...}
ScanViewModel: Parsed result: ScanResult(...)
ScanViewModel: Attempting to save scan to Firestore...
ScanHistoryRepo: Saving scan for user: abc123xyz
ScanHistoryRepo: Scan data prepared: {...}
ScanHistoryRepo: Scan saved with ID: xyz789
ScanViewModel: Scan saved successfully with ID: xyz789
```

---

## ⚠️ Common Issues & Solutions

### **Issue 1: Firebase Not Enabled**

**Symptoms:**

- Logs show: `Failed to save scan: PERMISSION_DENIED`
- Recent scans stay empty

**Solution:**

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Select project: "food-label-scanner-7f5f8"
3. Go to **Firestore Database**
4. If not enabled: Click "Create database"
5. Select **"Start in test mode"**
6. Click **Enable**

### **Issue 2: Authentication Not Enabled**

**Symptoms:**

- Can't login/signup
- Logs show authentication errors

**Solution:**

1. Go to Firebase Console
2. Go to **Authentication** → **Sign-in method**
3. Click **Email/Password**
4. Toggle **Enable**
5. Click **Save**

### **Issue 3: User Not Logged In**

**Symptoms:**

- Logs show: `User not logged in, scan not saved to Firestore`
- userId is null

**Solution:**

- Make sure you've signed up or logged in
- Check if login is successful
- Verify AuthViewModel is managing state correctly

### **Issue 4: No Internet Connection**

**Symptoms:**

- Logs show: `Failed to fetch scans: UNAVAILABLE`
- Firestore can't connect

**Solution:**

- Check device/emulator has internet
- Verify `INTERNET` permission in AndroidManifest.xml (already added)
- Try restarting the emulator

### **Issue 5: Firestore Rules Too Strict**

**Symptoms:**

- Logs show: `PERMISSION_DENIED`
- Test mode expired or rules changed

**Solution:**

1. Go to Firebase Console → Firestore Database → **Rules**
2. For development, use these rules:

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /{document=**} {
      allow read, write: if request.time < timestamp.date(2025, 12, 31);
    }
  }
}
```

3. Click **Publish**

### **Issue 6: Scans Not Refreshing**

**Symptoms:**

- Scans saved but home screen doesn't update
- Need to restart app to see scans

**Solution:**

- After scanning, tap "Home" button to return to home screen
- Home screen will reload scans automatically
- Check HomeViewModel logs for loading confirmation

---

## 🧪 Testing Checklist

After rebuilding, test in this order:

1. **[ ]** Launch app → Check logs for Firebase initialization
2. **[ ]** Sign up with new account → Check logs for user creation
3. **[ ]** Check home screen → Should see "No scans yet"
4. **[ ]** Tap "Scan Label" → Camera opens
5. **[ ]** Take photo → Check logs for OCR/LLM processing
6. **[ ]** View results → Check logs for Firestore save
7. **[ ]** Tap "Home" → Check logs for scan loading
8. **[ ]** Verify scan appears in Recent Scans list
9. **[ ]** Try deleting scan → Check logs for deletion
10. **[ ]** Logout and login again → Scans should still be there

---

## 📊 What to Look For in Firestore Console

1. Go to Firebase Console → **Firestore Database**
2. You should see a collection named: `scans`
3. Each document should have:
    - `userId`: String (your Firebase Auth UID)
    - `productName`: String
    - `calories`, `sugar`, `sodium`, etc.: Numbers
    - `allergens`, `watchlistIngredients`: Arrays
    - `timestamp`: Timestamp

---

## 🚨 If Still Not Working

**Check these in order:**

1. **Verify google-services.json is in place**
    - Location: `app/google-services.json`
    - Package name matches: `com.runanywhere.startup_hackathon20`

2. **Clean and rebuild**
   ```bash
   ./gradlew clean assembleDebug
   ```

3. **Check Gradle sync**
    - Make sure no Gradle errors
    - Firebase dependencies properly loaded

4. **Restart emulator/device**
    - Sometimes Firebase needs a fresh start

5. **Check Android Studio Logcat**
    - Look for any red error messages
    - Filter by "Firebase" to see Firebase-specific logs

---

## 📱 Build & Test

```bash
cd C:/Users/User/StudioProjects/Hackss
./gradlew assembleDebug
./gradlew installDebug
```

Then check Logcat for the debug messages!

---

## ✅ Success Indicators

You'll know it's working when you see:

1. ✅ "Firebase initialized successfully" in logs
2. ✅ "Scan saved successfully with ID: xyz" after scanning
3. ✅ "Loaded X scans successfully" on home screen
4. ✅ Scan cards visible in the Recent Scans section
5. ✅ Scans persist after logout/login

---

If you still have issues after checking all of the above, share the Logcat output and I can help
debug further!
