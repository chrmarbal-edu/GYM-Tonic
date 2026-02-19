@file:OptIn(ExperimentalMaterial3Api::class)

package edu.gymtonic_app.ui.screens

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
import edu.gymtonic_app.viewmodel.RegisterState
import edu.gymtonic_app.viewmodel.RegisterViewModel

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
    val bg = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1F3F73),
            Color(0xFF3A2F7A),
            Color(0xFF2A3344)
        )
    )

    // Campos
    var fechaNacimiento by remember { mutableStateOf("") }
    var altura by remember { mutableStateOf("") }
    var peso by remember { mutableStateOf("") }
    var objetivo by remember { mutableStateOf("") }

    // Errores
    var fechaError by remember { mutableStateOf(false) }
    var alturaError by remember { mutableStateOf(false) }
    var pesoError by remember { mutableStateOf(false) }
    var objetivoError by remember { mutableStateOf(false) }

    // Dropdown clásico
    val objetivos = listOf("Perder peso", "Tonificar", "Ganar masa muscular")
    var dropdownExpanded by remember { mutableStateOf(false) }

    fun validateForm(): Boolean {
        // Fecha debe ser exactamente yyyy-MM-dd
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
            text = "Crea tu cuenta",
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
            color = Color(0xFFD9D9D9),
            shadowElevation = 10.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 36.dp, vertical = 46.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Fecha con máscara simple
                FechaNacimientoField(
                    fecha = fechaNacimiento,
                    onFechaChange = { fechaNacimiento = it; fechaError = false },
                    isError = fechaError
                )

                Spacer(Modifier.height(18.dp))

                UnderlineLabeledField(
                    label = "Altura (cm)",
                    value = altura,
                    onValueChange = {
                        altura = it
                        alturaError = false
                    },
                    placeholder = "185",
                    isError = alturaError,
                    errorText = "Campo obligatorio"
                )

                Spacer(Modifier.height(18.dp))

                UnderlineLabeledField(
                    label = "Peso (kg)",
                    value = peso,
                    onValueChange = {
                        peso = it
                        pesoError = false
                    },
                    placeholder = "79",
                    isError = pesoError,
                    errorText = "Campo obligatorio"
                )

                Spacer(Modifier.height(22.dp))

                // Dropdown clásico
                Box(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = { dropdownExpanded = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(if (objetivo.isEmpty()) "Selecciona un objetivo" else objetivo)
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
                        text = "Campo obligatorio",
                        color = Color.Red,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(Modifier.height(28.dp))

                Button(
                    onClick = {
                        if (validateForm()) {
                            val objetivoValue = when (objetivo) {
                                "Perder peso" -> 0
                                "Tonificar" -> 1
                                "Ganar masa muscular" -> 2
                                else -> -1
                            }

                            registerViewModel.register(
                                username = username,
                                name = fullName,
                                password = password,
                                birthdate = fechaNacimiento,
                                email = email,
                                altura.toDouble(),
                                peso.toDouble(),
                                objetivoValue
                            )
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
                    if(registerState is RegisterState.Loading){
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = Color(0xFFFFFFFF)
                        )
                    }else{
                        Text(
                            text = "ENTRAR",
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
    fecha: String,
    onFechaChange: (String) -> Unit,
    isError: Boolean
) {
    UnderlineLabeledField(
        label = "Fecha de nacimiento",
        value = fecha,
        onValueChange = {
            // Solo números y guiones automáticos
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
        errorText = "Formato: yyyy-MM-dd",
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
        placeholder = { Text(placeholder) },
        visualTransformation = visualTransformation,
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
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
