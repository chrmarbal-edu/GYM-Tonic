const { OAuth2Client } = require('google-auth-library')
require('dotenv').config()
const userModel = require("../models/users.model")

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

    const ticket = await googleClient.verifyIdToken({
        idToken: idToken,
        audience: process.env.GOOGLE_CLIENT_ID,
    })

    const payload = ticket.getPayload()
    
    const userEmail = payload.email
    const userName = payload.name

    // TODO COMPROBAR SI EXISTE EL USUARIO EN BD, SI NO EXISTE CREARLO
})