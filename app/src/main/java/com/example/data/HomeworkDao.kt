package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.model.HomeworkItem
import kotlinx.coroutines.flow.Flow

@Dao
interface HomeworkDao {
    @Query("SELECT * FROM homework_items ORDER BY dueTimestamp ASC")
    fun getAllHomework(): Flow<List<HomeworkItem>>

    @Query("SELECT * FROM homework_items WHERE id = :id LIMIT 1")
    suspend fun getHomeworkById(id: Long): HomeworkItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHomework(item: HomeworkItem): Long

    @Update
    suspend fun updateHomework(item: HomeworkItem)

    @Delete
    suspend fun deleteHomework(item: HomeworkItem)

    @Query("DELETE FROM homework_items WHERE id = :id")
    suspend fun deleteHomeworkById(id: Long)

    @Query("DELETE FROM homework_items WHERE status = 'COMPLETED'")
    suspend fun deleteCompletedHomework()

    @Query("UPDATE homework_items SET status = :status, completedAt = :completedAt WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String, completedAt: Long?)
}
