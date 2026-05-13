/* <=============================== DEPENDENCIAS ===============================> */
const dbConn = require("../utils/mssql.config")
const sql = require("mssql")

/* <=============================== CONSTRUCTOR ===============================> */
let userRoutine = function (userRoutineRow) {
    this.user_x_routine_id = userRoutineRow.user_x_routine_id
    this.user_x_routine_userid = userRoutineRow.user_x_routine_userid
    this.user_x_routine_routineid = userRoutineRow.user_x_routine_routineid
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
        const response = await pool
            .request()
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
        request.input("userId", sql.Int, newUserRoutine.user_x_routine_userid)
        request.input("routineId", sql.Int, newUserRoutine.user_x_routine_routineid)

        const sqlQuery = `
            INSERT INTO User_X_Routine (
                user_x_routine_userid, user_x_routine_routineid
            )
            OUTPUT INSERTED.*
            VALUES (
                @userId, @routineId
            )
        `

        const response = await request.query(sqlQuery)
        result(null, response.recordset[0])
    } catch (err) {
        result(err, null)
    }
}

/* <=============================== DELETE BY ID ===============================> */
userRoutine.deleteById = async function (id, result) {
    try {
        const pool = await sql.connect(dbConn)
        const response = await pool
            .request()
            .input("id", sql.Int, id)
            .query("DELETE FROM User_X_Routine WHERE user_x_routine_id = @id")

        result(null, response.rowsAffected[0] || 0)
    } catch (err) {
        result(err, null)
    }
}

/* <=============================== DELETE LINKS FOR ROUTINE IN GROUP ===============================> */
userRoutine.deleteByRoutineForGroupMembers = async function (groupId, routineId, result) {
    try {
        const pool = await sql.connect(dbConn)
        const response = await pool
            .request()
            .input("groupId", sql.Int, groupId)
            .input("routineId", sql.Int, routineId)
            .query(`
                DELETE ur
                FROM User_X_Routine ur
                INNER JOIN Group_x_user gx
                    ON gx.Group_x_user_userid = ur.user_x_routine_userid
                    AND gx.Group_x_user_groupid = @groupId
                WHERE ur.user_x_routine_routineid = @routineId
            `)

        result(null, response.rowsAffected[0] || 0)
    } catch (err) {
        result(err, null)
    }
}

/* <======- EXPORTAMOS EL MODELO -======> */
module.exports = userRoutine
