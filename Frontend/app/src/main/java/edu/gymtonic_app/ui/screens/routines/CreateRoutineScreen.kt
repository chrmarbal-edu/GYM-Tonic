package edu.gymtonic_app.ui.screens.routines

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.gymtonic_app.ui.components.BottomNavItem
import edu.gymtonic_app.ui.i18n.LocalStrings
import edu.gymtonic_app.ui.screens.exercise.TrainingShellScreen
import edu.gymtonic_app.ui.theme.LocalColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateRoutineScreen(
    onBack: () -> Unit,
    onSave: () -> Unit,
    onOpenHome: () -> Unit = {},
    onOpenTraining: () -> Unit = {},
    onOpenChallenges: () -> Unit = {},
    onOpenProfile: () -> Unit = {}
) {
    val strings = LocalStrings.current
    val colors = LocalColors.current
    var routineName by remember { mutableStateOf("") }
    var routineDescription by remember { mutableStateOf("") }
    val exercises = remember { mutableStateListOf<String>() }

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
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                item {
                    Text(
                        text = strings.basicInfo,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.textPrimary
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

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = routineDescription,
                        onValueChange = { routineDescription = it },
                        label = { Text(strings.routineDescription) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        minLines = 2,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colors.accent,
                            unfocusedBorderColor = Color(0xFFC4C4C4)
                        )
                    )
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = strings.exercisesSection,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )

                        IconButton(
                            onClick = { exercises.add(strings.newExercise(exercises.size + 1)) },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = colors.surfaceAccent,
                                contentColor = colors.textOnAccent
                            )
                        ) {
                            Icon(Icons.Default.Add, contentDescription = strings.addExercise)
                        }
                    }
                }

                if (exercises.isEmpty()) {
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            color = colors.surfaceCard.copy(alpha = 0.5f)
                        ) {
                            Text(
                                text = strings.noExercisesAdded,
                                modifier = Modifier.padding(24.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                color = colors.textSecondary,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                items(exercises) { exercise ->
                    ExerciseItemRow(
                        name = exercise,
                        customizeLabel = strings.customizeSetsReps,
                        deleteLabel = strings.deleteExercise,
                        onDelete = { exercises.remove(exercise) }
                    )
                }
            }

            Button(
                onClick = onSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colors.accent,
                    contentColor = Color.White
                )
            ) {
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

@Composable
fun ExerciseItemRow(
    name: String,
    customizeLabel: String,
    deleteLabel: String,
    onDelete: () -> Unit
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
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = colors.textPrimary
                )
                Text(
                    text = customizeLabel,
                    fontSize = 11.sp,
                    color = colors.textSecondary
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = deleteLabel,
                    tint = Color(0xFFE57373)
                )
            }
        }
    }
}
