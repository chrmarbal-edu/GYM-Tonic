package edu.gymtonic_app.core.network

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import retrofit2.Response
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

object ErrorManager {
    private val gson = Gson()

    fun normalizeError(throwable: Throwable): String {
        Log.e("ErrorManager", "Normalizando error", throwable)
        
        var current: Throwable? = throwable
        while (current != null) {
            val msg = current.message ?: ""
            if (msg.contains("No tienes conexión a internet", ignoreCase = true) ||
                current is UnknownHostException ||
                current is ConnectException || 
                msg.contains("Failed to connect", ignoreCase = true) ||
                msg.contains("Unable to resolve host", ignoreCase = true) ||
                msg.contains("route to host", ignoreCase = true) ||
                msg.contains("Connection refused", ignoreCase = true)) {
                return "No tienes conexión a internet"
            }
            current = current.cause
        }

        return when (throwable) {
            is SocketTimeoutException -> "El servidor está tardando demasiado en responder"
            else -> throwable.message ?: "Ha ocurrido un error inesperado"
        }
    }

    fun parseResponseError(response: Response<*>): String {
        val errorBody = try { response.errorBody()?.string() } catch (e: Exception) { null }
        Log.e("ErrorManager", "Error en respuesta HTTP ${response.code()}: $errorBody")
        
        return try {
            if (!errorBody.isNullOrBlank()) {
                val jsonObject = gson.fromJson(errorBody, JsonObject::class.java)
                when {
                    jsonObject.has("message") -> jsonObject.get("message").asString
                    jsonObject.has("msg") -> jsonObject.get("msg").asString
                    jsonObject.has("error") -> jsonObject.get("error").asString
                    else -> getDefaultErrorMessage(response.code())
                }
            } else {
                getDefaultErrorMessage(response.code())
            }
        } catch (e: Exception) {
            Log.e("ErrorManager", "Error al parsear el cuerpo del error", e)
            getDefaultErrorMessage(response.code())
        }
    }

    private fun getDefaultErrorMessage(code: Int): String {
        return when (code) {
            400 -> "Solicitud incorrecta"
            401 -> "No autorizado"
            403 -> "Acceso denegado"
            404 -> "Recurso no encontrado"
            500 -> "Error interno del servidor"
            else -> "Error del servidor ($code)"
        }
    }
}
