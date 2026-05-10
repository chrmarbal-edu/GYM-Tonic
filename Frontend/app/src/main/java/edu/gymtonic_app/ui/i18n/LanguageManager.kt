package edu.gymtonic_app.ui.i18n

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class AppLanguage { SPANISH, ENGLISH }

object LanguageManager {
    private val _language = MutableStateFlow(AppLanguage.SPANISH)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    fun toggle() {
        _language.value = if (_language.value == AppLanguage.SPANISH) AppLanguage.ENGLISH else AppLanguage.SPANISH
    }

    fun setLanguage(lang: AppLanguage) {
        _language.value = lang
    }
}
