package edu.gymtonic_app.ui.theme

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AppTheme { LIGHT, DARK }

object ThemeManager {
    private val _theme = MutableStateFlow(AppTheme.LIGHT)
    val theme: StateFlow<AppTheme> = _theme.asStateFlow()

    fun toggle() {
        _theme.value = if (_theme.value == AppTheme.LIGHT) AppTheme.DARK else AppTheme.LIGHT
    }

    fun setTheme(theme: AppTheme) {
        _theme.value = theme
    }
}
