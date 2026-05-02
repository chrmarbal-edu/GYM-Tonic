/* <=============================== DEPENDENCIAS ===============================> */
const dbConn = require("../utils/mssql.config")
const sql = require("mssql")

/* <=============================== CONSTRUCTOR ===============================> */
let userRoutine = function(userRoutine){
    this.user_x_routine_id = userRoutine.user_x_routine_id // AUTO INCREMENTAL
    this.user_x_routine_userid = userRoutine.user_x_routine_userid 
    this.user_x_routine_routineid = userRoutine.user_x_routine_routineid
}

/* <=============================== FIND ALL ===============================> */
userRoutine.findAll = async (result) => {
    try {
        const pool = await sql.connect(dbConn)
        const response = await pool.request().query("SELECT * FROM User_X_Routine")
        result(null, response.recordset)
        
    } catch (err) {
        result(err, null)
    }
}

/* <=============================== FIND BY ID ===============================> */
userRoutine.findById = async function (id, result) {
    try {
        const pool = await sql.connect(dbConn)
        const response = await pool.request()
            .input("id", sql.Int, id)
            .query("SELECT * FROM User_X_Routine WHERE user_x_routine_id = @id")

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
userRoutine.create = async (newUserRoutine, result) => {
    try {
        const pool = await sql.connect(dbConn)

        const request = pool.request()
        request.input("id", sql.Int, id)
        request.input("userId", sql.Int, newUserRoutine.routine_x_exercise_userid)
        request.input("routineId", sql.Int, newUserRoutine.user_x_routine_routineid)

        const sqlQuery = `
            INSERT INTO User_X_Routine (
                user_x_routine_userid, user_x_routine_routineid
            )
            VALUES (
                @userId, @routineId
            )
        `
    } catch (err) {
        result(err, null)
        
    }
}

/* <=============================== DELETE ===============================> */
routineExercise.delete = async function (id, result) {
    try {
        const pool = await sql.connect(dbConn)
        const response = await pool.request()
            .input("id", sql.Int, id)
            .query("DELETE FROM User_X_Routine WHERE user_x_routine_id = @id")

        result(null, response)
        
    } catch (err) {
        result(err, null)
        
    }
}

/* <======- EXPORTAMOS EL MODELO -======> */
module.exports = routine