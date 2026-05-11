package edu.gymtonic_app.ui.screens.routines

import android.app.Application
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.gymtonic_app.data.local.localModel.ExerciseEntity
import edu.gymtonic_app.ui.components.BottomNavItem
import edu.gymtonic_app.ui.i18n.LocalStrings
import edu.gymtonic_app.ui.screens.exercise.TrainingShellScreen
import edu.gymtonic_app.ui.viewmodel.ExerciseViewModel
import edu.gymtonic_app.ui.viewmodel.ExerciseViewModelFactory
import edu.gymtonic_app.ui.viewmodel.RoutineCatalogViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRoutineScreen(
    onBack: () -> Unit,
    onRoutineCreated: () -> Unit = {},
    onOpenHome: () -> Unit = {},
    onOpenTraining: () -> Unit = {},
    onOpenChallenges: () -> Unit = {},
    onOpenProfile: () -> Unit = {},
    routineViewModel: RoutineCatalogViewModel = viewModel()
) {
    val strings = LocalStrings.current
    val context = LocalContext.current
    val application = context.applicationContext as Application

    val exerciseViewModel: ExerciseViewModel =
        viewModel(factory = ExerciseViewModelFactory(application))

    val favoriteExercises by exerciseViewModel.favoriteExercises.collectAsState()

    var routineName by rememberSaveable { mutableStateOf("") }
    var isSaving by rememberSaveable { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val selectedExerciseIds = remember { mutableStateListOf<Int>() }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(favoriteExercises) {
        selectedExerciseIds.retainAll(favoriteExercises.map { it.exercise_id }.toSet())
    }

    fun toggleSelection(exerciseId: Int) {
        if (selectedExerciseIds.contains(exerciseId)) {
            selectedExerciseIds.remove(exerciseId)
        } else {
            selectedExerciseIds.add(exerciseId)
        }
    }

    fun saveRoutine() {
        val trimmedName = routineName.trim()
        val selectedExercises = favoriteExercises.filter { exercise ->
            selectedExerciseIds.contains(exercise.exercise_id)
        }

        if (trimmedName.isBlank()) {
            errorMessage = "El nombre de la rutina no puede estar vacío"
            return
        }

        if (selectedExercises.isEmpty()) {
            errorMessage = "Debes seleccionar al menos un ejercicio"
            return
        }

        isSaving = true
        errorMessage = null

        val imageKey = selectedExercises.firstOrNull()?.exercise_image

        routineViewModel.createUserRoutineWithExercises(
            routineName = trimmedName,
            exercises = selectedExercises,
            imageKey = imageKey,
            onSuccess = {
                isSaving = false
                onRoutineCreated()
            },
            onError = { message ->
                isSaving = false
                errorMessage = message
                scope.launch {
                    snackbarHostState.showSnackbar(message)
                }
            }
        )
    }

    TrainingShellScreen(
        title = strings.createRoutineTitle,
        onBack = onBack,
        showBottomBar = true,
        selectedBottomItem = BottomNavItem.TRAINING,
        onOpenHome = onOpenHome,
        onOpenTraining = onOpenTraining,
        onOpenChallenges = onOpenChallenges,
        onOpenProfile = onOpenProfile
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            SnackbarHost(hostState = snackbarHostState)

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
            ) {
                item {
                    Text(
                        text = strings.basicInfo,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1D1D1D)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = routineName,
                        onValueChange = { routineName = it },
                        label = { Text(strings.routineName) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF3B4EE8),
                            unfocusedBorderColor = Color(0xFFC4C4C4)
                        )
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Ejercicios favoritos",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1D1D1D),
                            modifier = Modifier.weight(1f)
                        )

                        Text(
                            text = "${selectedExerciseIds.size}/${favoriteExercises.size}",
                            fontSize = 12.sp,
                            color = Color(0xFF5D6270)
                        )
                    }
                }

                if (favoriteExercises.isEmpty()) {
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFFE9EBF2)
                        ) {
                            Text(
                                text = "No tienes ejercicios favoritos guardados todavía",
                                modifier = Modifier.padding(20.dp),
                                color = Color(0xFF4E5360)
                            )
                        }
                    }
                } else {
                    items(
                        items = favoriteExercises,
                        key = { it.exercise_id }
                    ) { exercise ->
                        FavoriteExerciseRow(
                            exercise = exercise,
                            selected = selectedExerciseIds.contains(exercise.exercise_id),
                            onToggle = { toggleSelection(exercise.exercise_id) }
                        )
                    }
                }

                if (errorMessage != null) {
                    item {
                        Text(
                            text = errorMessage.orEmpty(),
                            color = Color(0xFFD32F2F),
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Button(
                onClick = { saveRoutine() },
                enabled = !isSaving && routineName.isNotBlank() && selectedExerciseIds.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3B4EE8),
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFFB7BCEB),
                    disabledContentColor = Color.White
                )
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(20.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                } else {
                    Text(
                        text = strings.saveRoutine,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun FavoriteExerciseRow(
    exercise: ExerciseEntity,
    selected: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = exercise.exercise_name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF1F2330)
                )
                Text(
                    text = exercise.exercise_description,
                    fontSize = 11.sp,
                    color = Color(0xFF5D6270)
                )
            }

            TextButton(onClick = onToggle) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = if (selected) Color(0xFF3B4EE8) else Color(0xFF9EA3AF)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = if (selected) "Añadido" else "Añadir")
            }
        }
    }
}