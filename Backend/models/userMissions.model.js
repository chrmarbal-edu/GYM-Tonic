/* <=============================== DEPENDENCIAS ===============================> */
const dbConn = require("../utils/mssql.config")
const sql = require("mssql")

/* <=============================== CONSTRUCTOR ===============================> */
let userMission = function(userMission){
    this.user_x_mission_id = userMission.user_x_mission_id
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
        if (result) result(null, response.recordset)
    } catch (err) {
        if (result) result(err, null)
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
        if (result) result(null, response.recordset[0])
    } catch (err) {
        if (result) result(err, null)
    }
}

/* <=============================== FIND BY USER ID ===============================> */
userMission.findByUserId = async function (userId, result) {
    try {
        const pool = await sql.connect(dbConn)
        const now = new Date()

        // 1. Obtener todas las misiones del usuario con detalles de puntos
        const allMissionsRes = await pool.request()
            .input("userId", sql.Int, userId)
            .query(`
                SELECT um.*, m.mission_name, m.mission_points, m.mission_objective, m.mission_goal
                FROM User_X_Mission um
                INNER JOIN Missions m ON um.user_x_mission_missionid = m.mission_id
                WHERE um.user_x_mission_userid = @userId
            `)

        const allMissions = allMissionsRes.recordset
        const notifications = []

        // 2. Procesar deducción de puntos para misiones expiradas no completadas
        for (const um of allMissions) {
            const isExpired = new Date(um.user_x_mission_expiration) < now
            if (isExpired && !um.user_x_mission_completed && !um.user_x_mission_points_deducted) {
                // Deducir puntos (mínimo 0)
                await pool.request()
                    .input("uid", sql.Int, userId)
                    .input("pts", sql.Int, um.mission_points)
                    .query(`
                        UPDATE Users 
                        SET user_points = CASE WHEN user_points - @pts < 0 THEN 0 ELSE user_points - @pts END 
                        WHERE user_id = @uid
                    `)
                
                // Marcar como deducido
                await pool.request()
                    .input("umid", sql.Int, um.user_x_mission_id)
                    .query("UPDATE User_X_Mission SET user_x_mission_points_deducted = 1 WHERE user_x_mission_id = @umid")
                
                um.user_x_mission_points_deducted = true
                notifications.push({
                    message: `La misión "${um.mission_name}" ha expirado. Se han deducido ${um.mission_points} puntos.`,
                    missionName: um.mission_name,
                    expired: true
                })
            }
        }

        result(null, {
            missions: allMissions.filter(m => new Date(m.user_x_mission_expiration) >= now && !m.user_x_mission_completed),
            expiredMissions: allMissions.filter(m => new Date(m.user_x_mission_expiration) < now || m.user_x_mission_completed),
            notifications: notifications
        })
    } catch (err) {
        if (result) result(err, null)
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

        if (result) result(null, response.recordset)
    } catch (err) {
        if (result) result(err, null)
    }
}

/* <=============================== CREATE ===============================> */
userMission.create = async (newUserMission, result) => {
    try {
        const pool = await sql.connect(dbConn)
        const request = pool.request()
        
        request.input("userId", sql.Int, newUserMission.userId || newUserMission.user_x_mission_userid)
        request.input("missionId", sql.Int, newUserMission.missionId || newUserMission.user_x_mission_missionid)
        request.input("expiration", sql.DateTime, newUserMission.expiration || newUserMission.user_x_mission_expiration)
        request.input("completed", sql.Bit, newUserMission.completed !== undefined ? newUserMission.completed : (newUserMission.user_x_mission_completed || 0))
        request.input("progress", sql.Int, newUserMission.progress !== undefined ? newUserMission.progress : (newUserMission.user_x_mission_progress || 0))

        const sqlQuery = `
            INSERT INTO User_X_Mission (
                user_x_mission_userid, user_x_mission_missionid, user_x_mission_expiration, user_x_mission_completed, user_x_mission_progress
            )
            VALUES (
                @userId, @missionId, @expiration, @completed, @progress
            )
        `
        await request.query(sqlQuery)
        if (result) result(null, { msg: "Misión asignada con éxito" })
    } catch (err) {
        if (result) result(err, null)
    }
}

