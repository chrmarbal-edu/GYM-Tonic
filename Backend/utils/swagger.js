require("dotenv").config()
const swaggerJsdoc = require("swagger-jsdoc")

const options = {
    definition: {
        openapi: "3.0.0",
        info: {
            title: "API de GYM TONIC",
            version: process.env.API_VERSION || "v1",
            contact: {
                name: "GYM TONIC"
            }
        },
        components: {
            securitySchemes: {
                bearerAuth: {
                    type: "http",
                    scheme: "bearer",
                    bearerFormat: "JWT",
                },
            },
        },
        servers: [
            {
                url: `http://localhost:${process.env.PUERTO || 3000}/api/v1`,
                description: "Local Server"
            }
        ]
    },
    apis: ['./docs/*.js']
}

const specs = swaggerJsdoc(options)
module.exports = specs
