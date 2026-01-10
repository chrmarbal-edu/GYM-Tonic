package edu.gymtonic_app.ui.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import edu.gymtonic_app.ui.components.screens.GymTonicLoginScreen
import edu.gymtonic_app.ui.components.screens.LoginFormScreen

@Composable
fun Navigation() {
    val navController: NavHostController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.WELCOME
    ) {
        composable(Routes.WELCOME) {
            GymTonicLoginScreen(
                onLogin = { navController.navigate(Routes.LOGIN_FORM) },
                onRegister = { /* si tienes register route: navController.navigate(...) */ },
                onGoogle = { },
                onFacebook = { }
            )
        }

        composable(Routes.LOGIN_FORM) {
            LoginFormScreen(
                onEnter = {
                    // cuando tengas HOME:
                    // navController.navigate(Routes.HOME)
                    // de momento, para probar puedes volver:
                    // navController.popBackStack()
                },
                onRegister = { /* navController.navigate("register") */ },
                onForgotPassword = { /* navController.navigate("forgot") */ }
            )
        }
    }
}
