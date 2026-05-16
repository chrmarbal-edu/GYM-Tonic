const { OAuth2Client } = require('google-auth-library')
require('dotenv').config()
const userModel = require("../models/users.model")
const jwtMW = require("../middlewares/jwt.mw")
const AppError = require("../utils/AppError")

// oAuth Clients
const googleClient = new OAuth2Client(process.env.GOOGLE_CLIENT_ID)

// Función Para Capturar Errores Asíncronos
function wrapAsync(fn) {
    return function (req, res, next) {
        fn(req, res, next).catch(e => {
            next(e)
        })
    }
}

/* <=============================== OAUTH GOOGLE ===============================> */
exports.googleLogin = wrapAsync(async function(req, res, next) {
    const { idToken } = req.body

    try {
        const ticket = await googleClient.verifyIdToken({
            idToken: idToken,
            audience: process.env.GOOGLE_CLIENT_ID,
        })

        const payload = ticket.getPayload()
        const userEmail = payload.email
        const userPicture = payload.picture
        const userUsername = payload.given_name
        const userData = {
            email: userEmail,
            username: userUsername,
            picture: userPicture,
            oauth: "Google"
        }

        await userModel.findOAuthUserByEmail(userEmail, function(err, userFound) {
            if (err) return next(new AppError("Error al buscar usuario", 500))

            if (userFound) {
                const jwtToken = jwtMW.createJWT(req, res, next, userFound)
                return res.status(200).json({
                    data: userFound,
                    token: jwtToken
                })
            } else {
                return res.status(200).json(userData)
            }
        })
    } catch (error) {
        console.log(error)
        return next(new AppError("Token de Google inválido", 401))
    }
})

/* <=============================== OAUTH FACEBOOK ===============================> */
exports.facebookLogin = wrapAsync(async function(req, res, next) {
    const { accessToken } = req.body

    if (!accessToken) {
        return next(new AppError("Token de acceso de Facebook requerido", 400))
    }

    try {
        const fbResponse = await fetch(
            `https://graph.facebook.com/me?fields=id,name,email,birthday,friends,picture.type(large)&access_token=${accessToken}`
        )
        
        if (!fbResponse.ok) {
            return next(new AppError("Error al validar con Facebook", 401))
        }

        const fbData = await fbResponse.json()
    
        const userEmail = fbData.email
        const userName = fbData.name
        const userPicture = fbData.picture.data.url

        const userUserName = fbData.name.toLowerCase()
        .normalize("NFD").replace(/[\u0300-\u036f]/g, "")
        .replace(/\s+/g, '_')

        const userData = {
            email: userEmail,
            username: userUserName,
            name: userName,
            picture: userPicture,
            oauth: "Facebook"
        }

        await userModel.findOAuthUserByEmail(userEmail, function(err, userFound) {
            if (err) return next(new AppError("Error al buscar usuario", 500))

            if (userFound) {
                const jwtToken = jwtMW.createJWT(req, res, next, userFound)
                return res.status(200).json({
                    data: userFound,
                    token: jwtToken
                })
            } else {
                return res.status(200).json(userData)
            }
        })
    } catch (error) {
        return next(new AppError("Error en la autenticación con Facebook", 500))
    }
})