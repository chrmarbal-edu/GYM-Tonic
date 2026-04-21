package edu.gymtonic_app.ui.screens.exercise

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.gymtonic_app.ui.components.BottomNavBar
import edu.gymtonic_app.ui.components.BottomNavItem
import edu.gymtonic_app.ui.viewmodel.TrainingCategoryUi
import edu.gymtonic_app.ui.viewmodel.TrainingRoutineUi

//La que muestra la lista de entrenamientos disponibles
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TrainingScreen(
    onBack: () -> Unit,
    onSelect: (String) -> Unit,
    onOpenHome: () -> Unit = {},
    onOpenTraining: () -> Unit = {},
    onOpenChallenges: () -> Unit = {},
    onOpenProfile: () -> Unit = {},
    categories: List<TrainingCategoryUi> = emptyList(),
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
                    .padding(top = 24.dp)
            ) {
                TrainingHeaderRow(onBack = onBack)

                Text(
                    text = if (categories.isEmpty()) {
                        "Explora entrenamientos creados por la comunidad y el equipo"
                    } else {
                        "${categories.size} categorias disponibles para hoy"
                    },
                    color = Color(0xFF464A57),
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 8.dp)
                )

                Spacer(Modifier.height(6.dp))

                PullToRefreshBox(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 14.dp),
                    isRefreshing = isRefreshing,
                    // El refresh lo dispara la pantalla/contenedor (ViewModel en siguiente paso).
                    onRefresh = onRefresh
                ) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        contentPadding = PaddingValues(top = 4.dp, bottom = 14.dp),
                        content = {
                            if (categories.isEmpty() && !isRefreshing) {
                                item {
                                    EmptyTrainingState()
                                }
                            } else {
                                // Render dinamico: las categorias/rutinas vienen del backend via ViewModel.
                                for (category in categories) {
                                    item(key = category.id) {
                                        TrainingSection(
                                            title = category.title,
                                            routines = category.routines,
                                            onSelect = onSelect
                                        )
                                    }
                                }
                            }
                        }
                    )
                }

                BottomNavBar(
                    selectedItem = BottomNavItem.TRAINING,
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
private fun TrainingHeaderRow(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
                contentDescription = "Volver",
                tint = Color(0xFF2D2D2D)
            )
        }

        Text(
            text = "Entrenamientos",
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
private fun TrainingSection(
    title: String,
    routines: List<TrainingRoutineUi>,
    onSelect: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = Color(0xFFE9EBF2),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(vertical = 12.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D2D2D)
                )

                Text(
                    text = "${routines.size} rutinas",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF5D6270)
                )
            }

            Spacer(Modifier.height(10.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(start = 12.dp, end = 12.dp)
            ) {
                items(
                    items = routines,
                    key = { routine -> routine.id }
                ) { routine ->
                    TrainingCard(
                        option = routine,
                        onSelect = onSelect,
                        modifier = Modifier.width(158.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun TrainingCard(
    option: TrainingRoutineUi,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFFF8F9FC),
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .clickable { onSelect(option.id) }
                .padding(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.25f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White)
            ) {
                Image(
                    painter = painterResource(option.imageRes),
                    contentDescription = option.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color(0x7A000000))
                            )
                        )
                )
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = option.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = Color(0xFF1F2330)
            )

            Text(
                text = "Toca para abrir",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF5D6270),
                maxLines = 1
            )
        }
    }
}

@Composable
private fun EmptyTrainingState() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        shape = RoundedCornerShape(18.dp),
        color = Color(0xFFE9EBF2)
    ) {
        Text(
            text = "Aun no hay entrenamientos para mostrar.\nDesliza hacia abajo para recargar.",
            color = Color(0xFF4E5360),
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 26.dp)
        )
    }
}