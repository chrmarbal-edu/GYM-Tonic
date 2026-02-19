package edu.gymtonic_app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.gymtonic_app.viewmodel.LoginState
import edu.gymtonic_app.viewmodel.LoginViewModel


@Composable
fun LoginFormScreen(
    loginViewModel: LoginViewModel,
    onLoginSuccess: () -> Unit,
    onRegister: () -> Unit,
    onForgotPassword: () -> Unit
) {
    val loginState by loginViewModel.loginState.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1F3F73),
                        Color(0xFF3A2F7A),
                        Color(0xFF2A3344)
                    )
                )
            )
            .padding(horizontal = 18.dp, vertical = 18.dp)
    ) {
        // Panel gris
        Surface(
            modifier = Modifier
                .padding(12.dp)
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(670.dp),
            color = Color(0xFFD9D9D9),
            shape = RoundedCornerShape(70.dp),
            shadowElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 36.dp, vertical = 46.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // Email
                Text(text = "Email", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                UnderlineTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "john@gmail.com"
                )

                Spacer(Modifier.height(26.dp))

                // Contraseña
                Text(text = "Contraseña", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
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
                    Text("¿Has olvidado la contraseña?", fontSize = 11.sp)
                }

                Spacer(Modifier.height(18.dp))

                // Botón ENTRAR
                OutlinedButton(
                    onClick = { loginViewModel.login(email, password) },
                    modifier = Modifier.width(140.dp).height(38.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    if (loginState is LoginState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = Color(0xFF3B4EE8)
                        )
                    } else {
                        Text("ENTRAR", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(Modifier.weight(1f))

                // Texto final
                Text("¿No tienes cuenta?", fontSize = 11.sp)
                TextButton(onClick = onRegister) {
                    Text("¡Regístrate!", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                // Mostrar errores si los hay
                if (loginState is LoginState.Error) {
                    Text(
                        text = (loginState as LoginState.Error).message,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                // Navegar si login fue correcto
                if (loginState is LoginState.Success) {
                    LaunchedEffect(Unit) {
                        onLoginSuccess()
                    }
                }
            }
        }
    }
}