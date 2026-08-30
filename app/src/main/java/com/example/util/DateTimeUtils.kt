package com.example.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object DateTimeUtils {
    private val timeFormat = SimpleDateFormat("HH:mm", Locale("ru"))
    private val fullDateFormat = SimpleDateFormat("d MMMM, HH:mm", Locale("ru"))
    private val dayMonthFormat = SimpleDateFormat("d MMM", Locale("ru"))

    fun formatDateTime(timestamp: Long): String {
        return fullDateFormat.format(Date(timestamp))
    }

    fun formatTime(timestamp: Long): String {
        return timeFormat.format(Date(timestamp))
    }

    fun formatDate(timestamp: Long): String {
        return dayMonthFormat.format(Date(timestamp))
    }

    fun formatRelativeDeadline(dueTimestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = dueTimestamp - now

        val nowCal = Calendar.getInstance()
        val dueCal = Calendar.getInstance().apply { timeInMillis = dueTimestamp }

        val isSameDay = nowCal.get(Calendar.YEAR) == dueCal.get(Calendar.YEAR) &&
                nowCal.get(Calendar.DAY_OF_YEAR) == dueCal.get(Calendar.DAY_OF_YEAR)

        val tomorrowCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, 1) }
        val isTomorrow = tomorrowCal.get(Calendar.YEAR) == dueCal.get(Calendar.YEAR) &&
                tomorrowCal.get(Calendar.DAY_OF_YEAR) == dueCal.get(Calendar.DAY_OF_YEAR)

        val yesterdayCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
        val isYesterday = yesterdayCal.get(Calendar.YEAR) == dueCal.get(Calendar.YEAR) &&
                yesterdayCal.get(Calendar.DAY_OF_YEAR) == dueCal.get(Calendar.DAY_OF_YEAR)

        val timeStr = timeFormat.format(Date(dueTimestamp))

        if (diff < 0) {
            val overdueMillis = -diff
            val hours = TimeUnit.MILLISECONDS.toHours(overdueMillis)
            val days = TimeUnit.MILLISECONDS.toDays(overdueMillis)
            return when {
                days > 0 -> "Просрочено на $days дн."
                hours > 0 -> "Просрочено на $hours ч."
                else -> "Просрочено только что"
            }
        }

        return when {
            isSameDay -> "Сегодня в $timeStr"
            isTomorrow -> "Завтра в $timeStr"
            isYesterday -> "Вчера в $timeStr"
            diff < TimeUnit.DAYS.toMillis(7) -> {
                val dayOfWeekFormat = SimpleDateFormat("EEEE, HH:mm", Locale("ru"))
                val formatted = dayOfWeekFormat.format(Date(dueTimestamp))
                formatted.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale("ru")) else it.toString() }
            }
            else -> fullDateFormat.format(Date(dueTimestamp))
        }
    }

    fun getRemainingTimeText(dueTimestamp: Long): String {
        val diff = dueTimestamp - System.currentTimeMillis()
        if (diff <= 0) return "Время вышло"

        val days = TimeUnit.MILLISECONDS.toDays(diff)
        val hours = TimeUnit.MILLISECONDS.toHours(diff) % 24
        val minutes = TimeUnit.MILLISECONDS.toMinutes(diff) % 60

        return when {
            days > 0 -> "$days д. $hours ч."
            hours > 0 -> "$hours ч. $minutes мин."
            else -> "$minutes мин."
        }
    }
}
