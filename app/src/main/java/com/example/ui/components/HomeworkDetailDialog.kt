package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.HomeworkItem
import com.example.model.HomeworkStatus
import com.example.model.Priority
import com.example.ui.glass.GlassButton
import com.example.ui.glass.GlassTheme
import com.example.util.DateTimeUtils

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeworkDetailDialog(
    item: HomeworkItem,
    onDismiss: () -> Unit,
    onToggleStatus: () -> Unit,
    onToggleSubtask: (String) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val colors = GlassTheme.colors
    val isCompleted = item.status == HomeworkStatus.COMPLETED
    val isOverdue = item.isOverdue()
    val subtasks = item.getSubtaskList()

    val subjectColor = try {
        Color(android.graphics.Color.parseColor(item.subjectColorHex))
    } catch (e: Exception) {
        Color(0xFF6366F1)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .heightIn(max = 680.dp)
                .clip(RoundedCornerShape(26.dp))
                .background(
                    if (colors.isDark) {
                        Brush.verticalGradient(
                            listOf(
                                Color(0xEE111827),
                                Color(0xF50F172A),
                                Color(0xEE1E1B4B)
                            )
                        )
                    } else {
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFCFFFFFF),
                                Color(0xF8F8FAFC),
                                Color(0xFCFFFFFF)
                            )
                        )
                    }
                )
                .border(1.dp, colors.borderGradientLight, RoundedCornerShape(26.dp))
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Top Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(subjectColor.copy(alpha = if (colors.isDark) 0.25f else 0.15f))
                            .border(1.dp, subjectColor.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = item.subject,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            ),
                            color = if (colors.isDark) Color.White else subjectColor
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onEdit,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (colors.isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFF1F5F9))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Редактировать",
                                tint = colors.cyanGlow,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF43F5E).copy(alpha = 0.15f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Удалить",
                                tint = Color(0xFFF43F5E),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(if (colors.isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFF1F5F9))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Закрыть",
                                tint = colors.textSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None
                    ),
                    color = if (isCompleted) colors.textTertiary else colors.textPrimary
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Metadata Badges (Status, Priority, Deadline)
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Status Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                when (item.status) {
                                    HomeworkStatus.COMPLETED -> Color(0xFF10B981).copy(alpha = 0.15f)
                                    HomeworkStatus.IN_PROGRESS -> Color(0xFF3B82F6).copy(alpha = 0.15f)
                                    HomeworkStatus.TODO -> (if (colors.isDark) Color.White.copy(alpha = 0.1f) else Color(0xFFF1F5F9))
                                }
                            )
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "Статус: ${item.status.titleRu}",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = when (item.status) {
                                HomeworkStatus.COMPLETED -> Color(0xFF059669)
                                HomeworkStatus.IN_PROGRESS -> Color(0xFF2563EB)
                                HomeworkStatus.TODO -> colors.textSecondary
                            }
                        )
                    }

                    // Priority Badge
                    val (pColor, pText) = when (item.priority) {
                        Priority.LOW -> Color(0xFF10B981) to "Низкий приоритет"
                        Priority.MEDIUM -> Color(0xFF38BDF8) to "Средний приоритет"
                        Priority.HIGH -> Color(0xFFF59E0B) to "Высокий приоритет"
                        Priority.URGENT -> Color(0xFFF43F5E) to "Срочный приоритет"
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(pColor.copy(alpha = if (colors.isDark) 0.2f else 0.12f))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = pText,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = pColor
                        )
                    }

                    // Reminder Badge
                    if (item.isReminderSet && item.reminderMinutesBefore >= 0) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(colors.cyanGlow.copy(alpha = 0.12f))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Alarm,
                                    contentDescription = null,
                                    tint = colors.cyanGlow,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Напоминание активно",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = colors.cyanGlow
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Deadline card box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (isOverdue) Color(0xFFEF4444).copy(alpha = 0.12f)
                            else (if (colors.isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFF1F5F9))
                        )
                        .border(
                            1.dp,
                            if (isOverdue) SolidColor(Color(0xFFEF4444).copy(alpha = 0.4f)) else colors.borderGradientSubtle,
                            RoundedCornerShape(16.dp)
                        )
                        .padding(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = if (isOverdue) Color(0xFFF43F5E) else colors.cyanGlow,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Дедлайн: ${DateTimeUtils.formatDateTime(item.dueTimestamp)}",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.5.sp
                                ),
                                color = colors.textPrimary
                            )
                            Text(
                                text = DateTimeUtils.formatRelativeDeadline(item.dueTimestamp),
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                color = if (isOverdue) Color(0xFFE11D48) else colors.textSecondary
                            )
                        }
                    }
                }

                // Description if present
                if (item.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Описание и заметки",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (colors.isDark) Color.White.copy(alpha = 0.05f) else Color(0xFFF8FAFC))
                            .border(1.dp, colors.borderGradientSubtle, RoundedCornerShape(14.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = item.description,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 14.sp,
                                lineHeight = 20.sp
                            ),
                            color = colors.textPrimary
                        )
                    }
                }

                // Subtasks checklist
                if (subtasks.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Чек-лист (${subtasks.count { it.isCompleted }}/${subtasks.size})",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (colors.isDark) Color.White.copy(alpha = 0.05f) else Color(0xFFF8FAFC))
                            .border(1.dp, colors.borderGradientSubtle, RoundedCornerShape(14.dp))
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        subtasks.forEach { subtask ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onToggleSubtask(subtask.id) }
                                    .padding(vertical = 4.dp, horizontal = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (subtask.isCompleted) Color(0xFF10B981) else Color.Transparent
                                        )
                                        .border(
                                            1.5.dp,
                                            if (subtask.isCompleted) Color(0xFF10B981) else colors.textTertiary,
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (subtask.isCompleted) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                Text(
                                    text = subtask.title,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontSize = 14.sp,
                                        textDecoration = if (subtask.isCompleted) TextDecoration.LineThrough else TextDecoration.None
                                    ),
                                    color = if (subtask.isCompleted) colors.textTertiary else colors.textPrimary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Toggle status button
                GlassButton(
                    text = if (isCompleted) "Снять отметку о выполнении" else "Отметить как выполненное ✨",
                    onClick = onToggleStatus,
                    modifier = Modifier.fillMaxWidth(),
                    isPrimary = !isCompleted,
                    accentColor = if (isCompleted) Color(0xFF64748B) else Color(0xFF10B981),
                    testTag = "detail_toggle_status_button"
                )
            }
        }
    }
}
