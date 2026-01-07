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
        const response = await pool.request().query("SELECT * FROM Friend_Requests")
        result(null, response.recordset)
        sql.close()
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
            .query("SELECT * FROM Friend_Requests WHERE frequest_id = @id")

        if (response.recordset.length > 0) {
            result(null, response.recordset[0])
        } else {
            result({ err: "No hay datos" }, null)
        }

        sql.close()
    } catch (err) {
        result(err, null)
        sql.close()
    }
}

/* <=============================== CREATE ===============================> */
fRequest.create = async (newFRequest, result) => {
    try {
        const pool = await sql.connect(dbConn)

        const request = pool.request()
        request.input("id", sql.Int, id)
        request.input("sender", sql.Int, newFRequest.frequest_sender)
        request.input("receiver", sql.Int, newFRequest.frequest_receiver)
        request.input("status", sql.Int, newFRequest.frequest_status)

        const sqlQuery = `
            INSERT INTO Friend_Requests (
                frequest_sender, frequest_receiver, frequest_status
            )
            VALUES (
                @sender, @receiver, @status
            )
        `
    } catch (err) {
        result(err, null)
        sql.close()
    }
}

/* <=============================== DELETE ===============================> */
fRequest.delete = async function (id, result) {
    try {
        const pool = await sql.connect(dbConn)
        const response = await pool.request()
            .input("id", sql.Int, id)
            .query("DELETE FROM Friend_Requests WHERE frequest_id = @id")

        result(null, response)
        sql.close()
    } catch (err) {
        result(err, null)
        sql.close()
    }
}

/* <======- EXPORTAMOS EL MODELO -======> */
module.exports = routine