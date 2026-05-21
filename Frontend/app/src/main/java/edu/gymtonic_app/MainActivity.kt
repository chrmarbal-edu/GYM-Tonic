package edu.gymtonic_app

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import java.security.MessageDigest
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import edu.gymtonic_app.data.remote.remoteModel.auth.SessionManager
import edu.gymtonic_app.data.remote.remoteModel.auth.sessionDataStore
import edu.gymtonic_app.data.remote.services.RetrofitClient
import edu.gymtonic_app.ui.i18n.AppLanguage
import edu.gymtonic_app.ui.i18n.EnglishStrings
import edu.gymtonic_app.ui.i18n.LanguageManager
import edu.gymtonic_app.ui.i18n.LocalStrings
import edu.gymtonic_app.ui.i18n.SpanishStrings
import edu.gymtonic_app.ui.components.AppToastHost
import edu.gymtonic_app.ui.navigation.Navigation
import edu.gymtonic_app.ui.theme.AppTheme
import edu.gymtonic_app.ui.theme.DarkColors
import edu.gymtonic_app.ui.theme.LightColors
import edu.gymtonic_app.ui.theme.LocalColors
import com.facebook.FacebookSdk
import edu.gymtonic_app.ui.theme.ThemeManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FacebookSdk.sdkInitialize(applicationContext)

        // Inicializar managers con persistencia
        LanguageManager.init(this)
        ThemeManager.init(this)

        val sessionManager = SessionManager(sessionDataStore)
        RetrofitClient.setSessionManager(sessionManager)

        printKeyHash()

        setContent {
            val currentLanguage by LanguageManager.language.collectAsState()
            val strings = if (currentLanguage == AppLanguage.SPANISH) SpanishStrings else EnglishStrings

            val currentTheme by ThemeManager.theme.collectAsState()
            val themeColors = if (currentTheme == AppTheme.LIGHT) LightColors else DarkColors

            // Configurar Edge to Edge dinámicamente según el tema
            enableEdgeToEdge(
                statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
                navigationBarStyle = if (themeColors.isDark) {
                    SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
                } else {
                    SystemBarStyle.light(android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT)
                }
            )

            val view = LocalView.current
            if (!view.isInEditMode) {
                SideEffect {
                    val window = (view.context as Activity).window
                    val controller = WindowCompat.getInsetsController(window, view)
                    // Iconos de la barra de estado (arriba): siempre blancos porque el degradado es oscuro
                    controller.isAppearanceLightStatusBars = false
                    // Iconos de la barra de navegación (abajo): según el tema
                    controller.isAppearanceLightNavigationBars = !themeColors.isDark
                }
            }

            val materialColorScheme = if (currentTheme == AppTheme.LIGHT) {
                lightColorScheme(
                    primary = themeColors.accent,
                    onPrimary = Color.White,
                    surface = themeColors.surfaceMain,
                    onSurface = themeColors.textPrimary,
                    secondary = themeColors.accentDark,
                    onSecondary = Color.White
                )
            } else {
                darkColorScheme(
                    primary = themeColors.accent,
                    onPrimary = Color.White,
                    surface = themeColors.surfaceMain,
                    onSurface = themeColors.textPrimary,
                    secondary = themeColors.accentDark,
                    onSecondary = Color.White
                )
            }

            MaterialTheme(colorScheme = materialColorScheme) {
                CompositionLocalProvider(
                    LocalStrings provides strings,
                    LocalColors provides themeColors
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = themeColors.surfaceMain
                    ) {
                        AppToastHost {
                            val navController = rememberNavController()
                            Navigation(navController)
                        }
                    }
                }
            }
        }
    }

    private fun printKeyHash() {
        try {
            val info = packageManager.getPackageInfo(
                packageName,
                PackageManager.GET_SIGNING_CERTIFICATES
            )
            val signatures = info.signingInfo?.signingCertificateHistory
            if (signatures != null) {
                for (signature in signatures) {
                    val md = MessageDigest.getInstance("SHA")
                    md.update(signature.toByteArray())
                    val hash = Base64.encodeToString(md.digest(), Base64.DEFAULT)
                    Log.d("KeyHash", "KeyHash: $hash")
                }
            }
        } catch (e: Exception) {
            Log.e("KeyHash", "Error getting key hash", e)
        }
    }
}
