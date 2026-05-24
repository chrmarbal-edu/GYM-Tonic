package edu.gymtonic_app.ui.screens.friends

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import edu.gymtonic_app.core.MediaUtils
import edu.gymtonic_app.data.remote.remoteModel.social.FriendRequestWithUserDto
import edu.gymtonic_app.data.remote.remoteModel.user.UserSummaryDto
import edu.gymtonic_app.ui.components.BottomNavItem
import edu.gymtonic_app.ui.components.ObserveToastMessage
import edu.gymtonic_app.ui.components.ToastErrorRetryContent
import edu.gymtonic_app.ui.i18n.AppStrings
import edu.gymtonic_app.ui.i18n.LocalStrings
import edu.gymtonic_app.ui.screens.exercise.TrainingShellScreen
import edu.gymtonic_app.ui.theme.LocalColors
import edu.gymtonic_app.ui.viewmodel.FriendsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendsScreen(
    onBack: () -> Unit,
    onOpenTraining: () -> Unit,
    onOpenGroups: () -> Unit,
    onOpenFriends: () -> Unit,
    onOpenChallenges: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenFriendDetail: (Int) -> Unit,
    viewModel: FriendsViewModel = viewModel()
) {
    val strings = LocalStrings.current
    val colors = LocalColors.current
    val state by viewModel.uiState.collectAsState()
    val actionMessage by viewModel.actionMessage.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()

    ObserveToastMessage(
        message = actionMessage?.let { mapMessage(it, strings) },
        onConsumed = { viewModel.clearActionMessage() }
    )
    ObserveToastMessage(message = state.error)

    var selectedTab by rememberSaveable { mutableStateOf(0) }
    var showAddDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) { viewModel.loadAll() }

    if (showAddDialog) {
        AddFriendDialog(
            strings = strings,
            results = searchResults,
            onSearch = { viewModel.searchUsers(it) },
            onSend = { user ->
                viewModel.sendRequest(user.userId)
                showAddDialog = false
                viewModel.clearSearch()
            },
            onDismiss = {
                showAddDialog = false
                viewModel.clearSearch()
            }
        )
    }

    TrainingShellScreen(
        title = strings.friendsTitle,
        onBack = onBack,
        showBack = false,
        showBottomBar = true,
        selectedBottomItem = BottomNavItem.FRIENDS,
        onOpenTraining = onOpenTraining,
        onOpenGroups = onOpenGroups,
        onOpenFriends = onOpenFriends,
        onOpenChallenges = onOpenChallenges,
        onOpenProfile = onOpenProfile
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = {
                        viewModel.clearSearch()
                        viewModel.searchUsers("")
                        showAddDialog = true
                    },
                    containerColor = colors.accent,
                    contentColor = Color.White,
                    icon = { Icon(Icons.Outlined.PersonAdd, contentDescription = null) },
                    text = { Text(strings.friendsAddFab) }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                val tabs = listOf(
                    strings.friendsTabFriends to state.friends.size,
                    strings.friendsTabIncoming to state.incoming.size,
                    strings.friendsTabOutgoing to state.outgoing.size
                )
                TabRow(selectedTabIndex = selectedTab, containerColor = Color.Transparent) {
                    tabs.forEachIndexed { index, (title, count) ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = if (count > 0) "$title ($count)" else title,
                                    fontSize = 13.sp,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                when {
                    state.isLoading -> CenteredLoader()
                    state.error != null -> ToastErrorRetryContent(
                        retryLabel = strings.retry,
                        onRetry = { viewModel.refresh() }
                    )
                    else -> when (selectedTab) {
                        0 -> FriendsList(
                            friends = state.friends,
                            busyIds = state.busyFriendIds,
                            emptyText = strings.friendsEmptyFriends,
                            removeLabel = strings.friendsRemoveAction,
                            onRemove = { friendshipId -> viewModel.removeFriend(friendshipId) },
                            onClickFriend = onOpenFriendDetail
                        )
                        1 -> RequestsList(
                            requests = state.incoming,
                            otherUserOf = { req ->
                                UserSummaryDto(
                                    userId = req.frequestSender,
                                    userUsername = req.senderUsername,
                                    userName = req.senderName,
                                    userPicture = req.senderPicture
                                )
                            },
                            busyIds = state.busyRequestIds,
                            emptyText = strings.friendsEmptyIncoming,
                            primaryLabel = strings.friendsAcceptAction,
                            secondaryLabel = strings.friendsRejectAction,
                            onPrimary = { viewModel.acceptRequest(it) },
                            onSecondary = { viewModel.rejectRequest(it) }
                        )
                        2 -> RequestsList(
                            requests = state.outgoing,
                            otherUserOf = { req ->
                                UserSummaryDto(
                                    userId = req.frequestReceiver,
                                    userUsername = req.receiverUsername,
                                    userName = req.receiverName,
                                    userPicture = req.receiverPicture
                                )
                            },
                            busyIds = state.busyRequestIds,
                            emptyText = strings.friendsEmptyOutgoing,
                            primaryLabel = null,
                            secondaryLabel = strings.friendsCancelAction,
                            onPrimary = { },
                            onSecondary = { viewModel.cancelRequest(it) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FriendsList(
    friends: List<UserSummaryDto>,
    busyIds: Set<Int>,
    emptyText: String,
    removeLabel: String,
    onRemove: (Int) -> Unit,
    onClickFriend: (Int) -> Unit
) {
    if (friends.isEmpty()) {
        CenteredText(emptyText)
        return
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(friends, key = { it.friendId ?: it.userId }) { friend ->
            UserRow(
                user = friend,
                onClick = { onClickFriend(friend.userId) },
                trailing = {
                    OutlinedButton(
                        onClick = { friend.friendId?.let(onRemove) },
                        enabled = friend.friendId != null && friend.friendId !in busyIds
                    ) {
                        Text(text = removeLabel, fontSize = 12.sp)
                    }
                }
            )
        }
        item { Spacer(Modifier.height(80.dp)) }
    }
}

@Composable
private fun RequestsList(
    requests: List<FriendRequestWithUserDto>,
    otherUserOf: (FriendRequestWithUserDto) -> UserSummaryDto,
    busyIds: Set<Int>,
    emptyText: String,
    primaryLabel: String?,
    secondaryLabel: String,
    onPrimary: (Int) -> Unit,
    onSecondary: (Int) -> Unit
) {
    if (requests.isEmpty()) {
        CenteredText(emptyText)
        return
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(requests, key = { it.frequestId }) { req ->
            val user = otherUserOf(req)
            val busy = req.frequestId in busyIds
            UserRow(
                user = user,
                trailing = {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        if (primaryLabel != null) {
                            Button(
                                onClick = { onPrimary(req.frequestId) },
                                enabled = !busy
                            ) {
                                Text(text = primaryLabel, fontSize = 12.sp)
                            }
                        }
                        OutlinedButton(
                            onClick = { onSecondary(req.frequestId) },
                            enabled = !busy
                        ) {
                            Text(text = secondaryLabel, fontSize = 12.sp)
                        }
                    }
                }
            )
        }
        item { Spacer(Modifier.height(80.dp)) }
    }
}

@Composable
private fun UserRow(
    user: UserSummaryDto,
    onClick: (() -> Unit)? = null,
    trailing: @Composable () -> Unit
) {
    val colors = LocalColors.current
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = colors.surfaceCard,
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Avatar(user.userPicture)
            Spacer(Modifier.size(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.userName.orEmpty().ifBlank { user.userUsername.orEmpty() },
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "@${user.userUsername.orEmpty()}",
                    fontSize = 12.sp,
                    color = colors.textSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            trailing()
        }
    }
}

@Composable
private fun Avatar(url: String?) {
    val colors = LocalColors.current
    val resolvedUrl = MediaUtils.resolveUserPictureUrl(url)
    Box(
        modifier = Modifier
            .size(42.dp)
            .background(colors.surfaceMain, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = resolvedUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
        )
    }
}

@Composable
private fun CenteredLoader() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun CenteredText(text: String) {
    val colors = LocalColors.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = colors.textSecondary,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun AddFriendDialog(
    strings: AppStrings,
    results: List<UserSummaryDto>,
    onSearch: (String) -> Unit,
    onSend: (UserSummaryDto) -> Unit,
    onDismiss: () -> Unit
) {
    val colors = LocalColors.current
    var query by rememberSaveable { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = strings.friendsAddTitle, fontWeight = FontWeight.ExtraBold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = {
                        query = it
                        onSearch(it)
                    },
                    label = { Text(strings.friendsSearchHint) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                if (results.isEmpty()) {
                    Text(
                        text = strings.friendsSearchEmpty,
                        color = colors.textSecondary,
                        fontSize = 13.sp
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(results, key = { it.userId }) { user ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = colors.surfaceCard,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSend(user) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Avatar(user.userPicture)
                                    Spacer(Modifier.size(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = user.userName.orEmpty()
                                                .ifBlank { user.userUsername.orEmpty() },
                                            fontWeight = FontWeight.SemiBold,
                                            color = colors.textPrimary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "@${user.userUsername.orEmpty()}",
                                            fontSize = 12.sp,
                                            color = colors.textSecondary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    TextButton(onClick = { onSend(user) }) {
                                        Text(strings.friendsSendRequest, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.close)
            }
        }
    )
}

private fun mapMessage(raw: String, strings: AppStrings): String = when (raw) {
    FriendsViewModel.ALREADY_FRIEND -> strings.friendsAlreadyFriend
    FriendsViewModel.ALREADY_PENDING -> strings.friendsRequestAlready
    FriendsViewModel.REQUEST_SENT -> strings.friendsRequestSent
    FriendsViewModel.ACCEPTED -> strings.friendsAcceptedMsg
    FriendsViewModel.REJECTED -> strings.friendsRejectedMsg
    FriendsViewModel.CANCELLED -> strings.friendsCancelledMsg
    FriendsViewModel.REMOVED -> strings.friendsRemovedMsg
    else -> raw
}
