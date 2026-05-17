package edu.gymtonic_app.ui.screens.admin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.gymtonic_app.ui.i18n.LocalStrings
import edu.gymtonic_app.ui.viewmodel.admin.AdminUsersViewModel

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import edu.gymtonic_app.ui.theme.LocalColors

@Composable
fun AdminUsersListScreen(
    onBack: () -> Unit,
    onOpenDetail: (Int) -> Unit,
    viewModel: AdminUsersViewModel = viewModel()
) {
    val strings = LocalStrings.current
    val state by viewModel.listState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { viewModel.loadList() }

    AdminShellScreen(title = strings.adminUsers, onBack = onBack) {
        val filteredItems = remember(state.items, searchQuery) {
            state.items.filter { 
                (it.userName ?: "").contains(searchQuery, ignoreCase = true) ||
                (it.userUsername ?: "").contains(searchQuery, ignoreCase = true)
            }
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
                    titleFor = { it.userName ?: it.userUsername ?: "Usuario #${it.userId}" },
                    subtitleFor = { it.userUsername },
                    onItemClick = { onOpenDetail(it.userId) }
                )
            }
        }
    }
}

@Composable
fun AdminUserDetailScreen(
    userId: Int,
    onBack: () -> Unit,
    viewModel: AdminUsersViewModel = viewModel()
) {
    val strings = LocalStrings.current
    val state by viewModel.detailState.collectAsState()

    LaunchedEffect(userId) { viewModel.loadDetail(userId) }

    AdminShellScreen(
        title = strings.adminUserDetail,
        onBack = onBack
    ) {
        AdminListContent(
            isLoading = state.isLoading,
            error = state.error,
            emptyMessage = strings.adminEmptyList,
            itemsCount = if (state.item != null) 1 else 0,
            onRetry = { viewModel.loadDetail(userId) }
        ) {
            val user = state.item ?: return@AdminListContent
            val oauthLabel = oauthProviderLabel(user.userOauth)
            val colors = LocalColors.current

            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Profile Picture Section
                Surface(
                    modifier = Modifier
                        .size(120.dp)
                        .align(Alignment.CenterHorizontally)
                        .padding(bottom = 16.dp),
                    shape = CircleShape,
                    color = colors.surfaceCard
                ) {
                    val pictureUrl = resolveBackendMediaUrl(user.userPicture)
                    AsyncImage(
                        model = pictureUrl,
                        contentDescription = user.userName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                AdminDetailRow("ID", user.userId.toString())
                AdminDetailRow(strings.usernameField, user.userUsername)
                AdminDetailRow(strings.fullName, user.userName)
                AdminDetailRow(strings.email, user.userEmail)
                AdminDetailRow(strings.adminRole, user.userRole.toString())
                if (oauthLabel != null) {
                    AdminDetailRow(strings.adminOAuthProvider, oauthLabel)
                }
                AdminDetailRow(strings.adminPoints, user.userPoints.toString())
                AdminDetailRow(strings.height, "${user.userHeight} cm")
                AdminDetailRow(strings.weight, "${user.userWeight} kg")
            }
        }
    }
}
