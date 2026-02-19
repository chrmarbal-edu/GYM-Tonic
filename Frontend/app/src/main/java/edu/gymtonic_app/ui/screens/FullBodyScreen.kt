package edu.gymtonic_app.ui.screens



import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.gymtonic_app.R


data class Exercise(
    val name: String,
    val reps: String,
    val image: Int
)

@Composable
fun FullBodyScreen(
    onBack: () -> Unit
) {
    val bg = Brush.verticalGradient(
        listOf(
            Color(0xFF1F3F73),
            Color(0xFF3A2F7A),
            Color(0xFF2A3344)
        )
    )

    // hay que poner los iconos en los .expullup.. etc
    val exercises = listOf(
        Exercise("ESTOCADAS", "x10", R.drawable.estocadas),
        Exercise("PRESS BANCA", "x10", R.drawable.pressbanca),
        Exercise("PULL OVER", "x12", R.drawable.pullover),
        Exercise("REMO", "x15", R.drawable.remo),
        Exercise("SENTADILLA", "x15", R.drawable.sentadilla),
        Exercise("PESO MUERTO", "x20", R.drawable.pesomuerto),
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

                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onBack) {
                        Text("←", fontSize = 22.sp)
                    }

                    Spacer(Modifier.width(8.dp))

                    Text(
                        text = "FullBody",
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

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(exercises) { exercise ->
                        ExerciseRow(exercise)
                    }
                }
            }
        }
    }
}

@Composable
private fun ExerciseRow(exercise: Exercise) {

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(10.dp)
        ) {

            Icon(
                painter = painterResource(exercise.image),
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
