// #region <DEPENDENCIAS>
/* <=============================== DEPENDENCIAS ===============================> */
const routinesmodel = require("../models/routines.model.js")
const routineExerciseModel = require("../models/routineExercises.model.js")
const userModel = require("../models/users.model.js")
const fs = require("fs").promises
const AppError = require("../utils/AppError")
const bcrypt = require("../utils/bcrypt")
const jwtMW = require("../middlewares/jwt.mw")
const sql = require("mssql")
const dbConn = require("../utils/mssql.config")
const { log } = require("console")
const {
    buildRoutineImagePath,
    normalizeRoutineImageForClient,
    parseExerciseIds
} = require("../utils/routineHelpers.js")
const {
    assertCanManageRoutine,
    assertCanViewRoutine,
    canManageRoutine
} = require("../utils/routinePermissions.js")

const promisifyModel =
    (fn) =>
    (...args) =>
        new Promise((resolve, reject) => {
            fn(...args, (err, result) => {
                if (err) reject(err)
                else resolve(result)
            })
        })

const findRoutineById = promisifyModel(routinesmodel.findById)
const findRoutineByIdWithExercises = promisifyModel(routinesmodel.findByIdWithExercises)
const findPersonalRoutinesByUserId = promisifyModel(routinesmodel.findPersonalByUserId)

const parseIsPersonalFlag = (rawValue, userLogued) => {
    if (rawValue === undefined || rawValue === null || rawValue === "") {
        return 1
    }
    if (rawValue === "0" || rawValue === 0 || rawValue === false || rawValue === "false") {
        return 0
    }
    if (rawValue === "1" || rawValue === 1 || rawValue === true || rawValue === "true") {
        return 1
    }
    return Number(userLogued?.user_role) === 1 ? 0 : 1
}
const createRoutineModel = promisifyModel(routinesmodel.create)
const updateRoutineModel = promisifyModel(routinesmodel.updateById)
const deleteRoutineExercises = promisifyModel(routineExerciseModel.deleteByRoutineId)

const assertValidExerciseIds = async (exerciseIds) => {
    const pool = await sql.connect(dbConn)

    for (const exerciseId of exerciseIds) {
        const check = await pool
            .request()
            .input("exerciseId", sql.Int, exerciseId)
            .query("SELECT 1 FROM Exercises WHERE exercise_id = @exerciseId")

        if (check.recordset.length === 0) {
            throw new AppError(`El ejercicio ${exerciseId} no existe`, 400)
        }
    }
}

const replaceRoutineExercises = async (routineId, exerciseIds) => {
    await deleteRoutineExercises(routineId)

    for (const exerciseId of exerciseIds) {
        await new Promise((resolve, reject) => {
            routineExerciseModel.create(
                {
                    routine_x_exercise_routineid: routineId,
                    routine_x_exercise_exerciseid: exerciseId
                },
                (err) => {
                    if (err) reject(err)
                    else resolve()
                }
            )
        })
    }
}


function wrapAsync(fn) {
    return function(req, res, next) {
        fn(req, res, next).catch(e => {
            next(e);
        });
    }
}

const parseCount = (value) => Number(value) || 0

const toCategoryRoutine = (routine) => {
    const routineId = Number(routine.routine_id)
    const routineName = routine.routine_name || ""
    const routineImage = normalizeRoutineImageForClient(routine.routine_image)

    return {
        routine_id: Number.isFinite(routineId) ? routineId : 0,
        routine_name: routineName,
        routine_image: routineImage,
        id: String(routine.routine_id),
        title: routineName
    }
}

const byNewest = (a, b) => Number(b.routine_id) - Number(a.routine_id)
const byMoreExercises = (a, b) => {
    if (b.exercises_count === a.exercises_count) {
        return Number(b.routine_id) - Number(a.routine_id)
    }

    return b.exercises_count - a.exercises_count
}
const byLessExercises = (a, b) => {
    if (a.exercises_count === b.exercises_count) {
        return Number(b.routine_id) - Number(a.routine_id)
    }

    return a.exercises_count - b.exercises_count
}

const takeRoutines = (routines, max = 3) => routines.slice(0, max)
/* 
400 - BAD REQUEST (EL SERVIDOR NO PUEDE PROCESAR LA SOLICITUD)
404 - NOT FOUND (NO EXISTE EN EL SERVIDOR EL RECURSO PEDIDO)
500 - GENÃ‰RICO (ALGO HA IDO MAL EN EL SERVIDOR)
*/

// #region <---CSR GROUPS--->

