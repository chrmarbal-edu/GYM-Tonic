package edu.gymtonic_app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.gymtonic_app.ui.components.LanguageButton
import edu.gymtonic_app.ui.components.ThemeButton
import edu.gymtonic_app.ui.i18n.LocalStrings
import edu.gymtonic_app.ui.theme.LocalColors

data class HomeAction(
    val title: String,
    val icon: ImageVector,
    val highlight: Boolean = false,
    val onClick: () -> Unit
)

@Composable
fun MainViewScreen(
    onLogout: () -> Unit,
    onOpenTraining: () -> Unit,
    onCreateRoutine: () -> Unit,
    onOpenTechnogym: () -> Unit,
    onOpenDiscounts: () -> Unit,
    onOpenClientArea: () -> Unit,
    onInviteFriend: () -> Unit,
    onOpenMissions: () -> Unit
) {
    val strings = LocalStrings.current
    val colors = LocalColors.current
    val bg = Brush.verticalGradient(colors.gradientColors)

    val actions = listOf(
        HomeAction(strings.homePresetWorkouts, Icons.Outlined.FitnessCenter, onClick = onOpenTraining),
        HomeAction(strings.homeCreateRoutines, Icons.Outlined.FitnessCenter, onClick = onCreateRoutine),
        HomeAction(strings.homeTechnogym, Icons.Outlined.Devices, onClick = onOpenTechnogym),
        HomeAction(strings.homeDiscounts, Icons.Outlined.LocalOffer, onClick = onOpenDiscounts),
        HomeAction(strings.homeChallenges, Icons.Outlined.EventAvailable, onClick = onOpenMissions),
        HomeAction(strings.homeClientArea, Icons.Outlined.AccountCircle, onClick = onOpenClientArea),
        HomeAction(strings.homeChat, Icons.Outlined.GroupAdd, onClick = onInviteFriend),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(start = 18.dp, end = 18.dp, bottom = 18.dp, top = 50.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "GYMTONIC",
                    color = Color.White,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = strings.homeSlogan,
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 12.sp,
                    letterSpacing = 2.sp
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                ThemeButton(tint = Color.White)
                LanguageButton(tint = Color.White)
                IconButton(onClick = { onLogout() }) {
                    Icon(
                        imageVector = Icons.Outlined.Logout,
                        contentDescription = strings.profileSignOut,
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(35.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            horizontalArrangement = Arrangement.spacedBy(18.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(actions) { action ->
                HomeTile(action)
            }
        }
    }
}

@Composable
private fun HomeTile(action: HomeAction) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { action.onClick() }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(92.dp)
                .background(
                    if (action.highlight) Color(0xFFF2C94C) else Color.White,
                    RoundedCornerShape(18.dp)
                )
        ) {
            Icon(
                imageVector = action.icon,
                contentDescription = action.title,
                tint = Color(0xFF2D2D2D),
                modifier = Modifier.size(42.dp)
            )
        }

        Spacer(Modifier.height(8.dp))

        Text(
            text = action.title,
            color = Color.White,
            fontSize = 13.sp,
            textAlign = TextAlign.Center
        )
    }
}
