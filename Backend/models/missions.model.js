/* <=============================== DEPENDENCIAS ===============================> */
const dbConn = require("../utils/mssql.config")
const sql = require("mssql")

/* <=============================== CONSTRUCTOR ===============================> */
let mission = function(mission){
    this.mission_id = mission.mission_id // AUTO INCREMENTAL
    this.mission_name = mission.mission_name
    this.mission_type = mission.mission_type
    this.mission_points = mission.mission_points
    this.mission_objective = mission.mission_objective
}

/* <=============================== FIND ALL ===============================> */
mission.findAll = async (result) => {
    try {
        const pool = await sql.connect(dbConn)
        const response = await pool.request().query("SELECT * FROM Missions")
        result(null, response.recordset)
        sql.close()
    } catch (err) {
        result(err, null)
    }
}

/* <=============================== FIND BY ID ===============================> */
mission.findById = async function (id, result) {
    try {
        const pool = await sql.connect(dbConn)
        const response = await pool.request()
            .input("id", sql.Int, id)
            .query("SELECT * FROM Missions WHERE mission_id = @id")

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

/* <=============================== UPDATE BY ID ===============================> */
mission.updateById = async (id, updateMission, result) => {
    try {
        const pool = await sql.connect(dbConn)

        const request = pool.request()
        request.input("id", sql.Int, id)
        request.input("name", sql.VarChar, updateMission.mission_name)
        request.input("type", sql.Int, updateMission.mission_type)
        request.input("points", sql.Int, updateMission.mission_points)
        request.input("objective", sql.Int, updateMission.mission_objective)

        const sqlQuery = `
            UPDATE Missions SET
                mission_name = @name,
                mission_type = @type,
                mission_points = @points,
                mission_objective = @objective
            WHERE mission_id = @id
        `

        const response = await request.query(sqlQuery)
        result(null, response)
        sql.close()
    } catch (err) {
        result(err, null)
        sql.close()
    }
}

/* <=============================== CREATE ===============================> */
mission.create = async (newMission, result) => {
    try {
        const pool = await sql.connect(dbConn)

        const request = pool.request()
        request.input("name", sql.VarChar, newMission._name)
        request.input("type", sql.Int, newMission._type)
        request.input("points", sql.Int, newMission._points)
        request.input("objective", sql.Int, newMission._objective)

        const sqlQuery = `
            INSERT INTO Missions (
                mission_name,
                mission_type,
                mission_points,
                mission_objective
            )
            VALUES (
                @name,
                @type,
                @points,
                @objective
            )
        `
    } catch (err) {
        result(err, null)
        sql.close()
    }
}

/* <=============================== DELETE ===============================> */
mission.delete = async function (id, result) {
    try {
        const pool = await sql.connect(dbConn)
        const response = await pool.request()
            .input("id", sql.Int, id)
            .query("DELETE FROM Missions WHERE mission_id = @id")

        result(null, response)
        sql.close()
    } catch (err) {
        result(err, null)
        sql.close()
    }
}

/* <======- EXPORTAMOS EL MODELO -======> */
module.exports = mission