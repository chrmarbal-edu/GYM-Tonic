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
import edu.gymtonic_app.ui.components.ObserveToastMessage
import edu.gymtonic_app.ui.components.ThemeButton
import edu.gymtonic_app.ui.components.LanguageButton
import edu.gymtonic_app.ui.screens.register.UnderlineTextField
import edu.gymtonic_app.ui.theme.LocalColors
import edu.gymtonic_app.ui.viewmodel.LoginState
import edu.gymtonic_app.ui.viewmodel.LoginViewModel

import androidx.compose.foundation.border
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import edu.gymtonic_app.data.remote.remoteModel.user.RegisterResponse

import java.time.Duration
import java.time.Instant
import java.time.OffsetDateTime
import java.util.Locale
import kotlinx.coroutines.delay

@Composable
fun ResetPasswordScreen(
    email: String,
    loginViewModel: LoginViewModel,
    onBack: () -> Unit,
    onSuccess: () -> Unit
) {
    val strings = LocalStrings.current
    val colors = LocalColors.current
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val bg = Brush.verticalGradient(colors.gradientColors)

    var errorText by remember { mutableStateOf<String?>(null) }
    val loginState by loginViewModel.loginState.collectAsState()

    var otpValue by remember { mutableStateOf(TextFieldValue("")) }
    val focusRequester = remember { FocusRequester() }

    // Persistimos los datos de recuperación localmente para evitar saltos de pantalla durante el Loading/Error
    var isOtpStage by remember { mutableStateOf(false) }
    var recoveryToken by remember { mutableStateOf("") }

    ObserveToastMessage(
        message = (loginState as? LoginState.Error)?.message
    )

    // Cuando el estado cambia a AwaitingRecoveryConfirmation, guardamos los datos y pedimos foco
    LaunchedEffect(loginState) {
        if (loginState is LoginState.AwaitingRecoveryConfirmation) {
            val state = loginState as LoginState.AwaitingRecoveryConfirmation
            recoveryToken = state.recoveryToken
            isOtpStage = true
            focusRequester.requestFocus()
        }
    }

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
                text = strings.recoverTitle,
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
                    if (isOtpStage) {
                        // --- FASE DE INTRODUCCIÓN DE CÓDIGO (OTP) ---
                        Text(
                            text = strings.recoverEmailSent,
                            textAlign = TextAlign.Center,
                            color = colors.textPrimary,
                            fontSize = 15.sp
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        OtpInputField(
                            otpValue = otpValue,
                            onOtpValueChange = {
                                if (it.text.length <= 6) {
                                    otpValue = it
                                }
                            },
                            focusRequester = focusRequester
                        )

                        if (errorText != null) {
                            Text(
                                text = errorText!!,
                                color = Color.Red,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(40.dp))

                        Button(
                            onClick = { 
                                loginViewModel.confirmRecoveryCode(otpValue.text, recoveryToken) {
                                    onSuccess()
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(58.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colors.accent,
                                contentColor = Color.White
                            ),
                            enabled = otpValue.text.length == 6 && loginState !is LoginState.Loading
                        ) {
                            if (loginState is LoginState.Loading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Text(
                                    text = "RESTABLECER CONTRASEÑA",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    } else {
                        // --- FASE DE INTRODUCCIÓN DE NUEVA CONTRASEÑA ---
                        Text(
                            text = "Introduce tu nueva contraseña para la cuenta $email",
                            textAlign = TextAlign.Center,
                            color = colors.textPrimary,
                            fontSize = 15.sp
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        UnderlineTextField(
                            value = newPassword,
                            onValueChange = { 
                                newPassword = it.trim()
                                errorText = null
                            },
                            placeholder = "Nueva contraseña",
                            isPassword = true
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        UnderlineTextField(
                            value = confirmPassword,
                            onValueChange = { 
                                confirmPassword = it.trim()
                                errorText = null
                            },
                            placeholder = "Repetir contraseña",
                            isPassword = true
                        )

                        if (errorText != null) {
                            Text(
                                text = errorText!!,
                                color = Color.Red,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(40.dp))

                        Button(
                            onClick = { 
                                if (newPassword != confirmPassword) {
                                    errorText = strings.passwordsNoMatch
                                } else if (newPassword.length < 8) {
                                    errorText = "Mínimo 8 caracteres"
                                } else {
                                    loginViewModel.resetPassword(email, newPassword) {
                                        // El LaunchedEffect detectará el cambio a AwaitingRecoveryConfirmation
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(58.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colors.accent,
                                contentColor = Color.White
                            ),
                            enabled = newPassword.isNotBlank() && confirmPassword.isNotBlank() && loginState !is LoginState.Loading
                        ) {
                            if (loginState is LoginState.Loading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Text(
                                    text = "SOLICITAR CÓDIGO",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(onClick = {
                        if (isOtpStage) {
                            isOtpStage = false
                            loginViewModel.resetLoginState()
                        } else {
                            onBack()
                        }
                    }) {
                        Text(text = strings.back, color = colors.accent)
                    }
                }
            }
        }
    }
}

@Composable
fun OtpInputField(
    otpValue: TextFieldValue,
    onOtpValueChange: (TextFieldValue) -> Unit,
    focusRequester: FocusRequester
) {
    val colors = LocalColors.current

    BasicTextField(
        value = otpValue,
        onValueChange = onOtpValueChange,
        modifier = Modifier
            .focusRequester(focusRequester)
            .fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        decorationBox = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)
            ) {
                repeat(6) { index ->
                    val char = when {
                        index >= otpValue.text.length -> ""
                        else -> otpValue.text[index].toString()
                    }
                    val isFocused = otpValue.text.length == index

                    Box(
                        modifier = Modifier
                            .size(width = 38.dp, height = 46.dp)
                            .border(
                                width = if (isFocused) 2.dp else 1.dp,
                                color = if (isFocused) colors.accent else colors.fieldIndicator.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .background(colors.surfaceCard, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = char,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    )
}

