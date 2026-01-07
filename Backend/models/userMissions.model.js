/* <=============================== DEPENDENCIAS ===============================> */
const dbConn = require("../utils/mssql.config")
const sql = require("mssql")

/* <=============================== CONSTRUCTOR ===============================> */
let userMission = function(userMission){
    this.user_x_mission_id = userMission.user_x_mission_id // AUTO INCREMENTAL
    this.user_x_mission_userid = userMission.user_x_mission_userid
    this.user_x_mission_missionid = userMission.user_x_mission_missionid
    this.user_x_mission_expiration = userMission.user_x_mission_expiration
}

/* <=============================== FIND ALL ===============================> */
userMission.findAll = async (result) => {
    try {
        const pool = await sql.connect(dbConn)
        const response = await pool.request().query("SELECT * FROM User_X_Mission")
        result(null, response.recordset)
        sql.close()
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
            .query("SELECT * FROM User_X_Mission WHERE user_x_mission_id = @id")

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
userMission.create = async (newUserMission, result) => {
    try {
        const pool = await sql.connect(dbConn)

        const request = pool.request()
        request.input("id", sql.Int, id)
        request.input("userId", sql.Int, newUserMission.user_x_mission_userid)
        request.input("missionId", sql.Int, newUserMission.user_x_mission_missionid)
        request.input("expiration", sql.DateTime, newUserMission.user_x_mission_expiration)

        const sqlQuery = `
            INSERT INTO User_X_Mission (
                user_x_mission_userid, user_x_mission_missionid, user_x_mission_expiration
            )
            VALUES (
                @userId, @missionId, @expiration
            )
        `
    } catch (err) {
        result(err, null)
        sql.close()
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
        sql.close()
    } catch (err) {
        result(err, null)
        sql.close()
    }
}

/* <======- EXPORTAMOS EL MODELO -======> */
module.exports = routine