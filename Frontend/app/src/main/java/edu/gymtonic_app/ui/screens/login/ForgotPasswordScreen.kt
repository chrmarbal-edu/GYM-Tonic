package edu.gymtonic_app.ui.screens.login

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.gymtonic_app.ui.i18n.LocalStrings
import edu.gymtonic_app.ui.components.LanguageButton
import edu.gymtonic_app.ui.components.ThemeButton
import edu.gymtonic_app.ui.screens.register.UnderlineTextField
import edu.gymtonic_app.ui.theme.LocalColors

@Composable
fun ForgotPasswordScreen(
    onBack: () -> Unit,
    onContinue: (String) -> Unit
) {
    val strings = LocalStrings.current
    val colors = LocalColors.current
    var email by remember { mutableStateOf("") }
    val bg = Brush.verticalGradient(colors.gradientColors)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(18.dp)
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ThemeButton(tint = Color.White)
            LanguageButton(tint = Color.White)
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Recuperar Contraseña",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 80.dp, bottom = 20.dp)
            )

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                shape = RoundedCornerShape(30.dp),
                color = colors.surfaceMain,
                shadowElevation = 10.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Introduce tu correo electrónico para buscar tu cuenta.",
                        textAlign = TextAlign.Center,
                        color = colors.textPrimary,
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    UnderlineTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = "tuemail@gmail.com"
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    Button(
                        onClick = { onContinue(email) },
                        modifier = Modifier.fillMaxWidth().height(58.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.accent,
                            contentColor = Color.White
                        ),
                        enabled = email.isNotBlank() && email.contains("@")
                    ) {
                        Text(
                            text = "CONTINUAR",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(onClick = onBack) {
                        Text(text = strings.back, color = colors.accent)
                    }
                }
            }
        }
    }
}
