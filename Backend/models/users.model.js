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
    this.user_height = user.user_height
    this.user_weight = user.user_weight
    this.user_objective = user.user_objective
    this.user_points = user.user_points
    this.user_role = user.user_role
}

/* <=============================== FIND ALL ===============================> */
user.findAll = async function (result) {
    try {
        const pool = await sql.connect(dbConn)
        const response = await pool.request().query("SELECT * FROM Users")
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

        const request = pool.request()
        request.input("id", sql.Int, id)
        request.input("username", sql.NVarChar, updateUser.user_username)
        request.input("name", sql.NVarChar, updateUser.user_name)
        request.input("password", sql.NVarChar, updateUser.user_password)
        request.input("email", sql.NVarChar, updateUser.user_email)
        request.input("height", sql.Decimal(5,2), updateUser.user_height)
        request.input("weight", sql.Decimal(5,2), updateUser.user_weight)
        request.input("objective", sql.Int, updateUser.user_objective)
        request.input("points", sql.Int, updateUser.user_points)

        const sqlQuery = `
            UPDATE Users SET
                user_username = @username,
                user_name = @name,
                user_password = @password,
                user_email = @email,
                user_height = @height,
                user_weight = @weight,
                user_objective = @objective,
                user_points = @points
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

        const request = pool.request()
        request.input("username", sql.NVarChar, newUser.user_username)
        request.input("name", sql.NVarChar, newUser.user_name)
        request.input("password", sql.NVarChar, newUser.user_password)
        request.input("birthdate", sql.Date, newUser.user_birthdate)
        request.input("email", sql.NVarChar, newUser.user_email)
        request.input("height", sql.Decimal(5,2), newUser.user_height)
        request.input("weight", sql.Decimal(5,2), newUser.user_weight)
        request.input("objective", sql.Int, newUser.user_objective)

        const sqlQuery = `
            INSERT INTO Users (
                user_username, user_name, user_password, user_birthdate,
                user_email, user_height, user_weight,
                user_objective, user_role
            )
            OUTPUT INSERTED.*
            VALUES (
                @username, @name, @password, @birthdate,
                @email, @height, @weight,
                @objective, 0
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
user.findByUsername = async function (usernameParam, result) {
    try {
        const pool = await sql.connect(dbConn)
        const response = await pool.request()
            .input("username", sql.VarChar, usernameParam)
            .query("SELECT * FROM Users WHERE user_username = @username")

        if (response.recordset.length > 0) {
            result(null, response.recordset)
        } else {
            result("No hay datos del usuario " + usernameParam, null)
        }

    } catch (err) {
        result(err, null)
    }
}

/* <======- EXPORTAMOS EL MODELO -======> */
module.exports = user