package edu.gymtonic_app.ui.components.screens



import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
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

data class TrainingOption(
    val id: String,
    val title: String,
    val imageRes: Int
)

@Composable
fun TrainingScreen(
    onBack: () -> Unit,
    onSelect: (String) -> Unit
) {
    val bg = Brush.verticalGradient(
        listOf(
            Color(0xFF1F3F73),
            Color(0xFF3A2F7A),
            Color(0xFF2A3344)
        )
    )

    val recent = listOf(
        TrainingOption("back", "Espalda", R.drawable.espalda),
        TrainingOption("fullbody", "Full Body", R.drawable.fullbody),
    )
    val beginners = listOf(
        TrainingOption("stretch", "Estiramientos", R.drawable.estiramientos),
        TrainingOption("push", "Empujes", R.drawable.pushup),
    )
    val muscle = listOf(
        TrainingOption("calves", "Gemelos", R.drawable.pierna),
        TrainingOption("arm", "Brazo", R.drawable.brazo),
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(18.dp)
    ) {
        // Card grande gris (como tu imagen)
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(70.dp),
            color = Color(0xFFD9D9D9),
            shadowElevation = 10.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(22.dp)
            ) {
                // Cabecera con Back
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onBack, contentPadding = PaddingValues(0.dp)) {
                        Text("←", fontSize = 22.sp, color = Color(0xFF2D2D2D))
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Entrenamientos",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF2D2D2D)
                    )
                }

                Spacer(Modifier.height(10.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 12.dp)
                ) {
                    item {
                        TrainingSection(
                            title = "Recientes",
                            left = recent[0],
                            right = recent[1],
                            onSelect = onSelect
                        )
                    }
                    item {
                        TrainingSection(
                            title = "Para Principiantes",
                            left = beginners[0],
                            right = beginners[1],
                            onSelect = onSelect
                        )
                    }
                    item {
                        TrainingSection(
                            title = "Por Grupo Muscular",
                            left = muscle[0],
                            right = muscle[1],
                            onSelect = onSelect
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TrainingSection(
    title: String,
    left: TrainingOption,
    right: TrainingOption,
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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TrainingCard(option = left, onSelect = onSelect, modifier = Modifier.weight(1f))
            TrainingCard(option = right, onSelect = onSelect, modifier = Modifier.weight(1f))

            // Flecha derecha como en tu imagen
            Text(
                text = "→",
                fontSize = 20.sp,
                color = Color(0xFF2D2D2D),
                modifier = Modifier.padding(start = 2.dp)
            )
        }
    }
}

@Composable
private fun TrainingCard(
    option: TrainingOption,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable { onSelect(option.id) }
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
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF2D2D2D)
        )
    }
}
