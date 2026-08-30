package com.example.ui.glass

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.model.AppThemeMode

data class GlassColorScheme(
    val isDark: Boolean,
    val backgroundGradient: List<Color>,
    val surfaceCard: Color,
    val surfaceCardElevated: Color,
    val surfaceCardSubtle: Color,
    val surfaceDialog: Color,
    val inputBackground: Color,
    val borderGradientLight: Brush,
    val borderGradientSubtle: Brush,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val shadowAmbient: Color,
    val shadowSpot: Color,
    val cyanGlow: Color,
    val violetGlow: Color,
    val indigoGlow: Color,
    val coralGlow: Color,
    val emeraldGlow: Color,
    val amberGlow: Color,
    val orb1Color: Color,
    val orb2Color: Color,
    val orb3Color: Color
)

val LightGlassColors = GlassColorScheme(
    isDark = false,
    backgroundGradient = listOf(
        Color(0xFFF8FAFC),
        Color(0xFFF1F5F9),
        Color(0xFFEDE9FE),
        Color(0xFFE0F2FE)
    ),
    surfaceCard = Color(0xD8FFFFFF),
    surfaceCardElevated = Color(0xF2FFFFFF),
    surfaceCardSubtle = Color(0xA0FFFFFF),
    surfaceDialog = Color(0xFAFFFFFF),
    inputBackground = Color(0x80F1F5F9),
    borderGradientLight = Brush.linearGradient(
        listOf(
            Color.White,
            Color(0xFFE2E8F0).copy(alpha = 0.85f),
            Color.White.copy(alpha = 0.6f)
        )
    ),
    borderGradientSubtle = Brush.linearGradient(
        listOf(
            Color(0xFFE2E8F0).copy(alpha = 0.7f),
            Color(0xFFCBD5E1).copy(alpha = 0.4f)
        )
    ),
    textPrimary = Color(0xFF0F172A),
    textSecondary = Color(0xFF475569),
    textTertiary = Color(0xFF94A3B8),
    shadowAmbient = Color(0x140F172A),
    shadowSpot = Color(0x100F172A),
    cyanGlow = Color(0xFF0284C7),
    violetGlow = Color(0xFF7C3AED),
    indigoGlow = Color(0xFF4F46E5),
    coralGlow = Color(0xFFE11D48),
    emeraldGlow = Color(0xFF059669),
    amberGlow = Color(0xFFD97706),
    orb1Color = Color(0x40A5B4FC),
    orb2Color = Color(0x457DD3FC),
    orb3Color = Color(0x35F472B6)
)

val DarkGlassColors = GlassColorScheme(
    isDark = true,
    backgroundGradient = listOf(
        Color(0xFF0A0D18),
        Color(0xFF11172A),
        Color(0xFF1A1528)
    ),
    surfaceCard = Color(0x28FFFFFF),
    surfaceCardElevated = Color(0x38FFFFFF),
    surfaceCardSubtle = Color(0x18FFFFFF),
    surfaceDialog = Color(0xFA111827),
    inputBackground = Color(0x18FFFFFF),
    borderGradientLight = Brush.linearGradient(
        listOf(
            Color.White.copy(alpha = 0.45f),
            Color.White.copy(alpha = 0.12f),
            Color.White.copy(alpha = 0.04f),
            Color.White.copy(alpha = 0.25f)
        )
    ),
    borderGradientSubtle = Brush.linearGradient(
        listOf(
            Color.White.copy(alpha = 0.28f),
            Color.White.copy(alpha = 0.06f),
            Color.White.copy(alpha = 0.18f)
        )
    ),
    textPrimary = Color(0xFFF8FAFC),
    textSecondary = Color(0xFFCBD5E1),
    textTertiary = Color(0xFF94A3B8),
    shadowAmbient = Color.Black.copy(alpha = 0.45f),
    shadowSpot = Color.Black.copy(alpha = 0.45f),
    cyanGlow = Color(0xFF00F2FE),
    violetGlow = Color(0xFF8A2387),
    indigoGlow = Color(0xFF6366F1),
    coralGlow = Color(0xFFFF5252),
    emeraldGlow = Color(0xFF10B981),
    amberGlow = Color(0xFFFBBF24),
    orb1Color = Color(0x556366F1),
    orb2Color = Color(0x4006B6D4),
    orb3Color = Color(0x35EC4899)
)

val LocalGlassColors = compositionLocalOf { LightGlassColors }

object GlassTheme {
    val colors: GlassColorScheme
        @Composable
        @ReadOnlyComposable
        get() = LocalGlassColors.current
}

