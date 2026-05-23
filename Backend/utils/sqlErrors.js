const isInvalidColumnError = (err) => {
    const message = err?.message || err?.originalError?.message || String(err || "")
    return /invalid column name/i.test(message)
}

/**
 * Traduce errores técnicos de SQL Server o del modelo a mensajes entendibles por el usuario.
 */
const handleSqlError = (err) => {
    if (!err) return "Ocurrió un error inesperado."
    if (typeof err === 'string') return err
    if (err.err === "No hay datos") return "No se han encontrado los datos solicitados."

    const message = err?.message || err?.originalError?.message || ""
    const number = err?.number || err?.originalError?.number

    switch (number) {
        case 2627: // Unique Constraint
        case 2601: // Unique Index
            if (message.includes("UQ_Users_username")) return "Ese nombre de usuario ya está registrado."
            if (message.includes("UQ_Users_email")) return "Ese correo electrónico ya está en uso."
            return "Ya existe un registro con estos datos únicos."
        case 547: // Foreign Key Constraint
            if (message.includes("DELETE")) return "No se puede eliminar porque existen otros datos asociados (como rutinas o miembros)."
            return "Hubo un error de relación de datos. Verifica que los IDs sean correctos."
        case 8152: // String or binary data would be truncated
            return "Uno de los campos es demasiado largo. Por favor, acorta el texto."
        default:
            if (isInvalidColumnError(err)) return "Error interno en la estructura de la base de datos."
            return message || "Error interno en el servidor de base de datos."
    }
}

module.exports = {
    isInvalidColumnError,
    handleSqlError
}