// #region FINDALL - CSR
/* <=============================== 2. FINDALLROUTINES ===============================> */
// Buscamos todos los grupos.
exports.findAllRoutinesCSR = wrapAsync(async function (req,res,next) { 
    // Espera una promesa de lo que devuelva la funciÃ³n "findAll" del modelo.
    await routinesmodel.findAll(async function(err, datosRoutines){
        if(err){
            next(new AppError(err,400))
        } else{
            res.status(200).json(datosRoutines)
        }
    })        
})

// #region FIND-CATEGORIES - CSR
/* <=============================== 2.1 FINDROUTINECATEGORIES ===============================> */
exports.findRoutineCategoriesCSR = wrapAsync(async function (req,res,next) {
    const userLogued = req.userLogued

    if(!userLogued){
        return next(new AppError("No estÃ¡s registrado!", 403))
    }

    const userId = Number(userLogued.user_id)

    const [datosRoutines, datosPersonal] = await Promise.all([
        new Promise((resolve, reject) => {
            routinesmodel.findAllWithExerciseSummary((err, data) => {
                if (err) reject(err)
                else resolve(data)
            })
        }),
        Number.isFinite(userId)
            ? findPersonalRoutinesByUserId(userId).catch(() => [])
            : Promise.resolve([])
    ]).catch(() => {
        throw new AppError("Error al obtener categorias de rutinas", 500)
    })

    const routines = Array.isArray(datosRoutines)
        ? datosRoutines.map((routine) => ({
            ...routine,
            exercises_count: parseCount(routine.exercises_count),
            strength_exercises_count: parseCount(routine.strength_exercises_count),
            cardio_exercises_count: parseCount(routine.cardio_exercises_count),
            flexibility_exercises_count: parseCount(routine.flexibility_exercises_count)
        }))
        : []

    const personalRoutines = Array.isArray(datosPersonal)
        ? datosPersonal.map((routine) => ({
            ...routine,
            exercises_count: parseCount(routine.exercises_count),
            strength_exercises_count: parseCount(routine.strength_exercises_count),
            cardio_exercises_count: parseCount(routine.cardio_exercises_count),
            flexibility_exercises_count: parseCount(routine.flexibility_exercises_count)
        }))
        : []

    const recent = takeRoutines([...routines].sort(byNewest))

        const beginnerByName = routines.filter((routine) =>
            /principiante|beginner|iniciaci[oó]n|b[aá]sico/i.test(routine.routine_name || "")
        )
        const beginners = takeRoutines(
            beginnerByName.length > 0
                ? [...beginnerByName].sort(byNewest)
                : [...routines].sort(byLessExercises)
        )

        const muscleGroupByType = routines.filter((routine) =>
            routine.strength_exercises_count > 0 &&
            routine.strength_exercises_count >= routine.cardio_exercises_count &&
            routine.strength_exercises_count >= routine.flexibility_exercises_count
        )
        const muscleGroups = takeRoutines(
            [...muscleGroupByType].sort((a, b) => {
                if (b.strength_exercises_count === a.strength_exercises_count) {
                    return byMoreExercises(a, b)
                }

                return b.strength_exercises_count - a.strength_exercises_count
            })
        )

        const recommended = takeRoutines([...routines].sort(byMoreExercises))

        const myRoutinesCategory =
            personalRoutines.length > 0
                ? [
                      {
                          id: "my_routines",
                          title: "Mis rutinas",
                          routines: personalRoutines.map(toCategoryRoutine)
                      }
                  ]
                : []

        const categories = [
            ...myRoutinesCategory,
            {
                id: "recent",
                title: "Recientes",
                routines: recent.map(toCategoryRoutine)
            },
            {
                id: "beginners",
                title: "Para Principiantes",
                routines: beginners.map(toCategoryRoutine)
            },
            {
                id: "muscle_groups",
                title: "Por Grupo Muscular",
                routines: muscleGroups.map(toCategoryRoutine)
            },
            {
                id: "recommended",
                title: "Recomendados",
                routines: recommended.map(toCategoryRoutine)
            }
        ]

        return res.status(200).json(categories)
})

