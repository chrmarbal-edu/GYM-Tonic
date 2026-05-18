/**
 * Aplica multer solo cuando la petición es multipart/form-data.
 * Si no, deja pasar la petición JSON sin tocar el body.
 */
const optionalMultipart = (uploadMiddleware) => (req, res, next) => {
    const contentType = req.headers["content-type"] || ""

    if (contentType.includes("multipart/form-data")) {
        return uploadMiddleware(req, res, (err) => {
            if (err) {
                return next(err)
            }
            return next()
        })
    }

    return next()
}

module.exports = optionalMultipart
