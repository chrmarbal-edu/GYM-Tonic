package edu.gymtonic_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import edu.gymtonic_app.data.remote.remoteModel.auth.SessionManager
import edu.gymtonic_app.data.remote.remoteModel.auth.sessionDataStore
import edu.gymtonic_app.data.remote.services.RetrofitClient
import edu.gymtonic_app.ui.i18n.AppLanguage
import edu.gymtonic_app.ui.i18n.EnglishStrings
import edu.gymtonic_app.ui.i18n.LanguageManager
import edu.gymtonic_app.ui.i18n.LocalStrings
import edu.gymtonic_app.ui.i18n.SpanishStrings
import edu.gymtonic_app.ui.navigation.Navigation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val sessionManager = SessionManager(sessionDataStore)
        RetrofitClient.setSessionManager(sessionManager)

        setContent {
            val currentLanguage by LanguageManager.language.collectAsState()
            val strings = if (currentLanguage == AppLanguage.SPANISH) SpanishStrings else EnglishStrings

            MaterialTheme {
                CompositionLocalProvider(LocalStrings provides strings) {
                    val navController = rememberNavController()
                    val snackbarHostState = remember { SnackbarHostState() }
                    Navigation(navController, snackbarHostState)
                }
            }
        }
    }
}
