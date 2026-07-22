package com.example.myapplication.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    // Timer for 5 seconds (within 4-8 seconds requirement)
    LaunchedEffect(Unit) {
        delay(5000)
        onSplashFinished()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "water_drops")

    // Drop 1 falling animation
    val drop1Y by infiniteTransition.animateFloat(
        initialValue = -0.2f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = LinearOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "drop1_y"
    )
    val drop1Alpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "drop1_alpha"
    )

    // Drop 2 (offset timing)
    val drop2Y by infiniteTransition.animateFloat(
        initialValue = -0.3f,
        targetValue = 0.55f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, delayMillis = 600, easing = FastOutLinearInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "drop2_y"
    )

    // Ripple expansion animation
    val rippleScale by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 2.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ripple_scale"
    )
    val rippleAlpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ripple_alpha"
    )

    // Text fade-in and scale animation
    val textScale = remember { Animatable(0.6f) }
    val textAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        textScale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1400, easing = CubicBezierEasing(0.34f, 1.56f, 0.64f, 1.0f))
        )
    }
    LaunchedEffect(Unit) {
        textAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1200)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        BlueAbyss,
                        BlueNight,
                        BlueDeep,
                        Color(0xFF003865)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Decorative background water wave circles
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            
            // Draw background glowing water ambient circles
            drawCircle(
                color = BlueOcean.copy(alpha = 0.15f),
                radius = canvasWidth * 0.7f,
                center = androidx.compose.ui.geometry.Offset(canvasWidth * 0.5f, canvasHeight * 0.45f)
            )
            drawCircle(
                color = Color(0xFF00BCD4).copy(alpha = 0.08f),
                radius = canvasWidth * 0.9f,
                center = androidx.compose.ui.geometry.Offset(canvasWidth * 0.5f, canvasHeight * 0.45f)
            )
        }

        // Animated Drop 1
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 60.dp)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (drop1Y * 600).dp)
                    .alpha(drop1Alpha)
            ) {
                Icon(
                    imageVector = Icons.Default.WaterDrop,
                    contentDescription = null,
                    tint = BlueOcean,
                    modifier = Modifier.size(36.dp)
                )
            }

            // Animated Drop 2 (offset)
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(x = (-30).dp, y = (drop2Y * 600).dp)
                    .alpha(0.7f)
            ) {
                Icon(
                    imageVector = Icons.Default.WaterDrop,
                    contentDescription = null,
                    tint = Color(0xFF00E5FF),
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Main Center Content: Water Drop Ripple + Branding
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Water Drop Container with Expanding Ripple Circles
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(140.dp)
            ) {
                // Expanding Water Ripple Ring 1
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .scale(rippleScale)
                        .alpha(rippleAlpha)
                        .clip(CircleShape)
                        .background(Color(0xFF00E5FF).copy(alpha = 0.35f))
                )

                // Expanding Water Ripple Ring 2
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .scale(rippleScale * 0.7f)
                        .alpha(rippleAlpha * 0.8f)
                        .clip(CircleShape)
                        .background(BlueOcean.copy(alpha = 0.4f))
                )

                // Core Water Drop Circle Icon Container
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFF00E5FF),
                                    BlueOcean,
                                    BlueDeep
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.WaterDrop,
                        contentDescription = "Maji Salama Drop",
                        tint = WhitePure,
                        modifier = Modifier.size(52.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Branding Text "MAJI SALAMA"
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .scale(textScale.value)
                    .alpha(textAlpha.value)
            ) {
                Text(
                    text = "MAJI SALAMA",
                    color = WhitePure,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 6.sp,
                    fontFamily = FontFamily.Monospace,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Usalama na Uhakika wa Maji Safi",
                    color = WhitePure.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Animated Loading Dots Indicator
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val dot1Alpha by infiniteTransition.animateFloat(
                    initialValue = 0.3f,
                    targetValue = 1.0f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(600, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "dot1"
                )
                val dot2Alpha by infiniteTransition.animateFloat(
                    initialValue = 0.3f,
                    targetValue = 1.0f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(600, delayMillis = 200, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "dot2"
                )
                val dot3Alpha by infiniteTransition.animateFloat(
                    initialValue = 0.3f,
                    targetValue = 1.0f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(600, delayMillis = 400, easing = FastOutSlowInEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "dot3"
                )

                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .alpha(dot1Alpha)
                        .clip(CircleShape)
                        .background(Color(0xFF00E5FF))
                )
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .alpha(dot2Alpha)
                        .clip(CircleShape)
                        .background(Color(0xFF00E5FF))
                )
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .alpha(dot3Alpha)
                        .clip(CircleShape)
                        .background(Color(0xFF00E5FF))
                )
            }
        }
    }
}
