/* <=============================== DEPENDENCIAS ===============================> */
const dbConn = require("../utils/mssql.config")
const sql = require("mssql")

/* <=============================== CONSTRUCTOR ===============================> */
let user = function(user) {
    this.user_id = user.user_id // AUTO INCREMENTAL
    this.user_username = user.user_username
    this.user_name = user.user_name
    this.user_password = user.user_password
    this.user_birthdate = user.user_birthdate
    this.user_email = user.user_email
    this.user_picture = user.user_picture
    this.user_height = user.user_height
    this.user_weight = user.user_weight
    this.user_objective = user.user_objective
    this.user_points = user.user_points
    this.user_role = user.user_role
    this.user_oauth = user.user_oauth
}

/* <=============================== FIND ALL ===============================> */
user.findAll = async function (result) {
    try {
        const pool = await sql.connect(dbConn)
        const response = await pool.request().query("SELECT * FROM Users WHERE user_role = 0")
        result(null, response.recordset)
    } catch (err) {
        result(err, null)
    }
}

/* <=============================== FIND BY ID ===============================> */
user.findById = async function (id, result) {
    try {
        const pool = await sql.connect(dbConn)
        const response = await pool.request()
            .input("id", sql.Int, id)
            .query("SELECT * FROM Users WHERE user_id = @id")

        if (response.recordset.length > 0) {
            result(null, response.recordset[0])
        } else {
            result({ err: "No hay datos" }, null)
        }

    } catch (err) {
        result(err, null)
    }
}

/* <=============================== UPDATE BY ID ===============================> */
user.updateById = async function (id, updateUser, result) {
    try {
        const pool = await sql.connect(dbConn)

        // Fetch the current user data to compare against new values
        const currentUserResponse = await pool.request()
            .input("id", sql.Int, id)
            .query("SELECT user_username, user_email, user_oauth FROM Users WHERE user_id = @id");

        if (currentUserResponse.recordset.length === 0) {
            return result("Usuario no encontrado para actualizar", null);
        }
        const currentUser = currentUserResponse.recordset[0];

        // Validación de Nombre de Usuario Único (Solo para registros tradicionales/no-oAuth)
        // Solo verificar si el nombre de usuario se está actualizando y es diferente del nombre de usuario actual
        if (updateUser.user_username && updateUser.user_username !== currentUser.user_username) {
            // Si el usuario que se está actualizando NO es un usuario OAuth
            if (!currentUser.user_oauth) {
                const usernameCheck = await pool.request()
                    .input("username", sql.NVarChar, updateUser.user_username)
                    .input("currentUserId", sql.Int, id)
                    .query("SELECT 1 FROM Users WHERE user_username = @username AND user_oauth IS NULL AND user_id <> @currentUserId");

                if (usernameCheck.recordset.length > 0) {
                    return result("El nombre de usuario ya está en uso por otra cuenta tradicional", null);
                }
            }
        }

        const request = pool.request()
        request.input("id", sql.Int, id)
        request.input("username", sql.NVarChar, updateUser.user_username)
        request.input("name", sql.NVarChar, updateUser.user_name)
        request.input("password", sql.NVarChar, updateUser.user_password)
        request.input("height", sql.Decimal(5,2), updateUser.user_height)
        request.input("weight", sql.Decimal(5,2), updateUser.user_weight)
        request.input("objective", sql.Int, updateUser.user_objective)
        request.input("points", sql.Int, updateUser.user_points)
        request.input("picture", sql.NVarChar, updateUser.user_picture)

        const sqlQuery = `
            UPDATE Users SET
                user_username = @username,
                user_name = @name,
                user_password = @password, // Se mantiene la actualización de contraseña
                user_height = @height,
                user_weight = @weight,
                user_objective = @objective,
                user_points = @points,
                user_picture = @picture
            OUTPUT INSERTED.*
            WHERE user_id = @id
        `

        const response = await request.query(sqlQuery)
        result(null, response.recordsets[0][0])
    } catch (err) {
        result(err, null)
    }
}

