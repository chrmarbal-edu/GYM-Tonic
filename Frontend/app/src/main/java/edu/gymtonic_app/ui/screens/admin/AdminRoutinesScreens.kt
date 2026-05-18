package edu.gymtonic_app.ui.screens.admin

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import java.io.File
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import edu.gymtonic_app.ui.i18n.LocalStrings
import edu.gymtonic_app.ui.viewmodel.admin.AdminRoutinesViewModel

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import edu.gymtonic_app.data.remote.remoteModel.exercise.ExerciseDto
import edu.gymtonic_app.data.remote.remoteModel.routine.RoutineExerciseDto
import edu.gymtonic_app.ui.theme.LocalColors
import edu.gymtonic_app.ui.viewmodel.admin.AdminExercisesViewModel

@Composable
fun AdminRoutinesListScreen(
    onBack: () -> Unit,
    onOpenDetail: (Int) -> Unit,
    onCreate: () -> Unit,
    viewModel: AdminRoutinesViewModel = viewModel()
) {
    val strings = LocalStrings.current
    val state by viewModel.listState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { viewModel.loadList() }

    AdminShellScreen(
        title = strings.adminRoutines,
        onBack = onBack,
        onCreateClick = onCreate
    ) {
        val filteredItems = remember(state.items, searchQuery) {
            state.items.filter { (it.routine_name ?: "").contains(searchQuery, ignoreCase = true) }
        }

        AdminListContent(
            isLoading = state.isLoading,
            error = state.error,
            emptyMessage = strings.adminEmptyList,
            itemsCount = filteredItems.size,
            onRetry = { viewModel.loadList() }
        ) {
            Column(Modifier.fillMaxSize()) {
                AdminSearchBar(query = searchQuery, onQueryChange = { searchQuery = it })
                AdminSimpleList(
                    items = filteredItems,
                    titleFor = { it.routine_name ?: "Rutina #${it.routine_id}" },
                    subtitleFor = { "ID ${it.routine_id}" },
                    onItemClick = { onOpenDetail(it.routine_id) }
                )
            }
        }
    }
}

@Composable
fun AdminRoutineDetailScreen(
    routineId: Int,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onOpenExercise: (Int) -> Unit,
    viewModel: AdminRoutinesViewModel = viewModel()
) {
    val strings = LocalStrings.current
    val colors = LocalColors.current
    val state by viewModel.detailState.collectAsState()
    var showDelete by remember { mutableStateOf(false) }

    LaunchedEffect(routineId) { viewModel.loadDetail(routineId) }

    AdminShellScreen(
        title = strings.adminRoutineDetail,
        onBack = onBack,
        onDeleteClick = { showDelete = true }
    ) {
        AdminListContent(
            isLoading = state.isLoading,
            error = state.error,
            emptyMessage = strings.adminEmptyList,
            itemsCount = if (state.item != null) 1 else 0,
            onRetry = { viewModel.loadDetail(routineId) }
        ) {
            val routine = state.item ?: return@AdminListContent
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                val routineImageUrl = resolveRoutineImageUrl(routine.routine_image)
                if (!routineImageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = routineImageUrl,
                        contentDescription = routine.routine_name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .padding(bottom = 12.dp)
                    )
                }
                AdminDetailRow(strings.routineName, routine.routine_name.orEmpty())
                AdminDetailRow("ID", routine.routine_id.toString())
                Text(
                    strings.exercisesSection,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                if (routine.safeExercises().isEmpty()) {
                    Text(
                        strings.noExercisesAdded,
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = colors.textSecondary
                    )
                }
                routine.safeExercises().forEach { ex ->
                    AdminListItem(
                        title = ex.exercise_name.orEmpty(),
                        subtitle = listOfNotNull(
                            ex.reps,
                            exerciseTypeLabel(ex.exercise_type)
                        ).joinToString(" · ")
                    ) {
                        onOpenExercise(ex.exercise_id)
                    }
                }
                AdminSaveButton(
                    text = strings.adminEdit,
                    enabled = true,
                    loading = false,
                    onClick = onEdit
                )
            }
        }
    }

    AdminDeleteDialog(
        visible = showDelete,
        title = strings.deleteRoutineTitle,
        message = strings.deleteRoutineMessage,
        onDismiss = { showDelete = false },
        onConfirm = {
            showDelete = false
            viewModel.deleteRoutine(routineId, onBack)
        }
    )
}

