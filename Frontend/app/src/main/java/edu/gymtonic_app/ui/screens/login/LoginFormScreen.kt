package edu.gymtonic_app.ui.screens.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.gymtonic_app.ui.components.LanguageButton
import edu.gymtonic_app.ui.components.ThemeButton
import edu.gymtonic_app.ui.i18n.LocalStrings
import edu.gymtonic_app.ui.screens.register.UnderlineTextField
import edu.gymtonic_app.ui.theme.LocalColors
import edu.gymtonic_app.ui.components.ObserveToastMessage
import edu.gymtonic_app.ui.viewmodel.LoginState
import edu.gymtonic_app.ui.viewmodel.LoginViewModel


@Composable
fun LoginFormScreen(
    loginViewModel: LoginViewModel,
    onLoginSuccess: (userRole: Int) -> Unit,
    onRegister: () -> Unit,
    onForgotPassword: () -> Unit
) {
    val strings = LocalStrings.current
    val colors = LocalColors.current
    val loginState by loginViewModel.loginState.collectAsState()

    ObserveToastMessage(
        message = (loginState as? LoginState.Error)?.message
    )

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colors.gradientColors))
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

        Surface(
            modifier = Modifier
                .padding(12.dp)
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(670.dp),
            color = colors.surfaceMain,
            shape = RoundedCornerShape(70.dp),
            shadowElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 36.dp, vertical = 46.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = strings.usernameLabel,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary
                )
                Spacer(Modifier.height(8.dp))
                UnderlineTextField(
                    value = username,
                    onValueChange = { username = it },
                    placeholder = "client1"
                )

                Spacer(Modifier.height(26.dp))

                Text(
                    text = strings.password,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary
                )
                Spacer(Modifier.height(8.dp))
                UnderlineTextField(
                    value = password,
                    onValueChange = { password = it.trim() },
                    placeholder = "••••••••",
                    isPassword = true
                )

                Spacer(Modifier.height(14.dp))

                TextButton(
                    onClick = onForgotPassword,
                    modifier = Modifier.align(Alignment.End),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(strings.forgotPassword, fontSize = 11.sp)
                }

                Spacer(Modifier.height(18.dp))

                OutlinedButton(
                    onClick = { loginViewModel.login(username, password) },
                    modifier = Modifier.width(140.dp).height(38.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    if (loginState is LoginState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = colors.accent
                        )
                    } else {
                        Text(strings.enterButton, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(Modifier.weight(1f))

                Text(strings.noAccount, fontSize = 11.sp, color = colors.textSecondary)
                TextButton(onClick = onRegister) {
                    Text(strings.signUpLink, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                if (loginState is LoginState.Success) {
                    val success = loginState as LoginState.Success
                    LaunchedEffect(success) {
                        onLoginSuccess(success.response.data?.user_role ?: 0)
                    }
                }
            }
        }
    }
}
