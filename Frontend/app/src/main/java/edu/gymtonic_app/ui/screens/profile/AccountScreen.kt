package edu.gymtonic_app.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.gymtonic_app.R
import edu.gymtonic_app.ui.components.BottomNavItem
import edu.gymtonic_app.ui.i18n.LocalStrings
import edu.gymtonic_app.ui.screens.exercise.TrainingShellScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountScreen(
    onBack: () -> Unit,
    onOpenHome: () -> Unit,
    onOpenTraining: () -> Unit,
    onOpenChallenges: () -> Unit,
    onOpenProfile: () -> Unit,
) {
    val strings = LocalStrings.current

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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item {
                AccountSectionCard(title = strings.changePassword) {
                    var currentPassword by remember { mutableStateOf("") }
                    var newPassword by remember { mutableStateOf("") }
                    var confirmPassword by remember { mutableStateOf("") }

                    OutlinedTextField(
                        value = currentPassword,
                        onValueChange = { currentPassword = it },
                        label = { Text(strings.currentPassword) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF3B4EE8),
                            unfocusedBorderColor = Color(0xFFC4C4C4)
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text(strings.newPassword) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF3B4EE8),
                            unfocusedBorderColor = Color(0xFFC4C4C4)
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text(strings.confirmNewPassword) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF3B4EE8),
                            unfocusedBorderColor = Color(0xFFC4C4C4)
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF3B4EE8),
                            contentColor = Color.White
                        )
                    ) {
                        Text(strings.saveChanges, fontWeight = FontWeight.Bold)
                    }
                }
            }

            item {
                AccountSectionCard(title = strings.changeEmail) {
                    var newEmail by remember { mutableStateOf("") }
                    var passwordConfirm by remember { mutableStateOf("") }

                    OutlinedTextField(
                        value = newEmail,
                        onValueChange = { newEmail = it },
                        label = { Text(strings.newEmail) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF3B4EE8),
                            unfocusedBorderColor = Color(0xFFC4C4C4)
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = passwordConfirm,
                        onValueChange = { passwordConfirm = it },
                        label = { Text(strings.confirmPassword) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF3B4EE8),
                            unfocusedBorderColor = Color(0xFFC4C4C4)
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF3B4EE8),
                            contentColor = Color.White
                        )
                    ) {
                        Text(strings.saveEmail, fontWeight = FontWeight.Bold)
                    }
                }
            }

            item {
                AccountSectionCard(title = strings.twoFactorAuth) {
                    var twoFaEnabled by remember { mutableStateOf(false) }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (twoFaEnabled) strings.twoFaEnabled else strings.twoFaDisabled,
                            fontSize = 15.sp,
                            color = Color(0xFF1D1D1D)
                        )
                        Switch(
                            checked = twoFaEnabled,
                            onCheckedChange = { twoFaEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFF3B4EE8),
                                uncheckedThumbColor = Color(0xFFC4C4C4),
                                checkedTrackColor = Color(0xFFA8B2FF),
                                uncheckedTrackColor = Color(0xFFE0E0E0)
                            )
                        )
                    }
                    Text(
                        text = strings.twoFaDescription,
                        fontSize = 12.sp,
                        color = Color(0xFF5D6270),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            item {
                AccountSectionCard(title = strings.connectedAccounts) {
                    ConnectedAccountRow(
                        iconRes = R.drawable.google_logo,
                        name = "Google",
                        isConnected = true,
                        connectedLabel = strings.accountConnected,
                        disconnectedLabel = strings.accountDisconnected,
                        connectButton = strings.connectButton,
                        disconnectButton = strings.disconnectButton,
                        onToggleConnect = { }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ConnectedAccountRow(
                        iconRes = R.drawable.facebook_logo,
                        name = "Facebook",
                        isConnected = false,
                        connectedLabel = strings.accountConnected,
                        disconnectedLabel = strings.accountDisconnected,
                        connectButton = strings.connectButton,
                        disconnectButton = strings.disconnectButton,
                        onToggleConnect = { }
                    )
                }
            }

            item {
                AccountSectionCard(title = strings.deleteAccount, color = Color(0xFFFDE9E9)) {
                    var confirmDeletionText by remember { mutableStateOf("") }

                    Text(
                        text = strings.deleteAccountWarning,
                        color = Color(0xFFB00020),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = confirmDeletionText,
                        onValueChange = { confirmDeletionText = it },
                        label = { Text(strings.typeDeleteConfirmLabel) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFB00020),
                            unfocusedBorderColor = Color(0xFFC4C4C4)
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { },
                        enabled = confirmDeletionText == strings.typeDeleteConfirmWord,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFB00020),
                            contentColor = Color.White
                        )
                    ) {
                        Text(strings.deleteAccountButton, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun AccountSectionCard(
    title: String,
    color: Color = Color(0xFF8B8EEA),
    content: @Composable () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = color,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                color = Color(0xFF1D1D1D),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun ConnectedAccountRow(
    iconRes: Int,
    name: String,
    isConnected: Boolean,
    connectedLabel: String,
    disconnectedLabel: String,
    connectButton: String,
    disconnectButton: String,
    onToggleConnect: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFE9EBF2),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(iconRes),
                    contentDescription = name,
                    modifier = Modifier.size(30.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF1F2330)
                    )
                    Text(
                        text = if (isConnected) connectedLabel else disconnectedLabel,
                        fontSize = 11.sp,
                        color = if (isConnected) Color(0xFF388E3C) else Color(0xFFD32F2F)
                    )
                }
            }
            Button(
                onClick = onToggleConnect,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isConnected) Color(0xFFD32F2F) else Color(0xFF3B4EE8),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(if (isConnected) disconnectButton else connectButton, fontSize = 11.sp)
            }
        }
    }
}
