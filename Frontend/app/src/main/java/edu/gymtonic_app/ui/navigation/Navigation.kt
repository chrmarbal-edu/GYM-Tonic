package edu.gymtonic_app.ui.navigation

import edu.gymtonic_app.ui.screens.RegisterScreen
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import edu.gymtonic_app.ui.screens.FullBodyScreen
import edu.gymtonic_app.ui.screens.GymTonicLoginScreen
import edu.gymtonic_app.ui.screens.LoginFormScreen
import edu.gymtonic_app.ui.screens.MainViewScreen
import edu.gymtonic_app.ui.screens.TrainingScreen
import edu.gymtonic_app.viewmodel.LoginState
import edu.gymtonic_app.viewmodel.LoginViewModel
import edu.gymtonic_app.viewmodel.RegisterViewModel

@Composable
fun Navigation(navController: NavHostController, snackbarHostState: SnackbarHostState) {
    val loginViewModel: LoginViewModel = viewModel()
    val registerViewModel: RegisterViewModel = viewModel()

    // Determina la pantalla inicial según el loginState
    val startRoute = when(loginViewModel.loginState.collectAsState().value) {
        is LoginState.Success -> Routes.HOME
        else -> Routes.WELCOME
    }

    NavHost(
        navController = navController,
        startDestination = startRoute
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
                onRegister = { navController.navigate(Routes.REGISTER) },
                onForgotPassword = { },
                loginViewModel = loginViewModel,
                onLoginSuccess = { navController.navigate(Routes.HOME) }
            )
        }

        // ✅ Paso 1 del registro
        composable(Routes.REGISTER) {
            RegisterScreen(
                navController = navController,
                registerViewModel = registerViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.HOME) {
            MainViewScreen(
                onOpenTraining = {navController.navigate(Routes.TRAINING) },
                onOpenTechnogym = { },
                onOpenMusic = { },
                onOpenDiscounts = { },
                onOpenCoach = { },
                onOpenFindGym = { },
                onOpenClientArea = { },
                onOpenQr = { },
                onInviteFriend = { },
                onOpenBookings = { },
                onOpenWhatsapp = { },
                onOpenInstagram = { }
            )
        }

        composable(Routes.TRAINING) {
            TrainingScreen(
                onBack = {navController.popBackStack()},
                onSelect =  {navController.navigate(Routes.EXERCISES)}
            )
        }

        composable(Routes.EXERCISES)  {
            FullBodyScreen(
                onBack = {navController.popBackStack()}
            )
        }
    }
}
