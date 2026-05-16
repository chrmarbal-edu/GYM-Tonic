package edu.gymtonic_app.ui.screens.routines

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.util.Log
import coil.compose.AsyncImage
import edu.gymtonic_app.BuildConfig
import edu.gymtonic_app.data.remote.remoteModel.routine.RoutineExerciseDto
import edu.gymtonic_app.ui.i18n.LocalStrings
import edu.gymtonic_app.ui.theme.LocalColors

@Composable
fun RoutineTemplateScreen(
    exercises: List<RoutineExerciseDto>,
    onExerciseClick: (String, String) -> Unit,
    favoritesSet: Set<Int>,
    onToggleFavorite: (RoutineExerciseDto) -> Unit
) {
    val strings = LocalStrings.current
    val colors = LocalColors.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
    ) {
        Text(
            text = strings.exercisesAvailable(exercises.size),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = colors.textSubtle,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Spacer(Modifier.height(10.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 2.dp, vertical = 8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(exercises) { exercise ->
                RoutineExerciseRow(
                    exercise = exercise,
                    isFavorite = favoritesSet.contains(exercise.exercise_id),
                    favoriteEnabled = true,
                    removeFavoriteLabel = strings.removeFavorite,
                    markFavoriteLabel = strings.markFavorite,
                    setsAndRepsLabel = strings.setsAndReps,
                    onToggleFavorite = { onToggleFavorite(exercise) },
                    onClick = {
                        val repsText = if (!exercise.reps.isNullOrBlank()) exercise.reps!! else when (exercise.exercise_type) {
                            1 -> "x20"
                            2 -> "x30s"
                            else -> "x12"
                        }
                        onExerciseClick(exercise.exercise_id.toString(), repsText)
                    }
                )
            }
        }
    }
}

@Composable
private fun RoutineExerciseRow(
    exercise: RoutineExerciseDto,
    isFavorite: Boolean,
    favoriteEnabled: Boolean,
    removeFavoriteLabel: String,
    markFavoriteLabel: String,
    setsAndRepsLabel: String,
    onToggleFavorite: () -> Unit,
    onClick: () -> Unit
) {
    val colors = LocalColors.current
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = colors.surfaceCard,
        shadowElevation = 3.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            val imageKey = exercise.exercise_image
            val imageUrl = if (!imageKey.isNullOrBlank()) {
                if (imageKey.startsWith("http")) {
                    imageKey
                } else {
                    val normalizedKey = if (imageKey.startsWith("/")) imageKey else "/$imageKey"
                    val finalKey = if (!normalizedKey.contains(".")) "$normalizedKey.png" else normalizedKey
                    "${BuildConfig.BACKEND_BASE_URL}$finalKey"
                }
            } else null

            Log.d("RoutineTemplate", "Cargando imagen de ejercicio: $imageUrl")
            AsyncImage(
                model = imageUrl,
                contentDescription = exercise.exercise_name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(10.dp))
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = exercise.exercise_name ?: "Ejercicio sin nombre",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = colors.textPrimary
                )

                Text(
                    text = setsAndRepsLabel,
                    fontSize = 11.sp,
                    color = colors.textSecondary
                )
            }

            IconButton(
                onClick = onToggleFavorite,
                enabled = favoriteEnabled
            ) {
                Icon(
                    imageVector =
                        if (isFavorite) Icons.Filled.Favorite
                        else Icons.Outlined.FavoriteBorder,
                    contentDescription =
                        if (isFavorite) removeFavoriteLabel
                        else markFavoriteLabel,
                    tint =
                        if (favoriteEnabled) Color(0xFFE53935)
                        else Color(0xFF9EA3AF)
                )
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = colors.surfaceAccent
            ) {
                val repsText = if (!exercise.reps.isNullOrBlank()) exercise.reps!! else {
                    when (exercise.exercise_type) {
                        1 -> "x20"
                        2 -> "x30s"
                        else -> "x12"
                    }
                }
                Text(
                    text = repsText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textOnAccent,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}
