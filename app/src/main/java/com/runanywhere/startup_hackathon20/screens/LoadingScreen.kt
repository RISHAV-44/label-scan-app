package com.runanywhere.startup_hackathon20.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runanywhere.startup_hackathon20.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun LoadingScreen() {
    // Animated values
    val infiniteTransition = rememberInfiniteTransition(label = "loading")

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    // Loading messages rotation
    val messages = listOf(
        "🔍 Scanning nutrition label...",
        "📊 Analyzing ingredients...",
        "🧮 Calculating nutrition values...",
        "✨ Finalizing results..."
    )

    var currentMessageIndex by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(2000)
            currentMessageIndex = (currentMessageIndex + 1) % messages.size
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        BackgroundWhite,
                        SurfaceWhite
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            // Animated Icon
            Surface(
                modifier = Modifier
                    .size(140.dp)
                    .scale(scale)
                    .alpha(alpha),
                shape = CircleShape,
                color = SuccessGreenLight,
                shadowElevation = 8.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    SuccessGreenLight,
                                    SuccessGreenLight.copy(alpha = 0.7f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🥗",
                        fontSize = 72.sp
                    )
                }
            }

            // Progress Indicator
            CircularProgressIndicator(
                modifier = Modifier.size(60.dp),
                color = FreshGreen,
                strokeWidth = 6.dp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Title
            Text(
                text = "Analyzing Your Food",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )

            // Animated Message
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = CardGray
            ) {
                Text(
                    text = messages[currentMessageIndex],
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = FreshGreen,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 14.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Info text
            Text(
                text = "This usually takes a few seconds",
                fontSize = 14.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}
