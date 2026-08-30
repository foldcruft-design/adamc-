package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.model.HomeworkConverters
import com.example.model.HomeworkItem
import com.example.model.HomeworkStatus
import com.example.model.Priority
import com.example.model.Subtask
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

@Database(entities = [HomeworkItem::class], version = 1, exportSchema = false)
@TypeConverters(HomeworkConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun homeworkDao(): HomeworkDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "homework_planner_db"
                )
                    .addCallback(DatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.homeworkDao())
                    }
                }
            }
        }

        suspend fun populateInitialData(dao: HomeworkDao) {
            val now = Calendar.getInstance()

            // Task 1: Due today in 4 hours
            val cal1 = Calendar.getInstance().apply {
                add(Calendar.HOUR_OF_DAY, 4)
                set(Calendar.MINUTE, 0)
            }
            val subtasks1 = listOf(
                Subtask("1", "Параграф 14, прочитать теорию", true),
                Subtask("2", "Упражнение № 324 (а, б, в)", false),
                Subtask("3", "Задание со звездочкой № 330", false)
            )
            dao.insertHomework(
                HomeworkItem(
                    title = "Алгебра: Квадратные уравнения",
                    subject = "Алгебра",
                    subjectColorHex = "#6366F1",
                    subjectIconName = "calculate",
                    description = "Подготовиться к контрольной работе. Решить номера из учебника на стр. 88.",
                    dueTimestamp = cal1.timeInMillis,
                    priority = Priority.HIGH,
                    status = HomeworkStatus.IN_PROGRESS,
                    subtasksJson = HomeworkItem.subtasksToJson(subtasks1),
                    reminderMinutesBefore = 60,
                    isReminderSet = true
                )
            )

            // Task 2: Due tomorrow at 18:00
            val cal2 = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, 1)
                set(Calendar.HOUR_OF_DAY, 18)
                set(Calendar.MINUTE, 0)
            }
            val subtasks2 = listOf(
                Subtask("1", "Выбрать тему сочинения", true),
                Subtask("2", "Составить тезисный план", true),
                Subtask("3", "Написать черновик", false),
                Subtask("4", "Переписать в чистовик", false)
            )
            dao.insertHomework(
                HomeworkItem(
                    title = "Сочинение по повести «Капитанская дочка»",
                    subject = "Литература",
                    subjectColorHex = "#F43F5E",
                    subjectIconName = "menu_book",
                    description = "Тема: Образ Петра Гринева и нравственный выбор. Объем не менее 250 слов.",
                    dueTimestamp = cal2.timeInMillis,
                    priority = Priority.URGENT,
                    status = HomeworkStatus.TODO,
                    subtasksJson = HomeworkItem.subtasksToJson(subtasks2),
                    reminderMinutesBefore = 180,
                    isReminderSet = true
                )
            )

            // Task 3: Due in 3 days
            val cal3 = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, 3)
                set(Calendar.HOUR_OF_DAY, 15)
                set(Calendar.MINUTE, 30)
            }
            val subtasks3 = listOf(
                Subtask("1", "Выучить 20 новых слов из Unit 5", false),
                Subtask("2", "Workbook стр. 42-43", false)
            )
            dao.insertHomework(
                HomeworkItem(
                    title = "English Grammar: Present Perfect vs Past Simple",
                    subject = "Английский язык",
                    subjectColorHex = "#38BDF8",
                    subjectIconName = "translate",
                    description = "Выполнить упражнения в рабочей тетради и составить 5 собственных предложений.",
                    dueTimestamp = cal3.timeInMillis,
                    priority = Priority.MEDIUM,
                    status = HomeworkStatus.TODO,
                    subtasksJson = HomeworkItem.subtasksToJson(subtasks3),
                    reminderMinutesBefore = 1440,
                    isReminderSet = false
                )
            )

            // Task 4: Completed task
            val cal4 = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, -1)
                set(Calendar.HOUR_OF_DAY, 12)
            }
            dao.insertHomework(
                HomeworkItem(
                    title = "Законы Ньютона: Лабораторная работа",
                    subject = "Физика",
                    subjectColorHex = "#06B6D4",
                    subjectIconName = "bolt",
                    description = "Оформить отчет по лабораторной работе №2 и сделать выводы.",
                    dueTimestamp = cal4.timeInMillis,
                    priority = Priority.MEDIUM,
                    status = HomeworkStatus.COMPLETED,
                    subtasksJson = "[]",
                    reminderMinutesBefore = -1,
                    isReminderSet = false,
                    completedAt = System.currentTimeMillis() - 86400000L
                )
            )
        }
    }
}
