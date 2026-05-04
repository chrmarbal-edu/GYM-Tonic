package edu.gymtonic_app.ui.screens.profile

import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.gymtonic_app.ui.components.BottomNavItem
import edu.gymtonic_app.ui.screens.exercise.TrainingShellScreen
import edu.gymtonic_app.ui.viewmodel.ProfileGroupUi
import edu.gymtonic_app.ui.viewmodel.ProfileRoutineUi
import edu.gymtonic_app.ui.viewmodel.ProfileUiState
import edu.gymtonic_app.ui.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ProfileScreen(
    onBack: () -> Unit,
    onOpenHome: () -> Unit,
    onOpenTraining: () -> Unit,
    onOpenChallenges: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenWeek: () -> Unit,
    onOpenRoutine: (String) -> Unit,
    onLogout: () -> Unit,
    onOpenAccount: () -> Unit,
    onOpenSettings: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val drawerState = rememberDrawerState(initialValue = androidx.compose.material3.DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    text = "Configuracion",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp)
                )
                DrawerActionRow(
                    label = "Cuenta",
                    icon = Icons.Outlined.AccountCircle,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onOpenAccount()
                    }
                )
                DrawerActionRow(
                    label = "Ajustes",
                    icon = Icons.Outlined.Settings,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onOpenSettings()
                    }
                )
                DrawerActionRow(
                    label = "Cerrar sesion",
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
            title = "Perfil",
            onBack = onBack,
            showBack = false,
            showBottomBar = true,
            selectedBottomItem = BottomNavItem.PROFILE,
            onOpenHome = onOpenHome,
            onOpenTraining = onOpenTraining,
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
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.message,
                            textAlign = TextAlign.Center,
                            color = Color(0xFF303030)
                        )
                    }
                }

                is ProfileUiState.Success -> {
                    ProfileContent(
                        username = state.data.username,
                        streakLabel = state.data.streakLabel,
                        routines = state.data.recentRoutines,
                        groups = state.data.groups,
                        onOpenWeek = onOpenWeek,
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
    streakLabel: String,
    routines: List<ProfileRoutineUi>,
    groups: List<ProfileGroupUi>,
    onOpenWeek: () -> Unit,
    onOpenRoutine: (String) -> Unit,
    onOpenDrawer: () -> Unit
) {
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
                Text(
                    text = "Hola, $username",
                    color = Color(0xFF1D1D1D),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onOpenDrawer) {
                    Icon(
                        imageVector = Icons.Outlined.AccountCircle,
                        contentDescription = "Abrir configuracion",
                        tint = Color(0xFF2A2A2A),
                        modifier = Modifier.size(34.dp)
                    )
                }
            }
        }

        item {
            SectionCard(title = "Racha semanal", actionText = "Ver semana", onActionClick = onOpenWeek) {
                Text(
                    text = streakLabel,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D2D2D),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        item {
            SectionCard(title = "Ultimas rutinas") {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (routines.isEmpty()) {
                        Text(
                            text = "Sin rutinas recientes",
                            color = Color(0xFF505567),
                            fontSize = 13.sp
                        )
                    } else {
                        routines.forEach { routine ->
                            RoutineRow(routine = routine, onClick = { onOpenRoutine(routine.id) })
                        }
                    }
                }
            }
        }

        item {
            SectionCard(title = "Mis grupos") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (groups.isEmpty()) {
                        Text(
                            text = "Aun no perteneces a grupos",
                            color = Color(0xFF505567),
                            fontSize = 13.sp
                        )
                    } else {
                        groups.forEach { group ->
                            GroupRow(group = group)
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
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF8B8EEA),
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
                    color = Color(0xFF1D1D1D),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                if (actionText != null && onActionClick != null) {
                    Text(
                        text = actionText,
                        color = Color(0xFF1D1D1D),
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
    routine: ProfileRoutineUi,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFE9EBF2),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(routine.imageRes),
                contentDescription = routine.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = routine.title,
                color = Color(0xFF1F2330),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "Abrir",
                fontSize = 11.sp,
                color = Color(0xFF4A4F60),
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}

@Composable
private fun GroupRow(group: ProfileGroupUi) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFE9EBF2),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF757BDF))
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = group.name,
                    color = Color(0xFF1F2330),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = group.membersLabel,
                    color = Color(0xFF5A6072),
                    fontSize = 11.sp
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


