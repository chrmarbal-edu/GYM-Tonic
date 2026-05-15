package edu.gymtonic_app.ui.screens.groups

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import edu.gymtonic_app.ui.theme.LocalColors
import edu.gymtonic_app.ui.viewmodel.GroupViewModel
import edu.gymtonic_app.ui.viewmodel.exercise.ExerciseViewModel
import edu.gymtonic_app.ui.viewmodel.exercise.ExerciseViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGroupRoutineScreen(
    groupId: Int,
    onBack: () -> Unit,
    onRoutineAdded: () -> Unit,
    onOpenHome: () -> Unit,
    onOpenTraining: () -> Unit,
    onOpenChallenges: () -> Unit,
    onOpenProfile: () -> Unit,
    groupViewModel: GroupViewModel = viewModel()
) {
    val strings = LocalStrings.current
    val colors = LocalColors.current
    val context = LocalContext.current
    val application = context.applicationContext as Application

    val exerciseViewModel: ExerciseViewModel =
        viewModel(factory = ExerciseViewModelFactory(application))

    val favoriteExercises by exerciseViewModel.favoriteExercises.collectAsState()
    val actionMessage by groupViewModel.actionMessage.collectAsState()

    var routineName by rememberSaveable { mutableStateOf("") }
    var isSaving by rememberSaveable { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val selectedExerciseIds = remember { mutableStateListOf<Int>() }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(favoriteExercises) {
        selectedExerciseIds.retainAll(favoriteExercises.map { it.exercise_id }.toSet())
    }

    LaunchedEffect(actionMessage) {
        actionMessage?.let {
            snackbarHostState.showSnackbar(it)
            groupViewModel.clearActionMessage()
        }
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
        if (trimmedName.isBlank()) {
            errorMessage = strings.groupsRoutineNameRequired
            return
        }
        if (selectedExerciseIds.isEmpty()) {
            errorMessage = strings.groupsSelectExercisesRequired
            return
        }

        isSaving = true
        errorMessage = null
        groupViewModel.addGroupRoutine(
            groupId = groupId,
            name = trimmedName,
            exerciseIds = selectedExerciseIds.toList(),
            onSuccess = {
                isSaving = false
                onRoutineAdded()
            },
            onFailure = {
                isSaving = false
                errorMessage = it
            }
        )
    }

    TrainingShellScreen(
        title = strings.groupsAddRoutineTitle,
        onBack = onBack,
        showBottomBar = true,
        selectedBottomItem = BottomNavItem.PROFILE,
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
                        text = strings.groupsAddRoutineHint,
                        fontSize = 13.sp,
                        color = colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
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
                        text = strings.groupsSelectExercises,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
                    )
                }

                if (favoriteExercises.isEmpty()) {
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            color = colors.surfaceCard.copy(alpha = 0.5f)
                        ) {
                            Text(
                                text = strings.groupsNoFavoriteExercises,
                                modifier = Modifier.padding(20.dp),
                                color = colors.textSecondary
                            )
                        }
                    }
                } else {
                    items(favoriteExercises, key = { it.exercise_id }) { exercise ->
                        GroupExerciseRow(
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
                    containerColor = colors.accent,
                    contentColor = Color.White
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
                        text = strings.groupsShareRoutine,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun GroupExerciseRow(
    exercise: ExerciseEntity,
    selected: Boolean,
    onToggle: () -> Unit
) {
    val strings = LocalStrings.current
    val colors = LocalColors.current
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = colors.surfaceCard
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
                    color = colors.textPrimary
                )
                Text(
                    text = exercise.exercise_description,
                    fontSize = 11.sp,
                    color = colors.textSecondary
                )
            }
            TextButton(onClick = onToggle) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = if (selected) colors.accent else Color(0xFF9EA3AF)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = if (selected) strings.groupsAdded else strings.groupsAdd)
            }
        }
    }
}
