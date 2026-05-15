package edu.gymtonic_app.ui.screens.exercise

import android.app.Application
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import android.util.Log
import androidx.annotation.OptIn
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import edu.gymtonic_app.BuildConfig
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
            val isFavorite = favoritesSet.contains(exercise.exercise_id)

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
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // VIDEO SECTION - 55% de altura
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = colors.surfaceCard,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(380.dp)
                            .padding(horizontal = 14.dp)
                    ) {
                        val videoKey = exercise.exercise_video
                        if (!videoKey.isNullOrBlank()) {
                            val videoUrl = if (videoKey.startsWith("http")) videoKey
                            else "${BuildConfig.BACKEND_BASE_URL}${if (videoKey.startsWith("/")) "" else "/"}$videoKey"
                            Log.d("ExerciseDetail", "Reproduciendo video: $videoUrl")
                            VideoPlayer(
                                videoUrl = videoUrl,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(14.dp))
                            )
                        } else {
                            val imageKey = exercise.exercise_image
                            val imageUrl = if (!imageKey.isNullOrBlank()) {
                                if (imageKey.startsWith("http")) imageKey
                                else {
                                    val normalizedKey = if (imageKey.startsWith("/")) imageKey else "/$imageKey"
                                    val finalKey = if (!normalizedKey.contains(".")) "$normalizedKey.png" else normalizedKey
                                    "${BuildConfig.BACKEND_BASE_URL}$finalKey"
                                }
                            } else null

                            Log.d("ExerciseDetail", "Cargando imagen de detalle (fallback): $imageUrl")
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(14.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                coil.compose.AsyncImage(
                                    model = imageUrl,
                                    contentDescription = exercise.exercise_name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    // INFORMACIÓN SECTION - Con scroll
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = exercise.exercise_name,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = colors.textPrimary,
                                modifier = Modifier.weight(1f)
                            )

                            IconButton(
                                onClick = {
                                    resolvedViewModel.onToggleFavorite(
                                        FavoriteExercisePayload(
                                            id = exercise.exercise_id,
                                            name = exercise.exercise_name,
                                            description = exercise.exercise_description,
                                            type = exercise.exercise_type,
                                            video = exercise.exercise_video,
                                            image = exercise.exercise_image
                                        )
                                    )
                                }
                            ) {
                                Icon(
                                    imageVector =
                                        if (isFavorite) Icons.Filled.Favorite
                                        else Icons.Outlined.FavoriteBorder,
                                    contentDescription =
                                        if (isFavorite) strings.removeFavorite
                                        else strings.markFavorite,
                                    tint = Color(0xFFE53935)
                                )
                            }
                        }

                        Text(
                            text = strings.seconds(0), // No tenemos duración en el DTO por ahora
                            fontSize = 16.sp,
                            color = colors.accentDark,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 2.dp)
                        )

                        Spacer(Modifier.height(16.dp))

                        Log.d("ExerciseDetail", "Mostrando descripción: ${exercise.exercise_description}")
                        Text(
                            text = exercise.exercise_description,
                            fontSize = 14.sp,
                            color = colors.textPrimary,
                            modifier = Modifier.padding(vertical = 3.dp),
                            lineHeight = 20.sp
                        )

                        Spacer(Modifier.height(30.dp))
                    }
                }
            }
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayer(videoUrl: String, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(videoUrl)
            setMediaItem(mediaItem)
            repeatMode = Player.REPEAT_MODE_ALL
            prepare()
            playWhenReady = true
        }
    }

    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer.release()
        }
    }

    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
                useController = false
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
            }
        },
        modifier = modifier
    )
}