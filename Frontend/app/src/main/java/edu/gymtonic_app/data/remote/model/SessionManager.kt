/* edu.gymtonic_app.data.remote.model

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.prefs.Preferences
import edu.gymtonic_app.data.remote.model.SessionManager.Companion.TOKEN_KEY
import edu.gymtonic_app.data.remote.model.SessionManager.Companion.USERNAME_KEY


// Inicialización con extensión delegada.
val Context.dataStore by preferencesDataStore(name = "settings")

//Clase para gestionar la sesión del usuario con DataStore.
class SessionManager(private val dataStore: DataStore<Preferences>) {   // dataStore DataStore para almacenar la sesión.
    companion object {
        //Clave para almacenar el token.
        private val  TOKEN_KEY = stringPreferencesKey("token") //stringPreferences: prefenrencia en el archivo para token
        //USERNAME_KEY Clave para almacenar el nombre de usuario.
        private val USERNAME_KEY = stringPreferencesKey("username") //stringPreferences: prefenrencia en el archivo para token
    }

    // Flujo de datos para la sesión. Devuelve un par con el token y el nombre de usuario. si existen, en este caso devuelve los dos valores a la vez
    //interesa recogerlos juntos token-usuario oudiendo ser nulos. Datastore recupera los datos en orden de preferencia
    val sessionFlow: Flow<Pair<String?, String?>> = dataStore.data.map { preferences ->
        preferences[TOKEN_KEY] to preferences[USERNAME_KEY]
    }

    // Función para guardar los datos de la sesión.
    suspend fun saveSession(token: String, username: String) {
        dataStore.edit { preferences -> // Edita los datos del DataStore, si no existen los crea.
            preferences[TOKEN_KEY] = token
            preferences[USERNAME_KEY] = username
        }
    }

    // Función para limpiar la sesión. Borra los datos del fichero settings.
    suspend fun clearSession() {
        // dataStore.edit { it.clear() } // Limpia todos los datos del DataStore.

        dataStore.edit { // Versión para borrar únicamente el token.
            it.remove(TOKEN_KEY)
        }
    }
}*/