// #region FIND-ID - CSR
/* <=============================== 3. FINDROUTINEBYID ===============================> */
// Buscamos los grupos por "id".
exports.findRoutineByIdCSR = wrapAsync(async function (req,res,next){
    // Traemos por parÃ¡metro el id enviado como parÃ¡metro por la ruta.
    const {id} = req.params
    const userLogued = req.userLogued;
    // Espera una promesa de lo que devuelva la funciÃ³n "findById" del modelo.
    if(!userLogued){
        return next(new AppError("No estÃ¡s registrado!", 403))
    }else{
        await routinesmodel.findById(id, async function(err, datosRoutines){
            if(err){
                return next(new AppError(err,404))
            }

            if(!datosRoutines || datosRoutines.length == 0) {
                return next(new AppError("Rutina no encontrada", 404))
            }

            try {
                await assertCanViewRoutine(userLogued, {
                    routine_id: datosRoutines.routine_id,
                    routine_creator_id: datosRoutines.routine_creator_id,
                    routine_is_personal_routine: datosRoutines.routine_is_personal_routine,
                    routine_is_group_routine: datosRoutines.routine_is_group_routine,
                    routine_groupid: datosRoutines.routine_groupid
                })
            } catch (viewErr) {
                return next(viewErr)
            }

            res.status(200).json(datosRoutines)
        })
    }
})

// #region FIND-ID-WITH-EXERCISES - CSR
/* <=============================== 3.1 FINDROUTINEWITHEXERCISESBYID ===============================> */
exports.findRoutineWithExercisesByIdCSR = wrapAsync(async function (req,res,next){
    const {id} = req.params
    const userLogued = req.userLogued

    if(!userLogued){
        return next(new AppError("No estás registrado!", 403))
    }

    const datosRoutine = await findRoutineByIdWithExercises(id).catch((err) => {
        const isNotFound = err?.err === "No hay datos"
        const statusCode = isNotFound ? 404 : 500
        const message = isNotFound ? "Rutina no encontrada" : "Error al obtener rutina con ejercicios"
        throw new AppError(message, statusCode)
    })

    if(!datosRoutine){
        return next(new AppError("Rutina no encontrada", 404))
    }

    await assertCanViewRoutine(userLogued, {
        routine_id: datosRoutine.routine_id,
        routine_creator_id: datosRoutine.routine_creator_id,
        routine_is_group_routine: datosRoutine.routine_is_group_routine,
        routine_groupid: datosRoutine.routine_groupid
    })

    const canEdit = await canManageRoutine(userLogued, {
        routine_id: datosRoutine.routine_id,
        routine_creator_id: datosRoutine.routine_creator_id,
        routine_is_group_routine: datosRoutine.routine_is_group_routine,
        routine_groupid: datosRoutine.routine_groupid
    })

    return res.status(200).json({
        ...datosRoutine,
        routine_image: normalizeRoutineImageForClient(datosRoutine.routine_image),
        can_edit: canEdit
    })
})

// #region FIND-NAME - CSR
/* <=============================== 4. FINDROUTINEBYNAME ===============================> */
// Buscamos las rutinas por nombre o slug.
exports.findRoutineByNameCSR = wrapAsync(async function (req,res,next){
    const {name} = req.params
    const userLogued = req.userLogued

    if(!userLogued){
        return next(new AppError("No estas registrado!", 403))
    }

    await routinesmodel.findByNameOrSlug(name, async function(err, datosRoutine){
        if(err){
            const isNotFound = err?.err === "No hay datos"
            const statusCode = isNotFound ? 404 : 500
            const message = isNotFound ? "Rutina no encontrada" : "Error al buscar rutina"
            return next(new AppError(message, statusCode))
        }

        if(!datosRoutine || (Array.isArray(datosRoutine) && datosRoutine.length == 0)){
            return next(new AppError("Rutina no encontrada", 404))
        }

        try {
            await assertCanViewRoutine(userLogued, datosRoutine)
        } catch (viewErr) {
            return next(viewErr)
        }

        return res.status(200).json(datosRoutine)
    })
})

