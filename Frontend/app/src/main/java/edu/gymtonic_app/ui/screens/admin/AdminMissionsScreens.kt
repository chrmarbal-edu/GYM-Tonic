package edu.gymtonic_app.ui.screens.admin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.gymtonic_app.ui.i18n.LocalStrings
import edu.gymtonic_app.ui.viewmodel.admin.AdminMissionsViewModel

@Composable
fun AdminMissionsListScreen(
    onBack: () -> Unit,
    onOpenEdit: (Int) -> Unit,
    onCreate: () -> Unit,
    viewModel: AdminMissionsViewModel = viewModel()
) {
    val strings = LocalStrings.current
    val state by viewModel.listState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { viewModel.loadList() }

    AdminShellScreen(
        title = strings.adminMissions,
        onBack = onBack,
        onCreateClick = onCreate
    ) {
        val filteredItems = remember(state.items, searchQuery) {
            state.items.filter { it.missionName.contains(searchQuery, ignoreCase = true) }
        }

        Column(Modifier.fillMaxSize()) {
            AdminSearchBar(query = searchQuery, onQueryChange = { searchQuery = it })

            AdminListContent(
                isLoading = state.isLoading,
                error = state.error,
                emptyMessage = strings.adminEmptyList,
                itemsCount = filteredItems.size,
                onRetry = { viewModel.loadList() }
            ) {
                AdminSimpleList(
                    items = filteredItems,
                    titleFor = { it.missionName },
                    subtitleFor = {
                        "${missionTypeLabel(it.missionType)} · ${missionObjectiveLabel(it.missionObjective)} · Meta: ${it.missionGoal ?: 0} · ${it.missionPoints} pts"
                    },
                    onItemClick = { onOpenEdit(it.missionId) }
                )
            }
        }
    }
}

@Composable
fun AdminMissionEditScreen(
    missionId: Int?,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: AdminMissionsViewModel = viewModel()
) {
    val strings = LocalStrings.current
    val state by viewModel.detailState.collectAsState()
    var name by remember { mutableStateOf("") }
    var type by remember { mutableIntStateOf(0) }
    var points by remember { mutableStateOf("0") }
    var objective by remember { mutableIntStateOf(0) }
    var goal by remember { mutableStateOf("0") }
    var showDelete by remember { mutableStateOf(false) }

    LaunchedEffect(missionId) {
        if (missionId != null) viewModel.loadDetail(missionId)
        else viewModel.clearDetail()
    }

    LaunchedEffect(state.item) {
        state.item?.let { m ->
            name = m.missionName
            type = m.missionType
            points = m.missionPoints.toString()
            objective = m.missionObjective
            goal = (m.missionGoal ?: 0).toString()
        }
    }

    val isNew = missionId == null

    AdminShellScreen(
        title = if (isNew) strings.adminCreate else strings.adminEdit,
        onBack = onBack,
        onDeleteClick = if (!isNew) ({ showDelete = true }) else null
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            AdminField(strings.adminMissionName, name, onValueChange = { name = it })
            AdminIntDropdown(
                label = strings.adminMissionType,
                options = missionTypeOptions,
                selectedValue = type,
                onSelected = { type = it }
            )
            AdminField(strings.adminPoints, points, onValueChange = { points = it })
            AdminIntDropdown(
                label = strings.adminMissionObjective,
                options = missionObjectiveOptions,
                selectedValue = objective,
                onSelected = { objective = it }
            )
            AdminField(strings.adminMissionGoal, goal, onValueChange = { goal = it })
            AdminSaveButton(
                text = strings.saveChanges,
                enabled = name.isNotBlank(),
                loading = state.isSaving,
                onClick = {
                    viewModel.saveMission(
                        missionId,
                        name.trim(),
                        type,
                        points.toIntOrNull() ?: 0,
                        objective,
                        goal.toIntOrNull() ?: 0,
                        onSaved
                    )
                }
            )
            state.error?.let { Text(it, modifier = Modifier.padding(top = 8.dp)) }
        }
    }

    if (missionId != null) {
        AdminDeleteDialog(
            visible = showDelete,
            title = strings.adminDeleteTitle,
            message = strings.adminDeleteMissionMessage,
            onDismiss = { showDelete = false },
            onConfirm = {
                showDelete = false
                viewModel.deleteMission(missionId, onBack)
            }
        )
    }
}