/* <=============================== CREATE ===============================> */
user.create = async function (newUser, result) {
    try {
        const pool = await sql.connect(dbConn)

        // 1. Validación de Correo Electrónico Único (Siempre)
        const emailCheck = await pool.request()
            .input("email", sql.NVarChar, newUser.user_email)
            .query("SELECT 1 FROM Users WHERE user_email = @email")

        if (emailCheck.recordset.length > 0) {
            return result("El correo electrónico ya está registrado por otro usuario", null)
        }

        // 2. Validación de Nombre de Usuario Único (Solo para registros tradicionales/no-oAuth)
        if (!newUser.user_oauth) {
            const usernameCheck = await pool.request()
                .input("username", sql.NVarChar, newUser.user_username)
                .query("SELECT 1 FROM Users WHERE user_username = @username AND user_oauth IS NULL")

            if (usernameCheck.recordset.length > 0) {
                return result("El nombre de usuario ya está en uso por otra cuenta tradicional", null)
            }
        }

        const request = pool.request()
        request.input("username", sql.NVarChar, newUser.user_username)
        request.input("name", sql.NVarChar, newUser.user_name)
        request.input("password", sql.NVarChar, newUser.user_password)
        request.input("birthdate", sql.Date, newUser.user_birthdate)
        request.input("email", sql.NVarChar, newUser.user_email)
        request.input("height", sql.Decimal(5,2), newUser.user_height)
        request.input("weight", sql.Decimal(5,2), newUser.user_weight)
        request.input("objective", sql.Int, newUser.user_objective)
        request.input("oauth", sql.NVarChar, newUser.user_oauth || null)
        request.input("picture", sql.NVarChar, newUser.user_picture)

        const sqlQuery = `
            INSERT INTO Users (
                user_username, user_name, user_password, user_birthdate,
                user_email, user_picture, user_height, user_weight,
                user_objective, user_role, user_oauth
            )
            OUTPUT INSERTED.*
            VALUES (
                @username, @name, @password, @birthdate,
                @email, @picture, @height, @weight,
                @objective, 0, @oauth
            )
        `

        const response = await request.query(sqlQuery)
        result(null, response.recordset[0])
    } catch (err) {
        result(err, null)
    }
}

/* <=============================== DELETE ===============================> */
user.delete = async function (id, result) {
    try {
        const pool = await sql.connect(dbConn)
        const response = await pool.request()
            .input("id", sql.Int, id)
            .query(`
                DELETE FROM Users
                OUTPUT DELETED.*
                WHERE user_id = @id
            `)

        result(null, response.recordset[0])
    } catch (err) {
        result(err, null)
    }
}

/* <=============================== FIND BY USERNAME ===============================> */
/* <=============================== FIND BY USERNAME (Para Login Tradicional) ===============================> */
// Filtramos para que solo busque usuarios que NO sean OAuth, ya que permitimos duplicidad de nombres entre tipos
user.findByUsername = async function (usernameParam, result) {
    try {
        const pool = await sql.connect(dbConn)
        const response = await pool.request()
            .input("username", sql.VarChar, usernameParam)
            .query("SELECT * FROM Users WHERE user_username = @username")
            .query("SELECT * FROM Users WHERE user_username = @username AND user_oauth IS NULL")

        if (response.recordset.length > 0) {
            result(null, response.recordset)
        } else {
            result("No hay datos del usuario " + usernameParam, null)
        }

    } catch (err) {
        result(err, null)
    }
}

/* <=============================== FIND OAUTH USER BY EMAIL ===============================> */
user.findOAuthUserByEmail = async function (email, result) {
    try {
        const pool = await sql.connect(dbConn)
        const response = await pool.request()
            .input("email", sql.NVarChar, email)
            .query("SELECT * FROM Users WHERE user_email = @email AND user_oauth IS NOT NULL")

        if (response.recordset.length > 0) {
            // Devolvemos el primer usuario encontrado ya que el email es único
            result(null, response.recordset[0])
        } else {
            result(null, null)
        }
    } catch (err) {
        result(err, null)
    }
}

/* <======- EXPORTAMOS EL MODELO -======> */
module.exports = user