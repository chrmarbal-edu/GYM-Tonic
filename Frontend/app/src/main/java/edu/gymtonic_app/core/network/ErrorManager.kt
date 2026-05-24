package edu.gymtonic_app.core.network

import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonElement
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
            if ((msg.contains("No tienes conexión a internet", ignoreCase = true)) ||
                (current is UnknownHostException) ||
                (current is ConnectException) || 
                (msg.contains("Failed to connect", ignoreCase = true)) ||
                (msg.contains("Unable to resolve host", ignoreCase = true)) ||
                (msg.contains("route to host", ignoreCase = true)) ||
                (msg.contains("Connection refused", ignoreCase = true))) {
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
        val errorBody = try { response.errorBody()?.string() } catch (_: Exception) { null }
        Log.e("ErrorManager", "Error en respuesta HTTP ${response.code()}: $errorBody")
        
        if (errorBody.isNullOrBlank()) {
            return getDefaultErrorMessage(response.code())
        }

        val trimmedBody = errorBody.trim()
        if (trimmedBody.startsWith("<!DOCTYPE", ignoreCase = true) || 
            trimmedBody.startsWith("<html", ignoreCase = true)) {
            Log.e("ErrorManager", "Cuerpo del error es HTML, no se puede parsear como JSON")
            return getDefaultErrorMessage(response.code())
        }

        return try {
            val jsonElement = gson.fromJson(errorBody, JsonElement::class.java)
            if (jsonElement.isJsonObject) {
                val jsonObject = jsonElement.asJsonObject
                when {
                    jsonObject.has("message") -> jsonObject["message"]?.asString
                    jsonObject.has("msg") -> jsonObject["msg"]?.asString
                    jsonObject.has("error") -> {
                        val errorElem = jsonObject["error"]
                        when {
                            errorElem?.isJsonObject == true -> {
                                val errorObj = errorElem.asJsonObject
                                if (errorObj.has("message")) errorObj["message"]?.asString
                                else errorElem.toString()
                            }
                            errorElem?.isJsonPrimitive == true -> errorElem.asString
                            else -> errorElem?.toString()
                        }
                    }
                    jsonObject.has("detail") -> {
                        val detailElem = jsonObject["detail"]
                        if (detailElem?.isJsonPrimitive == true) detailElem.asString else detailElem?.toString()
                    }
                    else -> getDefaultErrorMessage(response.code())
                } ?: getDefaultErrorMessage(response.code())
            } else if (jsonElement.isJsonPrimitive) {
                jsonElement.asString
            } else {
                getDefaultErrorMessage(response.code())
            }
        } catch (e: Exception) {
            Log.e("ErrorManager", "Error al parsear el cuerpo del error", e)
            if (errorBody.length < 100) errorBody else getDefaultErrorMessage(response.code())
        }
    }

    private fun getDefaultErrorMessage(code: Int): String {
        return when (code) {
            400 -> "Solicitud incorrecta"
            401 -> "Usuario y/o contraseña incorrectos"
            403 -> "Acceso denegado"
            404 -> "Recurso no encontrado"
            500 -> "Error interno del servidor"
            else -> "Error del servidor ($code)"
        }
    }
}
