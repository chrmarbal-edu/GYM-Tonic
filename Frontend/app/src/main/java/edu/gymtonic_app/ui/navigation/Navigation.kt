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
import edu.gymtonic_app.data.remote.model.auth.SessionManager
import edu.gymtonic_app.data.remote.model.auth.sessionDataStore
import edu.gymtonic_app.ui.components.BottomNavItem
import edu.gymtonic_app.ui.screens.routines.RoutineCatalogScreen
import edu.gymtonic_app.ui.screens.login.GymTonicLoginScreen
import edu.gymtonic_app.ui.screens.login.LoginFormScreen
import edu.gymtonic_app.ui.screens.home.MainViewScreen
import edu.gymtonic_app.ui.screens.missions.WeekChallengesScreen
import edu.gymtonic_app.ui.screens.exercise.ExerciseDetailScreen
import edu.gymtonic_app.ui.screens.profile.ProfileScreen
import edu.gymtonic_app.ui.screens.exercise.TrainingShellScreen
import edu.gymtonic_app.ui.screens.exercise.TrainingScreen
import edu.gymtonic_app.ui.viewmodel.HomeViewModel
import edu.gymtonic_app.ui.viewmodel.LoginViewModel
import edu.gymtonic_app.ui.viewmodel.RegisterViewModel
import edu.gymtonic_app.ui.viewmodel.TrainingScreenViewModel
import edu.gymtonic_app.ui.viewmodel.WeekChallengesViewModel

@Composable
@Suppress("UNUSED_PARAMETER")
fun Navigation(navController: NavHostController, snackbarHostState: SnackbarHostState) {

    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context.sessionDataStore) }

    val loginViewModel: LoginViewModel = viewModel()
    val registerViewModel: RegisterViewModel = viewModel()
    val homeViewModel : HomeViewModel = viewModel()
    val trainingViewModel: TrainingScreenViewModel = viewModel()
    val weekChallengesViewModel: WeekChallengesViewModel = viewModel()

    val sessionState = sessionManager.sessionFlow.collectAsState(initial = null)
    val trainingUiState = trainingViewModel.uiState.collectAsState()
    val weekUiState = weekChallengesViewModel.uiState.collectAsState()

    val onOpenHomeGlobal = {
        navController.navigate(Routes.HOME) {
            popUpTo(Routes.HOME) { inclusive = false }
            launchSingleTop = true
        }
    }

    val onOpenTrainingGlobal = {
        navController.navigate(Routes.TRAINING) {
            launchSingleTop = true
        }
    }

    val onOpenChallengesGlobal = {
        navController.navigate(Routes.WEEK) {
            launchSingleTop = true
        }
    }

    val onOpenProfileGlobal = {
        navController.navigate(Routes.PROFILE) {
            launchSingleTop = true
        }
    }

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
                onOpenClientArea = { navController.navigate(Routes.PROFILE) },
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
                onOpenProfile = onOpenProfileGlobal,
                onShowMoreCalendar = { },
                goals = weekUiState.value.goals,
                calendarDays = weekUiState.value.calendarDays,
                achievedLabel = weekUiState.value.achievedLabel,
                isRefreshing = weekUiState.value.isRefreshing,
                onRefresh = { weekChallengesViewModel.refreshWeekGoals() }
            )
        }

        composable(Routes.TRAINING) {
            TrainingShellScreen(
                title = "Entrenamientos",
                onBack = { navController.popBackStack() },
                showBottomBar = true,
                selectedBottomItem = BottomNavItem.TRAINING,
                onOpenHome = onOpenHomeGlobal,
                onOpenTraining = onOpenTrainingGlobal,
                onOpenChallenges = onOpenChallengesGlobal,
                onOpenProfile = onOpenProfileGlobal
            ) {
                TrainingScreen(
                    // Cada card envía el routineId (backend) y se construye una ruta dinámica a screens/routines.
                    onSelect = { routineId -> navController.navigate(Routes.routine(routineId)) },
                    categories = trainingUiState.value.categories,
                    isRefreshing = trainingUiState.value.isRefreshing,
                    onRefresh = { trainingViewModel.refreshCategories() }
                )
            }
        }

        composable(
            route = Routes.ROUTINE_DETAIL,
            arguments = listOf(navArgument("routineId") { type = NavType.StringType })
        ) { backStackEntry ->
            val routineId = backStackEntry.arguments?.getString("routineId").orEmpty()

            RoutineCatalogScreen(
                routineId = routineId,
                onBack = { navController.popBackStack() },
                onExerciseClick = { exerciseId ->
                    navController.navigate(Routes.exercise(exerciseId))
                },
                onOpenHome = onOpenHomeGlobal,
                onOpenTraining = onOpenTrainingGlobal,
                onOpenChallenges = onOpenChallengesGlobal,
                onOpenProfile = onOpenProfileGlobal
            )
        }

        composable(Routes.EXERCISES) {
            RoutineCatalogScreen(
                routineId = "fullbody",
                onBack = { navController.popBackStack() },
                onExerciseClick = { exerciseId ->
                    navController.navigate(Routes.exercise(exerciseId))
                },
                onOpenHome = onOpenHomeGlobal,
                onOpenTraining = onOpenTrainingGlobal,
                onOpenChallenges = onOpenChallengesGlobal,
                onOpenProfile = onOpenProfileGlobal
            )
        }

        composable(
            route = Routes.EXERCISE_DETAIL,
            arguments = listOf(navArgument("exerciseId") { type = NavType.StringType })
        ) { backStackEntry ->
            val exerciseId = backStackEntry.arguments?.getString("exerciseId").orEmpty()

            ExerciseDetailScreen(
                exerciseId = exerciseId,
                onBack = { navController.popBackStack() },
                onOpenHome = onOpenHomeGlobal,
                onOpenTraining = onOpenTrainingGlobal,
                onOpenChallenges = onOpenChallengesGlobal,
                onOpenProfile = onOpenProfileGlobal,
                showBottomBar = false
            )
        }

        composable(Routes.PROFILE) {
            ProfileScreen(
                onBack = { navController.popBackStack() },
                onOpenHome = onOpenHomeGlobal,
                onOpenTraining = onOpenTrainingGlobal,
                onOpenChallenges = onOpenChallengesGlobal,
                onOpenProfile = onOpenProfileGlobal,
                onOpenWeek = { navController.navigate(Routes.WEEK) },
                onOpenRoutine = { routineId ->
                    navController.navigate(Routes.routine(routineId))
                },
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
                }
            )
        }
    }
}
