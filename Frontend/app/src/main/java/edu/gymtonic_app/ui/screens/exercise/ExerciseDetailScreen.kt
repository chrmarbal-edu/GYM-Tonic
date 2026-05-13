package edu.gymtonic_app.ui.screens.exercise

import android.app.Application
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.gymtonic_app.R
import edu.gymtonic_app.ui.components.BottomNavItem
import edu.gymtonic_app.ui.i18n.LocalStrings
import edu.gymtonic_app.ui.theme.LocalColors
import edu.gymtonic_app.ui.viewmodel.exercise.ExerciseUiState
import edu.gymtonic_app.ui.viewmodel.exercise.ExerciseViewModel
import edu.gymtonic_app.ui.viewmodel.exercise.ExerciseViewModelFactory
import edu.gymtonic_app.ui.viewmodel.exercise.FavoriteExercisePayload

@Composable
fun ExerciseDetailScreen(
    exerciseId: String,
    onBack: () -> Unit,
    onOpenHome: () -> Unit = {},
    onOpenTraining: () -> Unit = {},
    onOpenChallenges: () -> Unit = {},
    onOpenProfile: () -> Unit = {},
    showBottomBar: Boolean = false,
    viewModel: ExerciseViewModel? = null
) {
    val strings = LocalStrings.current
    val colors = LocalColors.current
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val resolvedViewModel = viewModel ?: viewModel<ExerciseViewModel>(factory = ExerciseViewModelFactory(application))
    val uiState by resolvedViewModel.uiState.collectAsState()
    val favoritesSet by resolvedViewModel.favoritesSet.collectAsState()

    LaunchedEffect(exerciseId) {
        resolvedViewModel.loadSpecificExercise(exerciseId)
    }

    when (val state = uiState) {
        ExerciseUiState.Idle,
        ExerciseUiState.Loading -> {
            TrainingShellScreen(
                title = strings.exerciseTitle,
                onBack = onBack,
                showBottomBar = showBottomBar,
                selectedBottomItem = BottomNavItem.TRAINING,
                onOpenHome = onOpenHome,
                onOpenTraining = onOpenTraining,
                onOpenChallenges = onOpenChallenges,
                onOpenProfile = onOpenProfile
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }

        is ExerciseUiState.Error -> {
            TrainingShellScreen(
                title = strings.exerciseTitle,
                onBack = onBack,
                showBottomBar = showBottomBar,
                selectedBottomItem = BottomNavItem.TRAINING,
                onOpenHome = onOpenHome,
                onOpenTraining = onOpenTraining,
                onOpenChallenges = onOpenChallenges,
                onOpenProfile = onOpenProfile
            ) {
                Text(
                    text = state.message,
                    color = colors.textPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                )
            }
        }

        is ExerciseUiState.Success -> {
            val exercise = state.exercise
            val parsedExerciseId = exercise.id.toIntOrNull()
            val isFavorite = parsedExerciseId?.let { favoritesSet.contains(it) } == true

            val imageResId = remember(exercise.imageKey) {
                resolveDrawableId(context, exercise.imageKey) ?: R.drawable.fondo_gris
            }

            TrainingShellScreen(
                title = strings.exerciseTitle,
                onBack = onBack,
                showBottomBar = showBottomBar,
                selectedBottomItem = BottomNavItem.TRAINING,
                onOpenHome = onOpenHome,
                onOpenTraining = onOpenTraining,
                onOpenChallenges = onOpenChallenges,
                onOpenProfile = onOpenProfile
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = colors.surfaceCard,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Image(
                            painter = painterResource(id = imageResId),
                            contentDescription = exercise.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(RoundedCornerShape(14.dp))
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = exercise.name,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = colors.textPrimary,
                            modifier = Modifier.weight(1f)
                        )

                        IconButton(
                            onClick = {
                                resolvedViewModel.onToggleFavorite(
                                    FavoriteExercisePayload(
                                        id = exercise.id,
                                        name = exercise.name,
                                        description = exercise.instructions.joinToString("\n"),
                                        type = 0,
                                        video = null,
                                        image = exercise.imageKey
                                    )
                                )
                            },
                            enabled = parsedExerciseId != null
                        ) {
                            Icon(
                                imageVector =
                                    if (isFavorite) Icons.Filled.Favorite
                                    else Icons.Outlined.FavoriteBorder,
                                contentDescription =
                                    if (isFavorite) strings.removeFavorite
                                    else strings.markFavorite,
                                tint =
                                    if (parsedExerciseId != null) Color(0xFFE53935)
                                    else Color(0xFF9EA3AF)
                            )
                        }
                    }

                    Text(
                        text = strings.seconds(exercise.durationSeconds),
                        fontSize = 16.sp,
                        color = colors.accentDark,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    Spacer(Modifier.height(12.dp))

                    exercise.instructions.forEachIndexed { index, instruction ->
                        Text(
                            text = "${index + 1}. $instruction",
                            fontSize = 14.sp,
                            color = colors.textPrimary,
                            modifier = Modifier.padding(vertical = 3.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun resolveDrawableId(context: Context, imageKey: String?): Int? {
    if (imageKey.isNullOrBlank()) return null

    val normalized = imageKey
        .substringBeforeLast(".")
        .trim()
        .lowercase()
        .replace(" ", "_")

    val id = context.resources.getIdentifier(normalized, "drawable", context.packageName)
    return if (id != 0) id else null
}