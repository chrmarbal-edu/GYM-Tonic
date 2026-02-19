package edu.gymtonic_app.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import edu.gymtonic_app.ui.screens.RegisterScreen
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import edu.gymtonic_app.data.remote.model.SessionManager
import edu.gymtonic_app.data.remote.model.sessionDataStore
import edu.gymtonic_app.ui.screens.FullBodyScreen
import edu.gymtonic_app.ui.screens.GymTonicLoginScreen
import edu.gymtonic_app.ui.screens.LoginFormScreen
import edu.gymtonic_app.ui.screens.MainViewScreen
import edu.gymtonic_app.ui.screens.TrainingScreen
import edu.gymtonic_app.viewmodel.HomeViewModel
import edu.gymtonic_app.viewmodel.LoginViewModel
import edu.gymtonic_app.viewmodel.RegisterViewModel

@Composable
fun Navigation(navController: NavHostController, snackbarHostState: SnackbarHostState) {

    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context.sessionDataStore) }

    val loginViewModel: LoginViewModel = viewModel()
    val registerViewModel: RegisterViewModel = viewModel()
    val homeViewModel : HomeViewModel = viewModel()

    val sessionState = sessionManager.sessionFlow.collectAsState(initial = null)

    val startRoute = when {
        sessionState.value == null -> null
        sessionState.value?.token != null -> Routes.HOME
        else -> Routes.WELCOME
    }

    // Splash mientras carga DataStore
    if (startRoute == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
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

        composable(Routes.REGISTER) {
            RegisterScreen(
                navController = navController,
                registerViewModel = registerViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.HOME) {
            MainViewScreen(
                onLogout = {
                    homeViewModel.logout()
                    navController.navigate(Routes.WELCOME)},
                onOpenTraining = { navController.navigate(Routes.TRAINING) },
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
                onBack = { navController.popBackStack() },
                onSelect = { navController.navigate(Routes.EXERCISES) }
            )
        }

        composable(Routes.EXERCISES) {
            FullBodyScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
