package edu.gymtonic_app.ui.screens.routines

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Modelo de cada ejercicio dentro de una rutina. En producción llegará del backend.
data class RoutineExerciseUi(
    val name: String,
    val reps: String,
    val imageRes: Int
)

@Composable
fun RoutineTemplateScreen(
    title: String,
    exercises: List<RoutineExerciseUi>,
    onBack: () -> Unit
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
            .padding(18.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(70.dp),
            color = Color(0xFFD9D9D9),
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(22.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = onBack) {
                        Text("←", fontSize = 22.sp)
                    }

                    Spacer(Modifier.width(8.dp))

                    Text(
                        text = title,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Spacer(Modifier.height(6.dp))

                Text(
                    text = "${exercises.size} Ejercicios",
                    fontSize = 13.sp,
                    color = Color.DarkGray
                )

                Spacer(Modifier.height(12.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(exercises) { exercise ->
                        RoutineExerciseRow(exercise = exercise)
                    }
                }
            }
        }
    }
}

@Composable
private fun RoutineExerciseRow(exercise: RoutineExerciseUi) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(10.dp)
        ) {
            Icon(
                painter = painterResource(exercise.imageRes),
                contentDescription = null,
                tint = Color.Unspecified,
                modifier = Modifier.size(48.dp)
            )

            Spacer(Modifier.width(12.dp))

            Column {
                Text(
                    text = exercise.name,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )

                Text(
                    text = exercise.reps,
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )
            }
        }
    }
}

