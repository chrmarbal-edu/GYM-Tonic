package edu.gymtonic_app.ui.screens.exercise

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.gymtonic_app.ui.components.BottomNavItem
import edu.gymtonic_app.ui.viewmodel.ExerciseUiState
import edu.gymtonic_app.ui.viewmodel.ExerciseViewModel
/*
ExerciseDetailScreen sí es una pantalla navegable real.
Es la pantalla de detalle del ejercicio.
Se abre cuando pulsas un ejercicio dentro de una rutina.
*/
@Composable
fun ExerciseDetailScreen(
    exerciseId: String,
    onBack: () -> Unit,
    onOpenHome: () -> Unit = {},
    onOpenTraining: () -> Unit = {},
    onOpenChallenges: () -> Unit = {},
    onOpenProfile: () -> Unit = {},
    showBottomBar: Boolean = false,
    viewModel: ExerciseViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(exerciseId) {
        viewModel.loadExercise(exerciseId)
    }

    when (val state = uiState) {
        ExerciseUiState.Idle,
        ExerciseUiState.Loading -> {
            TrainingShellScreen(
                title = "Ejercicio",
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
                title = "Ejercicio",
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
                    color = Color(0xFF1D1D1D),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                )
            }
        }

        is ExerciseUiState.Success -> {
            val exercise = state.exercise
            TrainingShellScreen(
                title = exercise.name,
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
                        color = Color(0xFFE9EBF2),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Image(
                            painter = painterResource(exercise.imageRes),
                            contentDescription = exercise.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(RoundedCornerShape(14.dp))
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = exercise.name,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1D1D1D)
                    )

                    Text(
                        text = "${exercise.durationSeconds} segundos",
                        fontSize = 16.sp,
                        color = Color(0xFF2C3ED6),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    Spacer(Modifier.height(12.dp))

                    exercise.instructions.forEachIndexed { index, instruction ->
                        Text(
                            text = "${index + 1}. $instruction",
                            fontSize = 14.sp,
                            color = Color(0xFF323846),
                            modifier = Modifier.padding(vertical = 3.dp)
                        )
                    }
                }
            }
        }
    }
}


