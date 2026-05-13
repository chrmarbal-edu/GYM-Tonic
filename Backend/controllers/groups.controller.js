// #region <DEPENDENCIAS>
/* <=============================== DEPENDENCIAS ===============================> */
const groupmodel = require("../models/groups.model.js")
const userModel = require("../models/users.model.js")
const groupUserModel = require("../models/groupUsers.model.js")
const userRoutineModel = require("../models/userRoutines.model.js")
const routineExerciseModel = require("../models/routineExercises.model.js")
const routinesModel = require("../models/routines.model.js")
const fs = require("fs").promises
const AppError = require("../utils/AppError")
const bcrypt = require("../utils/bcrypt")
const jwtMW = require("../middlewares/jwt.mw")
const { log } = require("console")
const sql = require("mssql")
const dbConn = require("../utils/mssql.config")


function wrapAsync(fn) {
    return function(req, res, next) {
        fn(req, res, next).catch(e => {
            next(e);
        });
    }
}

const fromCallback = (fn, ...args) =>
    new Promise((resolve, reject) => {
        fn(...args, (err, data) => {
            if (err) {
                reject(err)
            } else {
                resolve(data)
            }
        })
    })

const loadGroup = (id) =>
    new Promise((resolve, reject) => {
        groupmodel.findById(id, (err, data) => {
            if (err && err.err === "No hay datos") {
                resolve(null)
            } else if (err) {
                reject(err)
            } else {
                resolve(data)
            }
        })
    })

const loadUser = (id) =>
    new Promise((resolve, reject) => {
        userModel.findById(id, (err, data) => {
            if (err && err.err === "No hay datos") {
                resolve(null)
            } else if (err) {
                reject(err)
            } else {
                resolve(data)
            }
        })
    })

const loadRoutine = (id) =>
    new Promise((resolve, reject) => {
        routinesModel.findById(id, (err, data) => {
            if (err && err.err === "No hay datos") {
                resolve(null)
            } else if (err) {
                reject(err)
            } else {
                resolve(data)
            }
        })
    })

const assertGroupCreator = (group, userLogued) => {
    const uid = Number(userLogued.user_id)
    const creatorId = Number(group.group_creator_id)
    return uid === creatorId
}
/* 
400 - BAD REQUEST (EL SERVIDOR NO PUEDE PROCESAR LA SOLICITUD)
404 - NOT FOUND (NO EXISTE EN EL SERVIDOR EL RECURSO PEDIDO)
500 - GENÉRICO (ALGO HA IDO MAL EN EL SERVIDOR)
*/

// #region <---CSR GROUPS--->

// #region FINDALL - CSR
/* <=============================== 2. FINDALLGROUPS ===============================> */
// Buscamos todos los grupos.
exports.findAllGroupsCSR = wrapAsync(async function (req,res,next) { 
    // Espera una promesa de lo que devuelva la función "findAll" del modelo.
    await groupmodel.findAll(async function(err, datosGroups){
        if(err){
            next(new AppError(err,400))
        } else{
            res.status(200).json(datosGroups)
        }
    })        
})

// #region FIND-ID - CSR
/* <=============================== 3. FINDGROUPBYID ===============================> */
// Buscamos los grupos por "id".
exports.findGroupByIdCSR = wrapAsync(async function (req,res,next){
    // Traemos por parámetro el id enviado como parámetro por la ruta.
    const {id} = req.params
    const userLogued = req.userLogued;
    // Espera una promesa de lo que devuelva la función "findById" del modelo.
    if(!userLogued){
        return next(new AppError("No estás registrado!", 403))
    }else{
        await groupmodel.findById(id,function(err,datosGroups){
            if(err){
                next(new AppError(err,404))
            } 

            if(!datosGroups || datosGroups.length == 0) {
                return next(new AppError("Grupo no encontrado", 404))
            }

            res.status(200).json(datosGroups)


        })
    }
})

// #region UPDATE - CSR
/* <=============================== 5. UPDATEGROUP ===============================> */
// Actualizamos el grupo.
exports.updateGroupCSR = wrapAsync(async function (req,res, next) {    
    const {id} = req.params
    let { name, description, image, points, creator_id } = req.body

    console.log("id", id);

    let completeGroup    
   
    /* <================== PARTE 1 ==================> */
    // Espera una promesa de lo que devuelva la función "findById" del modelo. 
    await groupmodel.findById(id, async function(err,objetoDatos){
        if(err){
            console.log("ERROR UPDATE GROUP SSR");

            next(new AppError(err, 500))
        }else{     
            completeGroup = objetoDatos
        }

        let updateGroup = {            
            group_name: name || completeGroup.group_name,
            group_description: description || completeGroup.group_description,
            group_image: image || completeGroup.group_image,
            group_points: points !== undefined ? points : completeGroup.group_points,
            group_creator_id: creator_id || completeGroup.group_creator_id
        }

        
        // Realizamos la redirección en la promesa de la actualización.
        await groupmodel.updateById(id, updateGroup, function(err, datosGrupoActualizado){
            if(err){
                console.log("ERROR UPDATE BY ID SSR");

                next(new AppError(err, 500))
            } else{
                res.status(200).json(datosGrupoActualizado);
            }
        })
    })
})

