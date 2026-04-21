package edu.gymtonic_app.ui.screens.exercise

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
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
    viewModel: ExerciseViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(exerciseId) {
        viewModel.loadExercise(exerciseId)
    }

    when (val state = uiState) {
        ExerciseUiState.Idle,
        ExerciseUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is ExerciseUiState.Error -> {
            ExerciseDetailContainer(onBack = onBack) {
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
            ExerciseDetailContainer(onBack = onBack) {
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

@Composable
private fun ExerciseDetailContainer(
    onBack: () -> Unit,
    content: @Composable () -> Unit
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
                    .padding(top = 24.dp),
                verticalArrangement = Arrangement.Top
            ) {
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
                        text = "Entrenamiento",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1D1D1D),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.size(40.dp))
                }

                Spacer(Modifier.height(12.dp))
                content()
            }
        }
    }
}

