package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.HomeworkItem
import com.example.ui.components.AddEditHomeworkDialog
import com.example.ui.components.HomeworkCard
import com.example.ui.components.HomeworkDetailDialog
import com.example.ui.components.SettingsDialog
import com.example.ui.components.StatsGlassCard
import com.example.ui.glass.GlassBackgroundMesh
import com.example.ui.glass.GlassCard
import com.example.ui.glass.GlassColors
import com.example.ui.glass.GlassFilterChip
import com.example.ui.glass.GlassIconButton
import com.example.ui.glass.GlassTextField
import com.example.ui.glass.GlassTheme
import com.example.ui.glass.ProvideGlassTheme
import com.example.ui.viewmodel.HomeworkViewModel
import com.example.ui.viewmodel.SortOption
import com.example.ui.viewmodel.StatusFilter

@Composable
fun HomeworkMainScreen(
    viewModel: HomeworkViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    ProvideGlassTheme(themeMode = uiState.settings.themeMode) {
        val colors = GlassTheme.colors
        val listState = rememberLazyListState()
        var showSortMenu by remember { mutableStateOf(false) }

        GlassBackgroundMesh {
            Scaffold(
                containerColor = Color.Transparent,
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = { viewModel.openAddDialog() },
                        containerColor = colors.indigoGlow,
                        contentColor = Color.White,
                        shape = CircleShape,
                        modifier = Modifier
                            .testTag("fab_add_homework")
                            .padding(end = 6.dp, bottom = 6.dp)
                            .shadow(8.dp, CircleShape, ambientColor = colors.indigoGlow.copy(alpha = 0.5f), spotColor = colors.indigoGlow.copy(alpha = 0.5f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Добавить задание",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            ) { innerPadding ->
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp)
                ) {
                    Spacer(modifier = Modifier.height(10.dp))

                    // Top Bar (App Title, Total count badge, Settings button)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(13.dp))
                                    .background(
                                        Brush.linearGradient(
                                            listOf(colors.indigoGlow, colors.cyanGlow)
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MenuBook,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Домашка",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 22.sp,
                                        letterSpacing = (-0.5).sp
                                    ),
                                    color = colors.textPrimary
                                )
                                Text(
                                    text = "Планировщик & Дедлайны",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Medium
                                    ),
                                    color = colors.textSecondary
                                )
                            }
                        }

                        // Settings and Sort buttons
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box {
                                GlassIconButton(
                                    icon = Icons.Default.Sort,
                                    contentDescription = "Сортировка",
                                    onClick = { showSortMenu = true },
                                    size = 40.dp,
                                    testTag = "button_sort"
                                )
                                DropdownMenu(
                                    expanded = showSortMenu,
                                    onDismissRequest = { showSortMenu = false },
                                    modifier = Modifier
                                        .background(colors.surfaceDialog)
                                        .border(1.dp, colors.borderGradientSubtle, RoundedCornerShape(12.dp))
                                ) {
                                    SortOption.entries.forEach { opt ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = opt.titleRu,
                                                    style = MaterialTheme.typography.bodyMedium.copy(
                                                        fontWeight = if (uiState.selectedSortOption == opt) FontWeight.Bold else FontWeight.Normal
                                                    ),
                                                    color = if (uiState.selectedSortOption == opt) colors.indigoGlow else colors.textPrimary
                                                )
                                            },
                                            onClick = {
                                                viewModel.setSortOption(opt)
                                                showSortMenu = false
                                            }
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            GlassIconButton(
                                icon = Icons.Default.Settings,
                                contentDescription = "Настройки",
                                onClick = { viewModel.openSettingsDialog() },
                                size = 40.dp,
                                testTag = "button_settings"
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Lazy List with all sections
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        // 1. Stats Card
                        item {
                            StatsGlassCard(
                                stats = uiState.stats,
                                onActiveClick = { viewModel.setStatusFilter(StatusFilter.ACTIVE) },
                                onCompletedClick = { viewModel.setStatusFilter(StatusFilter.COMPLETED) },
                                onOverdueClick = { viewModel.setStatusFilter(StatusFilter.OVERDUE) }
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                        }

                        // 2. Search bar
                        item {
                            GlassTextField(
                                value = uiState.searchQuery,
                                onValueChange = { viewModel.setSearchQuery(it) },
                                placeholder = "Поиск по предмету, теме или заметкам...",
                                leadingIcon = Icons.Default.Search,
                                testTag = "search_input"
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        // 3. Status Filters (Horizontal Scroll)
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                StatusFilter.entries.forEach { filter ->
                                    val count = when (filter) {
                                        StatusFilter.ALL -> uiState.allItems.size
                                        StatusFilter.ACTIVE -> uiState.stats.activeCount
                                        StatusFilter.TODAY -> uiState.allItems.count { it.isDueToday() }
                                        StatusFilter.COMPLETED -> uiState.stats.completedCount
                                        StatusFilter.OVERDUE -> uiState.stats.overdueCount
                                    }

                                    GlassFilterChip(
                                        text = filter.titleRu,
                                        isSelected = uiState.selectedStatusFilter == filter,
                                        count = count,
                                        onClick = { viewModel.setStatusFilter(filter) },
                                        testTag = "filter_status_${filter.name}"
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        // 4. Subject Tags (if available)
                        if (uiState.activeSubjectNames.isNotEmpty()) {
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    // "All subjects" chip
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(
                                                if (uiState.selectedSubjectFilter == null) colors.indigoGlow.copy(alpha = if (colors.isDark) 0.5f else 0.85f)
                                                else (if (colors.isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFF1F5F9))
                                            )
                                            .clickable { viewModel.setSubjectFilter(null) }
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = "Все предметы",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = if (uiState.selectedSubjectFilter == null) FontWeight.Bold else FontWeight.Normal,
                                                fontSize = 11.5.sp
                                            ),
                                            color = if (uiState.selectedSubjectFilter == null) Color.White else colors.textSecondary
                                        )
                                    }

                                    uiState.activeSubjectNames.forEach { subject ->
                                        val isSelected = uiState.selectedSubjectFilter == subject
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(
                                                    if (isSelected) colors.indigoGlow.copy(alpha = if (colors.isDark) 0.5f else 0.85f)
                                                    else (if (colors.isDark) Color.White.copy(alpha = 0.08f) else Color(0xFFF1F5F9))
                                                )
                                                .clickable { viewModel.setSubjectFilter(subject) }
                                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                        ) {
                                            Text(
                                                text = subject,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                    fontSize = 11.5.sp
                                                ),
                                                color = if (isSelected) Color.White else colors.textSecondary
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }

                        // 5. Task List / Empty State
                        if (uiState.filteredItems.isEmpty()) {
                            item {
                                Spacer(modifier = Modifier.height(40.dp))
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.padding(horizontal = 24.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(72.dp)
                                                .clip(CircleShape)
                                                .background(colors.indigoGlow.copy(alpha = 0.15f)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.AutoAwesome,
                                                contentDescription = null,
                                                tint = colors.indigoGlow,
                                                modifier = Modifier.size(36.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(
                                            text = if (uiState.searchQuery.isNotBlank() || uiState.selectedSubjectFilter != null) {
                                                "Ничего не найдено"
                                            } else {
                                                "Заданий пока нет!"
                                            },
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 18.sp
                                            ),
                                            color = colors.textPrimary
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = if (uiState.searchQuery.isNotBlank() || uiState.selectedSubjectFilter != null) {
                                                "Попробуйте изменить поисковый запрос или фильтры"
                                            } else {
                                                "Нажмите на кнопку «+», чтобы добавить первое домашнее задание и не забыть о дедлайнах."
                                            },
                                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.5.sp),
                                            color = colors.textSecondary,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        } else {
                            items(
                                items = uiState.filteredItems,
                                key = { it.id }
                            ) { homework ->
                                HomeworkCard(
                                    item = homework,
                                    onClick = { viewModel.openDetailDialog(homework) },
                                    onToggleStatus = { viewModel.toggleTaskStatus(homework) },
                                    onToggleSubtask = { subId -> viewModel.toggleSubtask(homework, subId) },
                                    onEdit = { viewModel.openEditDialog(homework) },
                                    onDelete = { viewModel.deleteHomework(homework) },
                                    isCompactView = uiState.settings.isCompactView,
                                    showSubtasksSetting = uiState.settings.showSubtasksOnCard
                                )
                            }
                        }
                    }
                }

                // Add / Edit Dialog
                if (uiState.isAddEditOpen) {
                    AddEditHomeworkDialog(
                        itemToEdit = uiState.editingItem,
                        availableSubjects = uiState.allAvailableSubjects,
                        settings = uiState.settings,
                        onDismiss = { viewModel.closeAddEditDialog() },
                        onSave = { id, title, subject, color, icon, desc, due, priority, status, subtasks, reminder, isReminderSet ->
                            viewModel.saveHomework(
                                id = id,
                                title = title,
                                subject = subject,
                                subjectColorHex = color,
                                subjectIconName = icon,
                                description = desc,
                                dueTimestamp = due,
                                priority = priority,
                                status = status,
                                subtasks = subtasks,
                                reminderMinutesBefore = reminder,
                                isReminderSet = isReminderSet
                            )
                        }
                    )
                }

                // Detail Dialog
                if (uiState.isDetailOpen && uiState.detailItem != null) {
                    val currentItem = uiState.detailItem!!
                    HomeworkDetailDialog(
                        item = currentItem,
                        onDismiss = { viewModel.closeDetailDialog() },
                        onToggleStatus = { viewModel.toggleTaskStatus(currentItem) },
                        onToggleSubtask = { subId -> viewModel.toggleSubtask(currentItem, subId) },
                        onEdit = {
                            viewModel.closeDetailDialog()
                            viewModel.openEditDialog(currentItem)
                        },
                        onDelete = { viewModel.deleteHomework(currentItem) }
                    )
                }

                // Settings Dialog
                if (uiState.isSettingsOpen) {
                    SettingsDialog(
                        settings = uiState.settings,
                        allSubjects = uiState.allAvailableSubjects,
                        onUpdateSettings = { viewModel.updateSettings(it) },
                        onAddCustomSubject = { name, color -> viewModel.addCustomSubject(name, color) },
                        onDeleteCustomSubject = { name -> viewModel.deleteCustomSubject(name) },
                        onClearCompleted = { viewModel.deleteCompleted() },
                        onDismiss = { viewModel.closeSettingsDialog() }
                    )
                }
            }
        }
    }
}
