# 🚀 Quick Push to GitHub - Copy & Paste Commands

## Prerequisites

- Make sure you've created a GitHub repository at: https://github.com/new
- Name it: `foodlabel-scanner` (or your choice)
- **Don't** initialize with README/gitignore/license

---

## Step 1: Open Terminal/PowerShell

**In Android Studio:**

- View → Tool Windows → Terminal
- Or press `Alt + F12`

**Or use Windows PowerShell:**

- Open PowerShell in your project directory

---

## Step 2: Copy & Paste These Commands

### ⚠️ IMPORTANT: Replace YOUR_USERNAME with your GitHub username!

```bash
# Initialize Git (skip if already done)
git init

# Configure your identity (first time only)
git config user.name "Your Name"
git config user.email "your.email@example.com"

# Check what will be committed (verify no secrets!)
git status

# Add all files
git add .

# Commit
git commit -m "Initial commit: AI-powered nutrition label scanner with Gemini & ML Kit"

# Add GitHub remote (REPLACE YOUR_USERNAME!)
git remote add origin https://github.com/YOUR_USERNAME/foodlabel-scanner.git

# Push to GitHub
git branch -M main
git push -u origin main
```

---

## Step 3: Verify on GitHub

1. Go to: `https://github.com/YOUR_USERNAME/foodlabel-scanner`
2. You should see all your files!

---

## ⚠️ Security Check

Before pushing, make sure these are **NOT** visible in `git status`:

```
❌ google-services.json       (contains Firebase keys)
❌ local.properties           (contains local paths)
❌ build/                     (build files)
❌ .idea/                     (IDE files)
```

If you see any of these, they should be in `.gitignore` already!

---

## 🎉 Success!

Your code is now on GitHub! Others can:

- Clone your repo
- Set up their own Firebase
- Get their own Gemini API key
- Build and run your app!

**Share your repository:**

```
https://github.com/YOUR_USERNAME/foodlabel-scanner
```

---

## Optional: Tag a Release

```bash
# Create version tag
git tag -a v1.0.0 -m "Initial release - AI nutrition scanner"
git push origin v1.0.0
```

Then on GitHub:

1. Go to "Releases"
2. Click "Create a new release"
3. Select tag `v1.0.0`
4. Add release notes
5. Publish!

---

## Troubleshooting

### "Permission denied"

- Make sure you're logged into GitHub
- Try using GitHub Desktop instead
- Or use SSH keys

### "Repository not found"

- Double-check the repository name
- Make sure it's created on GitHub first
- Verify your username is correct

### "Files too large"

- Make sure `.gitignore` is working
- Run `git status` to check what's being committed
- Large files like APKs should be excluded

---

## 🎊 Done!

Your FoodLabel Scanner project is now on GitHub! 🚀
