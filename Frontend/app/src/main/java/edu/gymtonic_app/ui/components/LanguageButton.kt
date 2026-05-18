package edu.gymtonic_app.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.gymtonic_app.ui.i18n.LanguageManager
import edu.gymtonic_app.ui.i18n.LocalStrings

@Composable
fun LanguageButton(tint: Color = Color(0xFF2D2D2D), modifier: Modifier = Modifier) {
    val strings = LocalStrings.current
    TextButton(
        onClick = { LanguageManager.toggle() },
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        Icon(
            imageVector = Icons.Outlined.Language,
            contentDescription = "Switch language",
            tint = tint,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = strings.language,
            color = tint,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
        )
    }
}
