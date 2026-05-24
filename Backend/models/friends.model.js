/* <=============================== DEPENDENCIAS ===============================> */
const dbConn = require("../utils/mssql.config")
const sql = require("mssql")

/* <=============================== CONSTRUCTOR ===============================> */
let friend = function(friend){
    this.friend_id = friend.friend_id // AUTO INCREMENTAL
    this.friend_userid1 = friend.friend_userid1
    this.friend_userid2 = friend.friend_userid2
}

/* <=============================== FIND ALL ===============================> */
friend.findAll = async (result) => {
    try {
        const pool = await sql.connect(dbConn)
        const response = await pool.request().query("SELECT * FROM Friends")
        result(null, response.recordset)
        
    } catch (err) {
        result(err, null)
    }
}

/* <=============================== FIND BY ID ===============================> */
friend.findById = async function (id, result) {
    try {
        const pool = await sql.connect(dbConn)
        const response = await pool.request()
            .input("id", sql.Int, id)
            .query("SELECT * FROM Friends WHERE friend_id = @id")

        if (response.recordset.length > 0) {
            result(null, response.recordset[0])
        } else {
            result({ err: "No hay datos" }, null)
        }

        
    } catch (err) {
        result(err, null)
        
    }
}

/* <=============================== FIND FRIENDS BY USER ID ===============================> */
// Devolvemos tambien friend_id (la PK de la tabla Friends) para que el front
// pueda eliminar la amistad sin tener que consultarla aparte.
friend.findByUserId = async function (userId, result) {
    try {
        const pool = await sql.connect(dbConn)
        const response = await pool.request()
            .input("userId", sql.Int, userId)
            .query(`
                SELECT
                    f.friend_id,
                    u.user_id,
                    u.user_username,
                    u.user_name,
                    u.user_picture
                FROM Friends f
                JOIN Users u
                ON u.user_id = CASE
                    WHEN f.friend_userid1 = @userId THEN f.friend_userid2
                    ELSE f.friend_userid1
                END
                WHERE f.friend_userid1 = @userId OR f.friend_userid2 = @userId
            `)

        result(null, response.recordset)

    } catch (err) {
        result(err, null)

    }
}

/* <=============================== CREATE ===============================> */
friend.create = async (newFriend, result) => {
    try {
        const pool = await sql.connect(dbConn)

        const request = pool.request()
            .input("userId1", sql.Int, newFriend.friend_userid1)
            .input("userId2", sql.Int, newFriend.friend_userid2)

        const sqlQuery = `
            INSERT INTO Friends (
                friend_userid1, friend_userid2
            )
            OUTPUT INSERTED.*
            VALUES (
                @userId1, @userId2
            )
        `

        const response = await request.query(sqlQuery)
    
        result(null, response.recordset[0])
        
    } catch (err) {
        result(err, null)
        
    }
}

/* <=============================== DELETE ===============================> */
friend.delete = async function (id, result) {
    try {
        const pool = await sql.connect(dbConn)
        const response = await pool.request()
            .input("id", sql.Int, id)
            .query("DELETE FROM Friends WHERE friend_id = @id")

        result(null, response)
        
    } catch (err) {
        result(err, null)
        
    }
}

/* <======- EXPORTAMOS EL MODELO -======> */
module.exports = friend