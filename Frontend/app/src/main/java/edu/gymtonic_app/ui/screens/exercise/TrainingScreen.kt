package edu.gymtonic_app.ui.screens.exercise

import android.util.Log
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import coil.compose.AsyncImage
import edu.gymtonic_app.ui.screens.admin.resolveRoutineImageUrl
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.gymtonic_app.data.remote.remoteModel.training.TrainingCategoryDto
import edu.gymtonic_app.data.remote.remoteModel.training.TrainingRoutineDto
import edu.gymtonic_app.ui.i18n.LocalStrings
import edu.gymtonic_app.ui.theme.LocalColors

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TrainingScreen(
    onSelect: (Int, Boolean) -> Unit,
    onCreateRoutine: () -> Unit = {},
    recentRoutines: List<TrainingRoutineDto> = emptyList(),
    personalRoutines: List<TrainingRoutineDto> = emptyList(),
    groupRoutines: List<TrainingRoutineDto> = emptyList(),
    allRoutines: List<TrainingRoutineDto> = emptyList(),
    isRefreshing: Boolean = false,
    onRefresh: () -> Unit = {}
) {
    val strings = LocalStrings.current
    val colors = LocalColors.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = strings.trainingCategoriesAvailable(3),
                color = colors.textSubtle,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clickable { onCreateRoutine() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+",
                    color = colors.textSubtle,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                )
            }
        }

        Spacer(Modifier.height(6.dp))

        PullToRefreshBox(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            isRefreshing = isRefreshing,
            onRefresh = onRefresh
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(top = 4.dp, bottom = 14.dp),
            ) {
                // 1. Recientes
                if (recentRoutines.isNotEmpty()) {
                    item {
                        TrainingSection(
                            title = strings.trainingRecent,
                            routines = recentRoutines,
                            routinesLabel = strings.trainingRoutines,
                            tapToOpen = strings.trainingTapToOpen,
                            onSelect = onSelect,
                            isLocal = false
                        )
                    }
                }

                // 2. Mis rutinas (de verdad)
                if (personalRoutines.isNotEmpty()) {
                    item {
                        TrainingSection(
                            title = strings.trainingMyRoutines,
                            routines = personalRoutines,
                            routinesLabel = strings.trainingRoutines,
                            tapToOpen = strings.trainingTapToOpen,
                            onSelect = onSelect,
                            isLocal = false
                        )
                    }
                }

                // 3. Mis rutinas de grupo
                if (groupRoutines.isNotEmpty()) {
                    item {
                        TrainingSection(
                            title = strings.trainingGroupRoutines,
                            routines = groupRoutines,
                            routinesLabel = strings.trainingRoutines,
                            tapToOpen = strings.trainingTapToOpen,
                            onSelect = onSelect,
                            isLocal = false
                        )
                    }
                }

                // 4. Todas (Catálogo global)
                if (allRoutines.isNotEmpty()) {
                    item {
                        TrainingSection(
                            title = strings.trainingAll,
                            routines = allRoutines,
                            routinesLabel = strings.trainingRoutines,
                            tapToOpen = strings.trainingTapToOpen,
                            onSelect = onSelect,
                            isLocal = false
                        )
                    }
                }

                if (recentRoutines.isEmpty() && personalRoutines.isEmpty() && groupRoutines.isEmpty() && allRoutines.isEmpty() && !isRefreshing) {
                    item {
                        EmptyTrainingState(strings.trainingNoWorkouts)
                    }
                }
            }
        }
    }
}

@Composable
private fun TrainingSection(
    title: String,
    routines: List<TrainingRoutineDto>,
    routinesLabel: (Int) -> String,
    tapToOpen: String,
    onSelect: (Int, Boolean) -> Unit,
    isLocal: Boolean
) {
    val colors = LocalColors.current
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = colors.surfaceCard,
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
                    color = colors.textPrimary
                )

                Text(
                    text = routinesLabel(routines.size),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textSecondary
                )
            }

            Spacer(Modifier.height(10.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(start = 12.dp, end = 12.dp)
            ) {
                itemsIndexed(
                    items = routines,
                    key = { index, routine -> "${routine.routine_id}_$index" }
                ) { _, routine ->
                    TrainingCard(
                        option = routine,
                        tapToOpen = tapToOpen,
                        onSelect = onSelect,
                        isLocal = isLocal,
                        modifier = Modifier.width(158.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun TrainingCard(
    option: TrainingRoutineDto,
    tapToOpen: String,
    onSelect: (Int, Boolean) -> Unit,
    isLocal: Boolean,
    modifier: Modifier = Modifier
) {

    val colors = LocalColors.current
    val imageUrl = resolveRoutineImageUrl(option.routine_image)
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = colors.surfaceCard,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .clickable { onSelect(option.routine_id, isLocal) }
                .padding(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.25f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White)
            ) {
                if (!imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = option.displayName(),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

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
                text = option.displayName(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = colors.textPrimary
            )

            Text(
                text = tapToOpen,
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun EmptyTrainingState(message: String) {
    val colors = LocalColors.current
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        shape = RoundedCornerShape(18.dp),
        color = colors.surfaceCard
    ) {
        Text(
            text = message,
            color = colors.textSecondary,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 26.dp)
        )
    }
}
