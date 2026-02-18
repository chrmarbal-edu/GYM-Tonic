// <============ CONSTANTES Y DEPENDENCIAS ============>
require("dotenv").config() // npm i dotenv
const methodOverride = require("method-override") // npm i method-override
const session = require("express-session") // npm i express-session
const express = require("express") // npm i express
const app = express()
const port = process.env.PORT || process.env.PUERTO
const path = require("path") // npm i path
const errorHandlerMW = require("./middlewares/errorHandler.mw")
const userRoutes = require("./routes/users.routes")
const missionRoutes = require("./routes/missions.routes")
const routinesRoutes = require("./routes/routines.routes")
const groupRoutes = require("./routes/groups.routes")

// <============ COOKIE DE SESIÓN ============>
app.use(session({
    name: "gymtonic",
    secret: process.env.SECRET_SESSION,
    resave: false,
    saveUninitialized: false,
    cookie: {
        httpOnly: true,
        sameSite: "none"
    }
}))

// <============ CONFIGURACIÓN SERVIDOR ============>
app.use(express.urlencoded({extended: true})) // Leer Datos req.body
app.use(express.json()) // Leer Datos JSON en req.body
app.use(methodOverride("_method")) // Para Poder hacer Patch y Delete
app.use(express.static(path.join(__dirname, "public"))) // Asignamos la carpeta "public" como la carpeta de contenido estático (imágenes, css, etc.)

// <============ RUTAS ============>
app.use(`/api/${process.env.API_VERSION}/users`, userRoutes)
app.use(`/api/${process.env.API_VERSION}/missions`, missionRoutes)
app.use(`/api/${process.env.API_VERSION}/routines`, routinesRoutes)
app.use(`/api/${process.env.API_VERSION}/groups`, groupRoutes)

// <============ MANEJADOR DE ERRORES ============>
app.use(errorHandlerMW.errorHandler)

// <============ LEVANTAR SERVIDOR ============>
app.listen(port, () => {
    console.log("---------- GYM TONIC ----------")
    console.log(`http://localhost:${port}`)

    console.log()

    console.log("<============ USERS ============>")
    console.log(`http://localhost:${port}/api/${process.env.API_VERSION}/users`)
    console.log("============= MISSIONS =============")
    console.log(`http://localhost:${port}/api/${process.env.API_VERSION}/missions`)
    console.log("============= ROUTINES =============")
    console.log(`http://localhost:${port}/api/${process.env.API_VERSION}/routines`)
    console.log("============= GROUPS =============")
    console.log(`http://localhost:${port}/api/${process.env.API_VERSION}/groups`)

    console.log()

})