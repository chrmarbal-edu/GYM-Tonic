package edu.gymtonic_app.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.ViewList
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
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

data class AdminHomeAction(
    val title: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@Composable
fun AdminHomeScreen(
    onLogout: () -> Unit,
    onOpenRoutines: () -> Unit,
    onOpenExercises: () -> Unit,
    onOpenUsers: () -> Unit,
    onOpenGroups: () -> Unit,
    onOpenMissions: () -> Unit
) {
    val strings = LocalStrings.current
    val colors = LocalColors.current
    val bg = Brush.verticalGradient(colors.gradientColors)

    val actions = listOf(
        AdminHomeAction(strings.adminRoutines, Icons.Outlined.ViewList, onOpenRoutines),
        AdminHomeAction(strings.adminExercises, Icons.Outlined.FitnessCenter, onOpenExercises),
        AdminHomeAction(strings.adminUsers, Icons.Outlined.People, onOpenUsers),
        AdminHomeAction(strings.adminGroups, Icons.Outlined.Groups, onOpenGroups),
        AdminHomeAction(strings.adminMissions, Icons.Outlined.Assignment, onOpenMissions),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(start = 18.dp, end = 18.dp, bottom = 18.dp, top = 50.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "GYM TONIC",
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1
                )
                Text(
                    text = strings.adminPanelSubtitle,
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 11.sp,
                    letterSpacing = 0.5.sp,
                    maxLines = 1
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                ThemeButton(tint = Color.White, modifier = Modifier.size(42.dp))
                LanguageButton(tint = Color.White, modifier = Modifier.height(42.dp))
                
                // Botón de Logout compacto pero equilibrado
                IconButton(
                    onClick = onLogout,
                    modifier = Modifier.size(42.dp)
                ) {
                    Icon(
                        Icons.Outlined.Logout,
                        contentDescription = strings.profileSignOut,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(28.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            horizontalArrangement = Arrangement.spacedBy(18.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(actions) { action ->
                AdminHomeTile(action)
            }
        }
    }
}

@Composable
private fun AdminHomeTile(action: AdminHomeAction) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { action.onClick() }
    ) {
        androidx.compose.foundation.layout.Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(92.dp)
                .background(Color.White, RoundedCornerShape(18.dp))
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
