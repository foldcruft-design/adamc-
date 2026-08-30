package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.glass.GlassCard
import com.example.ui.glass.GlassTheme
import com.example.ui.viewmodel.HomeworkStats

@Composable
fun StatsGlassCard(
    stats: HomeworkStats,
    onActiveClick: () -> Unit,
    onCompletedClick: () -> Unit,
    onOverdueClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = GlassTheme.colors
    val animatedProgress by animateFloatAsState(
        targetValue = stats.completionPercentage / 100f,
        animationSpec = tween(durationMillis = 800),
        label = "progress"
    )

    GlassCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        elevation = 6.dp,
        testTag = "stats_glass_card"
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left Column: Main summary text
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Прогресс недели",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        color = colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (stats.totalCount == 0) {
                            "Нет активных заданий ✨"
                        } else if (stats.activeCount == 0) {
                            "🎉 Все уроки сделаны! Отличная работа!"
                        } else {
                            "Выполнено ${stats.completedCount} из ${stats.totalCount} заданий"
                        },
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.5.sp),
                        color = colors.textSecondary
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Right: Circular Progress Indicator
                Box(
                    modifier = Modifier.size(64.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val trackColor = if (colors.isDark) Color.White.copy(alpha = 0.12f) else Color(0xFFE2E8F0)
                    val gradientColors = listOf(colors.indigoGlow, colors.cyanGlow)

                    Canvas(modifier = Modifier.size(64.dp)) {
                        val strokeWidth = 6.dp.toPx()
                        // Track
                        drawCircle(
                            color = trackColor,
                            radius = (size.width - strokeWidth) / 2,
                            style = Stroke(width = strokeWidth)
                        )
                        // Progress arc
                        if (animatedProgress > 0f) {
                            drawArc(
                                brush = Brush.sweepGradient(gradientColors),
                                startAngle = -90f,
                                sweepAngle = 360f * animatedProgress,
                                useCenter = false,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                        }
                    }

                    Text(
                        text = "${stats.completionPercentage}%",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        ),
                        color = colors.textPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Stat pills row (clickable shortcuts)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Active pill
                StatPill(
                    count = stats.activeCount,
                    label = "В работе",
                    iconColor = colors.cyanGlow,
                    backgroundColor = if (colors.isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFF1F5F9),
                    onClick = onActiveClick,
                    modifier = Modifier.weight(1f)
                )

                // Completed pill
                StatPill(
                    count = stats.completedCount,
                    label = "Сделано",
                    iconColor = Color(0xFF10B981),
                    backgroundColor = if (colors.isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFF1F5F9),
                    onClick = onCompletedClick,
                    modifier = Modifier.weight(1f)
                )

                // Overdue pill
                if (stats.overdueCount > 0) {
                    StatPill(
                        count = stats.overdueCount,
                        label = "Срочно",
                        iconColor = Color(0xFFF43F5E),
                        backgroundColor = Color(0xFFF43F5E).copy(alpha = if (colors.isDark) 0.2f else 0.12f),
                        onClick = onOverdueClick,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatPill(
    count: Int,
    label: String,
    iconColor: Color,
    backgroundColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = GlassTheme.colors
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(1.dp, colors.borderGradientSubtle, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                ),
                color = iconColor
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                color = colors.textSecondary
            )
        }
    }
}
