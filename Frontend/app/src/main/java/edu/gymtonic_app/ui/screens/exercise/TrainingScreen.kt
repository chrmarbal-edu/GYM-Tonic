package edu.gymtonic_app.ui.screens.exercise

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.gymtonic_app.R
import edu.gymtonic_app.ui.components.BottomNavBar
import edu.gymtonic_app.ui.components.BottomNavItem

// routineId es la clave que vendrá del backend y se usa para enlazar con rutas de screens/routines.
data class TrainingRoutineUi(
    val id: String,
    val title: String,
    val imageRes: Int
)

// Cada categoría también llega del backend con una lista dinámica de rutinas.
data class TrainingCategoryUi(
    val id: String,
    val title: String,
    val routines: List<TrainingRoutineUi>
)

private fun trainingCategoriesFromBackendMock(): List<TrainingCategoryUi> {
    // Mock temporal: cuando se conecte el back, esta estructura debe venir del repositorio/ViewModel.
    return listOf(
        TrainingCategoryUi(
            id = "recent",
            title = "Recientes",
            routines = listOf(
                TrainingRoutineUi("back", "Espalda", R.drawable.espalda),
                TrainingRoutineUi("fullbody", "Full Body", R.drawable.fullbody),
                TrainingRoutineUi("push", "Empujes", R.drawable.pushup)
            )
        ),
        TrainingCategoryUi(
            id = "beginners",
            title = "Para Principiantes",
            routines = listOf(
                TrainingRoutineUi("stretch", "Estiramientos", R.drawable.estiramientos),
                TrainingRoutineUi("arm", "Brazo", R.drawable.brazo),
                TrainingRoutineUi("calves", "Gemelos", R.drawable.pierna)
            )
        ),
        TrainingCategoryUi(
            id = "muscle_groups",
            title = "Por Grupo Muscular",
            routines = listOf(
                TrainingRoutineUi("calves", "Gemelos", R.drawable.pierna),
                TrainingRoutineUi("arm", "Brazo", R.drawable.brazo),
                TrainingRoutineUi("back", "Espalda", R.drawable.espalda)
            )
        ),
        TrainingCategoryUi(
            id = "recommended",
            title = "Recomendados",
            routines = listOf(
                TrainingRoutineUi("fullbody", "Full Body", R.drawable.fullbody),
                TrainingRoutineUi("push", "Empujes", R.drawable.pushup),
                TrainingRoutineUi("stretch", "Estiramientos", R.drawable.estiramientos)
            )
        )
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TrainingScreen(
    onBack: () -> Unit,
    onSelect: (String) -> Unit,
    onOpenHome: () -> Unit = {},
    onOpenTraining: () -> Unit = {},
    onOpenChallenges: () -> Unit = {},
    onOpenProfile: () -> Unit = {},
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

    val categories = trainingCategoriesFromBackendMock()

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
                TrainingHeaderRow(onBack = onBack)

                Spacer(Modifier.height(14.dp))

                PullToRefreshBox(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 14.dp),
                    isRefreshing = isRefreshing,
                    // El refresh lo dispara la pantalla/contenedor (ViewModel en siguiente paso).
                    onRefresh = onRefresh
                ) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 12.dp),
                        content = {
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
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .size(36.dp)
                .align(Alignment.CenterStart)
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
            fontSize = 26.sp
        )
    }
}

@Composable
private fun TrainingSection(
    title: String,
    routines: List<TrainingRoutineUi>,
    onSelect: (String) -> Unit
) {
    Column {
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2D2D2D)
        )
        Spacer(Modifier.height(10.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(end = 4.dp)
        ) {
            items(
                items = routines,
                key = { routine -> routine.id }
            ) { routine ->
                TrainingCard(
                    option = routine,
                    onSelect = onSelect,
                    modifier = Modifier.width(150.dp)
                )
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
    Column(
        modifier = modifier.clickable { onSelect(option.id) }
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
        }

        Spacer(Modifier.height(6.dp))

        Text(
            text = option.title,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF2D2D2D)
        )
    }
}