@file:OptIn(ExperimentalMaterial3Api::class)

package edu.gymtonic_app.ui.screens.register

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
import edu.gymtonic_app.ui.navigation.Routes
import edu.gymtonic_app.ui.viewmodel.RegisterState
import edu.gymtonic_app.ui.components.LanguageButton
import edu.gymtonic_app.ui.components.ThemeButton
import edu.gymtonic_app.ui.i18n.LocalStrings
import edu.gymtonic_app.ui.theme.LocalColors
import edu.gymtonic_app.ui.viewmodel.RegisterViewModel

@Composable
fun RegisterScreen(
    navController: NavHostController,
    registerViewModel: RegisterViewModel,
    onBack: () -> Unit = {}
) {
    val strings = LocalStrings.current
    val colors = LocalColors.current
    val registerState by registerViewModel.registerState.collectAsState()

    // Datos iniciales desde registro social si existen
    val socialData = registerViewModel.socialUserData
    var showStep2 by remember { mutableStateOf(socialData != null) }

    val bg = Brush.verticalGradient(colors.gradientColors)

    LaunchedEffect(registerState) {
        if (registerState is RegisterState.Success) {
            navController.navigate(Routes.HOME) {
                popUpTo(navController.graph.startDestinationId) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
    }

    var fullName by remember { mutableStateOf(socialData?.name ?: "") }
    var username by remember { mutableStateOf(socialData?.name ?: "") }
    var email by remember { mutableStateOf(socialData?.email ?: "") }
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

    if (showStep2) {
        RegisterScreen2(
            fullName,
            username,
            email,
            password,
            registerViewModel = registerViewModel,
            onBack = { 
                if (socialData != null) {
                    registerViewModel.clearSocialData()
                    onBack()
                } else {
                    showStep2 = false 
                }
            },
            registerState
        )
    } else {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(horizontal = 18.dp, vertical = 18.dp)
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
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = strings.createAccount,
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(top = 55.dp, bottom = 20.dp)
            )

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(bottom = 80.dp),
                shape = RoundedCornerShape(70.dp),
                color = colors.surfaceMain,
                shadowElevation = 10.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 36.dp, vertical = 46.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    UnderlineLabeledField(
                        label = strings.fullName,
                        value = fullName,
                        onValueChange = { fullName = it; fullNameError = false },
                        placeholder = "Jhon Andrés",
                        isError = fullNameError,
                        errorText = strings.requiredField
                    )

                    Spacer(Modifier.height(18.dp))

                    UnderlineLabeledField(
                        label = strings.usernameField,
                        value = username,
                        onValueChange = { username = it; usernameError = false },
                        placeholder = "jhonandres_00",
                        isError = usernameError,
                        errorText = strings.requiredField
                    )

                    Spacer(Modifier.height(18.dp))

                    UnderlineLabeledField(
                        label = strings.email,
                        value = email,
                        onValueChange = { email = it; emailError = false },
                        placeholder = "john@gmail.com",
                        isError = emailError,
                        errorText = strings.requiredField
                    )

                    Spacer(Modifier.height(22.dp))

                    UnderlineLabeledField(
                        label = strings.password,
                        value = password,
                        onValueChange = {
                            password = it.trim()
                            passwordError = false
                            passwordsMatchError = false
                        },
                        placeholder = "********",
                        isError = passwordError,
                        errorText = strings.requiredField,
                        visualTransformation = PasswordVisualTransformation()
                    )

                    UnderlineLabeledField(
                        label = strings.confirmPassword,
                        value = confirmPassword,
                        onValueChange = {
                            confirmPassword = it.trim()
                            confirmPasswordError = false
                            passwordsMatchError = false
                        },
                        placeholder = "********",
                        isError = confirmPasswordError || passwordsMatchError,
                        errorText = if (passwordsMatchError) strings.passwordsNoMatch else strings.requiredField,
                        visualTransformation = PasswordVisualTransformation()
                    )

                    Spacer(Modifier.height(28.dp))

                    Button(
                        onClick = { if (validateForm()) showStep2 = true },
                        modifier = Modifier.fillMaxWidth().height(58.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.accent,
                            contentColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                    ) {
                        Text(
                            text = strings.nextButton,
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
    }
}

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
    val colors = LocalColors.current
    Text(
        text = label,
        modifier = Modifier.fillMaxWidth(),
        color = colors.fieldIndicator,
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
                color = colors.fieldIndicator.copy(alpha = 0.45f)
            )
        },
        singleLine = true,
        visualTransformation = visualTransformation,
        isError = isError,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            focusedIndicatorColor = if (isError) Color.Red else colors.fieldIndicator.copy(alpha = 0.65f),
            unfocusedIndicatorColor = if (isError) Color.Red else colors.fieldIndicator.copy(alpha = 0.35f),
            focusedTextColor = colors.textPrimary,
            unfocusedTextColor = colors.textPrimary,
            cursorColor = if (isError) Color.Red else colors.fieldIndicator
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


@Composable
fun UnderlineTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    val colors = LocalColors.current
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = placeholder,
                fontSize = 12.sp,
                color = colors.fieldIndicator.copy(alpha = 0.45f)
            )
        },
        singleLine = true,
        visualTransformation = visualTransformation,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            focusedIndicatorColor = colors.fieldIndicator.copy(alpha = 0.65f),
            unfocusedIndicatorColor = colors.fieldIndicator.copy(alpha = 0.35f),
            focusedTextColor = colors.textPrimary,
            unfocusedTextColor = colors.textPrimary,
            cursorColor = colors.fieldIndicator
        )
    )
}
