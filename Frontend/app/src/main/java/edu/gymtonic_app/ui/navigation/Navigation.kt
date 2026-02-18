package edu.gymtonic_app.ui.navigation

import RegisterScreen
import RegisterScreen2
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import edu.gymtonic_app.ui.components.screens.GymTonicLoginScreen
import edu.gymtonic_app.ui.components.screens.LoginFormScreen
import edu.gymtonic_app.viewmodel.LoginViewModel

@Composable
fun Navigation(navController: NavHostController, snackbarHostState: SnackbarHostState) {
    val loginViewModel: LoginViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Routes.WELCOME
    ) {
        composable(Routes.WELCOME) {
            GymTonicLoginScreen(
                onLogin = { navController.navigate(Routes.LOGIN_FORM) },
                onRegister = { navController.navigate(Routes.REGISTER) },
                onGoogle = { },
                onFacebook = { }
            )
        }

        composable(Routes.LOGIN_FORM) {
            LoginFormScreen(
                onEnter = {
                    // cuando tengas HOME:
                    // navController.navigate(Routes.HOME)
                },
                onRegister = { navController.navigate(Routes.REGISTER) },
                onForgotPassword = { },
                loginViewModel = loginViewModel,
                onLoginSuccess =
            )
        }

        // ✅ Paso 1 del registro
        composable(Routes.REGISTER) {
            RegisterScreen(
                onNext = { navController.navigate(Routes.REGISTER2) },
                onBack = { navController.popBackStack() }
            )
        }

        //  Paso 2 del registro
        composable(Routes.REGISTER2) {
            RegisterScreen2(
                onEnter = {
                    // De momento te llevo a WELCOME (cámbialo por HOME cuando lo tengas)
                    navController.navigate(Routes.WELCOME) {
                        popUpTo(Routes.REGISTER) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
    }
}