// #region UPDATE - CSR
/* <=============================== 5. UPDATEROUTINE ===============================> */
exports.updateRoutineCSR = wrapAsync(async function (req, res, next) {
    const userLogued = req.userLogued
    if (!userLogued) {
        return next(new AppError("No estás registrado!", 403))
    }

    const routineId = Number(req.params.id)
    const { name, routine_image: routineImageBody, exercise_ids: exerciseIdsRaw } = req.body
    const uploadedImage = buildRoutineImagePath(req.files)
    const exerciseIds = parseExerciseIds(exerciseIdsRaw)

    if (!Number.isFinite(routineId)) {
        return next(new AppError("routine_id inválido", 400))
    }

    let existingRoutine
    try {
        existingRoutine = await findRoutineById(routineId)
    } catch (err) {
        return next(new AppError("Rutina no encontrada", 404))
    }

    if (!existingRoutine) {
        return next(new AppError("Rutina no encontrada", 404))
    }

    await assertCanManageRoutine(userLogued, existingRoutine)

    if (name !== undefined && name !== null && typeof name !== "string") {
        return next(new AppError("El nombre de la rutina no es válido", 400))
    }

    if (exerciseIds !== null) {
        if (!Array.isArray(exerciseIds) || exerciseIds.length === 0) {
            return next(
                new AppError("exercise_ids debe ser un array con al menos un ejercicio", 400)
            )
        }

        if (exerciseIds.some((exerciseId) => !Number.isFinite(exerciseId))) {
            return next(new AppError("exercise_ids debe contener solo números enteros", 400))
        }

        await assertValidExerciseIds(exerciseIds)
    }

    const routineImage =
        uploadedImage ||
        (typeof routineImageBody === "string" && routineImageBody.trim()
            ? routineImageBody.trim()
            : undefined)

    const updateRoutine = {
        routine_name:
            typeof name === "string" && name.trim() ? name.trim() : existingRoutine.routine_name,
        routine_is_group_routine:
            existingRoutine.routine_is_group_routine !== undefined &&
            existingRoutine.routine_is_group_routine !== null
                ? existingRoutine.routine_is_group_routine
                : 0,
        routine_groupid: existingRoutine.routine_groupid ?? null
    }

    if (routineImage !== undefined) {
        updateRoutine.routine_image = routineImage
    }

    await updateRoutineModel(routineId, updateRoutine)

    if (exerciseIds !== null) {
        await replaceRoutineExercises(routineId, exerciseIds)
    }

    const updatedRoutine = await findRoutineByIdWithExercises(routineId)
    const canEdit = await canManageRoutine(userLogued, existingRoutine)
    return res.status(200).json({ ...updatedRoutine, can_edit: canEdit })
})

// #region CREATEROUTINE - CSR
/* <=============================== 7. CREATEROUTINE ===============================> */
exports.createRoutineCSR = wrapAsync(async function (req, res, next) {
    const userLogued = req.userLogued
    if (!userLogued) {
        return next(new AppError("No estás registrado!", 403))
    }

    const { name, routine_image: routineImageBody, exercise_ids: exerciseIdsRaw } = req.body
    const uploadedImage = buildRoutineImagePath(req.files)
    const exerciseIds = parseExerciseIds(exerciseIdsRaw)

    if (!name || typeof name !== "string" || !name.trim()) {
        return next(new AppError("El nombre de la rutina es obligatorio", 400))
    }

    if (!Array.isArray(exerciseIds) || exerciseIds.length === 0) {
        return next(
            new AppError("exercise_ids debe ser un array con al menos un ejercicio", 400)
        )
    }

    if (exerciseIds.some((exerciseId) => !Number.isFinite(exerciseId))) {
        return next(new AppError("exercise_ids debe contener solo números enteros", 400))
    }

    await assertValidExerciseIds(exerciseIds)

    const routineImage =
        uploadedImage ||
        (typeof routineImageBody === "string" && routineImageBody.trim()
            ? routineImageBody.trim()
            : null)

    const isPersonal = parseIsPersonalFlag(req.body.is_personal, userLogued)

    const createdRoutine = await createRoutineModel({
        routine_name: name.trim(),
        routine_image: routineImage,
        routine_creator_id: isPersonal === 1 ? Number(userLogued.user_id) : null,
        routine_is_personal_routine: isPersonal,
        routine_is_group_routine: 0
    })

    await replaceRoutineExercises(createdRoutine.routine_id, exerciseIds)

    const routineWithExercises = await findRoutineByIdWithExercises(createdRoutine.routine_id)
    return res.status(201).json({
        ...routineWithExercises,
        routine_image: normalizeRoutineImageForClient(routineWithExercises.routine_image),
        can_edit: true
    })
});

// #region DELETE - CSR
/* <=============================== 8. DELETEROUTINE ===============================> */
exports.deleteRoutineCSR = wrapAsync(async function (req, res, next) {
    const userLogued = req.userLogued
    if (!userLogued) {
        return next(new AppError("No estás registrado!", 403))
    }

    const routineId = Number(req.params.id)
    if (!Number.isFinite(routineId)) {
        return next(new AppError("routine_id inválido", 400))
    }

    let existingRoutine
    try {
        existingRoutine = await findRoutineById(routineId)
    } catch (err) {
        return next(new AppError("Rutina no encontrada", 404))
    }

    if (!existingRoutine) {
        return next(new AppError("Rutina no encontrada", 404))
    }

    await assertCanManageRoutine(userLogued, existingRoutine)

    await promisifyModel(routinesmodel.delete)(routineId)
    return res.status(200).json({ msg: "Rutina eliminada correctamente" })
});
