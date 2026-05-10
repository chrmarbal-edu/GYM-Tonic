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
import edu.gymtonic_app.ui.i18n.LocalStrings
import edu.gymtonic_app.ui.viewmodel.TrainingCategoryUi
import edu.gymtonic_app.ui.viewmodel.TrainingRoutineUi

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TrainingScreen(
    onSelect: (String, Boolean) -> Unit,
    onCreateRoutine: () -> Unit = {},
    categories: List<TrainingCategoryUi> = emptyList(),
    isRefreshing: Boolean = false,
    onRefresh: () -> Unit = {}
) {
    val strings = LocalStrings.current

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
                text = if (categories.isEmpty()) {
                    strings.trainingEmpty
                } else {
                    strings.trainingCategoriesAvailable(categories.size)
                },
                color = Color(0xFF464A57),
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
                    color = Color(0xFF464A57),
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
                content = {
                    if (categories.isEmpty() && !isRefreshing) {
                        item {
                            EmptyTrainingState(strings.trainingNoWorkouts)
                        }
                    } else {
                        for (category in categories) {
                            item(key = category.id) {
                                TrainingSection(
                                    title = category.title,
                                    routines = category.routines,
                                    routinesLabel = strings.trainingRoutines,
                                    tapToOpen = strings.trainingTapToOpen,
                                    onSelect = onSelect
                                )
                            }
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun TrainingSection(
    title: String,
    routines: List<TrainingRoutineUi>,
    routinesLabel: (Int) -> String,
    tapToOpen: String,
    onSelect: (String, Boolean) -> Unit
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
                    text = routinesLabel(routines.size),
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
                        tapToOpen = tapToOpen,
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
    tapToOpen: String,
    onSelect: (String, Boolean) -> Unit,
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
                .clickable { onSelect(option.id, option.isLocal) }
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
                text = tapToOpen,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF5D6270),
                maxLines = 1
            )
        }
    }
}

@Composable
private fun EmptyTrainingState(message: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        shape = RoundedCornerShape(18.dp),
        color = Color(0xFFE9EBF2)
    ) {
        Text(
            text = message,
            color = Color(0xFF4E5360),
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 26.dp)
        )
    }
}
