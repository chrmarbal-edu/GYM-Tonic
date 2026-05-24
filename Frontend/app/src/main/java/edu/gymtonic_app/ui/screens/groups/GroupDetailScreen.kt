package edu.gymtonic_app.ui.screens.groups

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import edu.gymtonic_app.ui.screens.admin.resolveRoutineImageUrl
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.gymtonic_app.data.remote.remoteModel.routine.RoutineDto
import edu.gymtonic_app.ui.components.BottomNavItem
import edu.gymtonic_app.ui.components.ObserveToastMessage
import edu.gymtonic_app.ui.components.ToastErrorRetryContent
import edu.gymtonic_app.ui.i18n.LocalStrings
import edu.gymtonic_app.ui.screens.admin.groupRoleLabel
import edu.gymtonic_app.ui.screens.exercise.TrainingShellScreen
import edu.gymtonic_app.ui.theme.LocalColors
import edu.gymtonic_app.ui.viewmodel.GroupDetailUiState
import edu.gymtonic_app.ui.viewmodel.GroupViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailScreen(
    groupId: Int,
    onBack: () -> Unit,
    onAddRoutine: (Int) -> Unit,
    onOpenRoutine: (Int) -> Unit,
    onEditRoutine: (Int) -> Unit = {},
    onOpenMember: (Int) -> Unit = {},
    onOpenTraining: () -> Unit,
    onOpenGroups: () -> Unit,
    onOpenFriends: () -> Unit,
    onOpenChallenges: () -> Unit,
    onOpenProfile: () -> Unit,
    viewModel: GroupViewModel = viewModel()
) {
    val strings = LocalStrings.current
    val colors = LocalColors.current
    val detailState by viewModel.detailState.collectAsState()
    val actionMessage by viewModel.actionMessage.collectAsState()
    ObserveToastMessage(message = actionMessage, onConsumed = { viewModel.clearActionMessage() })
    var showLeaveDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(groupId) {
        viewModel.loadGroupDetail(groupId)
    }

    if (showLeaveDialog) {
        AlertDialog(
            onDismissRequest = { showLeaveDialog = false },
            title = { Text(strings.groupsLeaveTitle) },
            text = { Text(strings.groupsLeaveMessage) },
            confirmButton = {
                Button(
                    onClick = {
                        showLeaveDialog = false
                        viewModel.leaveGroup(groupId, onLeft = onBack)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) {
                    Text(strings.groupsLeaveConfirm)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLeaveDialog = false }) {
                    Text(strings.groupsCancel)
                }
            }
        )
    }

    TrainingShellScreen(
        title = strings.groupsDetailTitle,
        onBack = onBack,
        showBottomBar = true,
        selectedBottomItem = BottomNavItem.GROUPS,
        onOpenTraining = onOpenTraining,
        onOpenGroups = onOpenGroups,
        onOpenFriends = onOpenFriends,
        onOpenChallenges = onOpenChallenges,
        onOpenProfile = onOpenProfile
    ) {
        when (val state = detailState) {
                GroupDetailUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is GroupDetailUiState.Error -> {
                    ObserveToastMessage(message = state.message)
                    ToastErrorRetryContent(
                        retryLabel = strings.retry,
                        onRetry = { viewModel.loadGroupDetail(groupId) }
                    )
                }

                is GroupDetailUiState.Success -> {
                    val data = state.data
                    val group = data.group

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        item {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = colors.surfaceAccent,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = group.group_name ?: "Grupo",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 22.sp,
                                        color = colors.textOnAccent,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    if (!group.group_description.isNullOrBlank()) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = group.group_description,
                                            fontSize = 14.sp,
                                            color = colors.textOnAccent.copy(alpha = 0.9f),
                                            maxLines = 3,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = strings.groupsMembersCount(data.members.size),
                                        fontSize = 13.sp,
                                        color = colors.textOnAccent
                                    )
                                    Text(
                                        text = strings.groupsPoints(group.group_points),
                                        fontSize = 13.sp,
                                        color = colors.textOnAccent
                                    )
                                }
                            }
                        }

                        if (!data.isMember) {
                            item {
                                Button(
                                    onClick = { viewModel.joinGroup(groupId) },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(strings.groupsJoin)
                                }
                            }
                        } else {
                            item {
                                if (data.isCreator) {
                                    Button(
                                        onClick = { onAddRoutine(groupId) },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(strings.groupsAddRoutine)
                                    }
                                } else {
                                    OutlinedButton(
                                        onClick = { showLeaveDialog = true },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(strings.groupsLeave)
                                    }
                                }
                            }

                            item {
                                Text(
                                    text = strings.groupsRoutinesSection,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = colors.textPrimary,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }

                            if (data.routines.isEmpty()) {
                                item {
                                    Text(
                                        text = strings.groupsNoRoutines,
                                        color = colors.textSecondary,
                                        fontSize = 13.sp
                                    )
                                }
                            } else {
                                items(data.routines, key = { it.routine_id }) { routine ->
                                    GroupRoutineRow(
                                        routine = routine,
                                        openLabel = strings.openLabel,
                                        editLabel = strings.adminEdit,
                                        showEdit = data.isCreator,
                                        onClick = { onOpenRoutine(routine.routine_id) },
                                        onEdit = { onEditRoutine(routine.routine_id) }
                                    )
                                }
                            }

                            item {
                                Text(
                                    text = strings.adminMembersSection,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = colors.textPrimary,
                                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                                )
                            }

                            items(data.members) { member ->
                                MemberRow(
                                    member = member,
                                    currentUserId = data.currentUserId,
                                    pointsLabel = strings.adminPoints,
                                    roleLabel = strings.adminRole,
                                    onOpenMember = onOpenMember
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
private fun GroupRoutineRow(
    routine: RoutineDto,
    openLabel: String,
    editLabel: String,
    showEdit: Boolean,
    onClick: () -> Unit,
    onEdit: () -> Unit
) {
    val colors = LocalColors.current
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = colors.surfaceCard,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val imageUrl = resolveRoutineImageUrl(routine.routine_image)
            if (!imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = routine.routine_name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                Spacer(modifier = Modifier.width(10.dp))
            }
            Text(
                text = routine.routine_name ?: "Rutina",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onClick)
            )
            if (showEdit) {
                TextButton(onClick = onEdit) {
                    Text(
                        text = editLabel,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Text(
                text = openLabel,
                fontSize = 11.sp,
                color = colors.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.clickable(onClick = onClick)
            )
        }
    }
}

@Composable
private fun MemberRow(
    member: edu.gymtonic_app.data.remote.remoteModel.group.GroupUserDto,
    currentUserId: Int?,
    pointsLabel: String,
    roleLabel: String,
    onOpenMember: (Int) -> Unit
) {
    val colors = LocalColors.current
    val isMe = member.userId == currentUserId

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = colors.surfaceCard,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(enabled = !isMe) { onOpenMember(member.userId) }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = colors.surfaceMain
            ) {
                if (!member.userPicture.isNullOrBlank()) {
                    AsyncImage(
                        model = edu.gymtonic_app.core.MediaUtils.resolveBackendMediaUrl(member.userPicture),
                        contentDescription = member.userUsername ?: member.userName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = colors.textSecondary.copy(alpha = 0.5f)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = member.userName ?: member.userUsername ?: "Usuario ${member.userId}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = groupRoleLabel(member.range),
                    fontSize = 12.sp,
                    color = colors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (member.userPoints != null) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = member.userPoints.toString(),
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = colors.accent
                    )
                    Text(
                        text = pointsLabel,
                        fontSize = 10.sp,
                        color = colors.textSecondary
                    )
                }
            }
        }
    }
}
