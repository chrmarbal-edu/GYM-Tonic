/* <=============================== DEPENDENCIAS ===============================> */
const dbConn = require("../utils/mssql.config")
const sql = require("mssql")

/* <=============================== CONSTRUCTOR ===============================> */
let groupUser = function(groupUser){
    this.user_x_group_id = groupUser.user_x_group_id // AUTO INCREMENTAL
    this.user_x_group_userid = groupUser.user_x_group_userid
    this.user_x_group_groupid = groupUser.user_x_group_groupid
    this.user_x_group_range = groupUser.user_x_group_range
}

/* <=============================== FIND ALL ===============================> */
groupUser.findAll = async (result) => {
    try {
        const pool = await sql.connect(dbConn)
        const response = await pool.request().query("SELECT * FROM User_X_Group")
        result(null, response.recordset)
        sql.close()
    } catch (err) {
        result(err, null)
    }
}

/* <=============================== FIND BY ID ===============================> */
groupUser.findById = async function (id, result) {
    try {
        const pool = await sql.connect(dbConn)
        const response = await pool.request()
            .input("id", sql.Int, id)
            .query("SELECT * FROM User_X_Group WHERE user_x_group_id = @id")

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
groupUser.create = async (newGroupUser, result) => {
    try {
        const pool = await sql.connect(dbConn)

        const request = pool.request()
        request.input("id", sql.Int, id)
        request.input("userId", sql.Int, newGroupUser.user_x_group_userid)
        request.input("groupId", sql.Int, newGroupUser.user_x_group_groupid)
        request.input("range", sql.Int, newGroupUser.user_x_group_range)

        const sqlQuery = `
            INSERT INTO User_X_Group (
                user_x_group_userid, user_x_group_groupid, user_x_group_range
            )
            VALUES (
                @userId, @groupId, @range
            )
        `
    } catch (err) {
        result(err, null)
        sql.close()
    }
}

/* <=============================== DELETE ===============================> */
groupUser.delete = async function (id, result) {
    try {
        const pool = await sql.connect(dbConn)
        const response = await pool.request()
            .input("id", sql.Int, id)
            .query("DELETE FROM User_X_Group WHERE user_x_group_id = @id")

        result(null, response)
        sql.close()
    } catch (err) {
        result(err, null)
        sql.close()
    }
}

/* <======- EXPORTAMOS EL MODELO -======> */
module.exports = routine