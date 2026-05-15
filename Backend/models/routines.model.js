/* <=============================== DEPENDENCIAS ===============================> */
const dbConn = require("../utils/mssql.config")
const sql = require("mssql")

/* <=============================== CONSTRUCTOR ===============================> */
let routine = function (routineRow) {
    this.routine_id = routineRow.routine_id
    this.routine_name = routineRow.routine_name
    this.routine_is_group_routine = routineRow.routine_is_group_routine
    this.routine_groupid = routineRow.routine_groupid
}

const normalizeRoutineNameOrSlug = (value = "") => {
    return value.toLowerCase().replace(/[\s_-]+/g, "")
}

const repsByExerciseType = (exerciseType) => {
    if (exerciseType === 1) {
        return "x20"
    }

    if (exerciseType === 2) {
        return "x30s"
    }

    return "x12"
}

const toImageKey = (image = "") => {
    if (!image || typeof image !== "string") {
        return ""
    }

    return image.replace(/\.[^/.]+$/, "")
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
                r.routine_is_group_routine,
                r.routine_groupid,
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
            GROUP BY r.routine_id, r.routine_name, r.routine_is_group_routine, r.routine_groupid
            ORDER BY r.routine_id DESC
        `)

        result(null, response.recordset)
    } catch (err) {
        result(err, null)
    }
}

/* <=============================== FIND BY GROUP ID ===============================> */
routine.findByGroupId = async function (groupId, result) {
    try {
        const pool = await sql.connect(dbConn)
        const response = await pool
            .request()
            .input("groupId", sql.Int, groupId)
            .query(`
                SELECT *
                FROM Routines
                WHERE routine_is_group_routine = 1 AND routine_groupid = @groupId
                ORDER BY routine_id DESC
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
                    r.routine_is_group_routine,
                    r.routine_groupid,
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
            routine_id: String(response.recordset[0].routine_id),
            routine_name: response.recordset[0].routine_name,
            routine_is_group_routine: response.recordset[0].routine_is_group_routine,
            routine_groupid: response.recordset[0].routine_groupid,
            exercises: []
        }

        response.recordset.forEach((row) => {
            if (row.exercise_id === null || row.exercise_id === undefined) {
                return
            }

            routineWithExercises.exercises.push({
                exercise_id: String(row.exercise_id),
                exercise_name: row.exercise_name,
                reps: repsByExerciseType(row.exercise_type),
                image_key: toImageKey(row.exercise_image),
                instructions: row.exercise_description ? [row.exercise_description] : []
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
        const rawIs = updateRoutine.routine_is_group_routine
        const isGroup = rawIs === 1 || rawIs === true ? 1 : 0
        request.input("isGroup", sql.Int, isGroup)
        request.input("groupId", sql.Int, updateRoutine.routine_groupid ?? null)

        const sqlQuery = `
            UPDATE Routines SET
                routine_name = @name,
                routine_is_group_routine = @isGroup,
                routine_groupid = CASE WHEN @isGroup = 1 THEN @groupId ELSE NULL END
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

        const routineName = newRoutine.routine_name ?? newRoutine.name
        const isGroup =
            newRoutine.routine_is_group_routine === 1 ||
            newRoutine.routine_is_group_routine === true
                ? 1
                : 0
        const groupId =
            isGroup === 1 && newRoutine.routine_groupid != null
                ? newRoutine.routine_groupid
                : null

        const request = pool.request()
        request.input("name", sql.NVarChar, routineName)
        request.input("isGroup", sql.Int, isGroup)
        request.input("groupId", sql.Int, groupId)

        const sqlQuery = `
            INSERT INTO Routines (
                routine_name, routine_is_group_routine, routine_groupid
            )
            OUTPUT INSERTED.*
            VALUES (
                @name, @isGroup, @groupId
            )
        `

        const response = await request.query(sqlQuery)
        result(null, response.recordset[0])
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
