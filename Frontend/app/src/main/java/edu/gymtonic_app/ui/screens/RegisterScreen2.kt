@file:OptIn(ExperimentalMaterial3Api::class)

package edu.gymtonic_app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.gymtonic_app.viewmodel.RegisterViewModel


@Composable
fun RegisterScreen2(
    fullName: String,
    username: String,
    email: String,
    password: String,
    registerViewModel: RegisterViewModel,
    onBack: () -> Unit = {}
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

    // Dropdown objetivo
    val objetivos = listOf("Perder peso", "Tonificar", "Ganar masa muscular")
    var expanded by remember { mutableStateOf(false) }

    fun validateForm(): Boolean {
        fechaError = fechaNacimiento.isBlank()
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
                UnderlineLabeledField(
                    label = "Fecha de nacimiento",
                    value = fechaNacimiento,
                    onValueChange = {
                        fechaNacimiento = it
                        fechaError = false
                    },
                    placeholder = "06/10/1999",
                    isError = fechaError,
                    errorText = "Campo obligatorio"
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

                // Dropdown Objetivo
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    TextField(
                        value = objetivo,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Objetivo") },
                        placeholder = { Text("Selecciona un objetivo") },
                        isError = objetivoError,
                        modifier = Modifier.fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        objetivos.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    objetivo = option
                                    expanded = false
                                    objetivoError = false
                                }
                            )
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
                }

                Spacer(Modifier.height(28.dp))

                Button(
                    onClick = {
                        if(validateForm()){

                            val objetivoValue = when(objetivo){
                                "Perder peso" -> 0
                                "Tonificar" -> 1
                                "Ganar masa muscular" -> 2
                                else -> -1
                            }

                            registerViewModel.register(
                                username = username, name = fullName, password = password, birthdate = fechaNacimiento,
                                email = email, altura.toDouble(), peso.toDouble(), objetivoValue
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