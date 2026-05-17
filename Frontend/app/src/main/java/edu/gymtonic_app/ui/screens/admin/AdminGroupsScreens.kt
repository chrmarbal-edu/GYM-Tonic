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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.gymtonic_app.ui.i18n.LocalStrings
import edu.gymtonic_app.ui.viewmodel.admin.AdminGroupsViewModel

@Composable
fun AdminGroupsListScreen(
    onBack: () -> Unit,
    onOpenDetail: (Int) -> Unit,
    viewModel: AdminGroupsViewModel = viewModel()
) {
    val strings = LocalStrings.current
    val state by viewModel.listState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { viewModel.loadList() }

    AdminShellScreen(title = strings.adminGroups, onBack = onBack) {
        val filteredItems = remember(state.items, searchQuery) {
            state.items.filter { (it.group_name ?: "").contains(searchQuery, ignoreCase = true) }
        }

        AdminListContent(
            isLoading = state.isLoading,
            error = state.error,
            emptyMessage = strings.adminEmptyList,
            itemsCount = filteredItems.size,
            onRetry = { viewModel.loadList() }
        ) {
            Column(Modifier.fillMaxSize()) {
                AdminSearchBar(query = searchQuery, onQueryChange = { searchQuery = it })
                AdminSimpleList(
                    items = filteredItems,
                    titleFor = { it.group_name ?: "Grupo #${it.group_id}" },
                    subtitleFor = { strings.groupsPoints(it.group_points) },
                    onItemClick = { onOpenDetail(it.group_id) }
                )
            }
        }
    }
}

@Composable
fun AdminGroupDetailScreen(
    groupId: Int,
    onBack: () -> Unit,
    onOpenUser: (Int) -> Unit,
    viewModel: AdminGroupsViewModel = viewModel()
) {
    val strings = LocalStrings.current
    val state by viewModel.detailState.collectAsState()
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var points by remember { mutableStateOf("0") }

    LaunchedEffect(groupId) { viewModel.loadDetail(groupId) }

    LaunchedEffect(state.group) {
        state.group?.let { g ->
            name = g.group_name.orEmpty()
            description = g.group_description.orEmpty()
            points = g.group_points.toString()
        }
    }

    AdminShellScreen(
        title = strings.groupsDetailTitle,
        onBack = onBack
    ) {
        AdminListContent(
            isLoading = state.isLoading,
            error = state.error,
            emptyMessage = strings.adminEmptyList,
            itemsCount = if (state.group != null) 1 else 0,
            onRetry = { viewModel.loadDetail(groupId) }
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                AdminDetailRow(strings.groupsNameLabel, name)
                AdminDetailRow(strings.groupsDescriptionLabel, description)
                AdminDetailRow(strings.adminPoints, points)
                AdminDetailRow("ID", groupId.toString())

                Text(
                    strings.adminMembersSection,
                    modifier = Modifier.padding(top = 20.dp, bottom = 8.dp)
                )
                state.members.forEach { member ->
                    AdminListItem(
                        title = member.displayName,
                        subtitle = groupRoleLabel(member.range)
                    ) {
                        onOpenUser(member.userId)
                    }
                }
                state.error?.let { Text(it, modifier = Modifier.padding(top = 8.dp)) }
            }
        }
    }
}
