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

// Función auxiliar para buscar o crear el usuario y generar el token
async function findOrCreateUser(email, name, oauthProvider, req, res, next) {
    await userModel.findByEmail(email, async function(err, userFound) {
        if (err) return next(new AppError("Error al buscar usuario", 500))

        let user = userFound ? userFound[0] : null

        if (!user) {
            // Si no existe, lo creamos con valores por defecto
            const newUser = {
                user_username: email.split('@')[0] + "_" + Math.floor(Math.random() * 1000),
                user_name: name || "Usuario OAuth",
                user_password: "oauth_default_password_" + Math.random(), // Password aleatoria para cumplir el NOT NULL
                user_birthdate: "1900-01-01",
                user_email: email,
                user_height: 0,
                user_weight: 0,
                user_objective: 0,
                user_oauth: oauthProvider
            }

            await userModel.create(newUser, function(createErr, createdUser) {
                if (createErr) return next(new AppError("Error al registrar usuario de red social", 500))
                
                const jwtToken = jwtMW.createJWT(req, res, next, createdUser)
                return res.status(200).json({ data: createdUser, token: jwtToken })
            })
        } else {
            // Si existe, generamos el token de sesión
            const jwtToken = jwtMW.createJWT(req, res, next, user)
            return res.status(200).json({ data: user, token: jwtToken })
        }
    })
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
        // Verificamos el token llamando a la Graph API de Facebook
        // Se asume que el entorno tiene fetch disponible (Node 18+)
        const fbResponse = await fetch(`https://graph.facebook.com/me?fields=id,name,email&access_token=${accessToken}`)
        
        if (!fbResponse.ok) {
            return next(new AppError("Error al validar con Facebook", 401))
        }

        const fbData = await fbResponse.json()
        
        const userEmail = fbData.email
        const userName = fbData.name

        if (!userEmail) {
            return next(new AppError("No se pudo obtener el email de la cuenta de Facebook", 400))
        }

        await findOrCreateUser(userEmail, userName, "Facebook", req, res, next)
    } catch (error) {
        return next(new AppError("Error en la autenticación con Facebook", 500))
    }
})