package edu.gymtonic_app

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import edu.gymtonic_app.data.remote.model.auth.SessionManager
import edu.gymtonic_app.data.remote.model.auth.sessionDataStore
import edu.gymtonic_app.data.remote.services.RetrofitClient
import edu.gymtonic_app.ui.i18n.AppLanguage
import edu.gymtonic_app.ui.i18n.EnglishStrings
import edu.gymtonic_app.ui.i18n.LanguageManager
import edu.gymtonic_app.ui.i18n.LocalStrings
import edu.gymtonic_app.ui.i18n.SpanishStrings
import edu.gymtonic_app.ui.navigation.Navigation
import edu.gymtonic_app.ui.theme.AppTheme
import edu.gymtonic_app.ui.theme.DarkColors
import edu.gymtonic_app.ui.theme.LightColors
import edu.gymtonic_app.ui.theme.LocalColors
import edu.gymtonic_app.ui.theme.ThemeManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val sessionManager = SessionManager(sessionDataStore)
        RetrofitClient.setSessionManager(sessionManager)

        setContent {
            val currentLanguage by LanguageManager.language.collectAsState()
            val strings = if (currentLanguage == AppLanguage.SPANISH) SpanishStrings else EnglishStrings

            val currentTheme by ThemeManager.theme.collectAsState()
            val themeColors = if (currentTheme == AppTheme.LIGHT) LightColors else DarkColors

            val view = LocalView.current
            if (!view.isInEditMode) {
                SideEffect {
                    val window = (view.context as Activity).window
                    val controller = WindowCompat.getInsetsController(window, view)
                    // El fondo superior siempre es degradado oscuro → iconos blancos siempre
                    controller.isAppearanceLightStatusBars = false
                    // Barra de navegación inferior: clara en tema claro, oscura en tema oscuro
                    controller.isAppearanceLightNavigationBars = !themeColors.isDark
                }
            }

            MaterialTheme {
                CompositionLocalProvider(
                    LocalStrings provides strings,
                    LocalColors provides themeColors
                ) {
                    val navController = rememberNavController()
                    val snackbarHostState = remember { SnackbarHostState() }
                    Navigation(navController, snackbarHostState)
                }
            }
        }
    }
}
