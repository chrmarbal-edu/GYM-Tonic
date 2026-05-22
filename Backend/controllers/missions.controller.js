// #region <DEPENDENCIAS>
/* <=============================== DEPENDENCIAS ===============================> */
const missionmodel = require("../models/missions.model.js")
const userModel = require("../models/users.model.js")
const userMissionsModel = require("../models/userMissions.model.js")
const sql = require("mssql")
const dbConn = require("../utils/mssql.config")
const fs = require("fs").promises
const AppError = require("../utils/AppError")
const bcrypt = require("../utils/bcrypt")
const jwtMW = require("../middlewares/jwt.mw")
const { log } = require("console")


function wrapAsync(fn) {
    return function(req, res, next) {
        fn(req, res, next).catch(e => {
            next(e);
        });
    }
}

/* <=============================== SYNC MISSION ASSIGNMENTS ===============================> */
async function syncMissionAssignments(missionId, objective, type) {
    try {
        const pool = await sql.connect(dbConn)
        const now = new Date()
        let expiration = new Date()

        // mission_type: 0=diaria, 1=semanal, 2=mensual
        if (type == 0) {
            expiration.setDate(now.getDate() + 1)
        } else if (type == 1) {
            expiration.setDate(now.getDate() + 7)
        } else if (type == 2) {
            expiration.setDate(now.getDate() + 30)
        }

        // 1. Eliminar asignaciones activas (no completadas) de usuarios que ya no coinciden con el objetivo de la misión
        await pool.request()
            .input("missionId", sql.Int, missionId)
            .input("objective", sql.Int, objective)
            .query(`
                DELETE FROM User_X_Mission 
                WHERE user_x_mission_missionid = @missionId 
                AND user_x_mission_completed = 0
                AND user_x_mission_userid IN (SELECT user_id FROM Users WHERE user_objective <> @objective)
            `)

        // 2. Asignar la misión a todos los usuarios que tienen el mismo objetivo y aún no la tienen asignada
        await pool.request()
            .input("missionId", sql.Int, missionId)
            .input("objective", sql.Int, objective)
            .input("expiration", sql.DateTime, expiration)
            .query(`
                INSERT INTO User_X_Mission (user_x_mission_userid, user_x_mission_missionid, user_x_mission_expiration, user_x_mission_completed, user_x_mission_progress, user_x_mission_points_deducted)
                SELECT u.user_id, @missionId, @expiration, 0, 0, 0
                FROM Users u
                WHERE u.user_objective = @objective
                AND NOT EXISTS (SELECT 1 FROM User_X_Mission ux WHERE ux.user_x_mission_userid = u.user_id AND ux.user_x_mission_missionid = @missionId)
            `)
    } catch (err) {
        console.error("Error inside syncMissionAssignments:", err)
    }
}

/* 
400 - BAD REQUEST (EL SERVIDOR NO PUEDE PROCESAR LA SOLICITUD)
404 - NOT FOUND (NO EXISTE EN EL SERVIDOR EL RECURSO PEDIDO)
500 - GENÉRICO (ALGO HA IDO MAL EN EL SERVIDOR)
*/

// #region <---CSR GROUPS--->

// #region FINDALL - CSR
/* <=============================== 2. FINDALLMISSIONS ===============================> */
// Buscamos todas las misiones.
exports.findAllMissionsCSR = wrapAsync(async function (req,res,next) { 
    // Espera una promesa de lo que devuelva la función "findAll" del modelo.
    await missionmodel.findAll(async function(err, datosMissions){
        if(err){
            next(new AppError(err,400))
        } else{
            res.status(200).json(datosMissions)
        }
    })        
})

// #region FIND-ID - CSR
/* <=============================== 3. FINDMISSIONBYID ===============================> */
// Buscamos las misiones por "id".
exports.findMissionByIdCSR = wrapAsync(async function (req,res,next){
    // Traemos por parámetro el id enviado como parámetro por la ruta.
    const {id} = req.params
    const userLogued = req.userLogued;
    // Espera una promesa de lo que devuelva la función "findById" del modelo.
    if(!userLogued){
        return next(new AppError("No estás registrado!", 403))
    }else{
        await missionmodel.findById(id,function(err,datosMissions){
            if(err){
                next(new AppError(err,404))
            } 

            if(!datosMissions || datosMissions.length == 0) {
                return next(new AppError("Usuario no encontrado", 404))
            }

            res.status(200).json(datosMissions)

        })
    }
})

// #region UPDATE - CSR
/* <=============================== 5. UPDATEMISSION ===============================> */
// Actualizamos la misión.
exports.updateMissionCSR = wrapAsync(async function (req,res, next) {    
    const {id} = req.params
    let { name, type, points, objective, goal } = req.body
   
    await missionmodel.findById(id, async function(err, existingMission){
        if(err){
            next(new AppError(err, 500))
        } else if (!existingMission || existingMission.length == 0) {
            return next(new AppError("Misión no encontrada", 404))
        }

        let updateMission = {            
            name: name !== undefined ? name : existingMission.mission_name,
            type: type !== undefined ? Number(type) : existingMission.mission_type,
            points: points !== undefined ? Number(points) : existingMission.mission_points,
            objective: objective !== undefined ? Number(objective) : existingMission.mission_objective,
            goal: goal !== undefined ? Number(goal) : existingMission.mission_goal
        }
        
        await missionmodel.updateById(id, updateMission, async function(err, datosMissionActualizada){
            if(err){
                next(new AppError(err, 500))
            } else{
                try {
                    // Sincronizar asignaciones tras la actualización
                    await syncMissionAssignments(id, updateMission.objective, updateMission.type)
                } catch (assignErr) {
                    console.error("Error updating mission assignments:", assignErr)
                }
                res.status(200).json(datosMissionActualizada);
            }
        })
    })
})

// #region CREATEMISSION - CSR
/* <=============================== 7. CREATEMISSION ===============================> */
exports.createMissionCSR = wrapAsync(async function (req, res, next) {
    const { name, type, points, objective, goal } = req.body

        let newMission = {}

        newMission = {
            name: name,
            type: Number(type),
            points: Number(points),
            objective: Number(objective),
            goal: Number(goal)
        }

        // Realizamos la redirección en la promesa de la creación.
        await missionmodel.create(newMission, async function(err,datosMisionCreada){
            if(err){
                console.log(err)
                console.log("ERROR CREATE MISSIONS CSR");
                return next(new AppError(err, 500))
            } else{
                // Asignar automáticamente a los usuarios que tengan el mismo objetivo
                await syncMissionAssignments(datosMisionCreada.mission_id, datosMisionCreada.mission_objective, datosMisionCreada.mission_type)
                res.status(200).json({ datosMisionCreada })
            }
        })
    
});

// #region DELETE - CSR
/* <=============================== 8. DELETEMISSION ===============================> */
exports.deleteMissionCSR = wrapAsync(async function (req, res, next) {
    const { id } = req.params;
        await missionmodel.findById(id, async function (err, objetoDatos) {
            if (err) {
                return next(new AppError("Misión no encontrada", 404));
            }

            if (!objetoDatos || objetoDatos.length == 0) {
                return next(new AppError("Misión no encontrada", 404));
            }

            /* <================== PARTE 2 ==================> */
            await missionmodel.delete(id, function (err, datosMisionEliminada) {
                if (err) {
                    return next(new AppError("Error al eliminar la misión", 500));
                }else {
                    return res.status(200).json({ msg: "Misión eliminada correctamente" });
                }
            });
        });
});

// #endregion