package com.runanywhere.startup_hackathon20.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runanywhere.startup_hackathon20.models.ScanResult
import com.runanywhere.startup_hackathon20.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreen(
    scanResult: ScanResult,
    onScanAnother: () -> Unit,
    onReturnHome: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "📊",
                            fontSize = 24.sp
                        )
                        Text(
                            text = "Nutrition Analysis",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onReturnHome) {
                        Surface(
                            shape = CircleShape,
                            color = CardGray
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Back",
                                    tint = FreshGreen
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceWhite
                )
            )
        },
        containerColor = BackgroundWhite
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // Product Header Card - Enhanced
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                shadowElevation = 8.dp,
                tonalElevation = 2.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    FreshGreen,
                                    FreshGreenLight
                                )
                            )
                        )
                        .padding(28.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "🥗",
                                fontSize = 32.sp
                            )
                            Text(
                                text = "Product Analyzed",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White.copy(alpha = 0.95f)
                            )
                        }
                        Text(
                            text = scanResult.productName ?: "Food Product",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            lineHeight = 34.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Main Macronutrients Grid - Enhanced
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Calories
                scanResult.calories?.let {
                    EnhancedMacroCard(
                        modifier = Modifier.weight(1f),
                        label = "Calories",
                        value = it.toString(),
                        unit = "kcal",
                        icon = "🔥",
                        backgroundColor = Color(0xFFFFF3E0),
                        gradientColors = listOf(Color(0xFFFFE0B2), Color(0xFFFFF3E0)),
                        textColor = FatOrange
                    )
                }

                // Protein
                scanResult.protein?.let {
                    EnhancedMacroCard(
                        modifier = Modifier.weight(1f),
                        label = "Protein",
                        value = it.toString(),
                        unit = "g",
                        icon = "💪",
                        backgroundColor = Color(0xFFE1BEE7),
                        gradientColors = listOf(Color(0xFFD1C4E9), Color(0xFFE1BEE7)),
                        textColor = ProteinPurple
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Detailed Nutrition Section Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Nutrition Details",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sugar - Warning if high
            scanResult.sugar?.let {
                val isHigh = it > 10
                ModernNutrientCard(
                    label = "Total Sugar",
                    value = "$it g",
                    isWarning = isHigh,
                    icon = "🍬",
                    warningText = if (isHigh) "High sugar content" else null,
                    statusIcon = if (isHigh) "⚠️" else "✓"
                )
            }

            // Sodium - Warning if high
            scanResult.sodium?.let {
                val isHigh = it > 500
                ModernNutrientCard(
                    label = "Sodium",
                    value = "$it mg",
                    isWarning = isHigh,
                    icon = "🧂",
                    warningText = if (isHigh) "High sodium content" else null,
                    statusIcon = if (isHigh) "⚠️" else "✓"
                )
            }

            // Total Fat
            scanResult.totalFat?.let {
                ModernNutrientCard(
                    label = "Total Fat",
                    value = "$it g",
                    isWarning = false,
                    icon = "🥑",
                    statusIcon = "✓"
                )
            }

            // Saturated Fat
            scanResult.saturatedFat?.let {
                val isHigh = it > 5
                ModernNutrientCard(
                    label = "Saturated Fat",
                    value = "$it g",
                    isWarning = isHigh,
                    icon = "🧈",
                    warningText = if (isHigh) "Watch saturated fat intake" else null,
                    statusIcon = if (isHigh) "⚠️" else "✓"
                )
            }

            // Fiber
            scanResult.fiber?.let {
                val isGood = it >= 3
                ModernNutrientCard(
                    label = "Dietary Fiber",
                    value = "$it g",
                    isWarning = false,
                    isPositive = isGood,
                    icon = "🌾",
                    warningText = if (isGood) "Good source of fiber" else null,
                    statusIcon = if (isGood) "✨" else "✓"
                )
            }

            // Allergens Section - Enhanced
            if (!scanResult.allergens.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Allergen Information",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = WarningRedLight,
                    shadowElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(48.dp),
                            shape = CircleShape,
                            color = WarningRed.copy(alpha = 0.2f)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = "Warning",
                                    tint = WarningRed,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Contains Allergens",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = WarningRed
                            )
                            Text(
                                text = scanResult.allergens?.joinToString(", ") ?: "",
                                fontSize = 15.sp,
                                lineHeight = 22.sp,
                                color = WarningRed
                            )
                        }
                    }
                }
            }

            // Watchlist Ingredients - Enhanced
            if (!scanResult.watchlistIngredients.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = AlertOrangeLight,
                    shadowElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Surface(
                            modifier = Modifier.size(48.dp),
                            shape = CircleShape,
                            color = AlertOrange.copy(alpha = 0.2f)
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "👀",
                                    fontSize = 24.sp
                                )
                            }
                        }
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Watchlist Ingredients",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = AlertOrange
                            )
                            Text(
                                text = scanResult.watchlistIngredients?.joinToString(", ") ?: "",
                                fontSize = 15.sp,
                                lineHeight = 22.sp,
                                color = AlertOrange
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Action Buttons - Enhanced
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Return to Home Button
                OutlinedButton(
                    onClick = onReturnHome,
                    modifier = Modifier
                        .weight(1f)
                        .height(58.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = FreshGreen
                    ),
                    border = androidx.compose.foundation.BorderStroke(2.dp, FreshGreen)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "🏠",
                            fontSize = 20.sp
                        )
                        Text(
                            text = "Home",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Scan Another Button
                Button(
                    onClick = onScanAnother,
                    modifier = Modifier
                        .weight(1f)
                        .height(58.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = WarmOrange
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 4.dp,
                        pressedElevation = 8.dp
                    )
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "📷",
                            fontSize = 20.sp
                        )
                        Text(
                            text = "Scan Again",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun EnhancedMacroCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    unit: String,
    icon: String,
    backgroundColor: Color,
    gradientColors: List<Color>,
    textColor: Color
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = backgroundColor,
        shadowElevation = 4.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = gradientColors
                    )
                )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = icon,
                    fontSize = 40.sp
                )
                Text(
                    text = value,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
                Text(
                    text = unit,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = textColor.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = label,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textColor.copy(alpha = 0.85f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun ModernNutrientCard(
    label: String,
    value: String,
    isWarning: Boolean,
    isPositive: Boolean = false,
    icon: String,
    warningText: String? = null,
    statusIcon: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(18.dp),
        color = when {
            isWarning -> WarningRedLight
            isPositive -> SuccessGreenLight
            else -> SurfaceWhite
        },
        shadowElevation = if (isWarning || isPositive) 4.dp else 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                Surface(
                    modifier = Modifier.size(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = when {
                        isWarning -> WarningRed.copy(alpha = 0.15f)
                        isPositive -> SuccessGreen.copy(alpha = 0.15f)
                        else -> CardGray
                    }
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = icon,
                            fontSize = 28.sp
                        )
                    }
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = label,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            isWarning -> WarningRed
                            isPositive -> SuccessGreen
                            else -> TextPrimary
                        }
                    )
                    if (warningText != null) {
                        Text(
                            text = warningText,
                            fontSize = 13.sp,
                            color = when {
                                isWarning -> WarningRed.copy(alpha = 0.8f)
                                isPositive -> SuccessGreen.copy(alpha = 0.8f)
                                else -> TextSecondary
                            }
                        )
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = value,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        isWarning -> WarningRed
                        isPositive -> SuccessGreen
                        else -> TextPrimary
                    }
                )
                Text(
                    text = statusIcon,
                    fontSize = 20.sp
                )
            }
        }
    }
}
