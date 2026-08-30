package com.example.util

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.model.HomeworkItem
import com.example.receiver.HomeworkReminderReceiver

object ReminderScheduler {

    @SuppressLint("ScheduleExactAlarm")
    fun scheduleReminder(context: Context, item: HomeworkItem) {
        if (!item.isReminderSet || item.reminderMinutesBefore < 0) {
            cancelReminder(context, item.id)
            return
        }

        val triggerTime = item.dueTimestamp - (item.reminderMinutesBefore * 60 * 1000L)
        if (triggerTime <= System.currentTimeMillis()) {
            // Target reminder time is already in the past
            return
        }

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, HomeworkReminderReceiver::class.java).apply {
            putExtra(HomeworkReminderReceiver.EXTRA_TASK_ID, item.id)
            putExtra(HomeworkReminderReceiver.EXTRA_TASK_TITLE, item.title)
            putExtra(HomeworkReminderReceiver.EXTRA_TASK_SUBJECT, item.subject)
            putExtra(HomeworkReminderReceiver.EXTRA_DEADLINE, DateTimeUtils.formatDateTime(item.dueTimestamp))
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            item.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                    )
                } else {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                    )
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun cancelReminder(context: Context, taskId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, HomeworkReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskId.toInt(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }
}
