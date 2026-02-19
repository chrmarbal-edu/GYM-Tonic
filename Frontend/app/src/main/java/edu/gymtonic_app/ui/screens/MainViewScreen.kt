package edu.gymtonic_app.ui.screens
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

data class HomeAction(
    val title: String,
    val icon: ImageVector,
    val highlight: Boolean = false,
    val onClick: () -> Unit
)

@Composable
fun MainViewScreen(
    onOpenTraining: () -> Unit,
    onOpenTechnogym: () -> Unit,
    onOpenMusic: () -> Unit,
    onOpenDiscounts: () -> Unit,
    onOpenCoach: () -> Unit,
    onOpenFindGym: () -> Unit,
    onOpenClientArea: () -> Unit,
    onOpenQr: () -> Unit,
    onInviteFriend: () -> Unit,
    onOpenBookings: () -> Unit,
    onOpenWhatsapp: () -> Unit,
    onOpenInstagram: () -> Unit,
) {
    val bg = Brush.verticalGradient(
        listOf(
            Color(0xFF1F3F73),
            Color(0xFF3A2F7A),
            Color(0xFF2A3344)
        )
    )

    val actions = listOf(
        HomeAction("Entrenamientos", Icons.Outlined.FitnessCenter, onClick = onOpenTraining),
        HomeAction("Technogym App", Icons.Outlined.Devices, onClick = onOpenTechnogym),
        HomeAction("Música del club", Icons.Outlined.MusicNote, onClick = onOpenMusic),
        HomeAction("Descuentos", Icons.Outlined.LocalOffer, onClick = onOpenDiscounts),
        HomeAction("Entrenador Personal", Icons.Outlined.Person, onClick = onOpenCoach),
        HomeAction("Encontrar un gimnasio", Icons.Outlined.LocationOn, onClick = onOpenFindGym),
        HomeAction("Mi Espacio Cliente", Icons.Outlined.AccountCircle, onClick = onOpenClientArea),
        HomeAction("Código QR", Icons.Outlined.QrCode2, highlight = true, onClick = onOpenQr),
        HomeAction("Invitar a un amigo", Icons.Outlined.GroupAdd, onClick = onInviteFriend),
        HomeAction("Reservas", Icons.Outlined.EventAvailable, onClick = onOpenBookings),
        HomeAction("WhatsApp", Icons.Outlined.Chat, onClick = onOpenWhatsapp),
        HomeAction("Instagram", Icons.Outlined.CameraAlt, onClick = onOpenInstagram),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(18.dp)
    ) {
        Text(
            text = "GYMTONIC",
            color = Color.White,
            fontSize = 36.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = "DESAFÍATE · SUPÉRATE",
            color = Color.White.copy(alpha = 0.85f),
            fontSize = 12.sp,
            letterSpacing = 2.sp
        )

        Spacer(Modifier.height(26.dp))

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