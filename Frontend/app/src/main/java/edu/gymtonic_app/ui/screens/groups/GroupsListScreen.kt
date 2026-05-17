package edu.gymtonic_app.ui.screens.groups

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.gymtonic_app.data.remote.remoteModel.group.GroupDto
import edu.gymtonic_app.ui.components.BottomNavItem
import edu.gymtonic_app.ui.components.ObserveToastMessage
import edu.gymtonic_app.ui.components.ToastErrorRetryContent
import edu.gymtonic_app.ui.i18n.LocalStrings
import edu.gymtonic_app.ui.screens.exercise.TrainingShellScreen
import edu.gymtonic_app.ui.theme.LocalColors
import edu.gymtonic_app.ui.viewmodel.GroupViewModel
import edu.gymtonic_app.ui.viewmodel.GroupsListUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupsListScreen(
    onBack: () -> Unit,
    onOpenGroup: (Int) -> Unit,
    onOpenHome: () -> Unit,
    onOpenTraining: () -> Unit,
    onOpenChallenges: () -> Unit,
    onOpenProfile: () -> Unit,
    viewModel: GroupViewModel = viewModel()
) {
    val strings = LocalStrings.current
    val colors = LocalColors.current
    val listState by viewModel.listState.collectAsState()
    val actionMessage by viewModel.actionMessage.collectAsState()

    ObserveToastMessage(message = actionMessage, onConsumed = { viewModel.clearActionMessage() })

    var showCreateDialog by rememberSaveable { mutableStateOf(false) }
    var groupName by rememberSaveable { mutableStateOf("") }
    var groupDescription by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.loadGroupsList()
    }

    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text(strings.groupsCreateTitle) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = groupName,
                        onValueChange = { groupName = it },
                        label = { Text(strings.groupsNameLabel) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = groupDescription,
                        onValueChange = { groupDescription = it },
                        label = { Text(strings.groupsDescriptionLabel) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.createGroup(groupName, groupDescription) { groupId ->
                            showCreateDialog = false
                            groupName = ""
                            groupDescription = ""
                            onOpenGroup(groupId)
                        }
                    },
                    enabled = groupName.isNotBlank()
                ) {
                    Text(strings.groupsCreateConfirm)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text(strings.groupsCancel)
                }
            }
        )
    }

    TrainingShellScreen(
        title = strings.groupsTitle,
        onBack = onBack,
        showBottomBar = true,
        selectedBottomItem = BottomNavItem.PROFILE,
        onOpenHome = onOpenHome,
        onOpenTraining = onOpenTraining,
        onOpenChallenges = onOpenChallenges,
        onOpenProfile = onOpenProfile
    ) {
        when (val state = listState) {
                GroupsListUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is GroupsListUiState.Error -> {
                    ObserveToastMessage(message = state.message)
                    ToastErrorRetryContent(
                        retryLabel = strings.discountsRetry,
                        onRetry = { viewModel.loadGroupsList() }
                    )
                }

                is GroupsListUiState.Success -> {
                    val myGroups = state.data.allGroups.filter { state.data.myGroupIds.contains(it.group_id) }
                    val exploreGroups = state.data.allGroups.filter { !state.data.myGroupIds.contains(it.group_id) }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Button(
                                onClick = { showCreateDialog = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp)
                            ) {
                                Text(strings.groupsCreateButton)
                            }
                        }

                        item {
                            Text(
                                text = strings.groupsMySection,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = colors.textPrimary,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        if (myGroups.isEmpty()) {
                            item {
                                Text(
                                    text = strings.noGroups,
                                    color = colors.textSecondary,
                                    fontSize = 13.sp
                                )
                            }
                        } else {
                            items(myGroups, key = { it.group_id }) { group ->
                                GroupListRow(
                                    group = group,
                                    actionLabel = strings.groupsOpen,
                                    onAction = { onOpenGroup(group.group_id) },
                                    onClick = { onOpenGroup(group.group_id) }
                                )
                            }
                        }

                        item {
                            Text(
                                text = strings.groupsExploreSection,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = colors.textPrimary,
                                modifier = Modifier.padding(top = 12.dp)
                            )
                        }

                        if (exploreGroups.isEmpty()) {
                            item {
                                Text(
                                    text = strings.groupsNoMoreToJoin,
                                    color = colors.textSecondary,
                                    fontSize = 13.sp
                                )
                            }
                        } else {
                            items(exploreGroups, key = { it.group_id }) { group ->
                                GroupListRow(
                                    group = group,
                                    actionLabel = strings.groupsJoin,
                                    onAction = { viewModel.joinGroup(group.group_id) },
                                    onClick = { onOpenGroup(group.group_id) }
                                )
                            }
                        }

                        item { Spacer(modifier = Modifier.height(16.dp)) }
                    }
                }
            }
    }
}

@Composable
private fun GroupListRow(
    group: GroupDto,
    actionLabel: String,
    onAction: () -> Unit,
    onClick: () -> Unit
) {
    val colors = LocalColors.current
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = colors.surfaceCard,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = group.group_name ?: "Grupo",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = group.group_description.orEmpty(),
                    fontSize = 12.sp,
                    color = colors.textSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            TextButton(onClick = onAction) {
                Text(text = actionLabel, fontSize = 12.sp)
            }
        }
    }
}