// #region CREATEGROUP - CSR
/* <=============================== 7. CREATEGROUP ===============================> */
exports.createGroupCSR = wrapAsync(async function (req, res, next) {
    const { name, description, image, points, creator_id } = req.body
    
        let newGroup = {
            group_name: name,
            group_description: description,
            group_image: image,
            group_points: points || 0,
            group_creator_id: creator_id
        }

        // Realizamos la redirección en la promesa de la creación.
        await groupmodel.create(newGroup,function(err,datosGrupoCreado){
            if(err){
                console.log(err)
                console.log(datosGrupoCreado)

                console.log("ERROR CREATE GROUPS CSR");

                res.status(500).json({error: err})
            } else{
                res.status(200).json({ datosGrupoCreado })
            }
        })
    
});

// #region DELETE - CSR
/* <=============================== 8. DELETEGROUP ===============================> */
exports.deleteGroupCSR = wrapAsync(async function (req, res, next) {
    const { id } = req.params;
        await groupmodel.findById(id, async function (err, objetoDatos) {
            if (err) {
                return next(new AppError("Grupo no encontrado", 404));
            }

            if (!objetoDatos || objetoDatos.length == 0) {
                return next(new AppError("Grupo no encontrado", 404));
            }

            /* <================== PARTE 2 ==================> */
            await groupmodel.delete(id, function (err, datosGrupoEliminado) {
                if (err) {
                    return next(new AppError("Error al eliminar el grupo", 500));
                }else {
                    return res.status(200).json({ msg: "Grupo eliminado correctamente" });
                }
            });
        });
});

// #region ADD USER TO GROUP (creador del grupo)
exports.addUserToGroupCSR = wrapAsync(async function (req, res, next) {
    const userLogued = req.userLogued
    if (!userLogued) {
        return next(new AppError("No estás registrado!", 403))
    }

    const groupId = Number(req.params.id)
    const { user_id: bodyUserId, range } = req.body
    const userIdToAdd = Number(bodyUserId)

    if (!Number.isFinite(groupId) || !Number.isFinite(userIdToAdd)) {
        return next(new AppError("group_id o user_id inválidos", 400))
    }

    const group = await loadGroup(groupId)
    if (!group) {
        return next(new AppError("Grupo no encontrado", 404))
    }

    if (!assertGroupCreator(group, userLogued)) {
        return next(new AppError("Solo el creador del grupo puede añadir miembros", 403))
    }

    const targetUser = await loadUser(userIdToAdd)
    if (!targetUser) {
        return next(new AppError("Usuario no encontrado", 404))
    }

    const existing = await fromCallback(
        groupUserModel.findMembership,
        groupId,
        userIdToAdd
    )
    if (existing) {
        return next(new AppError("El usuario ya pertenece al grupo", 409))
    }

    const memberRange = range !== undefined && range !== null ? Number(range) : 0
    if (!Number.isFinite(memberRange)) {
        return next(new AppError("range inválido", 400))
    }

    const inserted = await fromCallback(groupUserModel.create, {
        Group_x_user_groupid: groupId,
        Group_x_user_userid: userIdToAdd,
        Group_x_user_range: memberRange
    })

    return res.status(201).json(inserted)
})

// #region REMOVE USER FROM GROUP (creador del grupo)
exports.removeUserFromGroupCSR = wrapAsync(async function (req, res, next) {
    const userLogued = req.userLogued
    if (!userLogued) {
        return next(new AppError("No estás registrado!", 403))
    }

    const groupId = Number(req.params.id)
    const userIdToRemove = Number(req.params.userId)

    if (!Number.isFinite(groupId) || !Number.isFinite(userIdToRemove)) {
        return next(new AppError("Identificadores inválidos", 400))
    }

    const group = await loadGroup(groupId)
    if (!group) {
        return next(new AppError("Grupo no encontrado", 404))
    }

    if (!assertGroupCreator(group, userLogued)) {
        return next(new AppError("Solo el creador del grupo puede eliminar miembros", 403))
    }

    if (userIdToRemove === Number(group.group_creator_id)) {
        return next(
            new AppError("No se puede eliminar al creador del grupo de la lista de miembros", 403)
        )
    }

    const deletedRows = await fromCallback(
        groupUserModel.deleteByGroupAndUser,
        groupId,
        userIdToRemove
    )

    if (!deletedRows) {
        return next(new AppError("El usuario no pertenece a este grupo", 404))
    }

    return res.status(200).json({ msg: "Usuario eliminado del grupo correctamente" })
})

