package edu.gymtonic_app.ui.screens.login

import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import edu.gymtonic_app.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class GoogleAuthHelper(private val context: Context) {

    private val credentialManager = CredentialManager.create(context)

    fun signInWithGoogle(
        scope: CoroutineScope,
        onSuccess: (GoogleIdTokenCredential) -> Unit,
        onError: (String) -> Unit
    ) {
        val clientId = BuildConfig.GOOGLE_WEB_CLIENT_ID
        Log.d("GoogleAuthHelper", "Starting Google Sign In with Client ID: $clientId")

        if (clientId.isEmpty() || clientId == "YOUR_GOOGLE_WEB_CLIENT_ID_HERE") {
            onError("Configura el GOOGLE_WEB_CLIENT_ID en local.properties")
            return
        }

        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(BuildConfig.GOOGLE_WEB_CLIENT_ID)
            .setAutoSelectEnabled(true)
            .build()

        val request: GetCredentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        scope.launch {
            try {
                val result = credentialManager.getCredential(
                    request = request,
                    context = context,
                )
                handleSignIn(result, onSuccess, onError)
            } catch (e: GetCredentialException) {
                Log.e("GoogleAuthHelper", "Error getting credential", e)
                onError(e.message ?: "Error al obtener credenciales")
            } catch (e: Exception) {
                Log.e("GoogleAuthHelper", "Unexpected error", e)
                onError(e.message ?: "Error inesperado")
            }
        }
    }

    private fun handleSignIn(
        result: GetCredentialResponse,
        onSuccess: (GoogleIdTokenCredential) -> Unit,
        onError: (String) -> Unit
    ) {
        val credential = result.credential
        Log.d("GoogleAuthHelper", "Credential received: ${credential.type}")
        if (credential is GoogleIdTokenCredential) {
            Log.d("GoogleAuthHelper", "ID TOKEN OBTAINED: ${credential.idToken}")
            onSuccess(credential)
        } else {
            Log.e("GoogleAuthHelper", "Unexpected credential type: ${credential.type}")
            onError("Tipo de credencial inesperado: ${credential.type}")
        }
    }

    fun signOut(scope: CoroutineScope) {
        scope.launch {
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
        }
    }
}
