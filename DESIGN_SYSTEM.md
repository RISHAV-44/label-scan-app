# 🎨 FoodLabel Scanner - Design System

## Overview

This document outlines the complete design system implemented in the FoodLabel Scanner app,
including color palette, typography, spacing, components, and screen-specific designs inspired by
modern food scanner apps like Fig and Yuka.

## 🎯 Design Philosophy

### Core Principles

1. **Clean & Minimal** - Focus on essential information without clutter
2. **Trust & Transparency** - Clear nutrition information presentation
3. **Accessibility** - Easy-to-read text, clear icons, color-coded alerts
4. **Delight** - Smooth animations and engaging interactions
5. **Modern** - Material Design 3 with custom enhancements

### Visual Language

- **Gradients** - Soft, modern gradients for headers and highlights
- **Rounded Corners** - Consistent 12-24dp radius for friendly feel
- **Elevation** - Subtle shadows (2-12dp) for depth
- **White Space** - Generous padding (16-32dp) for breathing room
- **Color Coding** - Intuitive colors for nutrition and alerts

## 🎨 Color Palette

### Primary Colors

```kotlin
// Health & Nutrition
Fresh Green: #4CAF50
Fresh Green Light: #66BB6A
Fresh Green Dark: #388E3C
```

### Secondary Colors

```kotlin
// Energy & Action
Warm Orange: #FF9800
Warm Orange Light: #FFB74D
Warm Orange Dark: #F57C00
```

### Background & Surface

```kotlin
Background White: #FAFAFA
Surface White: #FFFFFF
Card Gray: #F5F5F5
```

### Text Colors

```kotlin
Text Primary: #212121      // Main content
Text Secondary: #757575    // Supporting text
Text Tertiary: #9E9E9E     // Hints, metadata
```

### Nutrition Colors

```kotlin
Calories Yellow: #FFB74D   // Energy
Protein Purple: #7B1FA2    // Building blocks
Carbs Blue: #1976D2        // Fuel
Fat Orange: #EF6C00        // Lipids
Sugar Pink: #C2185B        // Sweetness
```

### Alert Colors

```kotlin
// Warnings & Errors
Warning Red: #D32F2F
Warning Red Light: #FFEBEE

// Alerts & Caution
Alert Orange: #EF6C00
Alert Orange Light: #FFF3E0

// Success & Positive
Success Green: #4CAF50
Success Green Light: #E8F5E9
```

### Additional UI Colors

```kotlin
Divider Gray: #E0E0E0
Shadow Gray: #1A000000 (10% black)
```

## 📐 Spacing System

```kotlin
// Base unit: 4dp
XXS: 4.dp
XS: 8.dp
S: 12.dp
M: 16.dp
L: 20.dp
XL: 24.dp
XXL: 32.dp
XXXL: 48.dp
```

## 🔤 Typography

### Font Weights

- **Regular**: 400
- **Medium**: 500
- **SemiBold**: 600
- **Bold**: 700

### Type Scale

```kotlin
// Headings
H1: 38.sp, Bold           // App name (login)
H2: 28.sp, Bold           // Screen titles
H3: 22.sp, Bold           // Section headers
H4: 20.sp, SemiBold       // Card titles

// Body
Body Large: 17.sp, Medium
Body: 15.sp, Regular
Body Small: 14.sp, Regular

// Labels
Label Large: 16.sp, Medium
Label: 14.sp, Medium
Label Small: 13.sp, Medium

// Caption
Caption: 12.sp, Regular
```

## 🎭 Component Library

### Cards

#### Standard Card

```kotlin
shape: RoundedCornerShape(20.dp)
color: Surface White
shadowElevation: 4.dp
padding: 16-20.dp
```

#### Gradient Card (Hero)

```kotlin
shape: RoundedCornerShape(24.dp)
gradient: LinearGradient(Fresh Green → Fresh Green Light)
shadowElevation: 8.dp
padding: 28.dp
```

#### Macro Nutrient Card

```kotlin
shape: RoundedCornerShape(20.dp)
gradient: Custom per nutrient
shadowElevation: 4.dp
size: Flexible (weight-based)
padding: 20.dp
```

### Buttons

#### Primary Button

```kotlin
height: 58-80.dp
shape: RoundedCornerShape(14-20.dp)
color: Warm Orange
elevation: 4-8.dp
fontSize: 17-22.sp
fontWeight: Bold
```

#### Outlined Button

```kotlin
height: 58.dp
shape: RoundedCornerShape(16.dp)
border: 2.dp, Fresh Green
color: Transparent
```

#### Icon Button

```kotlin
size: 40.dp
shape: CircleShape
color: Card Gray
elevation: 0.dp
```

### Input Fields

#### Text Field

```kotlin
shape: RoundedCornerShape(14.dp)
border: 1.dp (Divider Gray → Fresh Green)
padding: 16.dp
fontSize: 16.sp
leadingIcon: 24.dp
```

### Badges

#### Nutrition Badge

```kotlin
shape: RoundedCornerShape(10.dp)
padding: horizontal 10.dp, vertical 6.dp
fontSize: 13.sp (value), 11.sp (unit)
color: Custom per nutrient
```

## 📱 Screen-Specific Design

### Login Screen

**Layout:**

