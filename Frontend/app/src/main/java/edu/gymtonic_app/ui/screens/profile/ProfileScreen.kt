package edu.gymtonic_app.ui.screens.profile

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.gymtonic_app.data.remote.remoteModel.group.GroupDto
import edu.gymtonic_app.data.remote.remoteModel.training.TrainingRoutineDto
import edu.gymtonic_app.ui.components.BottomNavItem
import edu.gymtonic_app.ui.components.ObserveToastMessage
import edu.gymtonic_app.ui.components.ToastErrorRetryContent
import edu.gymtonic_app.ui.i18n.LocalStrings
import edu.gymtonic_app.ui.screens.exercise.TrainingShellScreen
import edu.gymtonic_app.ui.theme.LocalColors
import edu.gymtonic_app.ui.viewmodel.ProfileUiState
import edu.gymtonic_app.ui.viewmodel.ProfileViewModel
import edu.gymtonic_app.ui.screens.admin.missionObjectiveLabel
import kotlinx.coroutines.launch

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ProfileScreen(
    onBack: () -> Unit,
    onOpenTraining: () -> Unit,
    onOpenGroups: () -> Unit,
    onOpenFriends: () -> Unit,
    onOpenChallenges: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenWeek: () -> Unit,
    onOpenGroup: (Int) -> Unit,
    onOpenRoutine: (Int) -> Unit,
    onLogout: () -> Unit,
    onOpenAccount: () -> Unit,
    onOpenSettings: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val strings = LocalStrings.current
    val uiState by viewModel.uiState.collectAsState()
    ObserveToastMessage(message = (uiState as? ProfileUiState.Error)?.message)
    val drawerState = rememberDrawerState(initialValue = androidx.compose.material3.DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = strings.profileDrawerTitle,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp)
                )
                DrawerActionRow(
                    label = strings.profileAccount,
                    icon = Icons.Outlined.AccountCircle,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onOpenAccount()
                    }
                )
                DrawerActionRow(
                    label = strings.profileAdjustes,
                    icon = Icons.Outlined.Settings,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onOpenSettings()
                    }
                )
                DrawerActionRow(
                    label = strings.profileSignOut,
                    icon = Icons.AutoMirrored.Outlined.Logout,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onLogout()
                    }
                )
            }
        }
    ) {
        TrainingShellScreen(
            title = strings.profileTitle,
            onBack = onBack,
            showBack = false,
            showBottomBar = true,
            selectedBottomItem = BottomNavItem.PROFILE,
            onOpenTraining = onOpenTraining,
            onOpenGroups = onOpenGroups,
            onOpenFriends = onOpenFriends,
            onOpenChallenges = onOpenChallenges,
            onOpenProfile = onOpenProfile
        ) {
            when (val state = uiState) {
                ProfileUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is ProfileUiState.Error -> {
                    ToastErrorRetryContent(
                        retryLabel = strings.discountsRetry,
                        onRetry = { viewModel.loadProfile() }
                    )
                }

                is ProfileUiState.Success -> {
                    val data = state.data
                    ProfileContent(
                        username = data.username,
                        userPoints = data.userPoints,
                        objective = data.objective,
                        streakLabel = data.streakLabel,
                        routines = data.recentRoutines,
                        groups = data.groups,
                        onOpenWeek = onOpenWeek,
                        onOpenGroups = onOpenGroups,
                        onOpenGroup = onOpenGroup,
                        onOpenRoutine = onOpenRoutine,
                        onOpenDrawer = { scope.launch { drawerState.open() } }
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileContent(
    username: String,
    userPoints: Int,
    objective: Int,
    streakLabel: String,
    routines: List<TrainingRoutineDto>,
    groups: List<GroupDto>,
    onOpenWeek: () -> Unit,
    onOpenGroups: () -> Unit,
    onOpenGroup: (Int) -> Unit,
    onOpenRoutine: (Int) -> Unit,
    onOpenDrawer: () -> Unit
) {
    val strings = LocalStrings.current
    val colors = LocalColors.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = strings.profileGreeting(username),
                        color = colors.textPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = missionObjectiveLabel(objective),
                        color = colors.textSecondary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = Color(0xFFF4C542),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(top = 2.dp)
                    ) {
                        Text(
                            text = "$userPoints pts",
                            color = Color(0xFF0D3200),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
                IconButton(onClick = onOpenDrawer) {
                    Icon(
                        imageVector = Icons.Outlined.AccountCircle,
                        contentDescription = strings.profileOpenSettings,
                        tint = colors.fieldIndicator,
                        modifier = Modifier.size(34.dp)
                    )
                }
            }
        }

        item {
            SectionCard(title = strings.weeklyStreak, actionText = strings.viewWeek, onActionClick = onOpenWeek) {
                Text(
                    text = streakLabel,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textOnAccent,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        item {
            SectionCard(title = strings.recentRoutines) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (routines.isEmpty()) {
                        Text(
                            text = strings.noRecentRoutines,
                            color = colors.textSecondary,
                            fontSize = 13.sp
                        )
                    } else {
                        routines.forEach { routine ->
                            RoutineRow(routine = routine, openLabel = strings.openLabel, onClick = { onOpenRoutine(routine.routine_id) })
                        }
                    }
                }
            }
        }

        item {
            SectionCard(
                title = strings.myGroups,
                actionText = strings.groupsManage,
                onActionClick = onOpenGroups
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (groups.isEmpty()) {
                        Text(
                            text = strings.noGroups,
                            color = colors.textSecondary,
                            fontSize = 13.sp
                        )
                    } else {
                        groups.forEach { group ->
                            GroupRow(
                                group = group,
                                onClick = { onOpenGroup(group.group_id) }
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val colors = LocalColors.current
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = colors.surfaceAccent,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = colors.textOnAccent,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                if (actionText != null && onActionClick != null) {
                    Text(
                        text = actionText,
                        color = colors.textOnAccent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { onActionClick() }
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
private fun RoutineRow(
    routine: TrainingRoutineDto,
    openLabel: String,
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
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {/*
            Image(
                painter = painterResource(routine.imageRes),
                contentDescription = routine.routine_name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(8.dp))
            )*/
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = routine.displayName(),
                color = colors.textPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = openLabel,
                fontSize = 11.sp,
                color = colors.textSecondary,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
private fun GroupRow(
    group: GroupDto,
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
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(colors.surfaceAccent)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = group.group_name ?: "Grupo sin nombre",
                    color = colors.textPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = group.group_description ?: "",
                    color = colors.textSecondary,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun DrawerActionRow(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = label)
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = label, fontSize = 15.sp)
    }
}
