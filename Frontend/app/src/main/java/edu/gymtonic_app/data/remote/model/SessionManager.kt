package edu.gymtonic_app.data.remote.model

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension property
val Context.sessionDataStore by preferencesDataStore(name = "gymtonic_session")

class SessionManager(private val dataStore: DataStore<Preferences>) {
    companion object {
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val USER_ID_KEY = intPreferencesKey("user_id")
        private val USERNAME_KEY = stringPreferencesKey("username")
        private val EMAIL_KEY = stringPreferencesKey("email")
        private val ROLE_KEY = intPreferencesKey("role")
    }

    // Session model
    data class SessionData(
        val token: String?,
        val userId: Int?,
        val username: String?,
        val email: String?,
        val role: Int?
    )

    // Flow session
    val sessionFlow: Flow<SessionData> = dataStore.data.map { prefs ->
        SessionData(
            token = prefs[TOKEN_KEY],
            userId = prefs[USER_ID_KEY],
            username = prefs[USERNAME_KEY],
            email = prefs[EMAIL_KEY],
            role = prefs[ROLE_KEY]
        )
    }

    // Save session
    suspend fun saveSession(
        token: String,
        userId: Int,
        username: String,
        email: String,
        role: Int
    ) {
        dataStore.edit { prefs ->
            prefs[TOKEN_KEY] = token
            prefs[USER_ID_KEY] = userId
            prefs[USERNAME_KEY] = username
            prefs[EMAIL_KEY] = email
            prefs[ROLE_KEY] = role
        }
    }

    // Clear session
    suspend fun clearSession() {
        dataStore.edit { it.clear() }
    }
}