- Full-screen gradient background
- Centered vertical layout
- Logo (120dp circle, 30dp corners)
- Title stack (38sp + 20sp)
- Auth card (28dp corners, 28dp padding)
- Segmented control (48dp height, 14dp corners)
- Input fields (14dp corners)
- Primary button (58dp height)
- Footer text

**Colors:**

- Background: Vertical gradient (Fresh Green → Fresh Green Light → #81C784)
- Card: White
- Buttons: Fresh Green (tabs), Warm Orange (submit)

### Home Screen

**Layout:**

- Top bar with logo and logout
- Hero card (24dp corners, 28dp padding)
- Large scan button (80dp height, 20dp corners)
- Section header with count badge
- Scan history list (20dp corner cards)
- Bottom spacing

**Components:**

- Hero card with gradient
- Scan button with icon + text
- History cards with nutrition badges
- Empty state with emoji + text

**Colors:**

- Background: Background White
- Hero: Gradient (Fresh Green → Fresh Green Light)
- Scan button: Warm Orange
- Cards: Surface White

### Camera Screen

**Layout:**

- Full-screen camera preview
- Dark overlay (60% opacity)
- Scan frame (85% width, 45% height)
- Corner accents (40dp, 8dp thick)
- Top instruction card
- Tip badge
- Bottom capture button (80dp)

**Visual Elements:**

- Animated gradient border (6dp stroke)
- Semi-transparent overlays
- Corner highlights
- Large circular capture button

**Colors:**

- Overlay: Black 60%
- Border: Gradient (Fresh Green animation)
- Accents: Fresh Green
- Button: Warm Orange on White

### Results Screen

**Layout:**

- Top bar with back button
- Product header card (24dp corners)
- Macro grid (2 columns)
- Section header
- Nutrient detail cards (18dp corners)
- Allergen warnings (20dp corners)
- Action buttons (58dp height)

**Components:**

- Gradient header card
- Macro cards with gradients
- Detail cards with icons
- Warning cards with colored backgrounds
- Button row (Home + Scan Again)

**Colors:**

- Header: Gradient (Fresh Green → Fresh Green Light)
- Warnings: Warning Red Light
- Alerts: Alert Orange Light
- Positive: Success Green Light

### Loading Screen

**Layout:**

- Centered vertical layout
- Animated icon (140dp, pulsing)
- Progress indicator (60dp)
- Title (26sp)
- Rotating message card
- Info text

**Animations:**

- Icon scale (0.9 → 1.1, 1s loop)
- Icon alpha (0.5 → 1.0, 0.8s loop)
- Message rotation (2s interval)

**Colors:**

- Background: Gradient (Background White → Surface White)
- Icon bg: Success Green Light
- Message card: Card Gray

## 🎬 Animations & Transitions

### Timing

```kotlin
Quick: 150ms
Standard: 300ms
Slow: 500ms
Pulsing: 800-1000ms
```

### Easing

- Enter: EaseOut
- Exit: EaseIn
- Shared: EaseInOut
- Pulsing: Linear

### Effects

- **Scale**: Buttons on press (0.95)
- **Alpha**: Fade in/out
- **Slide**: Screen transitions
- **Pulse**: Loading indicators
- **Bounce**: Success feedback

## 📊 Layout Guidelines

### Grid System

- Margins: 20dp
- Gutters: 12-16dp
- Columns: Flexible (weight-based)

### Safe Areas

- Top: Status bar + 16dp
- Bottom: 24-56dp
- Sides: 20dp

### Card Spacing

- Between cards: 12dp
- Card padding: 16-28dp
- Section spacing: 24-36dp

## ♿ Accessibility

### Touch Targets

- Minimum: 48dp × 48dp
- Buttons: 58-80dp height
- Icons: 40-48dp touch area

### Contrast Ratios

- Normal text: 4.5:1 minimum
- Large text: 3:1 minimum
- Icons: 3:1 minimum

### Text Sizes

- Minimum body: 14sp
- Recommended body: 15-16sp
- Headers: 20sp+

## 🎯 Design Inspiration

This design system draws inspiration from:

- **Fig App** - Clean nutrition display, color-coded badges
- **Yuka App** - Simple scan interface, health scores
- **Material Design 3** - Modern components, elevation
- **iOS Health** - Gradient cards, smooth animations
- **Google Fit** - Macro visualization, activity rings

## 📝 Usage Notes

### When to Use What

**Gradients:**

- Hero sections
- Headers
- Important CTAs
- Macro cards

**Flat Colors:**

- Body content
- Lists
- Inputs
- Secondary buttons

**Shadows:**

- Cards: 2-4dp
- Elevated cards: 8dp
- Buttons: 4-12dp
- Overlays: 16dp+

**Animations:**

- Loading: Continuous
- Transitions: Quick (150-300ms)
- Feedback: Immediate
- Decorative: Subtle

## 🔄 Maintenance

### Adding New Colors

1. Add to `Color.kt`
2. Document purpose
3. Update this file
4. Test contrast ratios

### Adding New Components

1. Create reusable Composable
2. Document parameters
3. Add to style guide
4. Include usage examples

### Updating Existing Styles

1. Update component definition
2. Test across all screens
3. Update documentation
4. Review accessibility

---

**Last Updated:** November 2025  
**Design Version:** 1.0  
**Platform:** Android (Jetpack Compose)
