/* <=============================== DEPENDENCIAS ===============================> */
const dbConn = require("../utils/mssql.config")
const sql = require("mssql")

/* <=============================== CONSTRUCTOR ===============================> */
let group = function(group){
    this.group_id = group.group_id // AUTO INCREMENTAL
    this.group_name = group.group_name
    this.group_description = group.group_description
    this.group_image = group.group_image
    this.group_points = group.group_points
    this.group_creator_id = group.group_creator_id
}

/* <=============================== FIND ALL ===============================> */
group.findAll = async (result) => {
    try {
        const pool = await sql.connect(dbConn)
        const response = await pool.request().query("SELECT * FROM Groups")
        result(null, response.recordset)
        
    } catch (err) {
        result(err, null)
    }
}

/* <=============================== FIND BY ID ===============================> */
group.findById = async function (id, result) {
    try {
        const pool = await sql.connect(dbConn)
        const response = await pool.request()
            .input("id", sql.Int, id)
            .query("SELECT * FROM Groups WHERE group_id = @id")

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
group.updateById = async (id, updateGroup, result) => {
    try {
        const pool = await sql.connect(dbConn)

        const request = pool.request()
        request.input("id", sql.Int, id)
        request.input("name", sql.NVarChar, updateGroup.group_name)
        request.input("description", sql.NVarChar, updateGroup.group_description)
        request.input("image", sql.NVarChar, updateGroup.group_image)
        request.input("points", sql.Int, updateGroup.group_points)
        request.input("creator_id", sql.Int, updateGroup.group_creator_id)

        const sqlQuery = `
            UPDATE Groups SET
                group_name = @name,
                group_description = @description,
                group_image = @image,
                group_points = @points,
                group_creator_id = @creator_id
            WHERE group_id = @id
        `

        const response = await request.query(sqlQuery)
        result(null, response)
        
    } catch (err) {
        result(err, null)
        
    }
}

/* <=============================== CREATE ===============================> */
group.create = async (newGroup, result) => {
    try {
        const pool = await sql.connect(dbConn)

        const request = pool.request()
        request.input("name", sql.NVarChar, newGroup.group_name)
        request.input("description", sql.NVarChar, newGroup.group_description)
        request.input("image", sql.NVarChar, newGroup.group_image)
        request.input("points", sql.Int, newGroup.group_points)
        request.input("creator_id", sql.Int, newGroup.group_creator_id)

        const sqlQuery = `
            INSERT INTO Groups (
                group_name, group_description, group_image, group_points, group_creator_id
            )
            OUTPUT INSERTED.*
            VALUES (
                @name, @description, @image, @points, @creator_id
            )
        `

        const response = await request.query(sqlQuery)
        result(null, response.recordset[0])

    } catch (err) {
        result(err, null)
        
    }
}

/* <=============================== DELETE ===============================> */
group.delete = async function (id, result) {
    try {
        const pool = await sql.connect(dbConn)
        const response = await pool.request()
            .input("id", sql.Int, id)
            .query("DELETE FROM Groups WHERE group_id = @id")

        result(null, response)
        
    } catch (err) {
        result(err, null)
        
    }
}

/* <======- EXPORTAMOS EL MODELO -======> */
module.exports = group