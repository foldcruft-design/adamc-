package com.example.ui.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.AppSettings
import com.example.model.HomeworkItem
import com.example.model.HomeworkStatus
import com.example.model.Priority
import com.example.model.SubjectPreset
import com.example.model.Subtask
import com.example.ui.glass.GlassButton
import com.example.ui.glass.GlassTextField
import com.example.ui.glass.GlassTheme
import com.example.util.DateTimeUtils
import java.util.Calendar
import java.util.UUID

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddEditHomeworkDialog(
    itemToEdit: HomeworkItem?,
    availableSubjects: List<SubjectPreset>,
    settings: AppSettings,
    onDismiss: () -> Unit,
    onSave: (
        id: Long,
        title: String,
        subject: String,
        subjectColorHex: String,
        subjectIconName: String,
        description: String,
        dueTimestamp: Long,
        priority: Priority,
        status: HomeworkStatus,
        subtasks: List<Subtask>,
        reminderMinutesBefore: Int,
        isReminderSet: Boolean
    ) -> Unit
) {
    val context = LocalContext.current
    val colors = GlassTheme.colors

    var title by remember { mutableStateOf(itemToEdit?.title ?: "") }
    var selectedSubject by remember {
        mutableStateOf(itemToEdit?.subject ?: availableSubjects.firstOrNull()?.name ?: "Алгебра")
    }
    var customSubject by remember { mutableStateOf("") }
    var isCustomSubjectSelected by remember {
        mutableStateOf(
            itemToEdit?.let { item ->
                availableSubjects.none { it.name == item.subject }
            } ?: false
        )
    }
    var selectedColorHex by remember {
        mutableStateOf(
            itemToEdit?.subjectColorHex ?: availableSubjects.firstOrNull()?.colorHex ?: "#6366F1"
        )
    }
    var description by remember { mutableStateOf(itemToEdit?.description ?: "") }

    // Due date state (calculated from user's default settings if new item)
    var dueTimestamp by remember {
        mutableLongStateOf(
            itemToEdit?.dueTimestamp ?: run {
                val cal = Calendar.getInstance().apply {
                    add(Calendar.DAY_OF_YEAR, settings.defaultDaysAhead)
                    set(Calendar.HOUR_OF_DAY, settings.defaultDeadlineHour)
                    set(Calendar.MINUTE, settings.defaultDeadlineMinute)
                    set(Calendar.SECOND, 0)
                }
                cal.timeInMillis
            }
        )
    }

    var priority by remember { mutableStateOf(itemToEdit?.priority ?: Priority.MEDIUM) }
    var status by remember { mutableStateOf(itemToEdit?.status ?: HomeworkStatus.TODO) }
    var reminderMinutes by remember {
        mutableIntStateOf(itemToEdit?.reminderMinutesBefore ?: settings.defaultReminderMinutes)
    }
    var isReminderEnabled by remember {
        mutableStateOf(itemToEdit?.isReminderSet ?: settings.isReminderEnabledByDefault)
    }

    // Subtasks
    val subtasksList = remember {
        mutableStateListOf<Subtask>().apply {
            if (itemToEdit != null) {
                addAll(itemToEdit.getSubtaskList())
            }
        }
    }
    var newSubtaskText by remember { mutableStateOf("") }
    var titleError by remember { mutableStateOf(false) }

    // Date / Time picker dialog openers
    val openDatePicker = {
        val cal = Calendar.getInstance().apply { timeInMillis = dueTimestamp }
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val newCal = Calendar.getInstance().apply {
                    timeInMillis = dueTimestamp
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                }
                dueTimestamp = newCal.timeInMillis
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    val openTimePicker = {
        val cal = Calendar.getInstance().apply { timeInMillis = dueTimestamp }
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                val newCal = Calendar.getInstance().apply {
                    timeInMillis = dueTimestamp
                    set(Calendar.HOUR_OF_DAY, hourOfDay)
                    set(Calendar.MINUTE, minute)
                }
                dueTimestamp = newCal.timeInMillis
            },
            cal.get(Calendar.HOUR_OF_DAY),
            cal.get(Calendar.MINUTE),
            true
        ).show()
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
                // Header: Title & Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (itemToEdit == null) "✨ Новое задание" else "✏️ Редактирование",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        ),
                        color = colors.textPrimary
                    )

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (colors.isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.05f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Закрыть",
                            tint = colors.textSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Task Name input
                Text(
                    text = "Что нужно сделать? *",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = colors.textSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                GlassTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        if (it.isNotBlank()) titleError = false
                    },
                    placeholder = "Например: Стр. 45 № 12, выучить стих",
                    testTag = "input_homework_title"
                )
                if (titleError) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Пожалуйста, введите название задания",
                        color = Color(0xFFF43F5E),
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Subject Selector
                Text(
                    text = "Предмет",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = colors.textSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    availableSubjects.forEach { preset ->
                        val isSelected = !isCustomSubjectSelected && selectedSubject == preset.name
                        val color = try {
                            Color(android.graphics.Color.parseColor(preset.colorHex))
                        } catch (e: Exception) {
                            Color(0xFF6366F1)
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) color.copy(alpha = if (colors.isDark) 0.6f else 0.85f)
                                    else (if (colors.isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFF1F5F9))
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) SolidColor(color) else colors.borderGradientSubtle,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    isCustomSubjectSelected = false
                                    selectedSubject = preset.name
                                    selectedColorHex = preset.colorHex
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = preset.name,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 12.5.sp
                                ),
                                color = if (isSelected) Color.White else colors.textSecondary
                            )
                        }
                    }

                    // Custom subject chip
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isCustomSubjectSelected) Color(0xFF8B5CF6).copy(alpha = 0.6f)
                                else (if (colors.isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFF1F5F9))
                            )
                            .border(
                                1.dp,
                                if (isCustomSubjectSelected) SolidColor(Color(0xFF8B5CF6)) else colors.borderGradientSubtle,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable {
                                isCustomSubjectSelected = true
                                selectedColorHex = "#8B5CF6"
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "+ Свой предмет",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = if (isCustomSubjectSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.5.sp
                            ),
                            color = if (isCustomSubjectSelected) Color.White else colors.textSecondary
                        )
                    }
                }

                if (isCustomSubjectSelected) {
                    Spacer(modifier = Modifier.height(8.dp))
                    GlassTextField(
                        value = customSubject,
                        onValueChange = { customSubject = it },
                        placeholder = "Введите название предмета",
                        testTag = "input_custom_subject"
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Deadline Date & Time Pickers
                Text(
                    text = "Дедлайн (срок сдачи)",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = colors.textSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Date button
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (colors.isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFF1F5F9))
                            .border(1.dp, colors.borderGradientSubtle, RoundedCornerShape(14.dp))
                            .clickable { openDatePicker() }
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = colors.cyanGlow,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = DateTimeUtils.formatDate(dueTimestamp),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp
                                ),
                                color = colors.textPrimary
                            )
                        }
                    }

                    // Time button
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (colors.isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFF1F5F9))
                            .border(1.dp, colors.borderGradientSubtle, RoundedCornerShape(14.dp))
                            .clickable { openTimePicker() }
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = colors.amberGlow,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = DateTimeUtils.formatTime(dueTimestamp),
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp
                                ),
                                color = colors.textPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Priority Selection
                Text(
                    text = "Приоритет",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = colors.textSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Priority.entries.forEach { p ->
                        val isSelected = priority == p
                        val (pColor, _) = when (p) {
                            Priority.LOW -> Color(0xFF10B981) to "Низкий"
                            Priority.MEDIUM -> Color(0xFF38BDF8) to "Средний"
                            Priority.HIGH -> Color(0xFFF59E0B) to "Высокий"
                            Priority.URGENT -> Color(0xFFF43F5E) to "Срочно"
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) pColor.copy(alpha = if (colors.isDark) 0.4f else 0.85f)
                                    else (if (colors.isDark) Color.White.copy(alpha = 0.07f) else Color(0xFFF1F5F9))
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) SolidColor(pColor) else colors.borderGradientSubtle,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { priority = p }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = p.titleRu,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 12.sp
                                ),
                                color = if (isSelected) Color.White else colors.textSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Reminder Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "🔔 Напоминание",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = colors.textSecondary
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isReminderEnabled) colors.indigoGlow.copy(alpha = 0.25f) else (if (colors.isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFF1F5F9)))
                            .clickable { isReminderEnabled = !isReminderEnabled }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isReminderEnabled) "Включено" else "Выключено",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = if (isReminderEnabled) colors.indigoGlow else colors.textTertiary
                        )
                    }
                }

                if (isReminderEnabled) {
                    Spacer(modifier = Modifier.height(6.dp))
                    val reminderOptions = listOf(
                        0 to "В дедлайн",
                        30 to "За 30 мин",
                        60 to "За 1 час",
                        180 to "За 3 часа",
                        1440 to "За 1 день"
                    )

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        reminderOptions.forEach { (mins, label) ->
                            val isSelected = reminderMinutes == mins
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (isSelected) colors.cyanGlow.copy(alpha = if (colors.isDark) 0.35f else 0.85f)
                                        else (if (colors.isDark) Color.White.copy(alpha = 0.07f) else Color(0xFFF1F5F9))
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) SolidColor(colors.cyanGlow) else colors.borderGradientSubtle,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .clickable { reminderMinutes = mins }
                                    .padding(horizontal = 9.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 11.5.sp
                                    ),
                                    color = if (isSelected) Color.White else colors.textSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Checklist / Subtasks section
                Text(
                    text = "Чек-лист (подзадачи)",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = colors.textSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))

                // List of current subtasks
                subtasksList.forEachIndexed { index, subtask ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "• ${subtask.title}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.5.sp),
                            color = colors.textPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { subtasksList.removeAt(index) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Удалить пункт",
                                tint = Color(0xFFF43F5E),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // Add subtask input row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        GlassTextField(
                            value = newSubtaskText,
                            onValueChange = { newSubtaskText = it },
                            placeholder = "Добавить пункт (напр. Упр. 1)",
                            testTag = "input_subtask"
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = {
                            if (newSubtaskText.isNotBlank()) {
                                subtasksList.add(
                                    Subtask(
                                        id = UUID.randomUUID().toString(),
                                        title = newSubtaskText.trim(),
                                        isCompleted = false
                                    )
                                )
                                newSubtaskText = ""
                            }
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(colors.indigoGlow.copy(alpha = 0.2f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Добавить",
                            tint = colors.indigoGlow,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Notes / Description
                Text(
                    text = "Заметки и описание (необязательно)",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = colors.textSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                GlassTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = "Ссылки, подробности, комментарии учителя...",
                    singleLine = false,
                    maxLines = 3,
                    shape = RoundedCornerShape(14.dp),
                    testTag = "input_description"
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Bottom Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GlassButton(
                        text = "Отмена",
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        isPrimary = false
                    )

                    GlassButton(
                        text = "Сохранить",
                        onClick = {
                            if (title.isBlank()) {
                                titleError = true
                                return@GlassButton
                            }

                            val finalSubject = if (isCustomSubjectSelected) {
                                if (customSubject.isNotBlank()) customSubject.trim() else "Другое"
                            } else {
                                selectedSubject
                            }

                            onSave(
                                itemToEdit?.id ?: 0L,
                                title,
                                finalSubject,
                                selectedColorHex,
                                "school",
                                description,
                                dueTimestamp,
                                priority,
                                status,
                                subtasksList.toList(),
                                if (isReminderEnabled) reminderMinutes else -1,
                                isReminderEnabled
                            )
                        },
                        modifier = Modifier.weight(1.3f),
                        isPrimary = true,
                        accentColor = colors.indigoGlow,
                        testTag = "save_homework_button"
                    )
                }
            }
        }
    }
}
