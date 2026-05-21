/* <=============================== DEPENDENCIAS ===============================> */
const dbConn = require("../utils/mssql.config")
const sql = require("mssql")
const { isInvalidColumnError } = require("../utils/sqlErrors.js")

/* <=============================== CONSTRUCTOR ===============================> */
let routine = function (routineRow) {
    this.routine_id = routineRow.routine_id
    this.routine_name = routineRow.routine_name
    this.routine_image = routineRow.routine_image
    this.routine_creator_id = routineRow.routine_creator_id
    this.routine_is_personal_routine = routineRow.routine_is_personal_routine
    this.routine_is_group_routine = routineRow.routine_is_group_routine
    this.routine_groupid = routineRow.routine_groupid
}

const normalizeRoutineNameOrSlug = (value = "") => {
    return value.toLowerCase().replace(/[\s_-]+/g, "")
}

/* <=============================== FIND ALL WITH EXERCISE SUMMARY ===============================> */
routine.findAllWithExerciseSummary = async (result) => {
    const summaryQueryFull = `
        SELECT
            r.routine_id,
            r.routine_name,
            r.routine_image,
            r.routine_creator_id,
            r.routine_is_personal_routine,
            r.routine_is_group_routine,
            r.routine_groupid,
            COUNT(rxe.routine_x_exercise_exerciseid) AS exercises_count,
            ISNULL(SUM(CASE WHEN e.exercise_type BETWEEN 1 AND 10 THEN 1 ELSE 0 END), 0) AS strength_exercises_count,
            ISNULL(SUM(CASE WHEN e.exercise_type = 0 THEN 1 ELSE 0 END), 0) AS cardio_exercises_count
        FROM Routines r
        LEFT JOIN Routine_X_Exercise rxe
            ON r.routine_id = rxe.routine_x_exercise_routineid
        LEFT JOIN Exercises e
            ON rxe.routine_x_exercise_exerciseid = e.exercise_id
        WHERE r.routine_is_group_routine = 0
            AND r.routine_is_personal_routine = 0
            AND r.routine_creator_id IS NULL
        GROUP BY
            r.routine_id,
            r.routine_name,
            r.routine_image,
            r.routine_creator_id,
            r.routine_is_personal_routine,
            r.routine_is_group_routine,
            r.routine_groupid
        ORDER BY r.routine_id DESC
    `

    const summaryQueryWithoutPersonalFlag = `
        SELECT
            r.routine_id,
            r.routine_name,
            r.routine_image,
            r.routine_creator_id,
            CAST(0 AS INT) AS routine_is_personal_routine,
            r.routine_is_group_routine,
            r.routine_groupid,
            COUNT(rxe.routine_x_exercise_exerciseid) AS exercises_count,
            ISNULL(SUM(CASE WHEN e.exercise_type BETWEEN 1 AND 10 THEN 1 ELSE 0 END), 0) AS strength_exercises_count,
            ISNULL(SUM(CASE WHEN e.exercise_type = 0 THEN 1 ELSE 0 END), 0) AS cardio_exercises_count
        FROM Routines r
        LEFT JOIN Routine_X_Exercise rxe
            ON r.routine_id = rxe.routine_x_exercise_routineid
        LEFT JOIN Exercises e
            ON rxe.routine_x_exercise_exerciseid = e.exercise_id
        WHERE r.routine_is_group_routine = 0
            AND r.routine_creator_id IS NULL
        GROUP BY
            r.routine_id,
            r.routine_name,
            r.routine_image,
            r.routine_creator_id,
            r.routine_is_group_routine,
            r.routine_groupid
        ORDER BY r.routine_id DESC
    `

    const summaryQueryLegacy = `
        SELECT
            r.routine_id,
            r.routine_name,
            r.routine_image,
            r.routine_is_group_routine,
            r.routine_groupid,
            COUNT(rxe.routine_x_exercise_exerciseid) AS exercises_count,
            ISNULL(SUM(CASE WHEN e.exercise_type BETWEEN 1 AND 10 THEN 1 ELSE 0 END), 0) AS strength_exercises_count,
            ISNULL(SUM(CASE WHEN e.exercise_type = 0 THEN 1 ELSE 0 END), 0) AS cardio_exercises_count
        FROM Routines r
        LEFT JOIN Routine_X_Exercise rxe
            ON r.routine_id = rxe.routine_x_exercise_routineid
        LEFT JOIN Exercises e
            ON rxe.routine_x_exercise_exerciseid = e.exercise_id
        WHERE r.routine_is_group_routine = 0
        GROUP BY
            r.routine_id,
            r.routine_name,
            r.routine_image,
            r.routine_is_group_routine,
            r.routine_groupid
        ORDER BY r.routine_id DESC
    `

    try {
        const pool = await sql.connect(dbConn)

        const queries = [summaryQueryFull, summaryQueryWithoutPersonalFlag, summaryQueryLegacy]

        for (const query of queries) {
            try {
                const response = await pool.request().query(query)
                return result(null, response.recordset)
            } catch (err) {
                if (!isInvalidColumnError(err)) {
                    throw err
                }
            }
        }

        return result(new Error("No se pudo consultar rutinas del catálogo"), null)
    } catch (err) {
        result(err, null)
    }
}

