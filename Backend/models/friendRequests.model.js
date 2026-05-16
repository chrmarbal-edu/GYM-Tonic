/* <=============================== DEPENDENCIAS ===============================> */
const dbConn = require("../utils/mssql.config")
const sql = require("mssql")

/* <=============================== CONSTRUCTOR ===============================> */
let fRequest = function(fRequest){
    this.frequest_id = fRequest.frequest_id // AUTO INCREMENTAL
    this.frequest_sender = fRequest.frequest_sender
    this.frequest_receiver = fRequest.frequest_receiver
    this.frequest_status = fRequest.frequest_status
}

/* <=============================== FIND ALL ===============================> */
fRequest.findAll = async (result) => {
    try {
        const pool = await sql.connect(dbConn)
        const response = await pool.request().query("SELECT * FROM Frequest")
        result(null, response.recordset)
    } catch (err) {
        result(err, null)
    }
}

/* <=============================== FIND BY USER ID (incoming + outgoing) ===============================> */
fRequest.findByUserId = async function (userId, result) {
    try {
        const pool = await sql.connect(dbConn)
        const response = await pool.request()
            .input("userId", sql.Int, userId)
            .query(`
                SELECT
                    fr.frequest_id,
                    fr.frequest_sender,
                    fr.frequest_receiver,
                    fr.frequest_status,
                    sender.user_username AS sender_username,
                    sender.user_name     AS sender_name,
                    sender.user_picture  AS sender_picture,
                    receiver.user_username AS receiver_username,
                    receiver.user_name     AS receiver_name,
                    receiver.user_picture  AS receiver_picture
                FROM Frequest fr
                JOIN Users sender   ON sender.user_id   = fr.frequest_sender
                JOIN Users receiver ON receiver.user_id = fr.frequest_receiver
                WHERE (fr.frequest_sender = @userId OR fr.frequest_receiver = @userId)
                  AND fr.frequest_status = 0
            `)

        result(null, response.recordset)
    } catch (err) {
        result(err, null)
    }
}

/* <=============================== FIND BY ID ===============================> */
fRequest.findById = async function (id, result) {
    try {
        const pool = await sql.connect(dbConn)
        const response = await pool.request()
            .input("id", sql.Int, id)
            .query("SELECT * FROM Frequest WHERE frequest_id = @id")

        if (response.recordset.length > 0) {
            result(null, response.recordset[0])
        } else {
            result({ err: "No hay datos" }, null)
        }

    } catch (err) {
        result(err, null)
    }
}

/* <=============================== CREATE ===============================> */
fRequest.create = async (newFRequest, result) => {
    try {
        const pool = await sql.connect(dbConn)

        const request = pool.request()
        request.input("sender", sql.Int, newFRequest.frequest_sender)
        request.input("receiver", sql.Int, newFRequest.frequest_receiver)

        const sqlQuery = `
            INSERT INTO Frequest (
                frequest_sender, frequest_receiver, frequest_status
            )
            VALUES (
                @sender, @receiver, 0
            )
        `

        const response = await request.query(sqlQuery)
        result(null, response)
    } catch (err) {
        result(err, null)
    }
}

/* <=============================== UPDATE ===============================> */
fRequest.update = async function (id, status, result) {
    try {
        const pool = await sql.connect(dbConn)

        const request = pool.request()
        request.input("id", sql.Int, id)
        request.input("status", sql.Int, status)

        const sqlQuery = `
            UPDATE Frequest 
            SET frequest_status = @status 
            WHERE frequest_id = @id
        `

        const response = await request.query(sqlQuery)
        result(null, response)
    } catch (err) {
        result(err, null)
    }
}

/* <=============================== DELETE ===============================> */
fRequest.delete = async function (id, result) {
    try {
        const pool = await sql.connect(dbConn)
        const response = await pool.request()
            .input("id", sql.Int, id)
            .query("DELETE FROM Frequest WHERE frequest_id = @id")

        result(null, response)
    } catch (err) {
        result(err, null)
    }
}

/* <======- EXPORTAMOS EL MODELO -======> */
module.exports = fRequest