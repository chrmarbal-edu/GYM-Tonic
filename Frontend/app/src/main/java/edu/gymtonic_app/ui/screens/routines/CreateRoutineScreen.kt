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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material3.*
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
import edu.gymtonic_app.ui.viewmodel.exercise.FavoriteExercisePayload
import edu.gymtonic_app.ui.screens.admin.exerciseTypeOptions
import edu.gymtonic_app.ui.screens.admin.AdminTypeFilter
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
    var selectedType by remember { mutableStateOf<Int?>(null) }
    
    var exerciseToConfigure by remember { mutableStateOf<edu.gymtonic_app.data.remote.remoteModel.exercise.ExerciseDto?>(null) }
    var favoritesExpanded by rememberSaveable { mutableStateOf(true) }

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri ?: return@rememberLauncherForActivityResult
        routineImageFile = uriToUploadFile(context, uri, "user_routine_image_")
        routineImageLabel = routineImageFile?.name
    }
    val snackbarHostState = LocalAppSnackbarHostState.current
    val scope = rememberCoroutineScope()

    if (exerciseToConfigure != null) {
        AddExerciseDetailsDialog(
            exerciseName = exerciseToConfigure?.exercise_name,
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

    val favoriteIds = remember(favoriteExercises) {
        favoriteExercises.map { it.exercise_id }.toSet()
    }

    val filteredExercises = remember(allExercises, searchQuery, selectedType) {
        allExercises.filter { exercise ->
            val matchesSearch = if (searchQuery.isBlank()) true
            else exercise.exercise_name?.contains(searchQuery, ignoreCase = true) == true
            
            val matchesType = if (selectedType == null) true
            else exercise.exercise_type == selectedType
            
            matchesSearch && matchesType
        }
    }

    val filteredFavorites = remember(filteredExercises, favoriteIds) {
        filteredExercises.filter { it.exercise_id in favoriteIds }
    }

    val filteredOthers = remember(filteredExercises, favoriteIds) {
        filteredExercises.filter { it.exercise_id !in favoriteIds }
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
                    Spacer(modifier = Modifier.height(8.dp))
                    AdminTypeFilter(
                        selectedType = selectedType,
                        onTypeSelected = { selectedType = it },
                        options = exerciseTypeOptions
                    )
                }

                if (filteredFavorites.isNotEmpty()) {
                    item(key = "header_favorites") {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { favoritesExpanded = !favoritesExpanded }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Favoritos (${filteredFavorites.size})",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = colors.accent
                            )
                            Icon(
                                imageVector = if (favoritesExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = colors.accent
                            )
                        }
                    }
                    if (favoritesExpanded) {
                        items(
                            items = filteredFavorites,
                            key = { "fav_${it.exercise_id}" }
                        ) { exercise ->
                            val isSelected = selectedExercises.any { it.exercise_id == exercise.exercise_id }
                            edu.gymtonic_app.ui.components.SelectableExerciseRow(
                                name = exercise.exercise_name,
                                image = exercise.exercise_image,
                                selected = isSelected,
                                isFavorite = true,
                                onToggle = { toggleSelection(exercise) },
                                onToggleFavorite = { 
                                    exerciseViewModel.onToggleFavorite(
                                        FavoriteExercisePayload(
                                            id = exercise.exercise_id,
                                            name = exercise.exercise_name,
                                            description = exercise.exercise_description,
                                            type = exercise.exercise_type,
                                            image = exercise.exercise_image
                                        )
                                    ) 
                                }
                            )
                        }
                    }
                }

                if (filteredOthers.isNotEmpty()) {
                    item(key = "header_all") {
                        Text(
                            text = if (filteredFavorites.isNotEmpty()) "Todos los ejercicios" else "Ejercicios",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                    }
                    items(
                        items = filteredOthers,
                        key = { it.exercise_id }
                    ) { exercise ->
                        val isSelected = selectedExercises.any { it.exercise_id == exercise.exercise_id }
                        edu.gymtonic_app.ui.components.SelectableExerciseRow(
                            name = exercise.exercise_name,
                            image = exercise.exercise_image,
                            selected = isSelected,
                            isFavorite = false,
                            onToggle = { toggleSelection(exercise) },
                            onToggleFavorite = { 
                                exerciseViewModel.onToggleFavorite(
                                    FavoriteExercisePayload(
                                        id = exercise.exercise_id,
                                        name = exercise.exercise_name,
                                        description = exercise.exercise_description,
                                        type = exercise.exercise_type,
                                        image = exercise.exercise_image
                                    )
                                )
                            }
                        )
                    }
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
fun AddExerciseDetailsDialog(
    exerciseName: String?,
    onDismiss: () -> Unit,
    onConfirm: (reps: String, series: Int) -> Unit
) {
    val strings = LocalStrings.current
    var reps by remember { mutableStateOf("10") }
    var series by remember { mutableStateOf("3") }
    val colors = LocalColors.current

    val seriesInt = series.toIntOrNull() ?: 0
    val repsInt = reps.toIntOrNull() ?: 0
    
    val isSeriesValid = seriesInt in 1..10
    val isRepsValid = repsInt in 1..100

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Detalles del ejercicio", fontWeight = FontWeight.Bold, color = colors.textPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(exerciseName ?: "Sin nombre", fontSize = 14.sp, color = colors.textSecondary)
                
                Column {
                    OutlinedTextField(
                        value = series,
                        onValueChange = { if (it.all { c -> c.isDigit() }) series = it },
                        label = { Text("Series (1-10)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        isError = series.isNotEmpty() && !isSeriesValid,
                        shape = RoundedCornerShape(12.dp)
                    )
                    if (series.isNotEmpty() && !isSeriesValid) {
                        Text(
                            text = strings.invalidSeriesRange,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                        )
                    }
                }

                Column {
                    OutlinedTextField(
                        value = reps,
                        onValueChange = { if (it.all { c -> c.isDigit() }) reps = it },
                        label = { Text("Repeticiones (1-100)") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        isError = reps.isNotEmpty() && !isRepsValid,
                        shape = RoundedCornerShape(12.dp)
                    )
                    if (reps.isNotEmpty() && !isRepsValid) {
                        Text(
                            text = strings.invalidRepsRange,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (isSeriesValid && isRepsValid) {
                        onConfirm(reps, seriesInt)
                    }
                },
                enabled = isSeriesValid && isRepsValid,
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
