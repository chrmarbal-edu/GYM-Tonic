package edu.gymtonic_app.ui.screens.missions

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.gymtonic_app.ui.components.BottomNavBar
import edu.gymtonic_app.ui.components.BottomNavItem

data class WeeklyGoalUi(
    val title: String,
    val progressLabel: String,
    val pointsLabel: String,
    val progress: Float
)

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun WeekChallengesScreen(
    onBack: () -> Unit,
    onOpenHome: () -> Unit,
    onOpenTraining: () -> Unit,
    onOpenChallenges: () -> Unit,
    onOpenProfile: () -> Unit,
    onShowMoreCalendar: () -> Unit,
    isRefreshing: Boolean = false,
    onRefresh: () -> Unit = {}
) {
    val bg = Brush.verticalGradient(
        listOf(
            Color(0xFF1F3F73),
            Color(0xFF3A2F7A),
            Color(0xFF2A3344)
        )
    )

    val goals = listOf(
        WeeklyGoalUi(
            title = "Entrenar 5 dias a la semana",
            progressLabel = "2/5",
            pointsLabel = "+ 300 pts",
            progress = 0.40f
        ),
        WeeklyGoalUi(
            title = "Quemar 1000 kcal",
            progressLabel = "882/1000",
            pointsLabel = "+ 120 pts",
            progress = 0.88f
        ),
        WeeklyGoalUi(
            title = "Manten la racha 2 dias seguidos",
            progressLabel = "1/2",
            pointsLabel = "+ 50 pts",
            progress = 0.50f
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(start = 12.dp, end = 12.dp, bottom = 12.dp, top = 42.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(34.dp),
            color = Color(0xFFD9D9D9),
            shadowElevation = 10.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 25.dp)
            ) {
                HeaderRow(onBack = onBack)

                PullToRefreshBox(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 14.dp),
                    isRefreshing = isRefreshing,
                    // El refresh recarga el contenido principal semanal desde el contenedor/ViewModel.
                    onRefresh = onRefresh
                ) {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 6.dp, bottom = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Objetivos semanales",
                                    color = Color(0xFF2D2D2D),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                                Text(
                                    text = "0/3 Logrados",
                                    color = Color(0xFF2D2D2D),
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        items(goals) { goal ->
                            GoalCard(goal = goal)
                        }

                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 12.dp, bottom = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Mi Calendario",
                                    color = Color(0xFF2D2D2D),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                                Text(
                                    text = "Mostrar mas",
                                    color = Color(0xFF3A42B9),
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    modifier = Modifier
                                        .clickable { onShowMoreCalendar() }
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color.Transparent)
                                        .padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }

                        item {
                            CalendarCard()
                        }
                    }
                }

                BottomNavBar(
                    selectedItem = BottomNavItem.CHALLENGES,
                    onOpenHome = onOpenHome,
                    onOpenTraining = onOpenTraining,
                    onOpenChallenges = onOpenChallenges,
                    onOpenProfile = onOpenProfile
                )
            }
        }
    }
}

@Composable
private fun HeaderRow(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
                contentDescription = "Volver",
                tint = Color(0xFF2D2D2D)
            )
        }

        Text(
            text = "Semana",
            color = Color(0xFF1D1D1D),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 26.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.size(40.dp))
    }
}

@Composable
private fun GoalCard(goal: WeeklyGoalUi) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = Color(0xFF8B8EEA),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.EmojiEvents,
                contentDescription = null,
                tint = Color(0xFFF4C542),
                modifier = Modifier.size(34.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = goal.title,
                    color = Color(0xFF1D1D1D),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = goal.progressLabel,
                    color = Color(0xFF1D1D1D),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 1.dp, bottom = 4.dp)
                )
                LinearProgressIndicator(
                    progress = { goal.progress },
                    color = Color(0xFF1EF847),
                    trackColor = Color(0xFFE6E8EB),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(9.dp)
                        .clip(RoundedCornerShape(6.dp))
                )
            }

            Text(
                text = goal.pointsLabel,
                color = Color(0xFF1D1D1D),
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun CalendarCard() {
    val weekColors = listOf(
        Color(0xFF22FF19), Color(0xFFFF1A1A), Color(0xFF22FF19), Color(0xFF22FF19), Color(0xFFFF1A1A), Color(0xFFFF1A1A), Color(0xFFFF1A1A),
        Color(0xFFFF1A1A), Color(0xFF22FF19), Color(0xFF22FF19), Color(0xFFFF1A1A), Color(0xFF22FF19), Color(0xFFFF1A1A), Color(0xFFFF1A1A),
        Color(0xFF22FF19), Color(0xFFFF1A1A), Color(0xFF22FF19), Color(0xFF66D9FF), Color(0xFFD0D3DB), Color(0xFFD0D3DB), Color(0xFFD0D3DB),
        Color(0xFFD0D3DB), Color(0xFFD0D3DB), Color(0xFFD0D3DB), Color(0xFFD0D3DB), Color(0xFFD0D3DB), Color(0xFFD0D3DB), Color(0xFFD0D3DB)
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFF8186EA)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (row in 0 until 4) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (col in 0 until 7) {
                        val colorIndex = row * 7 + col
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(24.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(weekColors[colorIndex])
                        )
                    }
                }
            }
        }
    }
}

