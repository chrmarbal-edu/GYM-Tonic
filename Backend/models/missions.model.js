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
    this.mission_goal = mission.mission_goal
}

/* <=============================== FIND ALL ===============================> */
mission.findAll = async (result) => {
    try {
        const pool = await sql.connect(dbConn)
        const response = await pool.request().query("SELECT * FROM Missions")
        result(null, response.recordset)
        
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

        
    } catch (err) {
        result(err, null)
        
    }
}

/* <=============================== UPDATE BY ID ===============================> */
mission.updateById = async (id, updateMission, result) => {
    try {
        const pool = await sql.connect(dbConn)

        const request = pool.request()
        request.input("id", sql.Int, id)
        
        const nameVal = updateMission.name !== undefined ? updateMission.name : updateMission.mission_name;
        const typeVal = updateMission.type !== undefined ? updateMission.type : updateMission.mission_type;
        const pointsVal = updateMission.points !== undefined ? updateMission.points : updateMission.mission_points;
        const objectiveVal = updateMission.objective !== undefined ? updateMission.objective : updateMission.mission_objective;
        const goalVal = updateMission.goal !== undefined ? updateMission.goal : updateMission.mission_goal;

        request.input("name", sql.VarChar, nameVal)
        request.input("type", sql.Int, typeVal)
        request.input("points", sql.Int, pointsVal)
        request.input("objective", sql.Int, objectiveVal)
        request.input("goal", sql.Int, goalVal)

        const sqlQuery = `
            UPDATE Missions SET
                mission_name = @name,
                mission_type = @type,
                mission_points = @points,
                mission_objective = @objective,
                mission_goal = @goal
            OUTPUT INSERTED.*
            WHERE mission_id = @id
        `

        const response = await request.query(sqlQuery)
        result(null, response.recordset[0])
        
    } catch (err) {
        result(err, null)
        
    }
}

/* <=============================== CREATE ===============================> */
mission.create = async (newMission, result) => {
    try {
        const pool = await sql.connect(dbConn)

        const request = pool.request()
        
        const nameVal = newMission.name !== undefined ? newMission.name : newMission.mission_name;
        const typeVal = newMission.type !== undefined ? newMission.type : newMission.mission_type;
        const pointsVal = newMission.points !== undefined ? newMission.points : newMission.mission_points;
        const objectiveVal = newMission.objective !== undefined ? newMission.objective : newMission.mission_objective;
        const goalVal = newMission.goal !== undefined ? newMission.goal : newMission.mission_goal;

        request.input("name", sql.VarChar, nameVal)
        request.input("type", sql.Int, typeVal)
        request.input("points", sql.Int, pointsVal)
        request.input("objective", sql.Int, objectiveVal)
        request.input("goal", sql.Int, goalVal)

        const sqlQuery = `
            INSERT INTO Missions (
                mission_name,
                mission_type,
                mission_points,
                mission_objective,
                mission_goal
            )
            OUTPUT INSERTED.*
            VALUES (
                @name,
                @type,
                @points,
                @objective,
                @goal
            )
        `
        const response = await request.query(sqlQuery)
        result(null, response.recordset[0])
    } catch (err) {
        result(err, null)
        
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
        
    } catch (err) {
        result(err, null)
        
    }
}

/* <======- EXPORTAMOS EL MODELO -======> */
module.exports = mission