/* <=============================== DEPENDENCIAS ===============================> */
const dbConn = require("../utils/mssql.config")
const sql = require("mssql")

/* <=============================== CONSTRUCTOR ===============================> */
let userMission = function(userMission){
    this.user_x_mission_userid = userMission.user_x_mission_userid
    this.user_x_mission_missionid = userMission.user_x_mission_missionid
    this.user_x_mission_expiration = userMission.user_x_mission_expiration
    this.user_x_mission_completed = userMission.user_x_mission_completed
    this.user_x_mission_progress = userMission.user_x_mission_progress
}

/* <=============================== FIND ALL ===============================> */
userMission.findAll = async (result) => {
    try {
        const pool = await sql.connect(dbConn)
        const response = await pool.request().query("SELECT * FROM User_X_Mission")
        result(null, response.recordset)
    } catch (err) {
        result(err, null)
    }
}

/* <=============================== FIND BY ID ===============================> */
userMission.findById = async function (id, result) {
    try {
        const pool = await sql.connect(dbConn)
        const response = await pool.request()
            .input("id", sql.Int, id)
            .query(`
                SELECT * FROM User_X_Mission
                WHERE user_x_mission_id = @id
            `)
        result(null, response.recordset[0])
    } catch (err) {
        result(err, null)
    }
}

/* <=============================== FIND BY USER ID ===============================> */
userMission.findByUserId = async function (userId, result) {
    try {
        const pool = await sql.connect(dbConn)
        const response = await pool.request()
            .input("userId", sql.Int, userId)
            .query(`
                SELECT um.*, m.mission_name, m.mission_points 
                FROM User_X_Mission um
                INNER JOIN Missions m ON um.user_x_mission_missionid = m.mission_id
                WHERE um.user_x_mission_userid = @userId
            `)

        result(null, response.recordset)
    } catch (err) {
        result(err, null)
    }
}


/* <=============================== FIND BY MISSION ID ===============================> */
userMission.findByMissionId = async function (missionId, result) {
    try {
        const pool = await sql.connect(dbConn)
        const response = await pool.request()
            .input("missionId", sql.Int, missionId)
            .query(`
                SELECT um.*, u.username, u.name 
                FROM User_X_Mission um
                INNER JOIN Users u ON um.user_x_mission_userid = u.user_id
                WHERE um.user_x_mission_missionid = @missionId
            `)

        result(null, response.recordset)
    } catch (err) {
        result(err, null)
    }
}

/* <=============================== CREATE ===============================> */
userMission.create = async (newUserMission, result) => {
    try {
        const pool = await sql.connect(dbConn)
        const request = pool.request()
        
        request.input("userId", sql.Int, newUserMission.userId)
        request.input("missionId", sql.Int, newUserMission.missionId)
        request.input("expiration", sql.DateTime, newUserMission.expiration)
        request.input("completed", sql.Bit, newUserMission.completed || 0)
        request.input("progress", sql.Int, newUserMission.progress || 0)

        const sqlQuery = `
            INSERT INTO User_X_Mission (
                user_x_mission_userid, user_x_mission_missionid, user_x_mission_expiration, user_x_mission_completed, user_x_mission_progress
            )
            VALUES (
                @userId, @missionId, @expiration, @completed, @progress
            )
        `
        await request.query(sqlQuery)
        result(null, { msg: "Misión asignada con éxito" })
    } catch (err) {
        result(err, null)
    }
}

/* <=============================== UPDATE BY ID ===============================> */
userMission.updateById = async (id, updatedUserMission, result) => {
    try {
        const pool = await sql.connect(dbConn)
        const request = pool.request()
        
        request.input("id", sql.Int, id)
        request.input("userId", sql.Int, updatedUserMission.user_x_mission_userid)
        request.input("missionId", sql.Int, updatedUserMission.user_x_mission_missionid)
        request.input("expiration", sql.DateTime, updatedUserMission.user_x_mission_expiration)
        request.input("completed", sql.Bit, updatedUserMission.user_x_mission_completed)
        request.input("progress", sql.Int, updatedUserMission.user_x_mission_progress)

        const sqlQuery = `
            UPDATE User_X_Mission SET
                user_x_mission_userid = @userId,
                user_x_mission_missionid = @missionId,
                user_x_mission_expiration = @expiration,
                user_x_mission_completed = @completed,
                user_x_mission_progress = @progress
            OUTPUT INSERTED.*
            WHERE user_x_mission_id = @id
        `
        const response = await request.query(sqlQuery)
        result(null, response.recordset[0])
    } catch (err) {
        result(err, null)
    }
}

/* <=============================== COMPLETE MISSION ===============================> */
userMission.completeMission = async (id, result) => {
    try {
        const pool = await sql.connect(dbConn)
        const response = await pool.request()
            .input("id", sql.Int, id)
            .query(`
                UPDATE User_X_Mission 
                SET user_x_mission_completed = 1
                OUTPUT INSERTED.* 
                WHERE user_x_mission_id = @id
            `)

        result(null, response.recordset[0])
    } catch (err) {
        result(err, null)
    }
}

/* <=============================== DELETE ===============================> */
userMission.delete = async function (id, result) {
    try {
        const pool = await sql.connect(dbConn)
        const response = await pool.request()
            .input("id", sql.Int, id)
            .query("DELETE FROM User_X_Mission WHERE user_x_mission_id = @id")

        result(null, response)
    } catch (err) {
        result(err, null)
    }
}

/* <======- EXPORTAMOS EL MODELO -======> */
module.exports = userMission