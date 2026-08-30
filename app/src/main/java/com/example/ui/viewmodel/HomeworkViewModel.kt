package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.HomeworkRepository
import com.example.data.SettingsManager
import com.example.model.AppSettings
import com.example.model.AppThemeMode
import com.example.model.HomeworkItem
import com.example.model.HomeworkStatus
import com.example.model.Priority
import com.example.model.SubjectPreset
import com.example.model.Subtask
import com.example.util.ReminderScheduler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

enum class StatusFilter(val titleRu: String) {
    ALL("Все"),
    ACTIVE("В работе"),
    TODAY("Сегодня"),
    COMPLETED("Готово"),
    OVERDUE("Просрочено")
}

enum class SortOption(val titleRu: String) {
    DEADLINE_ASC("Сначала срочные"),
    PRIORITY_DESC("По приоритету"),
    SUBJECT_ASC("По предмету"),
    CREATED_DESC("Недавно добавленные")
}

data class HomeworkStats(
    val totalCount: Int = 0,
    val activeCount: Int = 0,
    val completedCount: Int = 0,
    val overdueCount: Int = 0,
    val completionPercentage: Int = 0,
    val nearestItem: HomeworkItem? = null
)

data class HomeworkUiState(
    val allItems: List<HomeworkItem> = emptyList(),
    val filteredItems: List<HomeworkItem> = emptyList(),
    val searchQuery: String = "",
    val selectedSubjectFilter: String? = null,
    val selectedStatusFilter: StatusFilter = StatusFilter.ALL,
    val selectedSortOption: SortOption = SortOption.DEADLINE_ASC,
    val stats: HomeworkStats = HomeworkStats(),
    val isAddEditOpen: Boolean = false,
    val editingItem: HomeworkItem? = null,
    val isDetailOpen: Boolean = false,
    val detailItem: HomeworkItem? = null,
    val isSettingsOpen: Boolean = false,
    val settings: AppSettings = AppSettings(),
    val allAvailableSubjects: List<SubjectPreset> = SubjectPreset.DEFAULT_SUBJECTS,
    val activeSubjectNames: List<String> = emptyList()
)

class HomeworkViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: HomeworkRepository
    private val settingsManager: SettingsManager = SettingsManager(application)

    private val _searchQuery = MutableStateFlow("")
    private val _selectedSubjectFilter = MutableStateFlow<String?>(null)
    private val _selectedStatusFilter = MutableStateFlow(StatusFilter.ALL)
    private val _selectedSortOption = MutableStateFlow(SortOption.DEADLINE_ASC)
    private val _isAddEditOpen = MutableStateFlow(false)
    private val _editingItem = MutableStateFlow<HomeworkItem?>(null)
    private val _isDetailOpen = MutableStateFlow(false)
    private val _detailItem = MutableStateFlow<HomeworkItem?>(null)
    private val _isSettingsOpen = MutableStateFlow(false)

    init {
        val db = AppDatabase.getDatabase(application, viewModelScope)
        repository = HomeworkRepository(db.homeworkDao())
    }

    val uiState: StateFlow<HomeworkUiState> = combine(
        repository.allHomework,
        _searchQuery,
        _selectedSubjectFilter,
        _selectedStatusFilter,
        _selectedSortOption,
        _isAddEditOpen,
        _editingItem,
        _isDetailOpen,
        _detailItem,
        _isSettingsOpen,
        settingsManager.settings
    ) { params ->
        val rawList = params[0] as List<HomeworkItem>
        val query = params[1] as String
        val subjectFilter = params[2] as String?
        val statusFilter = params[3] as StatusFilter
        val sortOption = params[4] as SortOption
        val isAddEdit = params[5] as Boolean
        val editing = params[6] as HomeworkItem?
        val isDetail = params[7] as Boolean
        val detail = params[8] as HomeworkItem?
        val isSettings = params[9] as Boolean
        val currentSettings = params[10] as AppSettings

        val now = System.currentTimeMillis()

        // Combine default presets and custom subjects from settings
        val customPresets = SubjectPreset.parseCustomSubjects(currentSettings.customSubjectsJson)
        val allPresets = SubjectPreset.DEFAULT_SUBJECTS + customPresets

        // 1. Filter by query
        var list = if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            rawList.filter {
                it.title.lowercase().contains(q) ||
                        it.subject.lowercase().contains(q) ||
                        it.description.lowercase().contains(q)
            }
        } else {
            rawList
        }

        // 2. Filter by subject
        if (subjectFilter != null) {
            list = list.filter { it.subject == subjectFilter }
        }

        // 3. Filter by status
        list = when (statusFilter) {
            StatusFilter.ALL -> {
                if (currentSettings.hideCompletedTasks) list.filter { it.status != HomeworkStatus.COMPLETED }
                else list
            }
            StatusFilter.ACTIVE -> list.filter { it.status != HomeworkStatus.COMPLETED }
            StatusFilter.TODAY -> list.filter { it.isDueToday() || (it.dueTimestamp <= now + 86400000L && it.status != HomeworkStatus.COMPLETED) }
            StatusFilter.COMPLETED -> list.filter { it.status == HomeworkStatus.COMPLETED }
            StatusFilter.OVERDUE -> list.filter { it.isOverdue() }
        }

        // 4. Sort
        list = when (sortOption) {
            SortOption.DEADLINE_ASC -> list.sortedWith(
                compareBy<HomeworkItem> { it.status == HomeworkStatus.COMPLETED }
                    .thenBy { it.dueTimestamp }
            )
            SortOption.PRIORITY_DESC -> list.sortedWith(
                compareBy<HomeworkItem> { it.status == HomeworkStatus.COMPLETED }
                    .thenByDescending { it.priority.level }
                    .thenBy { it.dueTimestamp }
            )
            SortOption.SUBJECT_ASC -> list.sortedWith(
                compareBy<HomeworkItem> { it.status == HomeworkStatus.COMPLETED }
                    .thenBy { it.subject }
            )
            SortOption.CREATED_DESC -> list.sortedByDescending { it.createdAt }
        }

        // Stats calculation
        val total = rawList.size
        val completed = rawList.count { it.status == HomeworkStatus.COMPLETED }
        val active = total - completed
        val overdue = rawList.count { it.isOverdue() }
        val percentage = if (total > 0) ((completed.toFloat() / total) * 100).toInt() else 0

        val nearest = rawList
            .filter { it.status != HomeworkStatus.COMPLETED && it.dueTimestamp >= now }
            .minByOrNull { it.dueTimestamp }

        val activeSubjects = rawList.map { it.subject }.distinct().sorted()

        HomeworkUiState(
            allItems = rawList,
            filteredItems = list,
            searchQuery = query,
            selectedSubjectFilter = subjectFilter,
            selectedStatusFilter = statusFilter,
            selectedSortOption = sortOption,
            stats = HomeworkStats(
                totalCount = total,
                activeCount = active,
                completedCount = completed,
                overdueCount = overdue,
                completionPercentage = percentage,
                nearestItem = nearest
            ),
            isAddEditOpen = isAddEdit,
            editingItem = editing,
            isDetailOpen = isDetail,
            detailItem = detail,
            isSettingsOpen = isSettings,
            settings = currentSettings,
            allAvailableSubjects = allPresets,
            activeSubjectNames = activeSubjects
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeworkUiState()
    )

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSubjectFilter(subject: String?) {
        _selectedSubjectFilter.value = if (_selectedSubjectFilter.value == subject) null else subject
    }

    fun setStatusFilter(filter: StatusFilter) {
        _selectedStatusFilter.value = filter
    }

    fun setSortOption(sort: SortOption) {
        _selectedSortOption.value = sort
    }

    fun openAddDialog() {
        _editingItem.value = null
        _isAddEditOpen.value = true
    }

    fun openEditDialog(item: HomeworkItem) {
        _editingItem.value = item
        _isAddEditOpen.value = true
    }

    fun closeAddEditDialog() {
        _isAddEditOpen.value = false
        _editingItem.value = null
    }

    fun openDetailDialog(item: HomeworkItem) {
        _detailItem.value = item
        _isDetailOpen.value = true
    }

    fun closeDetailDialog() {
        _isDetailOpen.value = false
        _detailItem.value = null
    }

    fun openSettingsDialog() {
        _isSettingsOpen.value = true
    }

    fun closeSettingsDialog() {
        _isSettingsOpen.value = false
    }

    fun updateSettings(newSettings: AppSettings) {
        settingsManager.updateSettings(newSettings)
    }

    fun addCustomSubject(name: String, colorHex: String) {
        val currentCustom = SubjectPreset.parseCustomSubjects(settingsManager.settings.value.customSubjectsJson).toMutableList()
        if (currentCustom.none { it.name.equals(name, ignoreCase = true) }) {
            currentCustom.add(SubjectPreset(name = name, colorHex = colorHex, isCustom = true))
            val json = SubjectPreset.customSubjectsToJson(currentCustom)
            settingsManager.updateSettings(settingsManager.settings.value.copy(customSubjectsJson = json))
        }
    }

    fun deleteCustomSubject(name: String) {
        val currentCustom = SubjectPreset.parseCustomSubjects(settingsManager.settings.value.customSubjectsJson).toMutableList()
        currentCustom.removeAll { it.name.equals(name, ignoreCase = true) }
        val json = SubjectPreset.customSubjectsToJson(currentCustom)
        settingsManager.updateSettings(settingsManager.settings.value.copy(customSubjectsJson = json))
    }

    fun saveHomework(
        id: Long = 0,
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
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val item = HomeworkItem(
                id = id,
                title = title.trim(),
                subject = subject.trim(),
                subjectColorHex = subjectColorHex,
                subjectIconName = subjectIconName,
                description = description.trim(),
                dueTimestamp = dueTimestamp,
                priority = priority,
                status = status,
                subtasksJson = HomeworkItem.subtasksToJson(subtasks),
                reminderMinutesBefore = reminderMinutesBefore,
                isReminderSet = isReminderSet
            )
            val generatedId = if (id == 0L) {
                repository.insertHomework(item)
            } else {
                repository.updateHomework(item)
                id
            }

            val itemWithId = item.copy(id = generatedId)
            if (isReminderSet && reminderMinutesBefore >= 0) {
                ReminderScheduler.scheduleReminder(getApplication(), itemWithId)
            } else {
                ReminderScheduler.cancelReminder(getApplication(), generatedId)
            }
        }
        closeAddEditDialog()
    }

    fun toggleTaskStatus(item: HomeworkItem) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.toggleStatus(item)
            if (item.status != HomeworkStatus.COMPLETED) {
                ReminderScheduler.cancelReminder(getApplication(), item.id)
            }
            if (_detailItem.value?.id == item.id) {
                val updated = repository.getHomeworkById(item.id)
                _detailItem.value = updated
            }
        }
    }

    fun toggleSubtask(item: HomeworkItem, subtaskId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val subtasks = item.getSubtaskList().map { sub ->
                if (sub.id == subtaskId) sub.copy(isCompleted = !sub.isCompleted) else sub
            }
            val allDone = subtasks.isNotEmpty() && subtasks.all { it.isCompleted }
            val newStatus = if (allDone) HomeworkStatus.COMPLETED else item.status
            val updated = item.copy(
                subtasksJson = HomeworkItem.subtasksToJson(subtasks),
                status = newStatus
            )
            repository.updateHomework(updated)
            if (_detailItem.value?.id == item.id) {
                _detailItem.value = updated
            }
        }
    }

    fun deleteHomework(item: HomeworkItem) {
        viewModelScope.launch(Dispatchers.IO) {
            ReminderScheduler.cancelReminder(getApplication(), item.id)
            repository.deleteHomework(item)
            if (_detailItem.value?.id == item.id) {
                closeDetailDialog()
            }
        }
    }

    fun deleteCompleted() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteCompleted()
        }
    }
}
