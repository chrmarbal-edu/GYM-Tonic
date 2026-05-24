package edu.gymtonic_app.core

object ValidationUtils {
    /**
     * Valida que la contraseña tenga:
     * - Al menos 8 caracteres
     * - Al menos una mayúscula
     * - Al menos una minúscula
     * - Al menos un número
     * - Al menos un símbolo (cualquier carácter que no sea letra ni número)
     */
    fun isPasswordStrong(password: String): Boolean {
        if (password.length < 8) return false
        
        val hasUppercase = password.any { it.isUpperCase() }
        val hasLowercase = password.any { it.isLowerCase() }
        val hasDigit = password.any { it.isDigit() }
        val hasSymbol = password.any { !it.isLetterOrDigit() }
        
        return hasUppercase && hasLowercase && hasDigit && hasSymbol
    }
}
