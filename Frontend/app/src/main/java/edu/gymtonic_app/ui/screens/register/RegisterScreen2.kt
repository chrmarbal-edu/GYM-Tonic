@file:OptIn(ExperimentalMaterial3Api::class)

package edu.gymtonic_app.ui.screens.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.gymtonic_app.ui.i18n.LocalStrings
import edu.gymtonic_app.ui.theme.LocalColors
import edu.gymtonic_app.ui.viewmodel.RegisterState
import edu.gymtonic_app.ui.viewmodel.RegisterViewModel

@Composable
fun RegisterScreen2(
    fullName: String,
    username: String,
    email: String,
    password: String,
    registerViewModel: RegisterViewModel,
    onBack: () -> Unit = {},
    registerState: RegisterState
) {
    val strings = LocalStrings.current
    val colors = LocalColors.current
    val bg = Brush.verticalGradient(colors.gradientColors)

    var fechaNacimiento by remember { mutableStateOf("") }
    var altura by remember { mutableStateOf("") }
    var peso by remember { mutableStateOf("") }
    var objetivo by remember { mutableStateOf("") }

    var fechaError by remember { mutableStateOf(false) }
    var alturaError by remember { mutableStateOf(false) }
    var pesoError by remember { mutableStateOf(false) }
    var objetivoError by remember { mutableStateOf(false) }

    val objetivos = listOf(strings.goalLoseWeight, strings.goalToneUp, strings.goalBuildMuscle)
    var dropdownExpanded by remember { mutableStateOf(false) }

    fun validateForm(): Boolean {
        val fechaRegex = Regex("""\d{4}-\d{2}-\d{2}""")
        fechaError = !fechaNacimiento.matches(fechaRegex)
        alturaError = altura.isBlank()
        pesoError = peso.isBlank()
        objetivoError = objetivo.isBlank()
        return !fechaError && !alturaError && !pesoError && !objetivoError
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(horizontal = 18.dp, vertical = 18.dp)
    ) {
        Text(
            text = strings.createAccount,
            color = Color.White,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 55.dp)
        )

        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp)
                .fillMaxWidth()
                .heightIn(min = 520.dp, max = 620.dp),
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
                FechaNacimientoField(
                    label = strings.birthDate,
                    formatHint = strings.birthDateFormat,
                    fecha = fechaNacimiento,
                    onFechaChange = { fechaNacimiento = it; fechaError = false },
                    isError = fechaError
                )

                Spacer(Modifier.height(18.dp))

                UnderlineLabeledField(
                    label = strings.height,
                    value = altura,
                    onValueChange = { altura = it; alturaError = false },
                    placeholder = "185",
                    isError = alturaError,
                    errorText = strings.requiredField
                )

                Spacer(Modifier.height(18.dp))

                UnderlineLabeledField(
                    label = strings.weight,
                    value = peso,
                    onValueChange = { peso = it; pesoError = false },
                    placeholder = "79",
                    isError = pesoError,
                    errorText = strings.requiredField
                )

                Spacer(Modifier.height(22.dp))

                Box(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = { dropdownExpanded = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(if (objetivo.isEmpty()) strings.selectGoal else objetivo)
                    }

                    DropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false }
                    ) {
                        objetivos.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    objetivo = option
                                    dropdownExpanded = false
                                    objetivoError = false
                                }
                            )
                        }
                    }
                }
                if (objetivoError) {
                    Text(
                        text = strings.requiredField,
                        color = Color.Red,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(Modifier.height(28.dp))

                Button(
                    onClick = {
                        if (validateForm()) {
                            val objetivoValue = objetivos.indexOf(objetivo)

                            registerViewModel.register(
                                username = username,
                                name = fullName,
                                password = password,
                                birthdate = fechaNacimiento,
                                email = email,
                                height = altura.toDouble(),
                                weight = peso.toDouble(),
                                objective = objetivoValue,
                                oauth = registerViewModel.googleUserData?.oauth
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(58.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.accent,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    if (registerState is RegisterState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                    } else {
                        Text(
                            text = strings.signUpButton,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun FechaNacimientoField(
    label: String,
    formatHint: String,
    fecha: String,
    onFechaChange: (String) -> Unit,
    isError: Boolean
) {
    UnderlineLabeledField(
        label = label,
        value = fecha,
        onValueChange = {
            val digits = it.filter { c -> c.isDigit() }
            var formatted = ""
            for (i in digits.indices) {
                formatted += digits[i]
                if (i == 3 || i == 5) formatted += "-"
            }
            if (formatted.length <= 10) onFechaChange(formatted)
        },
        placeholder = "yyyy-MM-dd",
        isError = isError,
        errorText = formatHint,
        visualTransformation = VisualTransformation.None,
        keyboardType = KeyboardType.Number
    )
}

@Composable
private fun UnderlineLabeledField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    isError: Boolean = false,
    errorText: String = "",
    keyboardType: KeyboardType = KeyboardType.Text
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
        placeholder = {
            Text(
                placeholder,
                color = colors.fieldIndicator.copy(alpha = 0.45f)
            )
        },
        visualTransformation = visualTransformation,
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
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
    if (isError) {
        Text(
            text = errorText,
            color = Color.Red,
            fontSize = 11.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
