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
import edu.gymtonic_app.ui.i18n.AppLanguage
import edu.gymtonic_app.ui.i18n.LanguageManager
import edu.gymtonic_app.ui.i18n.LocalStrings
import edu.gymtonic_app.ui.screens.exercise.TrainingShellScreen
import edu.gymtonic_app.ui.theme.AppTheme
import edu.gymtonic_app.ui.theme.LocalColors
import edu.gymtonic_app.ui.theme.ThemeManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onOpenTraining: () -> Unit,
    onOpenGroups: () -> Unit,
    onOpenFriends: () -> Unit,
    onOpenChallenges: () -> Unit,
    onOpenProfile: () -> Unit,
) {
    val strings = LocalStrings.current
    val colors = LocalColors.current
    val currentLanguage by LanguageManager.language.collectAsState()
    val currentTheme by ThemeManager.theme.collectAsState()

    TrainingShellScreen(
        title = strings.settingsTitle,
        onBack = onBack,
        showBottomBar = true,
        selectedBottomItem = BottomNavItem.PROFILE,
        onOpenTraining = onOpenTraining,
        onOpenGroups = onOpenGroups,
        onOpenFriends = onOpenFriends,
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
                AccountSectionCard(title = strings.settingsNotifications) {
                    var routineCompletionNotifs by remember { mutableStateOf(true) }
                    var workoutReminderNotifs by remember { mutableStateOf(false) }
                    var newChallengeNotifs by remember { mutableStateOf(true) }

                    SettingsToggleRow(
                        label = strings.settingsCompletedRoutines,
                        checked = routineCompletionNotifs,
                        onCheckedChange = { routineCompletionNotifs = it }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    SettingsToggleRow(
                        label = strings.settingsWorkoutReminders,
                        checked = workoutReminderNotifs,
                        onCheckedChange = { workoutReminderNotifs = it }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    SettingsToggleRow(
                        label = strings.settingsNewChallenges,
                        checked = newChallengeNotifs,
                        onCheckedChange = { newChallengeNotifs = it }
                    )
                }
            }

            item {
                AccountSectionCard(title = strings.settingsAppearance) {
                    val selectedTheme = if (currentTheme == AppTheme.DARK)
                        strings.settingsThemeDark
                    else
                        strings.settingsThemeLight

                    val themeOptions = listOf(
                        strings.settingsThemeLight,
                        strings.settingsThemeDark
                    )

                    SettingsOptionRow(
                        label = strings.settingsTheme,
                        currentValue = selectedTheme,
                        options = themeOptions,
                        selectOption = strings.settingsSelectOption,
                        onOptionSelected = { selected ->
                            if (selected == strings.settingsThemeDark)
                                ThemeManager.setTheme(AppTheme.DARK)
                            else
                                ThemeManager.setTheme(AppTheme.LIGHT)
                        }
                    )
                }
            }

            item {
                AccountSectionCard(title = strings.settingsUnits) {
                    var weightUnit by remember { mutableStateOf("kg") }
                    val weightOptions = listOf("kg", "lbs")

                    SettingsOptionRow(
                        label = strings.settingsWeight,
                        currentValue = weightUnit,
                        options = weightOptions,
                        selectOption = strings.settingsSelectOption,
                        onOptionSelected = { weightUnit = it }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    var distanceUnit by remember { mutableStateOf("km") }
                    val distanceOptions = listOf("km", strings.settingsMiles)
                    SettingsOptionRow(
                        label = strings.settingsDistance,
                        currentValue = distanceUnit,
                        options = distanceOptions,
                        selectOption = strings.settingsSelectOption,
                        onOptionSelected = { distanceUnit = it }
                    )
                }
            }

            item {
                AccountSectionCard(title = strings.settingsLanguage) {
                    val selectedLanguage = if (currentLanguage == AppLanguage.SPANISH)
                        strings.settingsLanguageSpanish
                    else
                        strings.settingsLanguageEnglish

                    val languageOptions = listOf(
                        strings.settingsLanguageSpanish,
                        strings.settingsLanguageEnglish
                    )

                    SettingsOptionRow(
                        label = strings.settingsAppLanguage,
                        currentValue = selectedLanguage,
                        options = languageOptions,
                        selectOption = strings.settingsSelectOption,
                        onOptionSelected = { selected ->
                            val newLang = if (selected == strings.settingsLanguageEnglish)
                                AppLanguage.ENGLISH
                            else
                                AppLanguage.SPANISH
                            LanguageManager.setLanguage(newLang)
                        }
                    )
                }
            }

            item {
                AccountSectionCard(title = strings.settingsAbout) {
                    val colors2 = LocalColors.current
                    Text(
                        text = strings.settingsVersion,
                        fontSize = 14.sp,
                        color = colors2.textOnAccent,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun SettingsToggleRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    val colors = LocalColors.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 15.sp,
            color = colors.textOnAccent
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = colors.accent,
                uncheckedThumbColor = Color(0xFFC4C4C4),
                checkedTrackColor = colors.accent.copy(alpha = 0.4f),
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
    selectOption: String,
    onOptionSelected: (String) -> Unit
) {
    val colors = LocalColors.current
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
            color = colors.textOnAccent
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = currentValue,
                fontSize = 15.sp,
                color = colors.textOnAccent.copy(alpha = 0.7f),
                modifier = Modifier.padding(end = 4.dp)
            )
            Icon(
                Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                contentDescription = selectOption,
                tint = colors.textOnAccent
            )
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
