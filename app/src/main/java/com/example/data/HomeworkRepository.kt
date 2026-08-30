package com.example.data

import com.example.model.HomeworkItem
import com.example.model.HomeworkStatus
import kotlinx.coroutines.flow.Flow

class HomeworkRepository(private val dao: HomeworkDao) {
    val allHomework: Flow<List<HomeworkItem>> = dao.getAllHomework()

    suspend fun getHomeworkById(id: Long): HomeworkItem? = dao.getHomeworkById(id)

    suspend fun insertHomework(item: HomeworkItem): Long = dao.insertHomework(item)

    suspend fun updateHomework(item: HomeworkItem) = dao.updateHomework(item)

    suspend fun deleteHomework(item: HomeworkItem) = dao.deleteHomework(item)

    suspend fun deleteHomeworkById(id: Long) = dao.deleteHomeworkById(id)

    suspend fun deleteCompleted() = dao.deleteCompletedHomework()

    suspend fun toggleStatus(item: HomeworkItem) {
        val newStatus = when (item.status) {
            HomeworkStatus.TODO -> HomeworkStatus.IN_PROGRESS
            HomeworkStatus.IN_PROGRESS -> HomeworkStatus.COMPLETED
            HomeworkStatus.COMPLETED -> HomeworkStatus.TODO
        }
        val completedAt = if (newStatus == HomeworkStatus.COMPLETED) System.currentTimeMillis() else null
        dao.updateStatus(item.id, newStatus.name, completedAt)
    }

    suspend fun setStatus(id: Long, newStatus: HomeworkStatus) {
        val completedAt = if (newStatus == HomeworkStatus.COMPLETED) System.currentTimeMillis() else null
        dao.updateStatus(id, newStatus.name, completedAt)
    }
}
