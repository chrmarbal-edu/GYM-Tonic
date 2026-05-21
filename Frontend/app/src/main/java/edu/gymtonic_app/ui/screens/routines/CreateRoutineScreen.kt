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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import edu.gymtonic_app.data.remote.remoteModel.exercise.ExerciseDto
import edu.gymtonic_app.ui.components.BottomNavItem
import edu.gymtonic_app.ui.components.LocalAppSnackbarHostState
import edu.gymtonic_app.ui.components.showAppToast
import edu.gymtonic_app.ui.i18n.LocalStrings
import edu.gymtonic_app.ui.screens.exercise.TrainingShellScreen
import edu.gymtonic_app.ui.theme.LocalColors
import edu.gymtonic_app.ui.viewmodel.exercise.ExerciseViewModel
import edu.gymtonic_app.ui.viewmodel.exercise.ExerciseViewModelFactory
import edu.gymtonic_app.ui.screens.admin.uriToUploadFile
import edu.gymtonic_app.ui.viewmodel.routine.RoutineCatalogViewModel
import java.io.File
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRoutineScreen(
    onBack: () -> Unit,
    onRoutineCreated: () -> Unit = {},
    onOpenTraining: () -> Unit = {},
    onOpenGroups: () -> Unit = {},
    onOpenFriends: () -> Unit = {},
    onOpenChallenges: () -> Unit = {},
    onOpenProfile: () -> Unit = {},
    routineViewModel: RoutineCatalogViewModel = viewModel()
) {
    val strings = LocalStrings.current
    val colors = LocalColors.current
    val context = LocalContext.current
    val application = context.applicationContext as Application

    val exerciseViewModel: ExerciseViewModel =
        viewModel(factory = ExerciseViewModelFactory(application))

    val favoriteExercises by exerciseViewModel.favoriteExercises.collectAsState()
    val allExercises by exerciseViewModel.allExercises.collectAsState()

    var routineName by rememberSaveable { mutableStateOf("") }
    var isSaving by rememberSaveable { mutableStateOf(false) }
    var routineImageFile by remember { mutableStateOf<File?>(null) }
    var routineImageLabel by remember { mutableStateOf<String?>(null) }
    val selectedExercises = remember { mutableStateListOf<edu.gymtonic_app.data.remote.remoteModel.routine.RoutineExerciseDto>() }
    var searchQuery by remember { mutableStateOf("") }
    
    var exerciseToConfigure by remember { mutableStateOf<edu.gymtonic_app.data.remote.remoteModel.exercise.ExerciseDto?>(null) }

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri ?: return@rememberLauncherForActivityResult
        routineImageFile = uriToUploadFile(context, uri, "user_routine_image_")
        routineImageLabel = routineImageFile?.name
    }
    val snackbarHostState = LocalAppSnackbarHostState.current
    val scope = rememberCoroutineScope()

    if (exerciseToConfigure != null) {
        AddExerciseDetailsDialog(
            exerciseName = exerciseToConfigure!!.exercise_name,
            onDismiss = { exerciseToConfigure = null },
            onConfirm = { reps, series ->
                selectedExercises.add(
                    edu.gymtonic_app.data.remote.remoteModel.routine.RoutineExerciseDto(
                        exercise_id = exerciseToConfigure!!.exercise_id,
                        exercise_name = exerciseToConfigure!!.exercise_name,
                        exercise_description = exerciseToConfigure!!.exercise_description,
                        exercise_type = exerciseToConfigure!!.exercise_type,
                        exercise_image = exerciseToConfigure!!.exercise_image,
                        reps = reps,
                        series = series
                    )
                )
                exerciseToConfigure = null
            }
        )
    }

    fun toggleSelection(exercise: edu.gymtonic_app.data.remote.remoteModel.exercise.ExerciseDto) {
        val existing = selectedExercises.find { it.exercise_id == exercise.exercise_id }
        if (existing != null) {
            selectedExercises.remove(existing)
        } else {
            exerciseToConfigure = exercise
        }
    }

    fun saveRoutine() {
        val trimmedName = routineName.trim()

        if (trimmedName.isBlank()) {
            showAppToast(snackbarHostState, scope, "El nombre de la rutina no puede estar vacío")
            return
        }

        if (selectedExercises.isEmpty()) {
            showAppToast(snackbarHostState, scope, "Debes seleccionar al menos un ejercicio")
            return
        }

        isSaving = true

        routineViewModel.createUserRoutineWithExercises(
            routineName = trimmedName,
            exercises = selectedExercises.toList(),
            imageFile = routineImageFile,
            onSuccess = {
                isSaving = false
                onRoutineCreated()
            },
            onError = { message ->
                isSaving = false
                showAppToast(snackbarHostState, scope, message)
            }
        )
    }

    val filteredExercises = remember(allExercises, searchQuery) {
        if (searchQuery.isBlank()) allExercises
        else allExercises.filter { it.exercise_name.contains(searchQuery, ignoreCase = true) }
    }

    TrainingShellScreen(
        title = strings.createRoutineTitle,
        onBack = onBack,
        showBottomBar = true,
        selectedBottomItem = BottomNavItem.TRAINING,
        onOpenTraining = onOpenTraining,
        onOpenGroups = onOpenGroups,
        onOpenFriends = onOpenFriends,
        onOpenChallenges = onOpenChallenges,
        onOpenProfile = onOpenProfile
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
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
                        color = colors.textPrimary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { imagePicker.launch("image/*") },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            routineImageLabel ?: strings.adminUploadImage
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = routineName,
                        onValueChange = { routineName = it },
                        label = { Text(strings.routineName) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colors.accent,
                            unfocusedBorderColor = Color(0xFFC4C4C4)
                        )
                    )
                }

                item {
                    Text(
                        text = "Añadir ejercicios",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Buscar ejercicios...") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }

                items(
                    items = filteredExercises,
                    key = { it.exercise_id }
                ) { exercise ->
                    val isSelected = selectedExercises.any { it.exercise_id == exercise.exercise_id }
                    ExerciseRow(
                        exercise = exercise,
                        selected = isSelected,
                        onToggle = { toggleSelection(exercise) }
                    )
                }
            }
// ... resto del componente

            Button(
                onClick = { saveRoutine() },
                enabled = !isSaving && routineName.isNotBlank() && selectedExercises.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.accent,
                    contentColor = Color.White,
                    disabledContainerColor = colors.accent.copy(alpha = 0.4f),
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
private fun ExerciseRow(
    exercise: ExerciseDto,
    selected: Boolean,
    onToggle: () -> Unit
) {
    val colors = LocalColors.current
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = colors.surfaceCard,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(
                modifier = Modifier.size(64.dp),
                shape = RoundedCornerShape(12.dp),
                color = colors.surfaceMain
            ) {
                AsyncImage(
                    model = edu.gymtonic_app.core.MediaUtils.resolveBackendMediaUrl(exercise.exercise_image),
                    contentDescription = exercise.exercise_name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = exercise.exercise_name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = colors.textPrimary
                )
            }

            TextButton(onClick = onToggle) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = if (selected) colors.accent else Color(0xFF9EA3AF)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = if (selected) "Añadido" else "Añadir")
            }
        }
    }
}

@Composable
fun AddExerciseDetailsDialog(
    exerciseName: String,
    onDismiss: () -> Unit,
    onConfirm: (reps: String, series: Int) -> Unit
) {
    var reps by remember { mutableStateOf("10") }
    var series by remember { mutableStateOf("3") }
    val colors = LocalColors.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Detalles del ejercicio", fontWeight = FontWeight.Bold, color = colors.textPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(exerciseName, fontSize = 14.sp, color = colors.textSecondary)
                
                OutlinedTextField(
                    value = series,
                    onValueChange = { if (it.all { c -> c.isDigit() }) series = it },
                    label = { Text("Series") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = reps,
                    onValueChange = { reps = it },
                    label = { Text("Repeticiones") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val s = series.toIntOrNull() ?: 0
                    if (s > 0 && reps.isNotBlank()) {
                        onConfirm(reps, s)
                    }
                },
                enabled = (series.toIntOrNull() ?: 0) > 0 && reps.isNotBlank(),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Añadir")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
