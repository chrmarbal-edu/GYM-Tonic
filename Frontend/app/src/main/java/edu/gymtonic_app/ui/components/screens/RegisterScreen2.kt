@file:OptIn(ExperimentalMaterial3Api::class)

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RegisterScreen2(
   onEnter: () -> Unit,
    onBack: () -> Unit = {}
) {
    val bg = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1F3F73),
            Color(0xFF3A2F7A),
            Color(0xFF2A3344)
        )
    )

    var fechaNacimiento by remember { mutableStateOf("") }
    var altura by remember { mutableStateOf("") }
    var peso by remember { mutableStateOf("") }
    var objetivo by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(horizontal = 18.dp, vertical = 18.dp)
    ) {
        // Título arriba
        Text(
            text = "Crea tu cuenta",
            color = Color.White,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 55.dp)
        )

        // ✅ CARD REAL (Surface con elevación)
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp) // 🔥 sube el card (ajusta 60..110)
                .fillMaxWidth()
                .heightIn(min = 520.dp, max = 620.dp),
            shape = RoundedCornerShape(70.dp),
            color = Color(0xFFD9D9D9),
            shadowElevation = 10.dp // 🔥 esto es la “sombra” de card
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 36.dp, vertical = 46.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                UnderlineLabeledField(
                    label = "Fecha de nacimiento",
                    value = fechaNacimiento,
                    onValueChange = { fechaNacimiento = it },
                    placeholder = "06/10/1999"
                )

                Spacer(Modifier.height(18.dp))

                UnderlineLabeledField(
                    label = "Altura(cm)",
                    value = altura,
                    onValueChange = { altura = it },
                    placeholder = "185"
                )

                Spacer(Modifier.height(18.dp))

                UnderlineLabeledField(
                    label = "Peso(kg)",
                    value = peso,
                    onValueChange = { peso = it },
                    placeholder = "79"
                )

                Spacer(Modifier.height(22.dp))

                UnderlineLabeledField(
                    label = "Objetivo",
                    value = objetivo,
                    onValueChange = { objetivo = it },
                    placeholder = "********",
                    visualTransformation = PasswordVisualTransformation()
                )

                Spacer(Modifier.height(22.dp))

                Spacer(Modifier.height(28.dp))

                Button(
                    onClick = onEnter,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3B4EE8),
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Text(
                        text = "ENTRAR",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}
/**
 * Campo con etiqueta + TextField subrayado (reutilizable).
 */
@Composable
private fun UnderlineLabeledField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    Text(
        text = label,
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF2D2D2D),
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold
    )
    Spacer(Modifier.height(8.dp))
    UnderlineTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = placeholder,
        visualTransformation = visualTransformation
    )
}