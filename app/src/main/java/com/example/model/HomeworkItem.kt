package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import org.json.JSONArray
import org.json.JSONObject

@Entity(tableName = "homework_items")
data class HomeworkItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val subject: String,
    val subjectColorHex: String = "#6366F1",
    val subjectIconName: String = "school",
    val description: String = "",
    val dueTimestamp: Long,
    val priority: Priority = Priority.MEDIUM,
    val status: HomeworkStatus = HomeworkStatus.TODO,
    val subtasksJson: String = "[]",
    val reminderMinutesBefore: Int = -1, // -1 means no reminder, 0 = at deadline, 60 = 1 hr before, etc.
    val isReminderSet: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
) {
    fun getSubtaskList(): List<Subtask> {
        if (subtasksJson.isBlank() || subtasksJson == "[]") return emptyList()
        val list = mutableListOf<Subtask>()
        try {
            val jsonArray = JSONArray(subtasksJson)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    Subtask(
                        id = obj.optString("id", i.toString()),
                        title = obj.optString("title", ""),
                        isCompleted = obj.optBoolean("isCompleted", false)
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    fun isOverdue(): Boolean {
        return status != HomeworkStatus.COMPLETED && dueTimestamp < System.currentTimeMillis()
    }

    fun isDueToday(): Boolean {
        val now = java.util.Calendar.getInstance()
        val due = java.util.Calendar.getInstance().apply { timeInMillis = dueTimestamp }
        return now.get(java.util.Calendar.YEAR) == due.get(java.util.Calendar.YEAR) &&
                now.get(java.util.Calendar.DAY_OF_YEAR) == due.get(java.util.Calendar.DAY_OF_YEAR)
    }

    companion object {
        fun subtasksToJson(subtasks: List<Subtask>): String {
            val jsonArray = JSONArray()
            subtasks.forEach { subtask ->
                val obj = JSONObject()
                obj.put("id", subtask.id)
                obj.put("title", subtask.title)
                obj.put("isCompleted", subtask.isCompleted)
                jsonArray.put(obj)
            }
            return jsonArray.toString()
        }
    }
}

class HomeworkConverters {
    @TypeConverter
    fun fromPriority(priority: Priority): String = priority.name

    @TypeConverter
    fun toPriority(value: String): Priority = try {
        Priority.valueOf(value)
    } catch (e: Exception) {
        Priority.MEDIUM
    }

    @TypeConverter
    fun fromStatus(status: HomeworkStatus): String = status.name

    @TypeConverter
    fun toStatus(value: String): HomeworkStatus = try {
        HomeworkStatus.valueOf(value)
    } catch (e: Exception) {
        HomeworkStatus.TODO
    }
}
