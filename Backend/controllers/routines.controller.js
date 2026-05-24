// #region <DEPENDENCIAS>
/* <=============================== DEPENDENCIAS ===============================> */
const routinesmodel = require("../models/routines.model.js")
const routineExerciseModel = require("../models/routineExercises.model.js")
const userModel = require("../models/users.model.js")
const groupmodel = require("../models/groups.model.js")
const fs = require("fs").promises
const path = require("path")
const AppError = require("../utils/AppError")
const bcrypt = require("../utils/bcrypt")
const jwtMW = require("../middlewares/jwt.mw")
const sql = require("mssql")
const dbConn = require("../utils/mssql.config")
const { log } = require("console")
const { handleSqlError } = require("../utils/sqlErrors")
const { deleteResourceFile } = require("../utils/fileUtils") // Import the new utility
const {
    normalizeRoutineImageForClient,
} = require("../utils/routineHelpers.js")
const {
    assertCanManageRoutine,
    assertCanViewRoutine,
    canManageRoutine
} = require("../utils/routinePermissions.js")

/**
 * Una rutina es personal si NO es de grupo y el usuario NO es administrador.
 * @returns {number} 1 si es personal, 0 si es de catálogo (admin)
 */
const parseIsPersonalFlag = (userLogued) => {
    return userLogued.user_role === 1 ? 0 : 1
}

const assertValidExercises = async (exercises) => {
    const pool = await sql.connect(dbConn)

    for (const exercise of exercises) {
        if (!exercise || typeof exercise.exercise_id === 'undefined' || !Number.isFinite(exercise.exercise_id)) {
            throw new AppError("Cada ejercicio debe tener un 'exercise_id' numérico válido", 400);
        }
        if (!exercise.reps || typeof exercise.reps !== 'string' || exercise.reps.trim() === '') {
            throw new AppError(`El ejercicio ${exercise.exercise_id} debe tener 'reps' válidas (cadena no vacía)`, 400);
        }
        if (!exercise.sets || !Number.isFinite(exercise.sets) || exercise.sets <= 0) {
            throw new AppError(`El ejercicio ${exercise.exercise_id} debe tener 'sets' numéricas válidas y mayores que 0`, 400);
        }

        const check = await pool
            .request()
            .input("exerciseId", sql.Int, exercise.exercise_id)
            .query("SELECT 1 FROM Exercises WHERE exercise_id = @exerciseId")

        if (check.recordset.length === 0) {
            throw new AppError(`El ejercicio ${exercise.exercise_id} no existe`, 400)
        }
    }
}