// #region ADD GROUP ROUTINE FOR ALL MEMBERS (creador del grupo)
exports.addGroupRoutineCSR = wrapAsync(async function (req, res, next) {
    const userLogued = req.userLogued
    if (!userLogued) {
        return next(new AppError("No estás registrado!", 403))
    }

    const groupId = Number(req.params.id)
    const { name, exercise_ids: exerciseIds } = req.body

    if (!Number.isFinite(groupId)) {
        return next(new AppError("group_id inválido", 400))
    }

    if (!name || typeof name !== "string" || !name.trim()) {
        return next(new AppError("El nombre de la rutina es obligatorio", 400))
    }

    if (!Array.isArray(exerciseIds) || exerciseIds.length === 0) {
        return next(
            new AppError("exercise_ids debe ser un array con al menos un ejercicio", 400)
        )
    }

    const normalizedExerciseIds = exerciseIds.map((x) => Number(x))
    if (normalizedExerciseIds.some((x) => !Number.isFinite(x))) {
        return next(new AppError("exercise_ids debe contener solo números enteros", 400))
    }

    const group = await loadGroup(groupId)
    if (!group) {
        return next(new AppError("Grupo no encontrado", 404))
    }

    if (!assertGroupCreator(group, userLogued)) {
        return next(new AppError("Solo el creador del grupo puede añadir rutinas al grupo", 403))
    }

    const pool = await sql.connect(dbConn)
    for (const eid of normalizedExerciseIds) {
        const chk = await pool
            .request()
            .input("eid", sql.Int, eid)
            .query("SELECT 1 FROM Exercises WHERE exercise_id = @eid")
        if (chk.recordset.length === 0) {
            return next(new AppError(`El ejercicio ${eid} no existe`, 400))
        }
    }

    const transaction = new sql.Transaction(pool)
    await transaction.begin()
    try {
        const rqIns = new sql.Request(transaction)
        rqIns.input("name", sql.NVarChar(255), name.trim())
        rqIns.input("groupId", sql.Int, groupId)
        const insRoutine = await rqIns.query(`
            INSERT INTO Routines (routine_name, routine_is_group_routine, routine_groupid)
            OUTPUT INSERTED.*
            VALUES (@name, 1, @groupId)
        `)
        const routineRow = insRoutine.recordset[0]
        const routineId = routineRow.routine_id

        for (const eid of normalizedExerciseIds) {
            const rqEx = new sql.Request(transaction)
            rqEx.input("routineId", sql.Int, routineId)
            rqEx.input("exerciseId", sql.Int, eid)
            await rqEx.query(`
                INSERT INTO Routine_X_Exercise (routine_x_exercise_routineid, routine_x_exercise_exerciseid)
                VALUES (@routineId, @exerciseId)
            `)
        }

        const rqUr = new sql.Request(transaction)
        rqUr.input("routineId", sql.Int, routineId)
        rqUr.input("groupId", sql.Int, groupId)
        await rqUr.query(`
            INSERT INTO User_X_Routine (user_x_routine_userid, user_x_routine_routineid)
            SELECT gx.Group_x_user_userid, @routineId
            FROM Group_x_user gx
            WHERE gx.Group_x_user_groupid = @groupId
            AND NOT EXISTS (
                SELECT 1 FROM User_X_Routine ur
                WHERE ur.user_x_routine_userid = gx.Group_x_user_userid
                AND ur.user_x_routine_routineid = @routineId
            )
        `)

        await transaction.commit()
        return res.status(201).json(routineRow)
    } catch (e) {
        await transaction.rollback()
        return next(new AppError(e.message || "Error al crear la rutina de grupo", 500))
    }
})

// #region DELETE GROUP ROUTINE FOR ALL MEMBERS (creador del grupo)
exports.deleteGroupRoutineCSR = wrapAsync(async function (req, res, next) {
    const userLogued = req.userLogued
    if (!userLogued) {
        return next(new AppError("No estás registrado!", 403))
    }

    const groupId = Number(req.params.id)
    const routineId = Number(req.params.routineId)

    if (!Number.isFinite(groupId) || !Number.isFinite(routineId)) {
        return next(new AppError("Identificadores inválidos", 400))
    }

    const group = await loadGroup(groupId)
    if (!group) {
        return next(new AppError("Grupo no encontrado", 404))
    }

    if (!assertGroupCreator(group, userLogued)) {
        return next(new AppError("Solo el creador del grupo puede eliminar rutinas del grupo", 403))
    }

    const routine = await loadRoutine(routineId)
    if (!routine) {
        return next(new AppError("Rutina no encontrada", 404))
    }

    const isGroupRoutine = Number(routine.routine_is_group_routine) === 1
    const routineGroupId =
        routine.routine_groupid != null ? Number(routine.routine_groupid) : null

    if (!isGroupRoutine || routineGroupId !== groupId) {
        return next(
            new AppError("La rutina no es una rutina de este grupo o no pertenece al grupo", 403)
        )
    }

    await fromCallback(userRoutineModel.deleteByRoutineForGroupMembers, groupId, routineId)
    await fromCallback(routineExerciseModel.deleteByRoutineId, routineId)
    await fromCallback(routinesModel.delete, routineId)

    return res.status(200).json({ msg: "Rutina de grupo eliminada para todos los miembros" })
})
