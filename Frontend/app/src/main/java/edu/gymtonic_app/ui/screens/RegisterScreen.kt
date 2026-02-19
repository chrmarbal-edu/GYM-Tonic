@file:OptIn(ExperimentalMaterial3Api::class)

package edu.gymtonic_app.ui.screens

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
import androidx.navigation.NavHostController
import edu.gymtonic_app.viewmodel.RegisterViewModel

@Composable
fun RegisterScreen(
    navController: NavHostController,
    registerViewModel: RegisterViewModel,
    onBack: () -> Unit = {}
) {
    val registerState by registerViewModel.registerState.collectAsState()

    var showStep2 by remember { mutableStateOf(false) }

    val bg = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1F3F73),
            Color(0xFF3A2F7A),
            Color(0xFF2A3344)
        )
    )

    var fullName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var fullNameError by remember { mutableStateOf(false) }
    var usernameError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var confirmPasswordError by remember { mutableStateOf(false) }
    var passwordsMatchError by remember { mutableStateOf(false) }

    fun validateForm(): Boolean {
        fullNameError = fullName.isBlank()
        usernameError = username.isBlank()
        emailError = email.isBlank()
        passwordError = password.isBlank()
        confirmPasswordError = confirmPassword.isBlank()
        passwordsMatchError = password != confirmPassword

        return !fullNameError &&
                !usernameError &&
                !emailError &&
                !passwordError &&
                !confirmPasswordError &&
                !passwordsMatchError
    }


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
                    label = "Nombre completo",
                    value = fullName,
                    onValueChange = {
                        fullName = it
                        fullNameError = false
                    },
                    placeholder = "Jhon Andrés",
                    isError = fullNameError,
                    errorText = "Campo obligatorio"
                )


                Spacer(Modifier.height(18.dp))

                UnderlineLabeledField(
                    label = "Nombre de usuario",
                    value = username,
                    onValueChange = {
                        username = it
                        usernameError = false },
                    placeholder = "jhonandres_00",
                    isError = usernameError,
                    errorText = "Campo obligatorio"
                )

                Spacer(Modifier.height(18.dp))

                UnderlineLabeledField(
                    label = "Email",
                    value = email,
                    onValueChange = {
                        email = it
                                    emailError = false},
                    placeholder = "john@gmail.com",
                    isError = emailError,
                    errorText = "Campo obligatorio"
                )

                Spacer(Modifier.height(22.dp))

                UnderlineLabeledField(
                    label = "Contraseña",
                    value = password,
                    onValueChange = {
                        password = it
                        passwordError = false
                        passwordsMatchError = false
                    },
                    placeholder = "********",
                    isError = passwordError,
                    errorText = "Campo obligatorio",
                    visualTransformation = PasswordVisualTransformation()
                )

                UnderlineLabeledField(
                    label = "Confirmar contraseña",
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        confirmPasswordError = false
                        passwordsMatchError = false
                    },
                    placeholder = "********",
                    isError = confirmPasswordError || passwordsMatchError,
                    errorText = if (passwordsMatchError) "Las contraseñas no coinciden" else "Campo obligatorio",
                    visualTransformation = PasswordVisualTransformation()
                )


                Spacer(Modifier.height(28.dp))

                Button(
                    onClick = {
                        if (validateForm()){
                            showStep2 = true
                        }
                    },
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
                        text = "SIGUIENTE",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }

    if(showStep2){
        RegisterScreen2(
            fullName,
            username,
            email,
            password,
            registerViewModel = registerViewModel,
            onBack = { navController.popBackStack() },
            registerState
        )
    }
}
/**
 * Campo con etiqueta + TextField subrayado (reutilizable).
 */
@Composable
fun UnderlineLabeledField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isError: Boolean = false,
    errorText: String? = null,
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

    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text(
                placeholder,
                fontSize = 12.sp,
                color = Color(0xFF2D2D2D).copy(alpha = 0.45f)
            )
        },
        singleLine = true,
        visualTransformation = visualTransformation,
        isError = isError,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            focusedIndicatorColor = if (isError) Color.Red else Color(0xFF2D2D2D).copy(alpha = 0.65f),
            unfocusedIndicatorColor = if (isError) Color.Red else Color(0xFF2D2D2D).copy(alpha = 0.35f),
            cursorColor = if (isError) Color.Red else Color(0xFF2D2D2D)
        )
    )

    if (isError && errorText != null) {
        Spacer(Modifier.height(4.dp))
        Text(
            text = errorText,
            color = Color.Red,
            fontSize = 11.sp
        )
    }
}


/**
 * TextField con fondo transparente y línea inferior (Material 3).
 */
@Composable
fun UnderlineTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = placeholder,
                fontSize = 12.sp,
                color = Color(0xFF2D2D2D).copy(alpha = 0.45f)
            )
        },
        singleLine = true,
        visualTransformation = visualTransformation,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            focusedIndicatorColor = Color(0xFF2D2D2D).copy(alpha = 0.65f),
            unfocusedIndicatorColor = Color(0xFF2D2D2D).copy(alpha = 0.35f),
            focusedTextColor = Color(0xFF2D2D2D),
            unfocusedTextColor = Color(0xFF2D2D2D),
            cursorColor = Color(0xFF2D2D2D)
        )
    )
}