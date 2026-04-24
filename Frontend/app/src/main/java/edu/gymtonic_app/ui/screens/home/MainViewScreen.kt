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

// -------------------- DATA MODEL --------------------

data class HomeAction(
    val title: String,
    val icon: ImageVector,
    val highlight: Boolean = false,
    val onClick: () -> Unit
)

// -------------------- MAIN SCREEN --------------------

@Composable
fun MainViewScreen(
    onLogout : () -> Unit,
    onOpenTraining: () -> Unit,
    onCreateRoutine: () -> Unit,
    onOpenTechnogym: () -> Unit,
    onOpenDiscounts: () -> Unit,
    onOpenFindGym: () -> Unit,
    onOpenClientArea: () -> Unit,
    onInviteFriend: () -> Unit,
    onOpenMissions: () -> Unit
) {

    val bg = Brush.verticalGradient(
        listOf(
            Color(0xFF1F3F73),
            Color(0xFF3A2F7A),
            Color(0xFF2A3344)
        )
    )

    val actions = listOf(
        HomeAction("Entrenamientos Predefinidos", Icons.Outlined.FitnessCenter, onClick = onOpenTraining),
        HomeAction("Crear Rutinas", Icons.Outlined.FitnessCenter, onClick = onCreateRoutine),
        HomeAction("Technogym App", Icons.Outlined.Devices, onClick = onOpenTechnogym),

        HomeAction("Descuentos", Icons.Outlined.LocalOffer, onClick = onOpenDiscounts),
        HomeAction("Desafios", Icons.Outlined.EventAvailable, onClick = onOpenMissions),

        HomeAction("Encontrar gimnasio", Icons.Outlined.LocationOn, onClick = onOpenFindGym),
        HomeAction("Mi espacio cliente", Icons.Outlined.AccountCircle, onClick = onOpenClientArea),
        HomeAction("Chat", Icons.Outlined.GroupAdd, onClick = onInviteFriend),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(start = 18.dp, end = 18.dp, bottom = 18.dp, top = 50.dp)
    ) {

        // ---------------- HEADER ----------------

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
                    text = "DESAFÍATE · SUPÉRATE",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 12.sp,
                    letterSpacing = 2.sp
                )
            }

            // 🔥 LOGOUT BUTTON
            IconButton(
                onClick = { onLogout() }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Logout,
                    contentDescription = "Logout",
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }
        }

        Spacer(Modifier.height(35.dp))

        // ---------------- GRID ----------------

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

// ---------------- TILE COMPONENT ----------------

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