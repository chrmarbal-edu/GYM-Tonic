const isInvalidColumnError = (err) => {
    const message = err?.message || err?.originalError?.message || String(err || "")
    return /invalid column name/i.test(message)
}

module.exports = {
    isInvalidColumnError
}
