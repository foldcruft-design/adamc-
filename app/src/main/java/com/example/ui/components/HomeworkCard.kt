package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.HomeworkItem
import com.example.model.HomeworkStatus
import com.example.model.Priority
import com.example.ui.glass.GlassCard
import com.example.ui.glass.GlassTheme
import com.example.util.DateTimeUtils

@Composable
fun HomeworkCard(
    item: HomeworkItem,
    onClick: () -> Unit,
    onToggleStatus: () -> Unit,
    onToggleSubtask: (String) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    isCompactView: Boolean = false,
    showSubtasksSetting: Boolean = true,
    modifier: Modifier = Modifier
) {
    val colors = GlassTheme.colors
    val isCompleted = item.status == HomeworkStatus.COMPLETED
    val isOverdue = item.isOverdue()
    val subtasks = item.getSubtaskList()
    var isExpanded by remember { mutableStateOf(false) }

    val subjectColor = try {
        Color(android.graphics.Color.parseColor(item.subjectColorHex))
    } catch (e: Exception) {
        Color(0xFF6366F1)
    }

    val (priorityColor, priorityText) = when (item.priority) {
        Priority.LOW -> Color(0xFF10B981) to "Низкий"
        Priority.MEDIUM -> Color(0xFF38BDF8) to "Средний"
        Priority.HIGH -> Color(0xFFF59E0B) to "Высокий"
        Priority.URGENT -> Color(0xFFF43F5E) to "Срочно"
    }

    val cardAlpha = if (isCompleted) 0.65f else 1f

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = if (isCompactView) 3.dp else 5.dp),
        shape = RoundedCornerShape(if (isCompactView) 16.dp else 20.dp),
        contentPadding = PaddingValues(
            horizontal = if (isCompactView) 12.dp else 16.dp,
            vertical = if (isCompactView) 10.dp else 14.dp
        ),
        onClick = onClick,
        testTag = "homework_card_${item.id}"
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Main Top Row: Checkbox, Details, Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Circular completion button / checkbox
                Box(
                    modifier = Modifier
                        .size(if (isCompactView) 28.dp else 34.dp)
                        .clip(CircleShape)
                        .background(
                            if (isCompleted) Color(0xFF10B981)
                            else (if (colors.isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFE2E8F0).copy(alpha = 0.7f))
                        )
                        .border(
                            1.5.dp,
                            if (isCompleted) Color(0xFF10B981)
                            else (if (colors.isDark) Color.White.copy(alpha = 0.35f) else Color(0xFF94A3B8)),
                            CircleShape
                        )
                        .clickable { onToggleStatus() }
                        .testTag("checkbox_item_${item.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    if (isCompleted) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Выполнено",
                            tint = Color.White,
                            modifier = Modifier.size(if (isCompactView) 16.dp else 20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(if (isCompactView) 10.dp else 12.dp))

                // Title and badges
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 4.dp)
                ) {
                    // Subject and Priority Tag Row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Subject badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(subjectColor.copy(alpha = if (colors.isDark) 0.25f else 0.15f))
                                .border(0.8.dp, subjectColor.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 7.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = item.subject,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = if (isCompactView) 10.5.sp else 11.5.sp
                                ),
                                color = if (colors.isDark) Color.White else subjectColor
                            )
                        }

                        // Priority Badge
                        if (item.priority != Priority.MEDIUM) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(priorityColor.copy(alpha = if (colors.isDark) 0.2f else 0.12f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = priorityText,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = if (isCompactView) 10.sp else 11.sp
                                    ),
                                    color = priorityColor
                                )
                            }
                        }

                        // Reminder icon
                        if (item.isReminderSet && item.reminderMinutesBefore >= 0) {
                            Icon(
                                imageVector = Icons.Default.Alarm,
                                contentDescription = "Напоминание",
                                tint = colors.cyanGlow,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(if (isCompactView) 2.dp else 4.dp))

                    // Title
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = if (isCompactView) 14.sp else 15.5.sp,
                            textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None
                        ),
                        color = if (isCompleted) colors.textTertiary else colors.textPrimary,
                        maxLines = if (isExpanded) 10 else 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Deadline info
                    Spacer(modifier = Modifier.height(3.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = if (isOverdue) Color(0xFFF43F5E) else colors.textTertiary,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = DateTimeUtils.formatRelativeDeadline(item.dueTimestamp),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = if (isCompactView) 11.sp else 12.sp,
                                fontWeight = if (isOverdue) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (isOverdue) Color(0xFFF43F5E) else colors.textSecondary
                        )
                    }
                }

                // Action buttons: Edit & Delete
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(if (isCompactView) 28.dp else 32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Редактировать",
                            tint = colors.textTertiary,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(if (isCompactView) 28.dp else 32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Удалить",
                            tint = Color(0xFFF43F5E).copy(alpha = 0.8f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Optional Subtasks Progress & Expandable items
            if (showSubtasksSetting && subtasks.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))

                val completedCount = subtasks.count { it.isCompleted }
                val totalSubtasks = subtasks.size

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { isExpanded = !isExpanded }
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Чек-лист: $completedCount из $totalSubtasks",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                        color = if (completedCount == totalSubtasks) Color(0xFF10B981) else colors.textSecondary
                    )

                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = colors.textTertiary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                AnimatedVisibility(visible = isExpanded) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, start = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        subtasks.forEach { sub ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onToggleSubtask(sub.id) }
                                    .padding(vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (sub.isCompleted) Color(0xFF10B981) else Color.Transparent
                                        )
                                        .border(
                                            1.dp,
                                            if (sub.isCompleted) Color(0xFF10B981) else colors.textTertiary,
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (sub.isCompleted) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(12.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = sub.title,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 12.5.sp,
                                        textDecoration = if (sub.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                                    ),
                                    color = if (sub.isCompleted) colors.textTertiary else colors.textPrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
