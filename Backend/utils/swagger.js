require("dotenv").config()
const swaggerJsdoc = require("swagger-jsdoc")

const options = {
    definition: {
        openapi: "3.0.0",
        info: {
            title: "API de GYM TONIC",
            version: process.env.API_VERSION || "1.0.0",
            contact: {
                name: "GYM TONIC"
            }
        },
        servers: [
            {
                url: `http://localhost:${process.env.PUERTO || 3000}`,
                description: "Local Server"
            }
        ]
    },
    apis: ['./docs/*.js']
}

const specs = swaggerJsdoc(options)
module.exports = specs
