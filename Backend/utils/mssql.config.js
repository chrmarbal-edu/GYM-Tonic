require("dotenv").config() // npm i dotenv

const dbConn = {
    user: process.env.MSSQL_USER,
    password: process.env.MSSQL_PASS,
    server: process.env.MSSQL_HOST,
    database: process.env.MSSQL_DATABASE,
    options: {
        encrypt: false,
        trustServerCertificate: true // obligatorio en local casi siempre
    }
}

// Almacenamos la configuración de MSSQL para poder importarlo en las diferentes páginas.
module.exports = dbConn