const replaceRoutineExercises = async (routineId, exercises) => {
    return new Promise((resolve, reject) => {
        routineExerciseModel.deleteByRoutineId(routineId, async (err) => {
            if (err) return reject(err)
            try {
                for (const exercise of exercises) {
                    await new Promise((res, rej) => {
                        routineExerciseModel.create(
                            {
                                routine_x_exercise_routineid: routineId,
                                routine_x_exercise_exerciseid: exercise.exercise_id,
                                routine_x_exercise_reps: exercise.reps,
                                routine_x_exercise_sets: exercise.sets
                            },
                            (e) => (e ? rej(e) : res())
                        )
                    })
                }
                resolve()
            } catch (error) {
                reject(error)
            }
        })
    })
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
            next(new AppError(handleSqlError(err), 400))
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

    // Obtenemos las rutinas generales
    const datosRoutines = await new Promise((resolve) => {
        routinesmodel.findAllWithExerciseSummary((err, data) => resolve(err ? [] : data))
    })

    // Obtenemos las rutinas personales si el usuario existe
    const datosPersonal = await new Promise((resolve) => {
        if (!Number.isFinite(userId)) return resolve([])
        routinesmodel.findPersonalByUserId(userId, (err, data) => resolve(err ? [] : data))
    })

    const routines = Array.isArray(datosRoutines)
        ? datosRoutines.map((routine) => ({
            ...routine,
            exercises_count: parseCount(routine.exercises_count),
            strength_exercises_count: parseCount(routine.strength_exercises_count),
            cardio_exercises_count: parseCount(routine.cardio_exercises_count)
        }))
        : []

    const personalRoutines = Array.isArray(datosPersonal)
        ? datosPersonal.map((routine) => ({
            ...routine,
            exercises_count: parseCount(routine.exercises_count),
            strength_exercises_count: parseCount(routine.strength_exercises_count),
            cardio_exercises_count: parseCount(routine.cardio_exercises_count)
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
            routine.strength_exercises_count >= routine.cardio_exercises_count
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
        await routinesmodel.findById(id, async function (err, datosRoutines) {
            if(err){
                return next(new AppError(handleSqlError(err), 404))
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

    await routinesmodel.findByIdWithExercises(id, async function (err, datosRoutine) {
        if (err) {
            const isNotFound = err?.err === "No hay datos"
            return next(new AppError(handleSqlError(err), isNotFound ? 404 : 500))
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
            return next(new AppError(handleSqlError(err), statusCode))
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
    const { name, routine_image: routineImageBody, exercises: exercisesRaw } = req.body

    let exercises = null;
    if (exercisesRaw !== undefined && exercisesRaw !== null) {
        try {
            exercises = JSON.parse(exercisesRaw);
            if (!Array.isArray(exercises)) throw new Error("exercises no es un array válido");
        } catch (e) {
            return next(new AppError("El formato de 'exercises' no es válido. Debe ser un array JSON de objetos.", 400));
        }
    }

    if (!Number.isFinite(routineId)) {
        return next(new AppError("routine_id inválido", 400))
    }

    await routinesmodel.findById(routineId, async function (err, existingRoutine) {
        if (err || !existingRoutine) {
            return next(new AppError("Rutina no encontrada", 404))
        }

        await assertCanManageRoutine(userLogued, existingRoutine)

        let routineImage = undefined;
        if (req.files?.image?.[0]) {
            const file = req.files.image[0];
            let folderName = userLogued.user_username;
            let subfolder = "users";

            // Si la rutina es de grupo, guardamos en la carpeta del grupo
            if (Number(existingRoutine.routine_is_group_routine) === 1 && existingRoutine.routine_groupid) {
                const group = await new Promise((resolve) => {
                    groupmodel.findById(existingRoutine.routine_groupid, (err, data) => resolve(err ? null : data));
                });
                if (group) {
                    folderName = group.group_name;
                    subfolder = "groups";
                }
            }

            const targetDir = path.join("public", "images", "routines", subfolder, folderName);
            await fs.mkdir(targetDir, { recursive: true });
            const sanitizedName = (name || existingRoutine.routine_name).trim().toLowerCase().replace(/[^a-z0-9]+/g, '_');
            const fileName = `${sanitizedName}${path.extname(file.originalname)}`;
            await fs.rename(file.path, path.join(targetDir, fileName));
            routineImage = `images/routines/${subfolder}/${folderName}/${fileName}`.replace(/\\/g, "/");
        } else if (typeof routineImageBody === "string" && routineImageBody.trim()) {
            routineImage = routineImageBody.trim();
        }

        if (name !== undefined && name !== null && typeof name !== "string") {
            return next(new AppError("El nombre de la rutina no es válido", 400))
        }

        if (exercises !== null) {
            if (!Array.isArray(exercises) || exercises.length === 0) {
                return next(new AppError("La lista de ejercicios debe ser un array con al menos un ejercicio", 400))
            }
            await assertValidExercises(exercises)
        }

        const updateRoutine = {
            routine_name: typeof name === "string" && name.trim() ? name.trim() : existingRoutine.routine_name,
            routine_is_group_routine: existingRoutine.routine_is_group_routine ?? 0,
            routine_groupid: existingRoutine.routine_groupid ?? null
        }

        if (routineImage !== undefined) {
            updateRoutine.routine_image = routineImage
        }

        await routinesmodel.updateById(routineId, updateRoutine, async function (errUpdate) {
            if (errUpdate) return next(new AppError(handleSqlError(errUpdate), 500))

            if (exercises !== null) {
                await replaceRoutineExercises(routineId, exercises)
            }

            await routinesmodel.findByIdWithExercises(routineId, async function (errFinal, updatedRoutine) {
                const canEdit = await canManageRoutine(userLogued, existingRoutine)
                return res.status(200).json({ ...updatedRoutine, can_edit: canEdit })
            })
        })
    })
})

// #region CREATEROUTINE - CSR
/* <=============================== 7. CREATEROUTINE ===============================> */
exports.createRoutineCSR = wrapAsync(async function (req, res, next) {
    const userLogued = req.userLogued
    if (!userLogued) {
        return next(new AppError("No estás registrado!", 403))
    }

    const { name, routine_image: routineImageBody, exercises: exercisesRaw } = req.body

    let routineImage = null;
    if (req.files?.image?.[0]) {
        const file = req.files.image[0];
        const folderName = userLogued.user_username;
        const targetDir = path.join("public", "images", "routines", "users", folderName);
        await fs.mkdir(targetDir, { recursive: true });
        const sanitizedName = name.trim().toLowerCase().replace(/[^a-z0-9]+/g, '_');
        const fileName = `${sanitizedName}${path.extname(file.originalname)}`;
        await fs.rename(file.path, path.join(targetDir, fileName));
        routineImage = `images/routines/users/${folderName}/${fileName}`.replace(/\\/g, "/");
    } else if (typeof routineImageBody === "string" && routineImageBody.trim()) {
        routineImage = routineImageBody.trim();
    }

    let exercises;
    try {
        exercises = JSON.parse(exercisesRaw);
        if (!Array.isArray(exercises)) throw new Error("exercises no es un array válido");
    } catch (e) {
        return next(new AppError("El formato de 'exercises' no es válido. Debe ser un array JSON de objetos.", 400));
    }

    if (!name || typeof name !== "string" || !name.trim()) {
        return next(new AppError("El nombre de la rutina es obligatorio", 400))
    }

    if (!Array.isArray(exercises) || exercises.length === 0) {
        return next(
            new AppError("La lista de ejercicios debe ser un array con al menos un ejercicio", 400)
        )
    }
    await assertValidExercises(exercises)

    // Si el rol es 1 (Admin), no es personal (0). Si no, es personal (1).
    const isPersonal = Number(userLogued.user_role) === 1 ? 0 : 1
    
    // Nos aseguramos de pillar el ID con cualquiera de los dos nombres posibles
    const userId = Number(userLogued.user_id || userLogued.idUser)

    const newRoutineData = {
        routine_name: name.trim(),
        routine_image: routineImage,
        routine_creator_id: isPersonal === 1 ? userId : null,
        routine_is_personal_routine: isPersonal,
        routine_is_group_routine: 0
    }

    await routinesmodel.create(newRoutineData, async function (err, createdRoutine) {
        if (err) return next(new AppError(handleSqlError(err), 500))

        await replaceRoutineExercises(createdRoutine.routine_id, exercises)

        await routinesmodel.findByIdWithExercises(createdRoutine.routine_id, function (errEx, routineWithExercises) {
            return res.status(201).json({
                ...routineWithExercises,
                routine_image: normalizeRoutineImageForClient(routineWithExercises.routine_image),
                can_edit: true
            })
        })
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

    await routinesmodel.findById(routineId, async function (err, existingRoutine) {
        if (err || !existingRoutine) {
            return next(new AppError("Rutina no encontrada", 404))
        }

        await assertCanManageRoutine(userLogued, existingRoutine)

        await routinesmodel.delete(routineId, function (errDel) {
            if (errDel) return next(new AppError(handleSqlError(errDel), 500))
            return res.status(200).json({ msg: "Rutina eliminada correctamente" })
        })
    })
});
