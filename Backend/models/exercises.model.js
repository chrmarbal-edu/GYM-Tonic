/* <=============================== DEPENDENCIAS ===============================> */
const dbConn = require("../utils/mssql.config")
const sql = require("mssql")

/* <=============================== CONSTRUCTOR ===============================> */
let exercise = function(exercise){
    this.exercise_id = exercise.exercise_id // AUTO INCREMENTAL
    this.exercise_name = exercise.exercise_name
    this.exercise_description = exercise.exercise_description
    this.exercise_type = exercise.exercise_type
    this.exercise_video = exercise.exercise_video
    this.exercise_image = exercise.exercise_image
}

/* <=============================== FIND ALL ===============================> */
exercise.findAll = async (result) => {
    try {
        const pool = await sql.connect(dbConn)
        const response = await pool.request().query("SELECT * FROM Exercises")
        result(null, response.recordset)
        
    } catch (err) {
        result(err, null)
    }
}

/* <=============================== FIND BY ID ===============================> */
exercise.findById = async function (id, result) {
    try {
        const pool = await sql.connect(dbConn)
        const response = await pool.request()
            .input("id", sql.Int, id)
            .query("SELECT * FROM Exercises WHERE exercise_id = @id")

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
exercise.updateById = async (id, updateExercise, result) => {
    try {
        const pool = await sql.connect(dbConn)
        const request = await pool.request()
        request.input("id", sql.Int, id)
        request.input("name", sql.VarChar, updateExercise.exercise_name)
        request.input("description", sql.VarChar, updateExercise.exercise_description)
        request.input("type", sql.Int, updateExercise.exercise_type)
        request.input("video", sql.VarChar, updateExercise.exercise_video)
        request.input("image", sql.VarChar, updateExercise.exercise_image)

        const sqlQuery = `
            UPDATE Exercises SET
                exercise_name = @name,
                exercise_description = @description,
                exercise_type = @type,
                exercise_video = @video,
                exercise_image = @image
            OUTPUT INSERTED.*
            WHERE exercise_id = @id
        `

        const response = await request.query(sqlQuery)
        result(null, response.recordsets[0][0])
        
    } catch (err) {
        result(err, null)
        
    }
}

/* <=============================== CREATE ===============================> */
exercise.create = async (newExercise, result) => {
    try {
        const pool = await sql.connect(dbConn)

        const request = await pool.request()
        request.input("name", sql.VarChar, newExercise.name)
        request.input("description", sql.VarChar, newExercise.description)
        request.input("type", sql.Int, newExercise.type)
        request.input("video", sql.VarChar, newExercise.video)
        request.input("image", sql.VarChar, newExercise.image)

        const sqlQuery = `
            INSERT INTO Exercises (
                exercise_name, exercise_description, exercise_type, exercise_video,
                exercise_image
            )
            OUTPUT INSERTED.*
            VALUES (
                @name, @description, @type, @video,
                @image
            )
        `

        const response = await request.query(sqlQuery)
        result(null, response.recordsets[0][0])
        
    } catch (err) {
        result(err, null)
        
    }
}

/* <=============================== DELETE ===============================> */
exercise.delete = async function (id, result) {
    try {
        const pool = await sql.connect(dbConn)
        const response = await pool.request()
            .input("id", sql.Int, id)
            .query("DELETE FROM Exercises WHERE exercise_id = @id")

        result(null, response.recordsets[0])
        
    } catch (err) {
        result(err, null)
        
    }
}

/* <======- EXPORTAMOS EL MODELO -======> */
module.exports = exercise