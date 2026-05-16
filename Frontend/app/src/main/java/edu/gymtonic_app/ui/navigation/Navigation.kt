package edu.gymtonic_app.ui.navigation

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import edu.gymtonic_app.ui.screens.register.RegisterScreen
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import edu.gymtonic_app.ui.viewmodel.LoginState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import edu.gymtonic_app.data.remote.remoteModel.auth.SessionManager
import edu.gymtonic_app.data.remote.remoteModel.auth.sessionDataStore
import edu.gymtonic_app.ui.components.BottomNavItem
import edu.gymtonic_app.ui.i18n.LocalStrings
import edu.gymtonic_app.ui.screens.exercise.ExerciseDetailScreen
import edu.gymtonic_app.ui.screens.exercise.TrainingScreen
import edu.gymtonic_app.ui.screens.exercise.TrainingShellScreen
import edu.gymtonic_app.ui.screens.home.MainViewScreen
import edu.gymtonic_app.ui.screens.login.GymTonicLoginScreen
import edu.gymtonic_app.ui.screens.login.LoginFormScreen
import edu.gymtonic_app.ui.screens.login.GoogleAuthHelper
import edu.gymtonic_app.ui.screens.missions.WeekChallengesScreen
import edu.gymtonic_app.ui.screens.profile.AccountScreen
import edu.gymtonic_app.ui.screens.profile.ProfileScreen
import edu.gymtonic_app.ui.screens.profile.SettingsScreen
import edu.gymtonic_app.ui.screens.discounts.DiscountsScreen
import edu.gymtonic_app.ui.screens.friends.FriendsScreen
import edu.gymtonic_app.ui.screens.groups.AddGroupRoutineScreen
import edu.gymtonic_app.ui.screens.groups.GroupDetailScreen
import edu.gymtonic_app.ui.screens.groups.GroupsListScreen
import edu.gymtonic_app.ui.screens.routines.CreateRoutineScreen
import edu.gymtonic_app.ui.screens.routines.RoutineCatalogScreen
import edu.gymtonic_app.ui.viewmodel.HomeViewModel
import edu.gymtonic_app.ui.viewmodel.LoginViewModel
import edu.gymtonic_app.ui.viewmodel.RegisterViewModel
import edu.gymtonic_app.ui.viewmodel.TrainingScreenViewModel
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import androidx.compose.runtime.DisposableEffect
import edu.gymtonic_app.ui.viewmodel.UserMissionsViewModel
@Composable
@Suppress("UNUSED_PARAMETER")
fun Navigation(navController: NavHostController, snackbarHostState: SnackbarHostState) {

    val strings = LocalStrings.current
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context.sessionDataStore) }
    val coroutineScope = rememberCoroutineScope()
    val googleAuthHelper = remember { GoogleAuthHelper(context) }

    val loginViewModel: LoginViewModel = viewModel()
    val registerViewModel: RegisterViewModel = viewModel()
    val homeViewModel: HomeViewModel = viewModel()

    val sessionState = sessionManager.sessionFlow.collectAsState(initial = null)

    // CONFIGURACIÓN DE FACEBOOK DIRECTA
    val callbackManager = remember { CallbackManager.Factory.create() }
    val fbLauncher = rememberLauncherForActivityResult(
        LoginManager.getInstance().createLogInActivityResultContract(callbackManager)
    ) { /* El resultado se maneja en el callback de abajo */ }

    DisposableEffect(Unit) {
        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                val token = result.accessToken.token
                Log.d("FacebookAuth", "Token obtenido: ${token.take(10)}...")
                loginViewModel.facebookLogin(token)
            }
            override fun onCancel() {
                Log.d("FacebookAuth", "Login cancelado")
            }
            override fun onError(error: FacebookException) {
                Log.e("FacebookAuth", "Error: ${error.message}")
            }
        })
        onDispose {
            LoginManager.getInstance().unregisterCallback(callbackManager)
        }
    }

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

    val authRoutes = setOf(Routes.WELCOME, Routes.LOGIN_FORM, Routes.REGISTER)

    val onLogout: () -> Unit = {
        loginViewModel.resetLoginState()
        googleAuthHelper.signOut(coroutineScope)
        homeViewModel.logout(
            onError = { message ->
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(message)
                }
            }
        )
    }

    LaunchedEffect(sessionState.value?.token) {
        val session = sessionState.value ?: return@LaunchedEffect
        if (session.token != null) return@LaunchedEffect

        val currentRoute = navController.currentDestination?.route ?: return@LaunchedEffect
        if (currentRoute in authRoutes) return@LaunchedEffect

        loginViewModel.resetLoginState()
        googleAuthHelper.signOut(coroutineScope)
        navController.navigate(Routes.WELCOME) {
            popUpTo(navController.graph.id) {
                inclusive = true
            }
            launchSingleTop = true
        }
    }

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
            val loginState by loginViewModel.loginState.collectAsState()

            LaunchedEffect(loginState) {
                when (loginState) {
                    is LoginState.Success -> {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.WELCOME) { inclusive = true }
                        }
                    }
                    is LoginState.NeedsRegistration -> {
                        val socialData = (loginState as LoginState.NeedsRegistration).socialData
                        registerViewModel.prepareSocialRegistration(
                            name = socialData.username,
                            email = socialData.email,
                            picture = socialData.picture,
                            oauth = socialData.oauth
                        )
                        navController.navigate(Routes.REGISTER)
                    }
                    is LoginState.Error -> {
                        snackbarHostState.showSnackbar((loginState as LoginState.Error).message)
                    }
                    else -> {}
                }
            }

            GymTonicLoginScreen(
                onLogin = { navController.navigate(Routes.LOGIN_FORM) },
                onRegister = { navController.navigate(Routes.REGISTER) },
                onGoogle = {
                    googleAuthHelper.signInWithGoogle(
                        scope = coroutineScope,
                        onSuccess = { credential ->
                            loginViewModel.googleLogin(credential.idToken)
                        },
                        onError = { message ->
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Error Google: $message")
                            }
                            Log.e("Navigation", "Google Sign In Error: $message")
                        }
                    )
                },
                onFacebook = {
                    fbLauncher.launch(listOf("public_profile", "email"))
                }
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
                onLogout = onLogout,
                onOpenTraining = { navController.navigate(Routes.TRAINING) },
                onCreateRoutine = { navController.navigate(Routes.CREATE_ROUTINE) },
                onOpenTechnogym = { },
                onOpenDiscounts = { navController.navigate(Routes.DISCOUNTS) },
                onOpenClientArea = { navController.navigate(Routes.PROFILE) },
                onOpenGroup = { navController.navigate(Routes.GROUPS) },
                onOpenMissions = { navController.navigate(Routes.WEEK) },
                onOpenFriends = { navController.navigate(Routes.FRIENDS) },
            )
        }

        composable(Routes.WEEK) {
            val userMissionsViewModel: UserMissionsViewModel = viewModel()
            val weekUiState = userMissionsViewModel.uiState.collectAsState()
            val week = weekUiState.value
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
                goals = week.goals,
                calendarDays = week.calendarDays,
                calendarYear = week.calendarYear,
                calendarMonth = week.calendarMonth,
                achievedLabel = "${week.achievedCount}/${week.totalCount} ${strings.achieved}",
                isRefreshing = week.isRefreshing,
                onRefresh = { userMissionsViewModel.refreshUserMissions() }
            )
        }

        composable(Routes.TRAINING) {
            val trainingViewModel: TrainingScreenViewModel = viewModel()
            val trainingUiState = trainingViewModel.uiState.collectAsState()
            TrainingShellScreen(
                title = strings.trainingTitle,
                onBack = { navController.popBackStack() },
                showBottomBar = true,
                selectedBottomItem = BottomNavItem.TRAINING,
                onOpenHome = onOpenHomeGlobal,
                onOpenTraining = onOpenTrainingGlobal,
                onOpenChallenges = onOpenChallengesGlobal,
                onOpenProfile = onOpenProfileGlobal
            ) {
                TrainingScreen(
                    onSelect = { routineId, isLocal ->
                        navController.navigate(Routes.routine(routineId.toString(), isLocal)) },
                    onCreateRoutine = { navController.navigate(Routes.CREATE_ROUTINE) },
                    categories = trainingUiState.value.categories,
                    isRefreshing = trainingUiState.value.isRefreshing,
                    onRefresh = { trainingViewModel.refreshCategories() }
                )
            }
        }

        composable(Routes.CREATE_ROUTINE) {
            CreateRoutineScreen(
                onBack = { navController.popBackStack() },
                onRoutineCreated = { navController.popBackStack() },
                onOpenHome = onOpenHomeGlobal,
                onOpenTraining = onOpenTrainingGlobal,
                onOpenChallenges = onOpenChallengesGlobal,
                onOpenProfile = onOpenProfileGlobal
            )
        }

        composable(
            route = Routes.ROUTINE_DETAIL,
            arguments = listOf(
                navArgument("routineId") { type = NavType.StringType },
                navArgument("local") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStackEntry ->
            val routineId = backStackEntry.arguments?.getString("routineId").orEmpty()
            val isLocal = backStackEntry.arguments?.getBoolean("local") ?: false

            RoutineCatalogScreen(
                routineId = routineId,
                isLocal = isLocal,
                onBack = { navController.popBackStack() },
                onExerciseClick = { exerciseId, reps ->
                    navController.navigate(Routes.exercise(exerciseId, reps))
                },
                onOpenHome = onOpenHomeGlobal,
                onOpenTraining = onOpenTrainingGlobal,
                onOpenChallenges = onOpenChallengesGlobal,
                onOpenProfile = onOpenProfileGlobal
            )
        }

        composable(
            route = Routes.EXERCISE_DETAIL,
            arguments = listOf(
                navArgument("exerciseId") { type = NavType.StringType },
                navArgument("reps") {
                    type = NavType.StringType
                    defaultValue = "N/A"
                }
            )
        ) { backStackEntry ->
            val exerciseId = backStackEntry.arguments?.getString("exerciseId").orEmpty()
            val reps = backStackEntry.arguments?.getString("reps") ?: "N/A"

            ExerciseDetailScreen(
                exerciseId = exerciseId,
                reps = reps,
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
                onOpenGroups = { navController.navigate(Routes.GROUPS) },
                onOpenGroup = { groupId -> navController.navigate(Routes.groupDetail(groupId)) },
                onOpenRoutine = { routineId ->
                    navController.navigate(Routes.routine(routineId.toString(), isLocal = false))
                },
                onLogout = onLogout,
                onOpenAccount = { navController.navigate(Routes.ACCOUNT) },
                onOpenSettings = { navController.navigate(Routes.SETTINGS) }
            )
        }

        composable(Routes.ACCOUNT) {
            AccountScreen(
                onBack = { navController.popBackStack() },
                onOpenHome = onOpenHomeGlobal,
                onOpenTraining = onOpenTrainingGlobal,
                onOpenChallenges = onOpenChallengesGlobal,
                onOpenProfile = onOpenProfileGlobal,
                onDeleted = {
                    navController.navigate(Routes.WELCOME) {
                        popUpTo(navController.graph.id) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onOpenHome = onOpenHomeGlobal,
                onOpenTraining = onOpenTrainingGlobal,
                onOpenChallenges = onOpenChallengesGlobal,
                onOpenProfile = onOpenProfileGlobal
            )
        }

        composable(Routes.GROUPS) {
            GroupsListScreen(
                onBack = { navController.popBackStack() },
                onOpenGroup = { groupId -> navController.navigate(Routes.groupDetail(groupId)) },
                onOpenHome = onOpenHomeGlobal,
                onOpenTraining = onOpenTrainingGlobal,
                onOpenChallenges = onOpenChallengesGlobal,
                onOpenProfile = onOpenProfileGlobal
            )
        }

        composable(
            route = Routes.GROUP_DETAIL,
            arguments = listOf(navArgument("groupId") { type = NavType.IntType })
        ) { backStackEntry ->
            val groupId = backStackEntry.arguments?.getInt("groupId") ?: return@composable

            GroupDetailScreen(
                groupId = groupId,
                onBack = { navController.popBackStack() },
                onAddRoutine = { id -> navController.navigate(Routes.groupAddRoutine(id)) },
                onOpenRoutine = { routineId ->
                    navController.navigate(Routes.routine(routineId.toString(), isLocal = false))
                },
                onOpenHome = onOpenHomeGlobal,
                onOpenTraining = onOpenTrainingGlobal,
                onOpenChallenges = onOpenChallengesGlobal,
                onOpenProfile = onOpenProfileGlobal
            )
        }

        composable(
            route = Routes.GROUP_ADD_ROUTINE,
            arguments = listOf(navArgument("groupId") { type = NavType.IntType })
        ) { backStackEntry ->
            val groupId = backStackEntry.arguments?.getInt("groupId") ?: return@composable

            AddGroupRoutineScreen(
                groupId = groupId,
                onBack = { navController.popBackStack() },
                onRoutineAdded = { navController.popBackStack() },
                onOpenHome = onOpenHomeGlobal,
                onOpenTraining = onOpenTrainingGlobal,
                onOpenChallenges = onOpenChallengesGlobal,
                onOpenProfile = onOpenProfileGlobal
            )
        }

        composable(Routes.DISCOUNTS) {
            DiscountsScreen(
                onBack = { navController.popBackStack() },
                onOpenHome = onOpenHomeGlobal,
                onOpenTraining = onOpenTrainingGlobal,
                onOpenChallenges = onOpenChallengesGlobal,
                onOpenProfile = onOpenProfileGlobal
            )
        }

        composable(Routes.FRIENDS) {
            FriendsScreen(
                onBack = { navController.popBackStack() },
                onOpenHome = onOpenHomeGlobal,
                onOpenTraining = onOpenTrainingGlobal,
                onOpenChallenges = onOpenChallengesGlobal,
                onOpenProfile = onOpenProfileGlobal
            )
        }
    }
}