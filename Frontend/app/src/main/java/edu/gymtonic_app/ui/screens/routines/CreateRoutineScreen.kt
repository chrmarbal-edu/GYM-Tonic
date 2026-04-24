package edu.gymtonic_app.ui.screens.routines

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Pantalla de crear rutina.
 * De momento es un placeholder en blanco con el título "Crear Rutina".
 * En futuro, aquí irá el formulario/flujo para crear rutinas.
 */
@Composable
fun CreateRoutineScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Formulario de crear rutina (en desarrollo)",
                color = Color(0xFF464A57),
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )
        }
    }
}

