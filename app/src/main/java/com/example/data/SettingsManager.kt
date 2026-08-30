package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.AppSettings
import com.example.model.AppThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("homework_app_settings", Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    private fun loadSettings(): AppSettings {
        val themeStr = prefs.getString("theme_mode", AppThemeMode.LIGHT.name) ?: AppThemeMode.LIGHT.name
        val themeMode = try {
            AppThemeMode.valueOf(themeStr)
        } catch (e: Exception) {
            AppThemeMode.LIGHT
        }

        return AppSettings(
            themeMode = themeMode,
            defaultDeadlineHour = prefs.getInt("default_deadline_hour", 18),
            defaultDeadlineMinute = prefs.getInt("default_deadline_minute", 0),
            defaultDaysAhead = prefs.getInt("default_days_ahead", 1),
            defaultReminderMinutes = prefs.getInt("default_reminder_minutes", 60),
            isReminderEnabledByDefault = prefs.getBoolean("default_reminder_enabled", true),
            isCompactView = prefs.getBoolean("is_compact_view", false),
            showSubtasksOnCard = prefs.getBoolean("show_subtasks_card", true),
            hideCompletedTasks = prefs.getBoolean("hide_completed_tasks", false),
            customSubjectsJson = prefs.getString("custom_subjects_json", "") ?: ""
        )
    }

    fun updateSettings(newSettings: AppSettings) {
        prefs.edit().apply {
            putString("theme_mode", newSettings.themeMode.name)
            putInt("default_deadline_hour", newSettings.defaultDeadlineHour)
            putInt("default_deadline_minute", newSettings.defaultDeadlineMinute)
            putInt("default_days_ahead", newSettings.defaultDaysAhead)
            putInt("default_reminder_minutes", newSettings.defaultReminderMinutes)
            putBoolean("default_reminder_enabled", newSettings.isReminderEnabledByDefault)
            putBoolean("is_compact_view", newSettings.isCompactView)
            putBoolean("show_subtasks_card", newSettings.showSubtasksOnCard)
            putBoolean("hide_completed_tasks", newSettings.hideCompletedTasks)
            putString("custom_subjects_json", newSettings.customSubjectsJson)
            apply()
        }
        _settings.value = newSettings
    }

    fun setThemeMode(mode: AppThemeMode) {
        val current = _settings.value
        updateSettings(current.copy(themeMode = mode))
    }

    fun setCompactView(isCompact: Boolean) {
        val current = _settings.value
        updateSettings(current.copy(isCompactView = isCompact))
    }

    fun setShowSubtasksOnCard(show: Boolean) {
        val current = _settings.value
        updateSettings(current.copy(showSubtasksOnCard = show))
    }

    fun setHideCompletedTasks(hide: Boolean) {
        val current = _settings.value
        updateSettings(current.copy(hideCompletedTasks = hide))
    }
}
