package edu.gymtonic_app.ui.components.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
fun LoginFormScreen(
    onEnter: () -> Unit,
    onRegister: () -> Unit,
    onForgotPassword: () -> Unit
) {
    val bg = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1F3F73),
            Color(0xFF3A2F7A),
            Color(0xFF2A3344)
        )
    )

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(horizontal = 18.dp, vertical = 18.dp)
    ) {
        // Texto "¡Hola!" arriba a la izquierda
        Text(
            text = "¡Hola!",
            color = Color.White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 55.dp)
        )

        // Panel grande gris abajo
        Surface(
            modifier = Modifier
                .padding(12.dp)
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(670.dp),
            color = Color(0xFFD9D9D9),
            shape = RoundedCornerShape(topStart = 70.dp, topEnd = 70.dp, bottomStart = 70.dp, bottomEnd = 70.dp),
            shadowElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 36.dp, vertical = 46.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Email
                Text(
                    text = "Email",
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF2D2D2D),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(8.dp))
                UnderlineTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "john@gmail.com"
                )

                Spacer(Modifier.height(26.dp))

                // Contraseña
                Text(
                    text = "Contraseña",
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF2D2D2D),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(8.dp))
                UnderlineTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "••••••••",
                    visualTransformation = PasswordVisualTransformation()
                )

                Spacer(Modifier.height(14.dp))

                TextButton(
                    onClick = onForgotPassword,
                    modifier = Modifier.align(Alignment.End),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "¿Has olvidado la contraseña?",
                        fontSize = 11.sp,
                        color = Color(0xFF2D2D2D).copy(alpha = 0.8f)
                    )
                }

                Spacer(Modifier.height(18.dp))

                // Botón ENTRAR (blanco con borde azul y texto azul)
                OutlinedButton(
                    onClick = onEnter,
                    modifier = Modifier
                        .width(140.dp)
                        .height(38.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = Color(0xFF3B4EE8)
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        width = 1.2.dp,
                        brush = Brush.linearGradient(listOf(Color(0xFF3B4EE8), Color(0xFF3B4EE8)))
                    )
                ) {
                    Text(
                        "ENTRAR",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                }

                Spacer(Modifier.weight(1f))

                // Texto final (2 líneas)
                Text(
                    text = "¿No tienes cuenta?",
                    fontSize = 11.sp,
                    color = Color(0xFF2D2D2D).copy(alpha = 0.75f)
                )

                TextButton(
                    onClick = onRegister,
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "¡Regístrate!",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF2D2D2D)
                    )
                }
            }
        }
    }
}


@Composable
private fun UnderlineTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),

        // Texto que aparece cuando el campo está vacío
        placeholder = {
            Text(
                placeholder,
                fontSize = 11.sp,
                color = Color(0xFF2D2D2D).copy(alpha = 0.45f)
            )
        },

        singleLine = true,

        // Permite ocultar el texto (por ejemplo en contraseñas)
        visualTransformation = visualTransformation,

        // ⚠️ Material 3 usa TextFieldDefaults.colors(), NO textFieldColors()
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,      // Fondo cuando está enfocado
            unfocusedContainerColor = Color.Transparent,    // Fondo cuando NO está enfocado
            disabledContainerColor = Color.Transparent,

            focusedIndicatorColor = Color(0xFF2D2D2D).copy(alpha = 0.65f),   // Línea inferior activa
            unfocusedIndicatorColor = Color(0xFF2D2D2D).copy(alpha = 0.35f), // Línea inferior inactiva

            focusedTextColor = Color(0xFF2D2D2D),     // Color del texto al escribir
            unfocusedTextColor = Color(0xFF2D2D2D),

            cursorColor = Color(0xFF2D2D2D)            // Color del cursor
        )
    )
}
