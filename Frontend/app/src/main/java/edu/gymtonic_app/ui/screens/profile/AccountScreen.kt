@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)

package edu.gymtonic_app.ui.screens.profile

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import edu.gymtonic_app.BuildConfig
import edu.gymtonic_app.ui.components.BottomNavItem
import edu.gymtonic_app.ui.components.ObserveToastMessage
import edu.gymtonic_app.ui.components.ToastErrorRetryContent
import edu.gymtonic_app.ui.i18n.LocalStrings
import edu.gymtonic_app.ui.screens.exercise.TrainingShellScreen
import edu.gymtonic_app.ui.theme.LocalColors
import edu.gymtonic_app.ui.viewmodel.AccountUiState
import edu.gymtonic_app.ui.viewmodel.AccountViewModel
import java.io.ByteArrayOutputStream
import java.io.File

@Composable
fun AccountScreen(
    onBack: () -> Unit,
    onOpenHome: () -> Unit,
    onOpenTraining: () -> Unit,
    onOpenChallenges: () -> Unit,
    onOpenProfile: () -> Unit,
    onDeleted: () -> Unit,
    viewModel: AccountViewModel = viewModel()
) {
    val strings = LocalStrings.current
    val colors = LocalColors.current
    val uiState by viewModel.uiState.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()
    val context = LocalContext.current

    ObserveToastMessage(message = toastMessage, onConsumed = { viewModel.clearToastMessage() })
    ObserveToastMessage(message = (uiState as? AccountUiState.Error)?.message)

    TrainingShellScreen(
        title = strings.accountTitle,
        onBack = onBack,
        showBottomBar = true,
        selectedBottomItem = BottomNavItem.PROFILE,
        onOpenHome = onOpenHome,
        onOpenTraining = onOpenTraining,
        onOpenChallenges = onOpenChallenges,
        onOpenProfile = onOpenProfile
    ) {
        when (val state = uiState) {
            AccountUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is AccountUiState.Error -> {
                ToastErrorRetryContent(
                    retryLabel = strings.discountsRetry,
                    onRetry = { viewModel.loadUser() }
                )
            }
            is AccountUiState.Success -> {
                val user = state.user
                
                var username by remember(user.userId) { mutableStateOf(user.userUsername) }
                var password by remember { mutableStateOf("") }
                var height by remember(user.userId) { mutableStateOf(user.userHeight.toInt().toString()) }
                var weight by remember(user.userId) { mutableStateOf(user.userWeight.toInt().toString()) }
                
                var pictureBitmap by remember { mutableStateOf<Bitmap?>(null) }
                var pictureUri by remember { mutableStateOf<Uri?>(null) }
                // isDefaultPicture se activa si el usuario pulsa "Borrar"
                var isDefaultPicture by remember { mutableStateOf(user.userPicture == "default" || user.userPicture.isNullOrEmpty()) }
                
                var showOptionsDialog by remember { mutableStateOf(false) }
                var showChangeSourceDialog by remember { mutableStateOf(false) }

                val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
                    if (uri != null) {
                        pictureUri = uri
                        pictureBitmap = null
                        isDefaultPicture = false
                    }
                }
                val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
                    if (bitmap != null) {
                        pictureBitmap = bitmap
                        pictureUri = null
                        isDefaultPicture = false
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    contentPadding = PaddingValues(vertical = 20.dp)
                ) {
                    // 1. FOTO DE PERFIL
                    item {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            Box(
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(CircleShape)
                                    .background(colors.surfaceAccent)
                                    .border(3.dp, colors.accent, CircleShape)
                                    .combinedClickable(
                                        onClick = { showOptionsDialog = true },
                                        onLongClick = { showOptionsDialog = true }
                                    ),
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
                                            contentDescription = null,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                    pictureBitmap != null -> {
                                        Image(
                                            bitmap = pictureBitmap!!.asImageBitmap(),
                                            contentDescription = null,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                    else -> {
                                        val picPath = if (user.userPicture.isNullOrEmpty() || user.userPicture == "default") 
                                                        "images/users/default/user.jpg" 
                                                      else user.userPicture
                                        
                                        val fullUrl = when {
                                            picPath.startsWith("http") -> picPath
                                            picPath.startsWith("/") -> "${BuildConfig.BACKEND_BASE_URL}${picPath.removePrefix("/")}"
                                            else -> "${BuildConfig.BACKEND_BASE_URL}$picPath"
                                        }

                                        AsyncImage(
                                            model = fullUrl,
                                            contentDescription = null,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "Manten pulsado para editar",
                                fontSize = 11.sp,
                                color = colors.textSecondary,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }

                    // 2. DATOS EDITABLES
                    item {
                        AccountSectionCard(title = "Datos editables") {
                            EditableField(label = strings.usernameField, value = username, onValueChange = { username = it })
                            Spacer(Modifier.height(12.dp))
                            EditableField(label = strings.password, value = password, onValueChange = { password = it }, isPassword = true)
                            Spacer(Modifier.height(12.dp))
                            EditableField(label = strings.height, value = height, onValueChange = { height = it }, isNumber = true)
                            Spacer(Modifier.height(12.dp))
                            EditableField(label = strings.weight, value = weight, onValueChange = { weight = it }, isNumber = true)
                            
                            Spacer(Modifier.height(20.dp))
                            
                            Button(
                                onClick = {
                                    val imgFile = when {
                                        pictureBitmap != null -> createTempFile(context, pictureBitmap!!)
                                        pictureUri != null -> uriToFile(context, pictureUri!!)
                                        else -> null
                                    }
                                    viewModel.updateAccount(
                                        username = username,
                                        password = password,
                                        height = height.toDoubleOrNull(),
                                        weight = weight.toDoubleOrNull(),
                                        pictureFile = imgFile,
                                        isDefaultPicture = isDefaultPicture
                                    )
                                },
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = colors.accent)
                            ) {
                                Text("GUARDAR CAMBIOS", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // 3. DATOS DE LECTURA
                    item {
                        AccountSectionCard(title = "Información del perfil") {
                            val objetivos = listOf(strings.goalMaintenance, strings.goalLoseWeight, strings.goalBuildMuscle, strings.goalPerformance)
                            val objetivoText = if (user.userObjetive in objetivos.indices) objetivos[user.userObjetive] else "N/A"
                            
                            val formattedDate = try {
                                val parts = user.userBirthdate.split("-")
                                if (parts.size == 3) "${parts[2]}/${parts[1]}/${parts[0]}" else user.userBirthdate
                            } catch (e: Exception) { user.userBirthdate }

                            ReadOnlyField(label = strings.fullName, value = user.userName)
                            ReadOnlyField(label = strings.email, value = user.userEmail)
                            ReadOnlyField(label = strings.birthDate, value = formattedDate)
                            ReadOnlyField(label = "Objetivo", value = objetivoText)
                            ReadOnlyField(label = "Puntos", value = "${user.userPoints} pts")
                        }
                    }

                    // 4. ELIMINAR CUENTA
                    item {
                        AccountSectionCard(title = strings.deleteAccount, isDanger = true) {
                            var confirmText by remember { mutableStateOf("") }
                            Text(strings.deleteAccountWarning, color = Color.Red, fontSize = 12.sp)
                            Spacer(Modifier.height(10.dp))
                            OutlinedTextField(
                                value = confirmText,
                                onValueChange = { confirmText = it },
                                label = { Text(strings.typeDeleteConfirmLabel) },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(Modifier.height(12.dp))
                            Button(
                                onClick = { viewModel.deleteAccount(onDeleted) },
                                enabled = confirmText == strings.typeDeleteConfirmWord,
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(strings.deleteAccountButton, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // DIALOGOS DE OPCIONES DE FOTO
                if (showOptionsDialog) {
                    AlertDialog(
                        onDismissRequest = { showOptionsDialog = false },
                        title = { Text("Foto de perfil") },
                        text = { Text("¿Qué deseas hacer con tu foto?") },
                        confirmButton = {
                            TextButton(onClick = { 
                                showOptionsDialog = false
                                showChangeSourceDialog = true 
                            }) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CameraAlt, null, modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text("Cambiar")
                                }
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { 
                                showOptionsDialog = false
                                isDefaultPicture = true
                                pictureBitmap = null
                                pictureUri = null
                            }) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Delete, null, modifier = Modifier.size(18.dp), tint = Color.Red)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Borrar", color = Color.Red)
                                }
                            }
                        }
                    )
                }

                if (showChangeSourceDialog) {
                    AlertDialog(
                        onDismissRequest = { showChangeSourceDialog = false },
                        title = { Text("Seleccionar origen") },
                        confirmButton = {
                            TextButton(onClick = { 
                                showChangeSourceDialog = false
                                galleryLauncher.launch("image/*") 
                            }) {
                                Icon(Icons.Default.PhotoLibrary, null)
                                Spacer(Modifier.width(8.dp))
                                Text("Galería")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { 
                                showChangeSourceDialog = false
                                cameraLauncher.launch(null)
                            }) {
                                Icon(Icons.Default.CameraAlt, null)
                                Spacer(Modifier.width(8.dp))
                                Text("Cámara")
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AccountSectionCard(
    title: String,
    isDanger: Boolean = false,
    content: @Composable () -> Unit
) {
    val colors = LocalColors.current
    val cardColor = if (isDanger) colors.surfaceDanger else colors.surfaceAccent
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = cardColor,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                color = colors.textOnAccent,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun EditableField(label: String, value: String, onValueChange: (String) -> Unit, isPassword: Boolean = false, isNumber: Boolean = false) {
    val colors = LocalColors.current
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = colors.textPrimary)
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = if (isNumber) androidx.compose.ui.text.input.KeyboardType.Number else androidx.compose.ui.text.input.KeyboardType.Text
            ),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                focusedIndicatorColor = colors.accent,
                unfocusedIndicatorColor = colors.fieldIndicator.copy(alpha = 0.5f)
            )
        )
    }
}

@Composable
private fun ReadOnlyField(label: String, value: String) {
    val colors = LocalColors.current
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Text(text = label, fontSize = 12.sp, color = colors.textSecondary)
        Text(text = value, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = colors.textPrimary)
        HorizontalDivider(modifier = Modifier.padding(top = 4.dp), color = colors.fieldIndicator.copy(alpha = 0.2f))
    }
}

private fun createTempFile(context: android.content.Context, bitmap: Bitmap): File {
    val file = File(context.cacheDir, "profile_edit_${System.currentTimeMillis()}.jpg")
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
    file.writeBytes(outputStream.toByteArray())
    return file
}

private fun uriToFile(context: android.content.Context, uri: Uri): File? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.cacheDir, "profile_edit_${System.currentTimeMillis()}.jpg")
        file.writeBytes(inputStream?.readBytes() ?: return null)
        file
    } catch (e: Exception) {
        null
    }
}
