/* <=============================== DEPENDENCIAS ===============================> */
const dbConn = require("../utils/mssql.config")
const sql = require("mssql")

/* <=============================== CONSTRUCTOR ===============================> */
let routineExercise = function(routineExercise){
    this.routine_x_exercise_id = routineExercise.routine_x_exercise_id // AUTO INCREMENTAL
    this.routine_x_exercise_routineid = routineExercise.routine_x_exercise_routineid 
    this.routine_x_exercise_exerciseid = routineExercise.routine_x_exercise_exerciseid
}

/* <=============================== FIND ALL ===============================> */
routineExercise.findAll = async (result) => {
    try {
        const pool = await sql.connect(dbConn)
        const response = await pool.request().query("SELECT * FROM Routine_X_Exercise")
        result(null, response.recordset)
        
    } catch (err) {
        result(err, null)
    }
}

/* <=============================== FIND BY ID ===============================> */
routineExercise.findById = async function (id, result) {
    try {
        const pool = await sql.connect(dbConn)
        const response = await pool.request()
            .input("id", sql.Int, id)
            .query("SELECT * FROM Routine_X_Exercise WHERE routine_x_exercise_id = @id")

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
routineExercise.create = async (newRoutineExercise, result) => {
    try {
        const pool = await sql.connect(dbConn)

        const request = pool.request()
        request.input("id", sql.Int, id)
        request.input("routineId", sql.Int, newRoutineExercise.routine_x_exercise_routineid)
        request.input("exerciseId", sql.Int, newRoutineExercise.routine_x_exercise_exerciseid)

        const sqlQuery = `
            INSERT INTO Routine_X_Exercise (
                routine_x_exercise_routineid, routine_x_exercise_exerciseid
            )
            VALUES (
                @routineId, @exerciseId
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
            .query("DELETE FROM Routine_X_Exercise WHERE routine_x_exercise_id = @id")

        result(null, response)
        
    } catch (err) {
        result(err, null)
        
    }
}

/* <======- EXPORTAMOS EL MODELO -======> */
module.exports = routine