object GlassColors {
    val BackgroundStart @Composable @ReadOnlyComposable get() = GlassTheme.colors.backgroundGradient[0]
    val BackgroundMid @Composable @ReadOnlyComposable get() = GlassTheme.colors.backgroundGradient.getOrElse(1) { GlassTheme.colors.backgroundGradient[0] }
    val BackgroundEnd @Composable @ReadOnlyComposable get() = GlassTheme.colors.backgroundGradient.last()

    val SurfaceDark @Composable @ReadOnlyComposable get() = GlassTheme.colors.surfaceCardSubtle
    val SurfaceCard @Composable @ReadOnlyComposable get() = GlassTheme.colors.surfaceCard
    val SurfaceCardElevated @Composable @ReadOnlyComposable get() = GlassTheme.colors.surfaceCardElevated
    val SurfaceDialog @Composable @ReadOnlyComposable get() = GlassTheme.colors.surfaceDialog

    val BorderGradientLight @Composable @ReadOnlyComposable get() = GlassTheme.colors.borderGradientLight
    val BorderGradientSubtle @Composable @ReadOnlyComposable get() = GlassTheme.colors.borderGradientSubtle

    val CyanGlow @Composable @ReadOnlyComposable get() = GlassTheme.colors.cyanGlow
    val VioletGlow @Composable @ReadOnlyComposable get() = GlassTheme.colors.violetGlow
    val IndigoGlow @Composable @ReadOnlyComposable get() = GlassTheme.colors.indigoGlow
    val CoralGlow @Composable @ReadOnlyComposable get() = GlassTheme.colors.coralGlow
    val EmeraldGlow @Composable @ReadOnlyComposable get() = GlassTheme.colors.emeraldGlow
    val AmberGlow @Composable @ReadOnlyComposable get() = GlassTheme.colors.amberGlow

    val TextPrimary @Composable @ReadOnlyComposable get() = GlassTheme.colors.textPrimary
    val TextSecondary @Composable @ReadOnlyComposable get() = GlassTheme.colors.textSecondary
    val TextTertiary @Composable @ReadOnlyComposable get() = GlassTheme.colors.textTertiary
}

@Composable
fun ProvideGlassTheme(
    themeMode: AppThemeMode,
    content: @Composable () -> Unit
) {
    val isSystemDark = isSystemInDarkTheme()
    val isDark = when (themeMode) {
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
        AppThemeMode.SYSTEM -> isSystemDark
    }

    val currentColors = if (isDark) DarkGlassColors else LightGlassColors

    CompositionLocalProvider(
        LocalGlassColors provides currentColors,
        content = content
    )
}

fun Modifier.glassSurface(
    shape: Shape = RoundedCornerShape(20.dp),
    backgroundColor: Color? = null,
    borderBrush: Brush? = null,
    borderWidth: Dp = 1.dp,
    elevation: Dp = 8.dp
): Modifier = this

@Composable
fun GlassBackgroundMesh(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val colors = GlassTheme.colors
    val infiniteTransition = rememberInfiniteTransition(label = "glass_mesh")
    val animOffset1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 12000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb1"
    )
    val animOffset2 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 16000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "orb2"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors.backgroundGradient))
    ) {
        // Floating glass luminosity orbs
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            // Orb 1 (Top-right glow)
            val center1 = Offset(
                x = width * (0.85f - animOffset1 * 0.2f),
                y = height * (0.12f + animOffset1 * 0.15f)
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        colors.orb1Color,
                        colors.orb1Color.copy(alpha = colors.orb1Color.alpha * 0.4f),
                        Color.Transparent
                    ),
                    center = center1,
                    radius = width * 0.75f
                ),
                radius = width * 0.75f,
                center = center1
            )

            // Orb 2 (Middle-left glow)
            val center2 = Offset(
                x = width * (0.12f + animOffset2 * 0.2f),
                y = height * (0.42f + animOffset2 * 0.2f)
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        colors.orb2Color,
                        colors.orb2Color.copy(alpha = colors.orb2Color.alpha * 0.35f),
                        Color.Transparent
                    ),
                    center = center2,
                    radius = width * 0.65f
                ),
                radius = width * 0.65f,
                center = center2
            )

            // Orb 3 (Bottom-right glow)
            val center3 = Offset(
                x = width * (0.78f - animOffset2 * 0.15f),
                y = height * (0.82f - animOffset1 * 0.1f)
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        colors.orb3Color,
                        colors.orb3Color.copy(alpha = colors.orb3Color.alpha * 0.35f),
                        Color.Transparent
                    ),
                    center = center3,
                    radius = width * 0.8f
                ),
                radius = width * 0.8f,
                center = center3
            )
        }

        content()
    }
}
