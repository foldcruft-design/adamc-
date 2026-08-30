package com.example.ui.glass

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(22.dp),
    backgroundColor: Color? = null,
    borderBrush: Brush? = null,
    borderWidth: Dp = 1.dp,
    elevation: Dp = 6.dp,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    onClick: (() -> Unit)? = null,
    testTag: String? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val colors = GlassTheme.colors
    val actualBg = backgroundColor ?: colors.surfaceCard
    val actualBorder = borderBrush ?: colors.borderGradientLight

    val clickModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = ripple(bounded = true, color = if (colors.isDark) Color.White.copy(alpha = 0.3f) else colors.indigoGlow.copy(alpha = 0.15f)),
            onClick = onClick
        )
    } else Modifier

    val tagModifier = if (testTag != null) Modifier.testTag(testTag) else Modifier

    Box(
        modifier = modifier
            .then(tagModifier)
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = colors.shadowAmbient,
                spotColor = colors.shadowSpot
            )
            .clip(shape)
            .background(actualBg, shape)
            .border(borderWidth, actualBorder, shape)
            .then(clickModifier)
            .padding(contentPadding),
        content = content
    )
}

@Composable
fun GlassButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    shape: Shape = RoundedCornerShape(16.dp),
    isPrimary: Boolean = true,
    accentColor: Color = Color(0xFF6366F1),
    testTag: String = "glass_button"
) {
    val colors = GlassTheme.colors
    val bgBrush = if (isPrimary) {
        Brush.horizontalGradient(
            listOf(
                accentColor.copy(alpha = if (colors.isDark) 0.85f else 0.95f),
                accentColor.copy(alpha = 1.0f)
            )
        )
    } else {
        Brush.horizontalGradient(
            if (colors.isDark) {
                listOf(
                    Color.White.copy(alpha = 0.12f),
                    Color.White.copy(alpha = 0.06f)
                )
            } else {
                listOf(
                    Color.White.copy(alpha = 0.9f),
                    Color(0xFFF1F5F9).copy(alpha = 0.9f)
                )
            }
        )
    }

    val borderBrush = if (isPrimary) {
        Brush.linearGradient(
            listOf(
                Color.White.copy(alpha = 0.6f),
                accentColor.copy(alpha = 0.4f),
                Color.White.copy(alpha = 0.2f)
            )
        )
    } else {
        colors.borderGradientSubtle
    }

    val textColor = if (isPrimary) Color.White else colors.textPrimary

    Box(
        modifier = modifier
            .testTag(testTag)
            .defaultMinSize(minHeight = 48.dp)
            .shadow(
                elevation = if (isPrimary) 6.dp else 2.dp,
                shape = shape,
                ambientColor = if (isPrimary) accentColor.copy(alpha = 0.4f) else colors.shadowAmbient,
                spotColor = if (isPrimary) accentColor.copy(alpha = 0.4f) else colors.shadowSpot
            )
            .clip(shape)
            .background(bgBrush, shape)
            .border(1.dp, borderBrush, shape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, color = Color.White.copy(alpha = 0.3f)),
                onClick = onClick
            )
            .padding(horizontal = 18.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.5.sp,
                    letterSpacing = 0.2.sp
                ),
                color = textColor
            )
        }
    }
}

@Composable
fun GlassIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    backgroundColor: Color? = null,
    iconTint: Color? = null,
    size: Dp = 44.dp,
    testTag: String = "glass_icon_button"
) {
    val colors = GlassTheme.colors
    val actualBg = backgroundColor ?: (if (colors.isDark) Color.White.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.85f))
    val actualTint = iconTint ?: colors.textPrimary

    Box(
        modifier = modifier
            .testTag(testTag)
            .size(size)
            .shadow(
                elevation = if (colors.isDark) 2.dp else 3.dp,
                shape = CircleShape,
                ambientColor = colors.shadowAmbient,
                spotColor = colors.shadowSpot
            )
            .clip(CircleShape)
            .background(actualBg, CircleShape)
            .border(1.dp, colors.borderGradientSubtle, CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, color = if (colors.isDark) Color.White.copy(alpha = 0.35f) else Color.Black.copy(alpha = 0.15f)),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = actualTint,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun GlassFilterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    count: Int? = null,
    accentColor: Color = Color(0xFF6366F1),
    icon: ImageVector? = null,
    testTag: String = "glass_filter_chip"
) {
    val colors = GlassTheme.colors
    val shape = RoundedCornerShape(14.dp)

    val bgBrush = if (isSelected) {
        Brush.horizontalGradient(
            listOf(
                accentColor.copy(alpha = if (colors.isDark) 0.75f else 0.90f),
                accentColor.copy(alpha = 1.0f)
            )
        )
    } else {
        Brush.horizontalGradient(
            if (colors.isDark) {
                listOf(
                    Color.White.copy(alpha = 0.09f),
                    Color.White.copy(alpha = 0.05f)
                )
            } else {
                listOf(
                    Color.White.copy(alpha = 0.85f),
                    Color(0xFFF8FAFC).copy(alpha = 0.85f)
                )
            }
        )
    }

    val borderBrush = if (isSelected) {
        Brush.linearGradient(
            listOf(
                Color.White.copy(alpha = 0.7f),
                accentColor.copy(alpha = 0.4f),
                Color.White.copy(alpha = 0.3f)
            )
        )
    } else {
        colors.borderGradientSubtle
    }

    Box(
        modifier = modifier
            .testTag(testTag)
            .defaultMinSize(minHeight = 38.dp)
            .shadow(
                elevation = if (isSelected) 4.dp else 1.dp,
                shape = shape,
                ambientColor = if (isSelected) accentColor.copy(alpha = 0.3f) else colors.shadowAmbient,
                spotColor = colors.shadowSpot
            )
            .clip(shape)
            .background(bgBrush, shape)
            .border(1.dp, borderBrush, shape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, color = Color.White.copy(alpha = 0.3f)),
                onClick = onClick
            )
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) Color.White else colors.textSecondary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 13.5.sp
                ),
                color = if (isSelected) Color.White else colors.textSecondary
            )
            if (count != null && count > 0) {
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(
                            if (isSelected) Color.White.copy(alpha = 0.28f) else (if (colors.isDark) Color.White.copy(alpha = 0.12f) else Color(0xFFE2E8F0)),
                            CircleShape
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = count.toString(),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        ),
                        color = if (isSelected) Color.White else colors.textPrimary
                    )
                }
            }
        }
    }
}

@Composable
fun GlassTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    leadingIcon: ImageVector? = null,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    shape: Shape = RoundedCornerShape(16.dp),
    testTag: String = "glass_text_field"
) {
    val colors = GlassTheme.colors

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .testTag(testTag)
            .fillMaxWidth()
            .defaultMinSize(minHeight = 48.dp)
            .clip(shape)
            .background(colors.inputBackground, shape)
            .border(1.dp, colors.borderGradientSubtle, shape)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        textStyle = TextStyle(
            color = colors.textPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Normal
        ),
        cursorBrush = SolidColor(colors.indigoGlow),
        singleLine = singleLine,
        maxLines = maxLines,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        decorationBox = { innerTextField ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (leadingIcon != null) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = colors.textTertiary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                }
                Box(modifier = Modifier.weight(1f)) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            style = TextStyle(
                                color = colors.textTertiary,
                                fontSize = 15.sp
                            )
                        )
                    }
                    innerTextField()
                }
                if (value.isNotEmpty()) {
                    IconButton(
                        onClick = { onValueChange("") },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Очистить",
                            tint = colors.textTertiary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    )
}
