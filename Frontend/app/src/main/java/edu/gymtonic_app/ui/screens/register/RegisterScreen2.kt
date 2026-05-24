@file:OptIn(ExperimentalMaterial3Api::class)

package edu.gymtonic_app.ui.screens.register

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import edu.gymtonic_app.BuildConfig
import edu.gymtonic_app.ui.components.LanguageButton
import edu.gymtonic_app.ui.components.ThemeButton
import edu.gymtonic_app.ui.i18n.LocalStrings
import edu.gymtonic_app.ui.theme.LocalColors
import edu.gymtonic_app.ui.viewmodel.RegisterState
import edu.gymtonic_app.ui.viewmodel.RegisterViewModel
import java.io.ByteArrayOutputStream
import java.io.File
import java.time.LocalDate
import java.time.Period

@Composable
fun RegisterScreen2(
    fullName: String,
    username: String,
    email: String,
    password: String,
    registerViewModel: RegisterViewModel,
    onBack: () -> Unit = {},
    onLogin: () -> Unit = {},
    registerState: RegisterState
) {
    val strings = LocalStrings.current
    val colors = LocalColors.current
    val context = LocalContext.current
    val bg = Brush.verticalGradient(colors.gradientColors)

    val isSpanish = strings.language == "ES"

    var fechaNacimientoValue by remember { mutableStateOf(TextFieldValue("")) }
    
    LaunchedEffect(isSpanish) {
        val currentText = fechaNacimientoValue.text
        if (currentText.isBlank()) return@LaunchedEffect
        
        // Convert between formats on the fly
        if (isSpanish) { // EN -> ES
            val parts = currentText.split("-")
            if (parts.size == 3) {
                val newText = "${parts[2]}/${parts[1]}/${parts[0]}"
                fechaNacimientoValue = TextFieldValue(newText, TextRange(newText.length))
            }
        } else { // ES -> EN
            val parts = currentText.split("/")
            if (parts.size == 3) {
                val newText = "${parts[2]}-${parts[1]}-${parts[0]}"
                fechaNacimientoValue = TextFieldValue(newText, TextRange(newText.length))
            }
        }
    }

    var altura by remember { mutableStateOf("") }
    var peso by remember { mutableStateOf("") }
    var objetivo by remember { mutableStateOf("") }

    var pictureBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var pictureUri by remember { mutableStateOf<Uri?>(null) }
    var isDefaultPicture by remember { mutableStateOf(false) }
    
    val socialPictureUrl = registerViewModel.socialUserData?.picture

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        pictureUri = uri
        pictureBitmap = null
        isDefaultPicture = false
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
        pictureBitmap = bitmap
        pictureUri = null
        isDefaultPicture = false
    }

    var fechaError by remember { mutableStateOf(false) }
    var alturaError by remember { mutableStateOf(false) }
    var pesoError by remember { mutableStateOf(false) }
    var objetivoError by remember { mutableStateOf(false) }

    val objetivos = listOf(
        strings.goalMaintenance,    // 0 -> Mantenimiento
        strings.goalLoseWeight,     // 1 -> Pérdida de peso
        strings.goalBuildMuscle,    // 2 -> Ganancia muscular
        strings.goalPerformance     // 3 -> Rendimiento
    )
    var dropdownExpanded by remember { mutableStateOf(false) }

    fun validateForm(): Boolean {
        val rawDate = fechaNacimientoValue.text
        val fechaRegex = if (isSpanish) {
            Regex("""\d{2}/\d{2}/\d{4}""")
        } else {
            Regex("""\d{4}-\d{2}-\d{2}""")
        }

        var isDateValid = rawDate.matches(fechaRegex)
        
        if (isDateValid) {
            try {
                val birthDate = if (isSpanish) {
                    val parts = rawDate.split("/")
                    LocalDate.of(parts[2].toInt(), parts[1].toInt(), parts[0].toInt())
                } else {
                    LocalDate.parse(rawDate)
                }
                val age = Period.between(birthDate, LocalDate.now()).years
                if (age !in 14..100) {
                    isDateValid = false
                }
            } catch (e: Exception) {
                isDateValid = false
            }
        }

        // Normalizar altura y peso (reemplazar coma por punto)
        val normalizedAltura = altura.replace(',', '.')
        val normalizedPeso = peso.replace(',', '.')

        val h = normalizedAltura.toDoubleOrNull()
        val isAlturaValid = h != null && h in 130.0..230.0

        val w = normalizedPeso.toDoubleOrNull()
        val isPesoValid = w != null && w in 40.0..200.0

        val isObjetivoValid = objetivo.isNotBlank()

        // Actualizar estados para la UI
        fechaError = !isDateValid
        alturaError = !isAlturaValid
        pesoError = !isPesoValid
        objetivoError = !isObjetivoValid

        return isDateValid && isAlturaValid && isPesoValid && isObjetivoValid
    }

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

        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 4.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Atrás",
                tint = Color.White
            )
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
                        .padding(horizontal = 36.dp, vertical = 26.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // SECCIÓN FOTO DE PERFIL
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .background(colors.fieldIndicator.copy(alpha = 0.1f))
                            .border(2.dp, colors.accent, CircleShape)
                            .clickable { /* Opciones adicionales si quieres */ },
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            isDefaultPicture -> {
                                AsyncImage(
                                    model = "${BuildConfig.BACKEND_BASE_URL}images/users/default/user.jpg",
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            pictureUri != null -> {
                                AsyncImage(
                                    model = pictureUri,
                                    contentDescription = "Profile Picture",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            pictureBitmap != null -> {
                                Image(
                                    bitmap = pictureBitmap!!.asImageBitmap(),
                                    contentDescription = "Profile Picture",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            !socialPictureUrl.isNullOrEmpty() -> {
                                AsyncImage(
                                    model = socialPictureUrl,
                                    contentDescription = "Profile Picture",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            else -> {
                                AsyncImage(
                                    model = "${BuildConfig.BACKEND_BASE_URL}images/users/default/user.jpg",
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.padding(top = 12.dp, bottom = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        IconButton(onClick = { galleryLauncher.launch("image/*") }) {
                            Icon(Icons.Default.PhotoLibrary, contentDescription = "Gallery", tint = colors.accent)
                        }
                        IconButton(onClick = { cameraLauncher.launch(null) }) {
                            Icon(Icons.Default.AddAPhoto, contentDescription = "Camera", tint = colors.accent)
                        }
                        IconButton(onClick = { 
                            isDefaultPicture = true
                            pictureUri = null
                            pictureBitmap = null
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Remove Photo", tint = Color.Gray)
                        }
                    }

                    FechaNacimientoField(
                        label = strings.birthDate,
                        formatHint = when {
                            fechaNacimientoValue.text.isBlank() -> strings.requiredField
                            !fechaNacimientoValue.text.matches(if (isSpanish) Regex("""\d{2}/\d{2}/\d{4}""") else Regex("""\d{4}-\d{2}-\d{2}""")) -> strings.invalidDate
                            fechaError -> strings.invalidAge
                            else -> strings.invalidDate
                        },
                        fechaValue = fechaNacimientoValue,
                        onFechaValueChange = { 
                            fechaNacimientoValue = it
                            fechaError = false 
                        },
                        isError = fechaError,
                        isSpanish = isSpanish
                    )

                    Spacer(Modifier.height(18.dp))

                    UnderlineLabeledField(
                        label = strings.height,
                        value = altura,
                        onValueChange = { altura = it; alturaError = false },
                        placeholder = "185",
                        isError = alturaError,
                        errorText = if (altura.isBlank()) strings.requiredField else strings.invalidHeight,
                        keyboardType = KeyboardType.Number
                    )

                    Spacer(Modifier.height(18.dp))

                    UnderlineLabeledField(
                        label = strings.weight,
                        value = peso,
                        onValueChange = { peso = it; pesoError = false },
                        placeholder = "79",
                        isError = pesoError,
                        errorText = if (peso.isBlank()) strings.requiredField else strings.invalidWeight,
                        keyboardType = KeyboardType.Number
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
                                
                                val dateToSubmit = if (isSpanish) {
                                    // Convert dd/mm/yyyy to yyyy-mm-dd
                                    val parts = fechaNacimientoValue.text.split("/")
                                    if (parts.size == 3) "${parts[2]}-${parts[1]}-${parts[0]}" else fechaNacimientoValue.text
                                } else {
                                    fechaNacimientoValue.text
                                }

                                val imageFile = when {
                                    pictureBitmap != null -> createTempFile(context, pictureBitmap!!)
                                    pictureUri != null -> uriToFile(context, pictureUri!!)
                                    else -> null
                                }

                                val finalHeight = altura.replace(',', '.').toDouble()
                                val finalWeight = peso.replace(',', '.').toDouble()

                                registerViewModel.register(
                                    username = username,
                                    name = fullName,
                                    password = password.trim(),
                                    birthdate = dateToSubmit,
                                    email = email,
                                    height = finalHeight,
                                    weight = finalWeight,
                                    objective = objetivoValue,
                                    oauth = registerViewModel.socialUserData?.oauth,
                                    pictureFile = imageFile,
                                    pictureUrl = when {
                                        isDefaultPicture -> "default"
                                        imageFile == null -> socialPictureUrl
                                        else -> null
                                    }
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

                    Spacer(Modifier.height(24.dp))

                    Text(strings.alreadyHaveAccount, fontSize = 11.sp, color = colors.textSecondary)
                    TextButton(onClick = onLogin) {
                        Text(strings.loginLink, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun FechaNacimientoField(
    label: String,
    formatHint: String,
    fechaValue: TextFieldValue,
    onFechaValueChange: (TextFieldValue) -> Unit,
    isError: Boolean,
    isSpanish: Boolean
) {
    val separator = if (isSpanish) "/" else "-"
    val placeholder = if (isSpanish) "dd/mm/yyyy" else "yyyy-mm-dd"

    UnderlineLabeledFieldValue(
        label = label,
        value = fechaValue,
        onValueChange = { newValue ->
            val digits = newValue.text.filter { it.isDigit() }
            val isDeleting = newValue.text.length < fechaValue.text.length
            val formatted = StringBuilder()

            for (i in digits.indices) {
                if (i >= 8) break
                formatted.append(digits[i])
                if (isSpanish) {
                    if (i == 1 || i == 3) {
                        // Eager separator: add it if there's more text OR if we just finished the block and NOT deleting
                        if (i < digits.length - 1 || (!isDeleting && i == digits.length - 1)) {
                            formatted.append(separator)
                        }
                    }
                } else {
                    if (i == 3 || i == 5) {
                        if (i < digits.length - 1 || (!isDeleting && i == digits.length - 1)) {
                            formatted.append(separator)
                        }
                    }
                }
            }

            val finalStr = formatted.toString()
            onFechaValueChange(
                TextFieldValue(
                    text = finalStr,
                    selection = TextRange(finalStr.length)
                )
            )
        },
        placeholder = placeholder,
        isError = isError,
        errorText = formatHint,
        keyboardType = KeyboardType.Number
    )
}

@Composable
fun UnderlineLabeledFieldValue(
    label: String,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    placeholder: String,
    isError: Boolean = false,
    errorText: String? = null,
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
        modifier = Modifier.fillMaxWidth(),
        textStyle = androidx.compose.ui.text.TextStyle(
            color = colors.textPrimary,
            fontSize = 16.sp
        ),
        placeholder = {
            Text(text = placeholder, color = colors.textSecondary.copy(alpha = 0.5f))
        },
        isError = isError,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            errorContainerColor = Color.Transparent,
            focusedIndicatorColor = colors.accent,
            unfocusedIndicatorColor = colors.fieldIndicator.copy(alpha = 0.5f),
            errorIndicatorColor = Color.Red
        )
    )

    if (isError && errorText != null) {
        Text(
            text = errorText,
            color = Color.Red,
            fontSize = 11.sp,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

private fun createTempFile(context: android.content.Context, bitmap: Bitmap): File {
    val file = File(context.cacheDir, "profile_picture_${System.currentTimeMillis()}.jpg")
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
    file.writeBytes(outputStream.toByteArray())
    return file
}

private fun uriToFile(context: android.content.Context, uri: Uri): File? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, "profile_picture_${System.currentTimeMillis()}.jpg")
        file.writeBytes(inputStream?.readBytes() ?: return null)
        file
    } catch (e: Exception) {
        null
    }
}
