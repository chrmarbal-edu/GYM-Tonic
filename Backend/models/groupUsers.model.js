/* <=============================== DEPENDENCIAS ===============================> */
const dbConn = require("../utils/mssql.config")
const sql = require("mssql")

/* <=============================== CONSTRUCTOR ===============================> */
let groupUser = function (groupUserRow) {
    this.Group_x_user_id = groupUserRow.Group_x_user_id
    this.Group_x_user_groupid = groupUserRow.Group_x_user_groupid
    this.Group_x_user_userid = groupUserRow.Group_x_user_userid
    this.Group_x_user_range = groupUserRow.Group_x_user_range
}

/* <=============================== FIND BY GROUP ID ===============================> */
groupUser.findByGroupId = async function (groupId, result) {
    try {
        const pool = await sql.connect(dbConn)
        const response = await pool
            .request()
            .input("groupId", sql.Int, groupId)
            .query(
                `SELECT * FROM Group_x_user WHERE Group_x_user_groupid = @groupId`
            )

        result(null, response.recordset)
    } catch (err) {
        result(err, null)
    }
}

/* <=============================== FIND MEMBERSHIP ===============================> */
groupUser.findMembership = async function (groupId, userId, result) {
    try {
        const pool = await sql.connect(dbConn)
        const response = await pool
            .request()
            .input("groupId", sql.Int, groupId)
            .input("userId", sql.Int, userId)
            .query(
                `SELECT TOP 1 * FROM Group_x_user
                 WHERE Group_x_user_groupid = @groupId AND Group_x_user_userid = @userId`
            )

        if (response.recordset.length > 0) {
            result(null, response.recordset[0])
        } else {
            result(null, null)
        }
    } catch (err) {
        result(err, null)
    }
}

/* <=============================== CREATE ===============================> */
groupUser.create = async (row, result) => {
    try {
        const pool = await sql.connect(dbConn)

        const request = pool.request()
        request.input("groupId", sql.Int, row.Group_x_user_groupid)
        request.input("userId", sql.Int, row.Group_x_user_userid)
        request.input("range", sql.Int, row.Group_x_user_range ?? 0)

        const sqlQuery = `
            INSERT INTO Group_x_user (
                Group_x_user_groupid, Group_x_user_userid, Group_x_user_range
            )
            OUTPUT INSERTED.*
            VALUES (
                @groupId, @userId, @range
            )
        `

        const response = await request.query(sqlQuery)
        result(null, response.recordset[0])
    } catch (err) {
        result(err, null)
    }
}

/* <=============================== DELETE BY GROUP AND USER ===============================> */
groupUser.deleteByGroupAndUser = async function (groupId, userId, result) {
    try {
        const pool = await sql.connect(dbConn)
        const response = await pool
            .request()
            .input("groupId", sql.Int, groupId)
            .input("userId", sql.Int, userId)
            .query(
                `DELETE FROM Group_x_user
                 WHERE Group_x_user_groupid = @groupId AND Group_x_user_userid = @userId`
            )

        result(null, response.rowsAffected[0] || 0)
    } catch (err) {
        result(err, null)
    }
}

/* <======- EXPORTAMOS EL MODELO -======> */
module.exports = groupUser
