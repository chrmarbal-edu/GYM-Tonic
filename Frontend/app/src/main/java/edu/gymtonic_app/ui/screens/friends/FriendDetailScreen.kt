package edu.gymtonic_app.ui.screens.friends

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import edu.gymtonic_app.core.MediaUtils
import edu.gymtonic_app.data.remote.remoteModel.group.GroupDto
import edu.gymtonic_app.ui.components.BottomNavItem
import edu.gymtonic_app.ui.i18n.AppStrings
import edu.gymtonic_app.ui.i18n.LocalStrings
import edu.gymtonic_app.ui.screens.exercise.TrainingShellScreen
import edu.gymtonic_app.ui.theme.LocalColors
import edu.gymtonic_app.ui.viewmodel.FriendDetailViewModel

@Composable
fun FriendDetailScreen(
    friendId: Int,
    onBack: () -> Unit,
    onOpenTraining: () -> Unit,
    onOpenGroups: () -> Unit,
    onOpenFriends: () -> Unit,
    onOpenChallenges: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenGroup: (Int) -> Unit,
    viewModel: FriendDetailViewModel = viewModel()
) {
    val strings = LocalStrings.current
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(friendId) {
        viewModel.loadFriendDetail(friendId)
    }

    TrainingShellScreen(
        title = strings.friendDetail.title,
        onBack = onBack,
        showBack = true,
        showBottomBar = true,
        selectedBottomItem = BottomNavItem.FRIENDS,
        onOpenTraining = onOpenTraining,
        onOpenGroups = onOpenGroups,
        onOpenFriends = onOpenFriends,
        onOpenChallenges = onOpenChallenges,
        onOpenProfile = onOpenProfile
    ) {
        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (state.error != null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = state.error!!, textAlign = TextAlign.Center)
            }
        } else {
            val friend = state.friend
            if (friend != null) {
                FriendDetailContent(
                    strings = strings,
                    userName = friend.userName,
                    userUsername = friend.userUsername,
                    userPicture = friend.userPicture,
                    points = friend.userPoints,
                    objectiveId = friend.userObjective,
                    sharedGroups = state.sharedGroups,
                    onOpenGroup = onOpenGroup
                )
            }
        }
    }
}

@Composable
private fun FriendDetailContent(
    strings: AppStrings,
    userName: String,
    userUsername: String,
    userPicture: String?,
    points: Int,
    objectiveId: Int,
    sharedGroups: List<GroupDto>,
    onOpenGroup: (Int) -> Unit
) {
    val colors = LocalColors.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(colors.surfaceCard, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            val resolvedUrl = MediaUtils.resolveUserPictureUrl(userPicture)
            AsyncImage(
                model = resolvedUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
            )
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = userName,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = colors.textPrimary
        )
        Text(
            text = "@$userUsername",
            fontSize = 16.sp,
            color = colors.textSecondary
        )

        Spacer(Modifier.height(24.dp))

        // Info Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            InfoCard(
                label = strings.friendDetail.points,
                value = points.toString(),
                modifier = Modifier.weight(1f)
            )
            val goalText = when (objectiveId) {
                0 -> strings.register.goalMaintenance
                1 -> strings.register.goalLoseWeight
                2 -> strings.register.goalBuildMuscle
                3 -> strings.register.goalPerformance
                else -> strings.register.selectGoal
            }
            InfoCard(
                label = strings.friendDetail.goal,
                value = goalText,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(32.dp))

        // Shared Groups
        Text(
            text = strings.friendDetail.sharedGroups,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth(),
            color = colors.textPrimary
        )

        Spacer(Modifier.height(8.dp))

        if (sharedGroups.isEmpty()) {
            Text(
                text = strings.friendDetail.noSharedGroups,
                color = colors.textSecondary,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(sharedGroups) { group ->
                    SharedGroupRow(group, onClick = { onOpenGroup(group.group_id) })
                }
            }
        }
    }
}

@Composable
private fun InfoCard(label: String, value: String, modifier: Modifier = Modifier) {
    val colors = LocalColors.current
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = colors.surfaceCard,
        shadowElevation = 2.dp,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = label, fontSize = 12.sp, color = colors.textSecondary)
            Spacer(Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = colors.accent,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun SharedGroupRow(group: GroupDto, onClick: () -> Unit) {
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
            Icon(
                imageVector = Icons.Outlined.Group,
                contentDescription = null,
                tint = colors.accent,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = group.group_name ?: "",
                fontWeight = FontWeight.SemiBold,
                color = colors.textPrimary
            )
        }
    }
}