/* <=============================== FIND PERSONAL BY USER ===============================> */
routine.findPersonalByUserId = async function (userId, result) {
    const queryFull = `
        SELECT
            r.routine_id,
            r.routine_name,
            r.routine_image,
            r.routine_creator_id,
            r.routine_is_personal_routine,
            r.routine_is_group_routine,
            r.routine_groupid,
            COUNT(rxe.routine_x_exercise_exerciseid) AS exercises_count,
            ISNULL(SUM(CASE WHEN e.exercise_type BETWEEN 1 AND 10 THEN 1 ELSE 0 END), 0) AS strength_exercises_count,
            ISNULL(SUM(CASE WHEN e.exercise_type = 0 THEN 1 ELSE 0 END), 0) AS cardio_exercises_count
        FROM Routines r
        LEFT JOIN Routine_X_Exercise rxe
            ON r.routine_id = rxe.routine_x_exercise_routineid
        LEFT JOIN Exercises e
            ON rxe.routine_x_exercise_exerciseid = e.exercise_id
        WHERE r.routine_is_personal_routine = 1
            AND r.routine_is_group_routine = 0
            AND r.routine_creator_id = @userId
        GROUP BY
            r.routine_id,
            r.routine_name,
            r.routine_image,
            r.routine_creator_id,
            r.routine_is_personal_routine,
            r.routine_is_group_routine,
            r.routine_groupid
        ORDER BY r.routine_id DESC
    `

    const queryLegacy = `
        SELECT
            r.routine_id,
            r.routine_name,
            r.routine_image,
            r.routine_creator_id,
            CAST(1 AS INT) AS routine_is_personal_routine,
            r.routine_is_group_routine,
            r.routine_groupid,
            COUNT(rxe.routine_x_exercise_exerciseid) AS exercises_count,
            ISNULL(SUM(CASE WHEN e.exercise_type BETWEEN 1 AND 10 THEN 1 ELSE 0 END), 0) AS strength_exercises_count,
            ISNULL(SUM(CASE WHEN e.exercise_type = 0 THEN 1 ELSE 0 END), 0) AS cardio_exercises_count
        FROM Routines r
        LEFT JOIN Routine_X_Exercise rxe
            ON r.routine_id = rxe.routine_x_exercise_routineid
        LEFT JOIN Exercises e
            ON rxe.routine_x_exercise_exerciseid = e.exercise_id
        WHERE r.routine_is_group_routine = 0
            AND r.routine_creator_id = @userId
        GROUP BY
            r.routine_id,
            r.routine_name,
            r.routine_image,
            r.routine_creator_id,
            r.routine_is_group_routine,
            r.routine_groupid
        ORDER BY r.routine_id DESC
    `

    try {
        const pool = await sql.connect(dbConn)
        const request = pool.request().input("userId", sql.Int, userId)

        try {
            const response = await request.query(queryFull)
            return result(null, response.recordset)
        } catch (err) {
            if (!isInvalidColumnError(err)) {
                throw err
            }
            const response = await pool
                .request()
                .input("userId", sql.Int, userId)
                .query(queryLegacy)
            return result(null, response.recordset)
        }
    } catch (err) {
        result(err, null)
    }
}

