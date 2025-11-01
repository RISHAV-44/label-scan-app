# 📦 GitHub Repository Setup Instructions

## Step 1: Create GitHub Repository

1. **Go to GitHub**
    - Visit https://github.com/new
    - Or click the "+" icon → "New repository"

2. **Repository Settings**
    - **Name:** `foodlabel-scanner` (or your preferred name)
    - **Description:** "AI-powered Android app for scanning and analyzing nutrition labels"
    - **Visibility:** Public (or Private if you prefer)
    - ⚠️ **DO NOT** initialize with README, .gitignore, or license (we have these already)

3. **Click "Create repository"**

---

## Step 2: Prepare Your Local Project

### Important: Protect Your Secrets!

Before pushing, make sure these files are **NOT** included (already in `.gitignore`):

- ✅ `google-services.json` (contains Firebase config)
- ✅ `local.properties` (contains SDK path)
- ✅ API keys in `AIModels.kt`

### Create Example Files

1. **Create `app/google-services.json.example`** (already exists)
    - This is a template for others to follow
    - Real `google-services.json` is in `.gitignore`

2. **Create `.env.example`** (for API keys)
   ```bash
   # Copy this to .env and add your actual keys
   GEMINI_API_KEY=your_gemini_api_key_here
   ```

---

## Step 3: Initialize Git & Push

Open terminal in your project directory and run:

### 3.1 Initialize Git (if not already)

```bash
git init
```

### 3.2 Configure Git (first time only)

```bash
git config --global user.name "Your Name"
git config --global user.email "your.email@example.com"
```

### 3.3 Add Remote Repository

Replace `YOUR_USERNAME` with your GitHub username:

```bash
git remote add origin https://github.com/YOUR_USERNAME/foodlabel-scanner.git
```

### 3.4 Check What Will Be Committed

```bash
git status
```

**Verify these are NOT listed:**

- ❌ `google-services.json`
- ❌ `local.properties`
- ❌ `build/` directories
- ❌ `.idea/` files

### 3.5 Stage All Files

```bash
git add .
```

### 3.6 Commit

```bash
git commit -m "Initial commit: FoodLabel Scanner app with AI-powered nutrition analysis"
```

### 3.7 Push to GitHub

```bash
git branch -M main
git push -u origin main
```

---

## Step 4: Update README on GitHub

1. **Replace README.md**
    - Copy content from `GITHUB_README.md`
    - Paste into your GitHub repository's README.md

2. **Update Links**
    - Replace `YOUR_USERNAME` in clone URL
    - Add your actual repository URL

---

## Step 5: Add Topics & Description

On your GitHub repository page:

1. **Click "⚙️" next to "About"**

2. **Add Topics:**
    - `android`
    - `kotlin`
    - `jetpack-compose`
    - `machine-learning`
    - `nutrition`
    - `ocr`
    - `firebase`
    - `google-gemini`
    - `food-scanner`
    - `health-app`

3. **Add Website:** (if you have one)

---

## Step 6: Create Releases (Optional)

### Tag Your First Release

```bash
git tag -a v1.0.0 -m "Initial release - AI nutrition scanner"
git push origin v1.0.0
```

On GitHub:

1. Go to "Releases" → "Create a new release"
2. Select tag: `v1.0.0`
3. Release title: "v1.0.0 - Initial Release"
4. Add release notes
5. Attach APK (optional): `app/build/outputs/apk/release/app-release.apk`

---

## Step 7: Add Required Badges (Optional)

Add to top of README.md:

```markdown
![Build](https://img.shields.io/badge/build-passing-brightgreen)
![Android](https://img.shields.io/badge/Android-7.0%2B-blue)
![License](https://img.shields.io/badge/license-MIT-green)
```

---

## Step 8: Setup Issues & Discussions

### Enable Discussions

1. Go to repository "Settings"
2. Scroll to "Features"
3. Check "Discussions"

### Create Issue Templates

1. Go to "Issues" → "New issue"
2. Click "Set up templates"
3. Add "Bug Report" and "Feature Request" templates

---

## Step 9: Add LICENSE

1. **Create LICENSE file**
   ```bash
   # In project root
   touch LICENSE
   ```

2. **Add MIT License** (example):
   ```
   MIT License
   
   Copyright (c) 2025 [Your Name]
   
   Permission is hereby granted, free of charge, to any person obtaining a copy
   of this software and associated documentation files (the "Software"), to deal
   in the Software without restriction...
   ```

3. **Commit and push:**
   ```bash
   git add LICENSE
   git commit -m "Add MIT license"
   git push
   ```

---

## Step 10: Document Setup for Others

### Create CONTRIBUTING.md

Create a file explaining how others can contribute:

```markdown
# Contributing to FoodLabel Scanner

## Setup for New Contributors

1. Fork the repository
2. Clone your fork: `git clone https://github.com/YOUR_USERNAME/foodlabel-scanner.git`
3. Set up Firebase (see README.md)
4. Add Gemini API key (see README.md)
5. Build: `./gradlew assembleDebug`
6. Create a branch: `git checkout -b feature/my-feature`
7. Make changes and test
8. Commit: `git commit -m "Add my feature"`
9. Push: `git push origin feature/my-feature`
10. Create Pull Request on GitHub
```

---

## ✅ Verification Checklist

Before pushing, verify:

- [ ] `.gitignore` is properly configured
- [ ] `google-services.json` is NOT in git
- [ ] API keys are removed or in `.env`
- [ ] `local.properties` is excluded
- [ ] README.md is comprehensive
- [ ] Build files are excluded
- [ ] All documentation files are included

---

## 🚀 Quick Command Summary

```bash
# Initialize and push
git init
git add .
git commit -m "Initial commit: FoodLabel Scanner"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/foodlabel-scanner.git
git push -u origin main

# Tag release
git tag -a v1.0.0 -m "Initial release"
git push origin v1.0.0
```

---

## 📝 What Gets Pushed to GitHub

✅ **Included:**

- Source code (`app/src/`)
- Gradle configuration
- Documentation (`.md` files)
- `.gitignore`
- Example config files
- UI assets
- README

❌ **Excluded (in .gitignore):**

- `google-services.json` (Firebase config)
- `local.properties` (local paths)
- Build outputs (`build/`, `*.apk`)
- IDE files (`.idea/`)
- API keys and secrets

---

## 🎉 Your Repository is Ready!

Others can now:

1. Clone your repository
2. Set up their own Firebase project
3. Get their own Gemini API key
4. Build and run the app!

**Repository URL:**

```
https://github.com/YOUR_USERNAME/foodlabel-scanner
```

Share this link to let others use your app! 🚀
