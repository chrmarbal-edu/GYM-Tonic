require("dotenv").config()
const jwt = require("jsonwebtoken")
const AppError = require("../utils/AppError")
const logger = require("../utils/logger")

// EXTRAER TOKEN
function extractToken(req){
    // SI navega por cabecera de autorización.
    if(req.headers.authorization && req.headers.authorization.split(' ')[0] == "Bearer"){
        // De esta forma obtenemos los valores del JWT
        return req.headers.authorization.split(' ')[1]
    } else if (req.query && req.query.token){
        // En el caso de que venga por query el token
        return req.query.token
    } else{
        return null
    }
}

// AUTHENTICATE
exports.authenticate = (req, res, next) => {
    const token = extractToken(req)
    if(token){
        jwt.verify(token, process.env.SECRET_JWT, (err, decoded) => {
            if(err){
                if(err.name == "TokenExpiredError"){
                    next(new AppError("Sesión Expirada", 401))
                } else{
                    next(new AppError(err, 401))
                }
            } else{
                req.userLogued = decoded
                next()
            }
        })
    } else{
        next(new AppError("Token no Proveída", 401))
    }
}

// GENERAR TOKEN
exports.createJWT = (req, res, next, userData) => {
    try {
        const payload = userData

        const token = jwt.sign(payload, process.env.SECRET_JWT, {
            expiresIn: "2d" // El token expira en 2 días
        })

        if(token){
            return token
        } else{
            return null
        }
    } catch (error) {
        next(new AppError(error.message, 500))
    }
}