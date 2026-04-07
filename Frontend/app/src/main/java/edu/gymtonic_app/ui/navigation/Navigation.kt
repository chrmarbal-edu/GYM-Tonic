package edu.gymtonic_app.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import edu.gymtonic_app.ui.screens.register.RegisterScreen
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import edu.gymtonic_app.data.remote.datasource.model.Login.SessionManager
import edu.gymtonic_app.data.remote.datasource.model.Login.sessionDataStore
import edu.gymtonic_app.ui.screens.routines.FullBodyScreen
import edu.gymtonic_app.ui.screens.routines.ArmScreen
import edu.gymtonic_app.ui.screens.routines.BackScreen
import edu.gymtonic_app.ui.screens.routines.CalvesScreen
import edu.gymtonic_app.ui.screens.routines.PushScreen
import edu.gymtonic_app.ui.screens.routines.StretchScreen
import edu.gymtonic_app.ui.screens.login.GymTonicLoginScreen
import edu.gymtonic_app.ui.screens.login.LoginFormScreen
import edu.gymtonic_app.ui.screens.home.MainViewScreen
import edu.gymtonic_app.ui.screens.missions.WeekChallengesScreen
import edu.gymtonic_app.ui.screens.exercise.TrainingScreen
import edu.gymtonic_app.ui.viewmodel.HomeViewModel
import edu.gymtonic_app.ui.viewmodel.LoginViewModel
import edu.gymtonic_app.ui.viewmodel.RegisterViewModel

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
                    homeViewModel.logout(
                        onLoggedOut = {
                            navController.navigate(Routes.WELCOME) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        },
                        onError = { }
                    )
                },
                onOpenTraining = { navController.navigate(Routes.TRAINING) },
                onOpenTechnogym = { },
                onOpenDiscounts = { },
                onOpenFindGym = { },
                onOpenClientArea = { },
                onInviteFriend = { },
                onOpenMissions = { navController.navigate(Routes.WEEK) },
            )
        }

        composable(Routes.WEEK) {
            WeekChallengesScreen(
                onBack = { navController.popBackStack() },
                onOpenHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onOpenTraining = { navController.navigate(Routes.TRAINING) },
                onOpenChallenges = { },
                onOpenProfile = { },
                onShowMoreCalendar = { }
            )
        }

        composable(Routes.TRAINING) {
            TrainingScreen(
                onBack = { navController.popBackStack() },
                // Cada card envía el routineId (backend) y se construye una ruta dinámica a screens/routines.
                onSelect = { routineId -> navController.navigate(Routes.routine(routineId)) },
                onOpenHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onOpenTraining = { },
                onOpenChallenges = { navController.navigate(Routes.WEEK) },
                onOpenProfile = { }
            )
        }

        composable(
            route = Routes.ROUTINE_DETAIL,
            arguments = listOf(navArgument("routineId") { type = NavType.StringType })
        ) { backStackEntry ->
            val routineId = backStackEntry.arguments?.getString("routineId").orEmpty()

            // Este when es el punto de enlace entre IDs del backend y pantallas reales en screens/routines.
            // A medida que se creen nuevas pantallas (Espalda, Brazo, etc), se añade aquí su destino.
            when (routineId) {
                "fullbody" -> FullBodyScreen(onBack = { navController.popBackStack() })
                "back" -> BackScreen(onBack = { navController.popBackStack() })
                "push" -> PushScreen(onBack = { navController.popBackStack() })
                "stretch" -> StretchScreen(onBack = { navController.popBackStack() })
                "arm" -> ArmScreen(onBack = { navController.popBackStack() })
                "calves" -> CalvesScreen(onBack = { navController.popBackStack() })

                else -> {
                    // Fallback temporal para IDs aún no implementados en una screen específica.
                    FullBodyScreen(onBack = { navController.popBackStack() })
                }
            }
        }

        composable(Routes.EXERCISES) {
            FullBodyScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
