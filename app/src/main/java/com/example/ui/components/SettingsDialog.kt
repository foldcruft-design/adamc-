package com.example.ui.components

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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ViewAgenda
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.AppSettings
import com.example.model.AppThemeMode
import com.example.model.SubjectPreset
import com.example.ui.glass.GlassButton
import com.example.ui.glass.GlassCard
import com.example.ui.glass.GlassColors
import com.example.ui.glass.GlassTextField
import com.example.ui.glass.GlassTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsDialog(
    settings: AppSettings,
    allSubjects: List<SubjectPreset>,
    onUpdateSettings: (AppSettings) -> Unit,
    onAddCustomSubject: (name: String, colorHex: String) -> Unit,
    onDeleteCustomSubject: (name: String) -> Unit,
    onClearCompleted: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val colors = GlassTheme.colors

    var newSubjectName by remember { mutableStateOf("") }
    var newSubjectColor by remember { mutableStateOf("#6366F1") }
    var showAddSubjectSection by remember { mutableStateOf(false) }

    var showClearCompletedConfirm by remember { mutableStateOf(false) }
    var showApkInfoDialog by remember { mutableStateOf(false) }

    val presetColors = listOf(
        "#6366F1", "#3B82F6", "#06B6D4", "#10B981",
        "#EC4899", "#F43F5E", "#F59E0B", "#8B5CF6",
        "#14B8A6", "#38BDF8", "#22C55E", "#EAB308"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .heightIn(max = 700.dp)
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
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(colors.indigoGlow.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = null,
                                tint = colors.indigoGlow,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Настройки",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            ),
                            color = colors.textPrimary
                        )
                    }

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

                // SECTION 1: THEME SELECTION
                SettingsSectionTitle(title = "🎨 Тема оформления", icon = Icons.Default.Palette)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppThemeMode.entries.forEach { mode ->
                        val isSelected = settings.themeMode == mode
                        val (modeIcon, shortTitle) = when (mode) {
                            AppThemeMode.LIGHT -> Icons.Default.LightMode to "Светлая"
                            AppThemeMode.DARK -> Icons.Default.DarkMode to "Тёмная"
                            AppThemeMode.SYSTEM -> Icons.Default.PhoneAndroid to "Авто"
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    if (isSelected) colors.indigoGlow.copy(alpha = if (colors.isDark) 0.5f else 0.85f)
                                    else (if (colors.isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFF1F5F9))
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) Color.White.copy(alpha = 0.5f) else Color.Transparent,
                                    RoundedCornerShape(14.dp)
                                )
                                .clickable { onUpdateSettings(settings.copy(themeMode = mode)) }
                                .padding(vertical = 10.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = modeIcon,
                                    contentDescription = null,
                                    tint = if (isSelected) Color.White else colors.textSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = shortTitle,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 12.sp
                                    ),
                                    color = if (isSelected) Color.White else colors.textSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // SECTION 2: DEFAULT DEADLINE & REMINDERS
                SettingsSectionTitle(title = "⏰ Дедлайны по умолчанию", icon = Icons.Default.Schedule)

                // Default Time Picker Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (colors.isDark) Color.White.copy(alpha = 0.06f) else Color(0xFFF1F5F9))
                        .clickable {
                            TimePickerDialog(
                                context,
                                { _, hour, minute ->
                                    onUpdateSettings(
                                        settings.copy(
                                            defaultDeadlineHour = hour,
                                            defaultDeadlineMinute = minute
                                        )
                                    )
                                },
                                settings.defaultDeadlineHour,
                                settings.defaultDeadlineMinute,
                                true
                            ).show()
                        }
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Время сдачи по умолчанию",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = colors.textPrimary
                        )
                        Text(
                            text = "Подставляется при создании новых заданий",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = colors.textTertiary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(colors.indigoGlow.copy(alpha = 0.2f))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = String.format("%02d:%02d", settings.defaultDeadlineHour, settings.defaultDeadlineMinute),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            ),
                            color = colors.indigoGlow
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Default Days Ahead Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Срок по умолчанию:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colors.textSecondary
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(0 to "Сегодня", 1 to "Завтра", 2 to "+2 дня").forEach { (days, label) ->
                            val isSelected = settings.defaultDaysAhead == days
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (isSelected) colors.indigoGlow.copy(alpha = if (colors.isDark) 0.5f else 0.85f)
                                        else (if (colors.isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFF1F5F9))
                                    )
                                    .clickable { onUpdateSettings(settings.copy(defaultDaysAhead = days)) }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 12.sp
                                    ),
                                    color = if (isSelected) Color.White else colors.textSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // SECTION 3: DISPLAY & VIEW OPTIONS
                SettingsSectionTitle(title = "👁️ Отображение и карточки", icon = Icons.Default.ViewAgenda)

                // Compact view toggle
                SettingsToggleRow(
                    title = "Компактный вид карточек",
                    subtitle = "Уменьшает отступы для отображения большего числа заданий",
                    isChecked = settings.isCompactView,
                    onCheckedChange = { onUpdateSettings(settings.copy(isCompactView = it)) }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Show subtasks toggle
                SettingsToggleRow(
                    title = "Чек-лист на карточке",
                    subtitle = "Показывать подзадачи прямо в списке без открытия задания",
                    isChecked = settings.showSubtasksOnCard,
                    onCheckedChange = { onUpdateSettings(settings.copy(showSubtasksOnCard = it)) }
                )

                Spacer(modifier = Modifier.height(18.dp))

                // SECTION 4: SUBJECTS MANAGEMENT
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SettingsSectionTitle(title = "📚 Управление предметами", icon = Icons.Default.School)

                    TextButton(onClick = { showAddSubjectSection = !showAddSubjectSection }) {
                        Text(
                            text = if (showAddSubjectSection) "Скрыть" else "+ Добавить",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = colors.indigoGlow
                        )
                    }
                }

                if (showAddSubjectSection) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (colors.isDark) Color.White.copy(alpha = 0.06f) else Color(0xFFF1F5F9))
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(
                                text = "Новый предмет",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = colors.textPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            GlassTextField(
                                value = newSubjectName,
                                onValueChange = { newSubjectName = it },
                                placeholder = "Название (напр. Черчение, Музыка)",
                                testTag = "input_new_custom_subject"
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Цвет предмета:",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                color = colors.textSecondary
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                presetColors.forEach { hex ->
                                    val isColorSelected = newSubjectColor == hex
                                    val c = try {
                                        Color(android.graphics.Color.parseColor(hex))
                                    } catch (e: Exception) {
                                        Color.Gray
                                    }
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(c)
                                            .border(
                                                2.dp,
                                                if (isColorSelected) Color.White else Color.Transparent,
                                                CircleShape
                                            )
                                            .clickable { newSubjectColor = hex }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))
                            GlassButton(
                                text = "Сохранить предмет",
                                onClick = {
                                    if (newSubjectName.isNotBlank()) {
                                        onAddCustomSubject(newSubjectName.trim(), newSubjectColor)
                                        newSubjectName = ""
                                        showAddSubjectSection = false
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                isPrimary = true,
                                accentColor = colors.indigoGlow
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // List of all subjects with delete option for custom ones
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    allSubjects.forEach { subj ->
                        val c = try {
                            Color(android.graphics.Color.parseColor(subj.colorHex))
                        } catch (e: Exception) {
                            Color(0xFF6366F1)
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(c.copy(alpha = if (colors.isDark) 0.25f else 0.15f))
                                .border(1.dp, c.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = subj.name,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 12.sp
                                    ),
                                    color = if (colors.isDark) Color.White else colors.textPrimary
                                )
                                if (subj.isCustom) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Удалить",
                                        tint = Color(0xFFF43F5E),
                                        modifier = Modifier
                                            .size(14.dp)
                                            .clickable { onDeleteCustomSubject(subj.name) }
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // SECTION 5: DATA CLEANUP & INFO
                SettingsSectionTitle(title = "⚙️ Данные и справка", icon = Icons.Default.Info)

                // Clear completed tasks button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFF43F5E).copy(alpha = if (colors.isDark) 0.12f else 0.08f))
                        .clickable { showClearCompletedConfirm = true }
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = null,
                            tint = Color(0xFFF43F5E),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Удалить все выполненные задания",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = Color(0xFFF43F5E)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // APK info & Native android explanation
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (colors.isDark) Color.White.copy(alpha = 0.06f) else Color(0xFFF1F5F9))
                        .clickable { showApkInfoDialog = true }
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = colors.indigoGlow,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "О приложении и файле APK",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = colors.textPrimary
                            )
                            Text(
                                text = "Нативное приложение Android (Kotlin + Compose)",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                                color = colors.textTertiary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                GlassButton(
                    text = "Готово",
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    isPrimary = true,
                    accentColor = colors.indigoGlow
                )
            }
        }
    }

    // Confirmation dialog for clearing completed tasks
    if (showClearCompletedConfirm) {
        AlertDialog(
            onDismissRequest = { showClearCompletedConfirm = false },
            title = { Text("Очистить выполненные?", color = colors.textPrimary) },
            text = { Text("Все завершенные домашние задания будут безвозвратно удалены.", color = colors.textSecondary) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onClearCompleted()
                        showClearCompletedConfirm = false
                    }
                ) {
                    Text("Удалить", color = Color(0xFFF43F5E))
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearCompletedConfirm = false }) {
                    Text("Отмена", color = colors.textSecondary)
                }
            },
            containerColor = colors.surfaceDialog
        )
    }

    // APK & Native App explanation dialog
    if (showApkInfoDialog) {
        AlertDialog(
            onDismissRequest = { showApkInfoDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PhoneAndroid,
                        contentDescription = null,
                        tint = colors.indigoGlow
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Чистый нативный Android", color = colors.textPrimary)
                }
            },
            text = {
                Column {
                    Text(
                        text = "Это приложение написано на 100% чистом Kotlin и Jetpack Compose для Android (без веб-оберток вроде Capacitor/Vite).\n\nПоэтому сервисы сборки веб-сайтов (Corenna и т.д.) выдают ошибки — им нужен сайт с package.json, а здесь уже готовый нативный проект!\n\nВы можете скачать APK напрямую из верхнего меню AI Studio (Download APK).",
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.5.sp, lineHeight = 20.sp),
                        color = colors.textSecondary
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showApkInfoDialog = false }) {
                    Text("Понятно", color = colors.indigoGlow)
                }
            },
            containerColor = colors.surfaceDialog
        )
    }
}

@Composable
private fun SettingsSectionTitle(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    val colors = GlassTheme.colors
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 13.5.sp
            ),
            color = colors.textPrimary
        )
    }
}

@Composable
private fun SettingsToggleRow(
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val colors = GlassTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (colors.isDark) Color.White.copy(alpha = 0.06f) else Color(0xFFF1F5F9))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = colors.textPrimary
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.5.sp),
                color = colors.textTertiary
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = colors.indigoGlow,
                uncheckedThumbColor = colors.textTertiary,
                uncheckedTrackColor = if (colors.isDark) Color.White.copy(alpha = 0.15f) else Color(0xFFCBD5E1)
            )
        )
    }
}
