package com.example.model

enum class AppThemeMode(val titleRu: String) {
    LIGHT("Светлая (белое стекло)"),
    DARK("Тёмная (ночное стекло)"),
    SYSTEM("Как в системе")
}

data class AppSettings(
    val themeMode: AppThemeMode = AppThemeMode.LIGHT,
    val defaultDeadlineHour: Int = 18,
    val defaultDeadlineMinute: Int = 0,
    val defaultDaysAhead: Int = 1, // 0 = today, 1 = tomorrow, 2 = day after tomorrow
    val defaultReminderMinutes: Int = 60, // 60 min before
    val isReminderEnabledByDefault: Boolean = true,
    val isCompactView: Boolean = false,
    val showSubtasksOnCard: Boolean = true,
    val hideCompletedTasks: Boolean = false,
    val customSubjectsJson: String = ""
)
