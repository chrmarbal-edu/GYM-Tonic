package edu.gymtonic_app.ui.screens.exercise

import android.app.Application
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.gymtonic_app.R
import edu.gymtonic_app.ui.components.BottomNavItem
import edu.gymtonic_app.ui.viewmodel.ExerciseUiState
import edu.gymtonic_app.ui.viewmodel.ExerciseViewModel
import edu.gymtonic_app.ui.viewmodel.ExerciseViewModelFactory

/*
Esta clase se encarga de mostrar los detalles de un ejercicio.
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
    viewModel: ExerciseViewModel? = null
) {
    // Obtenemos el contexto y la aplicación
    val context = LocalContext.current
    val application = context.applicationContext as Application
    // Si no se pasa el viewModel, se crea uno nuevo
    val resolvedViewModel = viewModel ?: viewModel<ExerciseViewModel>(factory = ExerciseViewModelFactory(application))
    // Obtenemos el estado del viewModel
    val uiState by resolvedViewModel.uiState.collectAsState()
    // Obtenemos el conjunto de favoritos
    val favoritesSet by resolvedViewModel.favoritesSet.collectAsState()

    LaunchedEffect(exerciseId) {
        resolvedViewModel.loadSpecificExercise(exerciseId)
    }

    //Según el estado del state, se muestra u otro contenido
    when (val state = uiState) {
        // Mostramos el estado inicial
        ExerciseUiState.Idle,

        //  Mostramos el estado de carga
        ExerciseUiState.Loading -> {
            TrainingShellScreen(    // Esto es para que se vea mientras carga el ejercicio
                title = stringResource(R.string.ejercicio),
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

        // Si hay un error, mostramos el mensaje de error
        is ExerciseUiState.Error -> {
            TrainingShellScreen(
                title = stringResource(R.string.ejercicio),
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

        // Si la carga es exitosa, mostramos el contenido
        is ExerciseUiState.Success -> {
            val exercise = state.exercise // Obtenemos el ejercicio de la UI
            val parsedExerciseId = exercise.id.toIntOrNull() // Parseamos el id del ejercicio
            val isFavorite = parsedExerciseId?.let { favoritesSet.contains(it) } == true // Comprobamos si es favorito

            // Esto es para que se vea el ejercicio dentro de la pantalla
            TrainingShellScreen(
                title = stringResource(R.string.ejercicio),
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

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = exercise.name,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1D1D1D),
                            modifier = Modifier.weight(1f)
                        )

                        // Icono de favorito con el color correspondiente
                        IconButton(
                            // llamo al metodo del vm encargado de swichear el favorito
                            onClick = { resolvedViewModel.onToggleFavorite(exercise.id) },
                            enabled = parsedExerciseId != null // Solo se puede pulsar si el id es valido
                        ) {
                            Icon(
                                imageVector =
                                    if (isFavorite) Icons.Filled.Favorite
                                    else Icons.Outlined.FavoriteBorder,
                                contentDescription =
                                    if (isFavorite) stringResource(R.string.quitar_favorito)
                                    else stringResource(R.string.marcar_favorito),
                                tint =
                                    if (parsedExerciseId != null) Color(0xFFE53935)
                                    else Color(0xFF9EA3AF)
                            )
                        }
                    }

                    // Mostramos los detalles del ejercicio
                    Text(
                        text = "${exercise.durationSeconds} segundos",
                        fontSize = 16.sp,
                        color = Color(0xFF2C3ED6),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    Spacer(Modifier.height(12.dp))

                    // Mostramos las instrucciones del ejercicio
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
