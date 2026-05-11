package edu.gymtonic_app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NightsStay
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import edu.gymtonic_app.ui.theme.AppTheme
import edu.gymtonic_app.ui.theme.ThemeManager

@Composable
fun ThemeButton(tint: Color = Color(0xFF2D2D2D), modifier: Modifier = Modifier) {
    val currentTheme by ThemeManager.theme.collectAsState()
    IconButton(onClick = { ThemeManager.toggle() }, modifier = modifier) {
        Icon(
            imageVector = if (currentTheme == AppTheme.LIGHT) Icons.Outlined.NightsStay else Icons.Outlined.WbSunny,
            contentDescription = "Toggle theme",
            tint = tint
        )
    }
}