/* <=============================== ASSIGN BY OBJECTIVE ===============================> */
userMission.assignMissionsByObjective = async function (userId, objective) {
    try {
        const pool = await sql.connect(dbConn)
        const missionsRes = await pool.request()
            .input("obj", sql.Int, objective)
            .query("SELECT * FROM Missions WHERE mission_objective = @obj")
        
        const now = new Date()
        for (const m of missionsRes.recordset) {
            let expiration = new Date(now)
            // m.mission_type: 0 = daily, 1 = weekly, 2 = monthly
            if (m.mission_type === 0) expiration.setDate(expiration.getDate() + 1)
            else if (m.mission_type === 1) expiration.setDate(expiration.getDate() + 7)
            else if (m.mission_type === 2) expiration.setDate(expiration.getDate() + 30)
            else expiration.setDate(expiration.getDate() + 1)

            await pool.request()
                .input("uid", sql.Int, userId)
                .input("mid", sql.Int, m.mission_id)
                .input("exp", sql.DateTime, expiration)
                .query(`
                    INSERT INTO User_X_Mission (
                        user_x_mission_userid, user_x_mission_missionid, 
                        user_x_mission_expiration, user_x_mission_completed, 
                        user_x_mission_progress, user_x_mission_points_deducted
                    ) VALUES (@uid, @mid, @exp, 0, 0, 0)
                `)
        }
    } catch (err) {
        console.error("Error en assignMissionsByObjective:", err)
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
        if (result) result(null, response.recordset[0])
    } catch (err) {
        if (result) result(err, null)
    }
}

/* <=============================== COMPLETE MISSION ===============================> */
userMission.completeMission = async (id, result) => {
    try {
        const pool = await sql.connect(dbConn)
        
        // 1. Obtener puntos y userId
        const info = await pool.request()
            .input("id", sql.Int, id)
            .query(`
                SELECT um.user_x_mission_userid, m.mission_points, um.user_x_mission_completed
                FROM User_X_Mission um
                INNER JOIN Missions m ON um.user_x_mission_missionid = m.mission_id
                WHERE um.user_x_mission_id = @id
            `)
        
        if (info.recordset.length === 0) return result("Misión no encontrada", null)
        const mInfo = info.recordset[0]
        
        if (mInfo.user_x_mission_completed) return result("Misión ya completada", null)

        // 2. Sumar puntos al usuario
        await pool.request()
            .input("uid", sql.Int, mInfo.user_x_mission_userid)
            .input("pts", sql.Int, mInfo.mission_points)
            .query("UPDATE Users SET user_points = user_points + @pts WHERE user_id = @uid")

        // 3. Marcar como completada y guardar fecha de hoy
        const response = await pool.request()
            .input("id", sql.Int, id)
            .query(`
                UPDATE User_X_Mission
                SET user_x_mission_completed = 1,
                    user_x_mission_completed_date = CAST(GETDATE() AS DATE)
                OUTPUT INSERTED.*
                WHERE user_x_mission_id = @id
            `)
        if (result) result(null, response.recordset[0])
    } catch (err) {
        if (result) result(err, null)
    }
}

/* <=============================== DELETE ===============================> */
userMission.delete = async function (id, result) {
    try {
        const pool = await sql.connect(dbConn)
        const response = await pool.request()
            .input("id", sql.Int, id)
            .query("DELETE FROM User_X_Mission WHERE user_x_mission_id = @id")

        if (result) result(null, response)
    } catch (err) {
        if (result) result(err, null)
    }
}

/* <======- EXPORTAMOS EL MODELO -======> */
module.exports = userMission