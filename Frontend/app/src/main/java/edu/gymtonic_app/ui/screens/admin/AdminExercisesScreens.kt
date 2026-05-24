package edu.gymtonic_app.ui.screens.admin

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import androidx.compose.foundation.shape.RoundedCornerShape
import edu.gymtonic_app.ui.i18n.LocalStrings
import edu.gymtonic_app.ui.screens.exercise.VideoPlayer
import edu.gymtonic_app.ui.theme.LocalColors
import edu.gymtonic_app.ui.viewmodel.admin.AdminExercisesViewModel
import java.io.File

@Composable
fun AdminExercisesListScreen(
    onBack: () -> Unit,
    onOpenDetail: (Int) -> Unit,
    onCreate: () -> Unit,
    viewModel: AdminExercisesViewModel = viewModel()
) {
    val strings = LocalStrings.current
    val state by viewModel.listState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) { viewModel.loadList() }

    AdminShellScreen(
        title = strings.adminExercises,
        onBack = onBack,
        onCreateClick = onCreate
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
                        AdminExerciseListItem(
                            title = exercise.exercise_name,
                            subtitle = "${exerciseTypeLabel(exercise.exercise_type)} · ID ${exercise.exercise_id}",
                            imageUrl = resolveBackendMediaUrl(exercise.exercise_image),
                            onClick = { onOpenDetail(exercise.exercise_id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdminExerciseDetailScreen(
    exerciseId: Int,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    viewModel: AdminExercisesViewModel = viewModel()
) {
    val strings = LocalStrings.current
    val state by viewModel.detailState.collectAsState()
    var showDelete by remember { mutableStateOf(false) }

    LaunchedEffect(exerciseId) { viewModel.loadDetail(exerciseId) }

    AdminShellScreen(
        title = strings.adminExerciseDetail,
        onBack = onBack,
        onDeleteClick = { showDelete = true }
    ) {
        AdminListContent(
            isLoading = state.isLoading,
            error = state.error,
            emptyMessage = strings.adminEmptyList,
            itemsCount = if (state.item != null) 1 else 0,
            onRetry = { viewModel.loadDetail(exerciseId) }
        ) {
            val exercise = state.item ?: return@AdminListContent
            val colors = LocalColors.current

            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // VIDEO SECTION - Matching user visualization
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = colors.surfaceCard,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .padding(bottom = 16.dp)
                ) {
                    val videoUrl = resolveBackendMediaUrl(exercise.exercise_video)
                    if (!videoUrl.isNullOrBlank()) {
                        VideoPlayer(
                            videoUrl = videoUrl,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(14.dp))
                        )
                    } else {
                        val imageUrl = resolveBackendMediaUrl(exercise.exercise_image)
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = exercise.exercise_name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                AdminDetailRow(strings.adminExerciseName, exercise.exercise_name)
                AdminDetailRow(strings.routineDescription, exercise.exercise_description)
                AdminDetailRow(strings.adminExerciseType, exerciseTypeLabel(exercise.exercise_type))
                AdminDetailRow("ID", exercise.exercise_id.toString())

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
        title = strings.adminDeleteTitle,
        message = strings.adminDeleteExerciseMessage,
        onDismiss = { showDelete = false },
        onConfirm = {
            showDelete = false
            viewModel.deleteExercise(exerciseId, onBack)
        }
    )
}

@Composable
fun AdminExerciseEditScreen(
    exerciseId: Int?,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: AdminExercisesViewModel = viewModel()
) {
    val strings = LocalStrings.current
    val context = LocalContext.current
    val state by viewModel.detailState.collectAsState()
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var type by remember { mutableIntStateOf(0) }
    var videoFile by remember { mutableStateOf<File?>(null) }
    var imageFile by remember { mutableStateOf<File?>(null) }
    var videoSelectedLabel by remember { mutableStateOf<String?>(null) }
    var imageSelectedLabel by remember { mutableStateOf<String?>(null) }
    var showDelete by remember { mutableStateOf(false) }

    val videoPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri ?: return@rememberLauncherForActivityResult
        videoFile = uriToUploadFile(context, uri, "exercise_video_")
        videoSelectedLabel = videoFile?.name ?: strings.adminVideoSelected
    }

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri ?: return@rememberLauncherForActivityResult
        imageFile = uriToUploadFile(context, uri, "exercise_image_")
        imageSelectedLabel = imageFile?.name ?: strings.adminImageSelected
    }

    LaunchedEffect(exerciseId) {
        if (exerciseId != null) viewModel.loadDetail(exerciseId)
        else viewModel.clearDetail()
    }

    LaunchedEffect(state.item) {
        state.item?.let { ex ->
            name = ex.exercise_name
            description = ex.exercise_description
            type = ex.exercise_type
        }
    }

    val isNew = exerciseId == null

    AdminShellScreen(
        title = if (isNew) strings.adminCreate else strings.adminEdit,
        onBack = onBack,
        onDeleteClick = if (!isNew) ({ showDelete = true }) else null
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            AdminField(strings.adminExerciseName, name, onValueChange = { name = it })
            AdminField(strings.routineDescription, description, onValueChange = { description = it }, singleLine = false)
            AdminIntDropdown(
                label = strings.adminExerciseType,
                options = exerciseTypeOptions,
                selectedValue = type,
                onSelected = { type = it }
            )
            Button(
                onClick = { videoPicker.launch("video/*") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                Text(strings.adminUploadVideo)
            }
            videoSelectedLabel?.let {
                Text(it, modifier = Modifier.padding(bottom = 4.dp))
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
                Text(it, modifier = Modifier.padding(bottom = 4.dp))
            }
            AdminSaveButton(
                text = strings.saveChanges,
                enabled = name.isNotBlank() && description.isNotBlank(),
                loading = state.isSaving,
                onClick = {
                    viewModel.saveExercise(
                        id = exerciseId,
                        name = name.trim(),
                        description = description.trim(),
                        type = type,
                        videoFile = videoFile,
                        imageFile = imageFile,
                        onSuccess = onSaved
                    )
                }
            )
            state.error?.let { Text(it, modifier = Modifier.padding(top = 8.dp)) }
        }
    }

    if (exerciseId != null) {
        AdminDeleteDialog(
            visible = showDelete,
            title = strings.adminDeleteTitle,
            message = strings.adminDeleteExerciseMessage,
            onDismiss = { showDelete = false },
            onConfirm = {
                showDelete = false
                viewModel.deleteExercise(exerciseId, onBack)
            }
        )
    }
}