@Composable
fun AdminRoutineEditScreen(
    routineId: Int?,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: AdminRoutinesViewModel = viewModel()
) {
    val strings = LocalStrings.current
    val colors = LocalColors.current
    val context = LocalContext.current
    val state by viewModel.detailState.collectAsState()
    var name by remember { mutableStateOf("") }
    val selectedExercises = remember { mutableStateListOf<RoutineExerciseDto>() }
    var showSelection by remember { mutableStateOf(false) }
    var imageFile by remember { mutableStateOf<File?>(null) }
    var imageSelectedLabel by remember { mutableStateOf<String?>(null) }

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri ?: return@rememberLauncherForActivityResult
        imageFile = uriToUploadFile(context, uri, "routine_image_")
        imageSelectedLabel = imageFile?.name ?: strings.adminImageSelected
    }

    LaunchedEffect(routineId) {
        if (routineId != null) viewModel.loadDetail(routineId)
        else viewModel.clearDetail()
    }

    LaunchedEffect(state.item) {
        state.item?.let { routine ->
            if (name.isEmpty()) name = routine.routine_name.orEmpty()
            selectedExercises.clear()
            selectedExercises.addAll(routine.safeExercises())
        }
    }

    if (showSelection) {
        AdminExerciseSelectionScreen(
            alreadySelectedIds = selectedExercises.map { it.exercise_id }.toSet(),
            onBack = { showSelection = false },
            onSelected = { ex ->
                if (selectedExercises.none { it.exercise_id == ex.exercise_id }) {
                    selectedExercises.add(
                        RoutineExerciseDto(
                            exercise_id = ex.exercise_id,
                            exercise_name = ex.exercise_name,
                            exercise_type = ex.exercise_type,
                            exercise_image = ex.exercise_image,
                            exercise_video = ex.exercise_video
                        )
                    )
                }
                showSelection = false
            }
        )
        return
    }

    val isNew = routineId == null
    val title = if (isNew) strings.adminCreate else strings.adminEdit

    AdminShellScreen(title = title, onBack = onBack) {
        Column(Modifier.fillMaxSize()) {
            Column(Modifier.weight(1f).verticalScroll(rememberScrollState())) {
                AdminField(strings.routineName, name, onValueChange = { name = it })

                val previewUrl = imageFile?.absolutePath?.let { "file://$it" }
                    ?: resolveRoutineImageUrl(state.item?.routine_image)
                if (!previewUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = previewUrl,
                        contentDescription = name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .padding(vertical = 8.dp)
                    )
                }
                Button(
                    onClick = { imagePicker.launch("image/*") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Text(strings.adminUploadImage)
                }
                imageSelectedLabel?.let {
                    Text(it, modifier = Modifier.padding(bottom = 4.dp), color = colors.textSecondary)
                }

                Row(
                    Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(strings.exercisesSection, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    TextButton(onClick = { showSelection = true }) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Text(strings.addExercise)
                    }
                }

                if (selectedExercises.isEmpty()) {
                    Text(
                        strings.noExercisesAdded,
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = colors.textSecondary,
                        fontSize = 14.sp
                    )
                }

                selectedExercises.forEachIndexed { index, ex ->
                    AdminSelectedExerciseItem(
                        exercise = ex,
                        onRemove = { selectedExercises.removeAt(index) }
                    )
                }
            }

            AdminSaveButton(
                text = strings.saveChanges,
                enabled = name.isNotBlank(),
                loading = state.isSaving,
                onClick = {
                    val ids = selectedExercises.map { it.exercise_id }
                    viewModel.saveRoutine(
                        id = routineId,
                        name = name.trim(),
                        exerciseIds = ids,
                        imageFile = imageFile,
                        onSuccess = onSaved
                    )
                }
            )
            state.error?.let { Text(it, modifier = Modifier.padding(top = 8.dp)) }
        }
    }
}

@Composable
fun AdminSelectedExerciseItem(
    exercise: RoutineExerciseDto,
    onRemove: () -> Unit
) {
    val colors = LocalColors.current
    Surface(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        color = colors.surfaceCard.copy(alpha = 0.5f)
    ) {
        Row(
            Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(exercise.exercise_name.orEmpty(), fontWeight = FontWeight.SemiBold)
                Text(exerciseTypeLabel(exercise.exercise_type), fontSize = 12.sp, color = colors.textSecondary)
            }
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFE53935))
            }
        }
    }
}

@Composable
fun AdminExerciseSelectionScreen(
    alreadySelectedIds: Set<Int> = emptySet(),
    onBack: () -> Unit,
    onSelected: (ExerciseDto) -> Unit,
    viewModel: AdminExercisesViewModel = viewModel()
) {
    val strings = LocalStrings.current
    val state by viewModel.listState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) { viewModel.loadList() }

    AdminShellScreen(
        title = strings.groupsSelectExercises,
        onBack = onBack
    ) {
        val filteredItems = remember(state.items, searchQuery, selectedType) {
            state.items.filter { 
                it.exercise_name.contains(searchQuery, ignoreCase = true) &&
                (selectedType == null || it.exercise_type == selectedType)
            }
        }

        Column(Modifier.fillMaxSize()) {
            AdminSearchBar(query = searchQuery, onQueryChange = { searchQuery = it })
            AdminTypeFilter(
                selectedType = selectedType,
                onTypeSelected = { selectedType = it },
                options = exerciseTypeOptions
            )
            
            AdminListContent(
                isLoading = state.isLoading,
                error = state.error,
                emptyMessage = strings.adminEmptyList,
                itemsCount = filteredItems.size,
                onRetry = { viewModel.loadList() }
            ) {
                LazyColumn(Modifier.fillMaxSize()) {
                    items(filteredItems) { exercise ->
                        val isAdded = alreadySelectedIds.contains(exercise.exercise_id)
                        AdminExerciseListItem(
                            title = exercise.exercise_name,
                            subtitle = if (isAdded) {
                                "${exerciseTypeLabel(exercise.exercise_type)} · ${strings.groupsAdded}"
                            } else {
                                exerciseTypeLabel(exercise.exercise_type)
                            },
                            imageUrl = resolveBackendMediaUrl(exercise.exercise_image),
                            onClick = { onSelected(exercise) }
                        )
                    }
                }
            }
        }
    }
}
