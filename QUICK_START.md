# 🚀 Quick Start Guide

## Get Your App Running in 5 Minutes!

### 1. Build the App

```bash
./gradlew assembleDebug
```

### 2. Install on Device

```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

### 3. Watch Logcat (Optional)

Open a second terminal to monitor what's happening:

```bash
adb logcat | grep -E "(MyApp|AIModels)"
```

### 4. Test the App

1. **Open app** → Login/Signup screen
2. **Create account** or login
3. **Tap "Scan Label"** → Camera opens
4. **Point at nutrition label** and capture
5. **Wait for results** → See nutrition info!

---

## What to Expect

### First Launch

- App starts downloading AI model (374 MB) in background
- Check logcat for progress: `🚀 Starting auto-download...`
- Takes 2-5 minutes on WiFi

### First Scan (Before Model Downloads)

```
✅ Camera works
✅ ML Kit OCR extracts text
⚠️ Shows "Sample Product - Please Download AI Model"
```

**This is normal!** The OCR works immediately, but the LLM needs to download first.

### After Model Downloads

```
✅ Camera works
✅ ML Kit OCR extracts text
✅ Local LLM analyzes text
✅ Shows REAL nutrition data! 🎉
```

---

## Troubleshooting

### "Sample Product" showing?

**Cause:** LLM model not downloaded yet

**Fix:**

- Wait 2-5 minutes for download
- Check logcat: `adb logcat | grep "MyApp"`
- Restart app after download completes

### "No text detected"?

**Cause:** ML Kit couldn't read the image

**Fix:**

- Better lighting
- Hold camera steady
- Frame label clearly
- Avoid glare/shadows

---

## Testing Checklist

- [ ] App installs successfully
- [ ] Can create account/login
- [ ] Camera opens
- [ ] Can capture image
- [ ] ML Kit extracts text (check logcat)
- [ ] Shows results (sample or real)
- [ ] Can view scan history

---

## Verify Everything Works

### Check OCR (Works Immediately)

```bash
adb logcat | grep "ML Kit"
```

**Look for:**

- `Starting ML Kit OCR on captured image...`
- `ML Kit OCR completed. Extracted X characters`

### Check LLM (After Model Download)

```bash
adb logcat | grep "LLM"
```

**Look for:**

- `Starting local LLM inference...`
- `LLM response received`

### Check Auto-Download

```bash
adb logcat | grep "MyApp"
```

**Look for:**

- `🚀 Starting auto-download of AI model...`
- `📥 Download progress: 10%, 20%...`
- `✅ SUCCESS! Model loaded and ready to use!`

---

## Quick Reference

| What | Where | Command |
|------|-------|---------|
| Build | Terminal | `./gradlew assembleDebug` |
| Install | Terminal | `adb install app/build/.../app-debug.apk` |
| Watch logs | Terminal | `adb logcat \| grep "MyApp"` |
| Test scan | App | Login → Scan Label → Capture |

---

## Next Steps

1. ✅ Build and install
2. ✅ Test basic functionality
3. ⚠️ Wait for model download
4. ✅ Test with real nutrition labels
5. ✅ Enjoy your privacy-first scanner! 🎉

---

**For detailed docs, see:**

- [HOW_IT_WORKS.md](HOW_IT_WORKS.md) - Complete pipeline explanation
- [FINAL_FIX_SUMMARY.md](FINAL_FIX_SUMMARY.md) - What was fixed
- [README.md](README.md) - Full documentation