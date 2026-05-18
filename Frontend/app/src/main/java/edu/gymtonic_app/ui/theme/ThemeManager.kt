package edu.gymtonic_app.ui.theme

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import edu.gymtonic_app.ui.i18n.settingsDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

enum class AppTheme { LIGHT, DARK }

object ThemeManager {
    private val _theme = MutableStateFlow(AppTheme.LIGHT)
    val theme: StateFlow<AppTheme> = _theme.asStateFlow()

    private val THEME_KEY = stringPreferencesKey("app_theme")
    private lateinit var context: Context
    private val scope = CoroutineScope(Dispatchers.Main)

    fun init(context: Context) {
        this.context = context.applicationContext
        scope.launch {
            val savedTheme = context.settingsDataStore.data
                .map { prefs -> prefs[THEME_KEY] }
                .first()
            
            if (savedTheme != null) {
                _theme.value = AppTheme.valueOf(savedTheme)
            }
        }
    }

    fun toggle() {
        val newTheme = if (_theme.value == AppTheme.LIGHT) AppTheme.DARK else AppTheme.LIGHT
        setTheme(newTheme)
    }

    fun setTheme(theme: AppTheme) {
        _theme.value = theme
        scope.launch {
            context.settingsDataStore.edit { prefs ->
                prefs[THEME_KEY] = theme.name
            }
        }
    }
}
