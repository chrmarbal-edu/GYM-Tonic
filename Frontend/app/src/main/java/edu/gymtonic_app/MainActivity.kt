package edu.gymtonic_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import edu.gymtonic_app.data.remote.model.auth.SessionManager
import edu.gymtonic_app.data.remote.model.auth.sessionDataStore
import edu.gymtonic_app.data.remote.services.RetrofitClient
import edu.gymtonic_app.ui.navigation.Navigation


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Inicializar RetrofitClient con SessionManager para autorización
        val sessionManager = SessionManager(sessionDataStore)
        RetrofitClient.setSessionManager(sessionManager)

        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                val snackbarHostState = remember { SnackbarHostState() }
                Navigation(navController, snackbarHostState)
            }
        }
    }
}