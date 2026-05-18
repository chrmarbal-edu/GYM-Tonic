package edu.gymtonic_app.ui.screens.register

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.gymtonic_app.data.remote.remoteModel.user.RegisterResponse
import edu.gymtonic_app.ui.theme.LocalColors
import edu.gymtonic_app.ui.viewmodel.RegisterViewModel

@Composable
fun EmailConfirmationScreen(
    registerViewModel: RegisterViewModel,
    response: RegisterResponse,
    onBack: () -> Unit
) {
    val colors = LocalColors.current
    var otpValue by remember { mutableStateOf(TextFieldValue("")) }
    val bg = Brush.verticalGradient(colors.gradientColors)
    val focusRequester = remember { FocusRequester() }

    // Auto-validación cuando llega a 6 dígitos
    LaunchedEffect(otpValue.text) {
        if (otpValue.text.length == 6) {
            registerViewModel.confirmCode(otpValue.text, response)
        }
    }

    // Pedir foco al entrar
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(18.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Confirmación de Email",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 55.dp, bottom = 20.dp)
            )

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 80.dp),
                shape = RoundedCornerShape(30.dp),
                color = colors.surfaceMain,
                shadowElevation = 10.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Hemos enviado un código de 6 dígitos a tu correo electrónico. Por favor, introdúcelo a continuación.",
                        textAlign = TextAlign.Center,
                        color = colors.textPrimary,
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // INPUT DE DÍGITOS
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        OtpInputField(
                            otpValue = otpValue,
                            onOtpValueChange = {
                                if (it.text.length <= 6) {
                                    otpValue = it
                                }
                            },
                            focusRequester = focusRequester
                        )
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    Button(
                        onClick = { registerViewModel.confirmCode(otpValue.text, response) },
                        modifier = Modifier.fillMaxWidth().height(58.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.accent,
                            contentColor = Color.White
                        ),
                        enabled = otpValue.text.length == 6
                    ) {
                        Text(
                            text = "Confirmar",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(onClick = onBack) {
                        Text(text = "Cancelar", color = colors.accent)
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
