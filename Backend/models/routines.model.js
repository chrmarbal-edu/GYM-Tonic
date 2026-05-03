/* <=============================== DEPENDENCIAS ===============================> */
const dbConn = require("../utils/mssql.config")
const sql = require("mssql")

/* <=============================== CONSTRUCTOR ===============================> */
let routine = function(routine){
    this.routine_id = routine.routine_id // AUTO INCREMENTAL
    this.routine_name = routine.routine_name
}

const normalizeRoutineNameOrSlug = (value = "") => {
    return value.toLowerCase().replace(/[\s_-]+/g, "")
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

/* <=============================== FIND ALL WITH EXERCISE SUMMARY ===============================> */
routine.findAllWithExerciseSummary = async (result) => {
    try {
        const pool = await sql.connect(dbConn)
        const response = await pool.request().query(`
            SELECT
                r.routine_id,
                r.routine_name,
                COUNT(rxe.routine_x_exercise_exerciseid) AS exercises_count,
                ISNULL(SUM(CASE WHEN e.exercise_type = 0 THEN 1 ELSE 0 END), 0) AS strength_exercises_count,
                ISNULL(SUM(CASE WHEN e.exercise_type = 1 THEN 1 ELSE 0 END), 0) AS cardio_exercises_count,
                ISNULL(SUM(CASE WHEN e.exercise_type = 2 THEN 1 ELSE 0 END), 0) AS flexibility_exercises_count,
                MAX(e.exercise_image) AS routine_image
            FROM Routines r
            LEFT JOIN Routine_X_Exercise rxe
                ON r.routine_id = rxe.routine_x_exercise_routineid
            LEFT JOIN Exercises e
                ON rxe.routine_x_exercise_exerciseid = e.exercise_id
            GROUP BY r.routine_id, r.routine_name
            ORDER BY r.routine_id DESC
        `)

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

/* <=============================== FIND ROUTINE WITH EXERCISES BY ID ===============================> */
routine.findByIdWithExercises = async function (id, result) {
    try {
        const pool = await sql.connect(dbConn)
        const response = await pool.request()
            .input("id", sql.Int, id)
            .query(`
                SELECT
                    r.routine_id,
                    r.routine_name,
                    e.exercise_id,
                    e.exercise_name,
                    e.exercise_description,
                    e.exercise_type,
                    e.exercise_video,
                    e.exercise_image
                FROM Routines r
                LEFT JOIN Routine_X_Exercise rxe
                    ON r.routine_id = rxe.routine_x_exercise_routineid
                LEFT JOIN Exercises e
                    ON rxe.routine_x_exercise_exerciseid = e.exercise_id
                WHERE r.routine_id = @id
            `)

        if (response.recordset.length === 0) {
            return result({ err: "No hay datos" }, null)
        }

        const routineWithExercises = {
            routine_id: response.recordset[0].routine_id,
            routine_name: response.recordset[0].routine_name,
            exercises: []
        }

        response.recordset.forEach((row) => {
            if (row.exercise_id === null || row.exercise_id === undefined) {
                return
            }

            routineWithExercises.exercises.push({
                exercise_id: row.exercise_id,
                exercise_name: row.exercise_name,
                exercise_description: row.exercise_description,
                exercise_type: row.exercise_type,
                exercise_video: row.exercise_video,
                exercise_image: row.exercise_image
            })
        })

        result(null, routineWithExercises)
    } catch (err) {
        result(err, null)
    }
}

/* <=============================== FIND BY NAME OR SLUG ===============================> */
routine.findByNameOrSlug = async function (nameOrSlug, result) {
    try {
        const normalizedNameOrSlug = normalizeRoutineNameOrSlug(nameOrSlug)
        const pool = await sql.connect(dbConn)
        const response = await pool.request()
            .input("nameOrSlug", sql.NVarChar, nameOrSlug)
            .input("normalizedNameOrSlug", sql.NVarChar, normalizedNameOrSlug)
            .query(`
                SELECT TOP 1 * FROM Routines
                WHERE LOWER(routine_name) COLLATE Latin1_General_CI_AI = LOWER(@nameOrSlug) COLLATE Latin1_General_CI_AI
                    OR LOWER(REPLACE(REPLACE(REPLACE(routine_name, ' ', ''), '-', ''), '_', '')) COLLATE Latin1_General_CI_AI = @normalizedNameOrSlug COLLATE Latin1_General_CI_AI
            `)

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
