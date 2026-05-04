package edu.gymtonic_app.ui.screens.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.gymtonic_app.ui.components.BottomNavItem
import edu.gymtonic_app.ui.screens.exercise.TrainingShellScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onOpenHome: () -> Unit,
    onOpenTraining: () -> Unit,
    onOpenChallenges: () -> Unit,
    onOpenProfile: () -> Unit,
) {
    TrainingShellScreen(
        title = "Ajustes",
        onBack = onBack,
        showBottomBar = true,
        selectedBottomItem = BottomNavItem.PROFILE,
        onOpenHome = onOpenHome,
        onOpenTraining = onOpenTraining,
        onOpenChallenges = onOpenChallenges,
        onOpenProfile = onOpenProfile
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item {
                AccountSectionCard(title = "Notificaciones") {
                    var routineCompletionNotifs by remember { mutableStateOf(true) }
                    var workoutReminderNotifs by remember { mutableStateOf(false) }
                    var newChallengeNotifs by remember { mutableStateOf(true) }

                    SettingsToggleRow(
                        label = "Rutinas completadas",
                        checked = routineCompletionNotifs,
                        onCheckedChange = { routineCompletionNotifs = it }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    SettingsToggleRow(
                        label = "Recordatorios de entrenamiento",
                        checked = workoutReminderNotifs,
                        onCheckedChange = { workoutReminderNotifs = it }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    SettingsToggleRow(
                        label = "Nuevos retos disponibles",
                        checked = newChallengeNotifs,
                        onCheckedChange = { newChallengeNotifs = it }
                    )
                }
            }

            item {
                AccountSectionCard(title = "Apariencia") {
                    var selectedTheme by remember { mutableStateOf("Sistema") }
                    val themeOptions = listOf("Sistema", "Claro", "Oscuro")

                    SettingsOptionRow(
                        label = "Tema",
                        currentValue = selectedTheme,
                        options = themeOptions,
                        onOptionSelected = { selectedTheme = it }
                    )
                }
            }

            item {
                AccountSectionCard(title = "Unidades de Medida") {
                    var weightUnit by remember { mutableStateOf("kg") }
                    val weightOptions = listOf("kg", "lbs")

                    SettingsOptionRow(
                        label = "Peso",
                        currentValue = weightUnit,
                        options = weightOptions,
                        onOptionSelected = { weightUnit = it }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    var distanceUnit by remember { mutableStateOf("km") }
                    val distanceOptions = listOf("km", "millas")
                    SettingsOptionRow(
                        label = "Distancia",
                        currentValue = distanceUnit,
                        options = distanceOptions,
                        onOptionSelected = { distanceUnit = it }
                    )
                }
            }

            item {
                AccountSectionCard(title = "Sincronización de Datos") {
                    var googleFitConnected by remember { mutableStateOf(false) }
                    SettingsToggleRow(
                        label = "Conectar con Google Fit",
                        checked = googleFitConnected,
                        onCheckedChange = { googleFitConnected = it }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    var appleHealthConnected by remember { mutableStateOf(false) }
                    SettingsToggleRow(
                        label = "Conectar con Apple Health",
                        checked = appleHealthConnected,
                        onCheckedChange = { appleHealthConnected = it }
                    )
                }
            }

            item {
                AccountSectionCard(title = "Idioma") {
                    var selectedLanguage by remember { mutableStateOf("Español") }
                    val languageOptions = listOf("Español", "Inglés", "Francés")

                    SettingsOptionRow(
                        label = "Idioma de la app",
                        currentValue = selectedLanguage,
                        options = languageOptions,
                        onOptionSelected = { selectedLanguage = it }
                    )
                }
            }

            item {
                AccountSectionCard(title = "Acerca de") {
                    Text(
                        text = "Versión: 1.0.0",
                        fontSize = 14.sp,
                        color = Color(0xFF1D1D1D),
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Términos de Servicio",
                        fontSize = 14.sp,
                        color = Color(0xFF3B4EE8),
                        modifier = Modifier.clickable { /* Abrir términos */ }
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Política de Privacidad",
                        fontSize = 14.sp,
                        color = Color(0xFF3B4EE8),
                        modifier = Modifier.clickable { /* Abrir política */ }
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsToggleRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 15.sp,
            color = Color(0xFF1D1D1D)
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color(0xFF3B4EE8),
                uncheckedThumbColor = Color(0xFFC4C4C4),
                checkedTrackColor = Color(0xFFA8B2FF),
                uncheckedTrackColor = Color(0xFFE0E0E0)
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsOptionRow(
    label: String,
    currentValue: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = true }
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 15.sp,
            color = Color(0xFF1D1D1D)
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = currentValue,
                fontSize = 15.sp,
                color = Color(0xFF5D6270),
                modifier = Modifier.padding(end = 4.dp)
            )
            Icon(Icons.AutoMirrored.Outlined.KeyboardArrowRight, contentDescription = "Seleccionar opción")
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
