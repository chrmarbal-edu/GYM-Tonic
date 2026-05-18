package edu.gymtonic_app.ui.screens.routines

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import edu.gymtonic_app.data.remote.remoteModel.routine.RoutineExerciseDto
import edu.gymtonic_app.ui.components.BottomNavItem
import edu.gymtonic_app.ui.i18n.LocalStrings
import edu.gymtonic_app.ui.screens.admin.AdminExerciseSelectionScreen
import edu.gymtonic_app.ui.screens.admin.AdminField
import edu.gymtonic_app.ui.screens.admin.AdminSaveButton
import edu.gymtonic_app.ui.screens.admin.AdminSelectedExerciseItem
import edu.gymtonic_app.ui.screens.admin.resolveRoutineImageUrl
import edu.gymtonic_app.ui.screens.admin.uriToUploadFile
import edu.gymtonic_app.ui.screens.exercise.TrainingShellScreen
import edu.gymtonic_app.ui.theme.LocalColors
import edu.gymtonic_app.ui.viewmodel.exercise.ExerciseViewModel
import edu.gymtonic_app.ui.viewmodel.exercise.ExerciseViewModelFactory
import edu.gymtonic_app.ui.viewmodel.routine.RoutineEditViewModel
import java.io.File

@Composable
fun RoutineEditScreen(
    routineId: Int,
    isLocal: Boolean,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    onOpenTraining: () -> Unit = {},
    onOpenGroups: () -> Unit = {},
    onOpenFriends: () -> Unit = {},
    onOpenChallenges: () -> Unit = {},
    onOpenProfile: () -> Unit = {},
    viewModel: RoutineEditViewModel = viewModel()
) {
    val strings = LocalStrings.current
    val colors = LocalColors.current
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()

    var name by remember { mutableStateOf("") }
    val selectedRemoteExercises = remember { mutableStateListOf<RoutineExerciseDto>() }
    var showRemoteSelection by remember { mutableStateOf(false) }
    var imageFile by remember { mutableStateOf<File?>(null) }
    var imageSelectedLabel by remember { mutableStateOf<String?>(null) }

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri ?: return@rememberLauncherForActivityResult
        imageFile = uriToUploadFile(context, uri, "routine_image_")
        imageSelectedLabel = imageFile?.name ?: strings.adminImageSelected
    }

    LaunchedEffect(routineId) {
        viewModel.load(routineId, isLocal)
    }

    LaunchedEffect(state.routine) {
        state.routine?.let { routine ->
            name = routine.routine_name.orEmpty()
            selectedRemoteExercises.clear()
            selectedRemoteExercises.addAll(routine.safeExercises())
        }
    }

    if (showRemoteSelection) {
        AdminExerciseSelectionScreen(
            alreadySelectedIds = selectedRemoteExercises.map { it.exercise_id }.toSet(),
            onBack = { showRemoteSelection = false },
            onSelected = { exercise ->
                if (selectedRemoteExercises.none { it.exercise_id == exercise.exercise_id }) {
                    selectedRemoteExercises.add(
                        RoutineExerciseDto(
                            exercise_id = exercise.exercise_id,
                            exercise_name = exercise.exercise_name,
                            exercise_type = exercise.exercise_type,
                            exercise_image = exercise.exercise_image,
                            exercise_video = exercise.exercise_video
                        )
                    )
                }
                showRemoteSelection = false
            }
        )
        return
    }

    TrainingShellScreen(
        title = strings.adminEdit,
        onBack = onBack,
        showBottomBar = true,
        selectedBottomItem = BottomNavItem.TRAINING,
        onOpenTraining = onOpenTraining,
        onOpenGroups = onOpenGroups,
        onOpenFriends = onOpenFriends,
        onOpenProfile = onOpenProfile
    ) {
        when {
            state.isLoading -> {
                Column(
                    Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                }
            }

            state.error != null && state.routine == null -> {
                Text(
                    text = state.error.orEmpty(),
                    modifier = Modifier.padding(16.dp),
                    color = colors.textSecondary
                )
            }

            else -> {
                Column(Modifier.fillMaxSize()) {
                    Column(
                        Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp)
                    ) {
                        AdminField(strings.routineName, name, onValueChange = { name = it })

                        val previewUrl = imageFile?.absolutePath?.let { "file://$it" }
                            ?: resolveRoutineImageUrl(state.routine?.routine_image)
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
                            Text(it, fontSize = 12.sp, color = colors.textSecondary)
                        }

                        Row(
                            Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(strings.exercisesSection, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            TextButton(onClick = { showRemoteSelection = true }) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Text(strings.addExercise)
                            }
                        }

                        if (selectedRemoteExercises.isEmpty()) {
                            Text(
                                strings.noExercisesAdded,
                                color = colors.textSecondary,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                        selectedRemoteExercises.forEachIndexed { index, exercise ->
                            AdminSelectedExerciseItem(
                                exercise = exercise,
                                onRemove = { selectedRemoteExercises.removeAt(index) }
                            )
                        }
                    }

                    AdminSaveButton(
                        text = strings.saveChanges,
                        enabled = name.isNotBlank() && selectedRemoteExercises.isNotEmpty(),
                        loading = state.isSaving,
                        onClick = {
                            viewModel.save(
                                routineId = routineId,
                                isLocal = isLocal,
                                name = name.trim(),
                                exerciseIds = selectedRemoteExercises.map { it.exercise_id },
                                imageFile = imageFile,
                                onSuccess = onSaved
                            )
                        }
                    )
                    state.error?.let {
                        Text(it, color = Color(0xFFB3261E), modifier = Modifier.padding(horizontal = 16.dp))
                    }
                }
            }
        }
    }
}