/* <=============================== FIND BY GROUP ID ===============================> */
routine.findByGroupId = async function (groupId, result) {
    const queryFull = `
        SELECT
            routine_id,
            routine_name,
            routine_image,
            routine_creator_id,
            routine_is_group_routine,
            routine_groupid
        FROM Routines
        WHERE routine_is_group_routine = 1 AND routine_groupid = @groupId
        ORDER BY routine_id DESC
    `

    const queryLegacy = `
        SELECT
            routine_id,
            routine_name,
            routine_is_group_routine,
            routine_groupid
        FROM Routines
        WHERE routine_is_group_routine = 1 AND routine_groupid = @groupId
        ORDER BY routine_id DESC
    `

    try {
        const pool = await sql.connect(dbConn)
        const request = pool.request().input("groupId", sql.Int, groupId)

        try {
            const response = await request.query(queryFull)
            return result(null, response.recordset)
        } catch (err) {
            if (!isInvalidColumnError(err)) {
                throw err
            }
            const response = await pool
                .request()
                .input("groupId", sql.Int, groupId)
                .query(queryLegacy)
            return result(null, response.recordset)
        }
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
    const queryFull = `
        SELECT
            r.routine_id,
            r.routine_name,
            r.routine_image,
            r.routine_creator_id,
            r.routine_is_personal_routine,
            r.routine_is_group_routine,
            r.routine_groupid,
            e.exercise_id,
            e.exercise_name,
            e.exercise_description,
            e.exercise_type,
            e.exercise_video,
            e.exercise_image,
            rxe.routine_x_exercise_reps,
            rxe.routine_x_exercise_sets
        FROM Routines r
        LEFT JOIN Routine_X_Exercise rxe
            ON r.routine_id = rxe.routine_x_exercise_routineid
        LEFT JOIN Exercises e
            ON rxe.routine_x_exercise_exerciseid = e.exercise_id
        WHERE r.routine_id = @id
    `

    const queryLegacy = `
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
            e.exercise_image,
            rxe.routine_x_exercise_reps,
            rxe.routine_x_exercise_sets
        FROM Routines r
        LEFT JOIN Routine_X_Exercise rxe
            ON r.routine_id = rxe.routine_x_exercise_routineid
        LEFT JOIN Exercises e
            ON rxe.routine_x_exercise_exerciseid = e.exercise_id
        WHERE r.routine_id = @id
    `

    try {
        const pool = await sql.connect(dbConn)
        let response

        try {
            response = await pool.request().input("id", sql.Int, id).query(queryFull)
        } catch (err) {
            if (!isInvalidColumnError(err)) {
                throw err
            }
            response = await pool.request().input("id", sql.Int, id).query(queryLegacy)
        }

        if (response.recordset.length === 0) {
            return result({ err: "No hay datos" }, null)
        }

        const firstRow = response.recordset[0]
        const routineWithExercises = {
            routine_id: firstRow.routine_id,
            routine_name: firstRow.routine_name,
            routine_image: firstRow.routine_image ?? null,
            routine_creator_id: firstRow.routine_creator_id ?? null,
            routine_is_personal_routine: firstRow.routine_is_personal_routine ?? 0,
            routine_is_group_routine: firstRow.routine_is_group_routine,
            routine_groupid: firstRow.routine_groupid,
            exercises: []
        }

        response.recordset.forEach((row) => {
            if (row.exercise_id === null || row.exercise_id === undefined) {
                return
            }

            routineWithExercises.exercises.push({
                exercise_id: String(row.exercise_id),
                exercise_name: row.exercise_name,
                reps: row.routine_x_exercise_reps ?? "12",
                sets: row.routine_x_exercise_sets ?? 3,
                exercise_image: row.exercise_image,
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
        request.input("name", sql.NVarChar, updateRoutine.routine_name)
        request.input("image", sql.NVarChar, updateRoutine.routine_image ?? null)
        const rawIs = updateRoutine.routine_is_group_routine
        const isGroup = rawIs === 1 || rawIs === true ? 1 : 0
        request.input("isGroup", sql.Int, isGroup)
        request.input("groupId", sql.Int, updateRoutine.routine_groupid ?? null)

        const sqlQuery = `
            UPDATE Routines SET
                routine_name = @name,
                routine_image = COALESCE(@image, routine_image),
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
        const isPersonal =
            isGroup === 0 &&
            (newRoutine.routine_is_personal_routine === 1 ||
                newRoutine.routine_is_personal_routine === true)
                ? 1
                : 0
        const groupId =
            isGroup === 1 && newRoutine.routine_groupid != null
                ? newRoutine.routine_groupid
                : null
        const creatorId = newRoutine.routine_creator_id ?? null

        const request = pool.request()
        request.input("name", sql.NVarChar, routineName)
        request.input("image", sql.NVarChar, newRoutine.routine_image ?? null)
        request.input("creatorId", sql.Int, creatorId)
        request.input("isPersonal", sql.Int, isPersonal)
        request.input("isGroup", sql.Int, isGroup)
        request.input("groupId", sql.Int, groupId)

        const sqlQueryFull = `
            INSERT INTO Routines (
                routine_name, routine_image, routine_creator_id,
                routine_is_personal_routine, routine_is_group_routine, routine_groupid
            )
            OUTPUT INSERTED.*
            VALUES (
                @name, @image, @creatorId, @isPersonal, @isGroup, @groupId
            )
        `

        const sqlQueryLegacy = `
            INSERT INTO Routines (
                routine_name, routine_image, routine_creator_id, routine_is_group_routine, routine_groupid
            )
            OUTPUT INSERTED.*
            VALUES (
                @name, @image, @creatorId, @isGroup, @groupId
            )
        `

        let response
        try {
            response = await request.query(sqlQueryFull)
        } catch (err) {
            if (!isInvalidColumnError(err)) {
                throw err
            }
            response = await pool
                .request()
                .input("name", sql.NVarChar, routineName)
                .input("image", sql.NVarChar, newRoutine.routine_image ?? null)
                .input("creatorId", sql.Int, creatorId)
                .input("isGroup", sql.Int, isGroup)
                .input("groupId", sql.Int, groupId)
                .query(sqlQueryLegacy)
        }

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
