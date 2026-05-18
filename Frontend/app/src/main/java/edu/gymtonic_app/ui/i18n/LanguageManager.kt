package edu.gymtonic_app.ui.i18n

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

enum class AppLanguage { SPANISH, ENGLISH }

val Context.settingsDataStore by preferencesDataStore(name = "settings")

object LanguageManager {
    private val _language = MutableStateFlow(AppLanguage.SPANISH)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    private val LANGUAGE_KEY = stringPreferencesKey("app_language")
    private lateinit var context: Context
    private val scope = CoroutineScope(Dispatchers.Main)

    fun init(context: Context) {
        this.context = context.applicationContext
        scope.launch {
            val savedLang = context.settingsDataStore.data
                .map { prefs -> prefs[LANGUAGE_KEY] }
                .first()
            
            if (savedLang != null) {
                _language.value = AppLanguage.valueOf(savedLang)
            }
        }
    }

    fun toggle() {
        val newLang = if (_language.value == AppLanguage.SPANISH) AppLanguage.ENGLISH else AppLanguage.SPANISH
        setLanguage(newLang)
    }

    fun setLanguage(lang: AppLanguage) {
        _language.value = lang
        scope.launch {
            context.settingsDataStore.edit { prefs ->
                prefs[LANGUAGE_KEY] = lang.name
            }
        }
    }
}
