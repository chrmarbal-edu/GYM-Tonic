/* <=============================== DEPENDENCIAS ===============================> */
const dbConn = require("../utils/mssql.config")
const sql = require("mssql")

/* <=============================== CONSTRUCTOR ===============================> */
let routine = function(routine){
    this.routine_id = routine.routine_id // AUTO INCREMENTAL
    this.routine_name = routine.routine_name
}

/* <=============================== FIND ALL ===============================> */
routine.findAll = async (result) => {
    try {
        const pool = await sql.connect(dbConn)
        const response = await pool.request().query("SELECT * FROM Routines")
        result(null, response.recordset)
        
    } catch (err) {
        result(err, null)
    }
}

/* <=============================== FIND BY ID ===============================> */
routine.findById = async function (id, result) {
    try {
        console.log(id)
        const pool = await sql.connect(dbConn)
        const response = await pool.request()
            .input("id", sql.Int, id)
            .query("SELECT * FROM Routines WHERE routine_id = @id")

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
routine.updateById = async (id, updateRoutine, result) => {
    try {
        const pool = await sql.connect(dbConn)

        const request = pool.request()
        request.input("id", sql.Int, id)
        request.input("name", sql.VarChar, updateRoutine.routine_name)

        const sqlQuery = `
            UPDATE Routines SET
                routine_name = @name
            WHERE routine_id = @id
        `

        const response = await request.query(sqlQuery)
        result(null, response)
        
    } catch (err) {
        result(err, null)
        
    }
}

/* <=============================== CREATE ===============================> */
routine.create = async (newRoutine, result) => {
    try {
        const pool = await sql.connect(dbConn)

        const request = pool.request()
        request.input("id", sql.Int, id)
        request.input("name", sql.VarChar, newRoutine.routine_name)

        const sqlQuery = `
            INSERT INTO Routines (
                routine_name
            )
            VALUES (
                @name
            )
        `
    } catch (err) {
        result(err, null)
        
    }
}

/* <=============================== DELETE ===============================> */
routine.delete = async function (id, result) {
    try {
        const pool = await sql.connect(dbConn)
        const response = await pool.request()
            .input("id", sql.Int, id)
            .query("DELETE FROM Routines WHERE routine_id = @id")

        result(null, response)
        
    } catch (err) {
        result(err, null)
        
    }
}

/* <======- EXPORTAMOS EL MODELO -======> */
module.exports = routine