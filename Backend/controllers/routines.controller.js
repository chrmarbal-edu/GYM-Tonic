// #region <DEPENDENCIAS>
/* <=============================== DEPENDENCIAS ===============================> */
const routinesmodel = require("../models/routines.model.js")
const userModel = require("../models/users.model.js")
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

const parseCount = (value) => Number(value) || 0

const toCategoryRoutine = (routine) => {
    const image = routine.routine_image || ""

    return {
        id: String(routine.routine_id),
        title: routine.routine_name,
        imageKey: image ? image.replace(/\.[^/.]+$/, "") : ""
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

    await routinesmodel.findAllWithExerciseSummary(async function(err, datosRoutines){
        if(err){
            return next(new AppError("Error al obtener categorias de rutinas", 500))
        }

        const routines = Array.isArray(datosRoutines)
            ? datosRoutines.map((routine) => ({
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

        const categories = [
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
        await routinesmodel.findById(id,function(err,datosRoutines){
            if(err){
                next(new AppError(err,404))
            } 

            if(!datosRoutines || datosRoutines.length == 0) {
                return next(new AppError("Rutina no encontrada", 404))
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
    }else{
        await routinesmodel.findByIdWithExercises(id,function(err,datosRoutine){
            if(err){
                const isNotFound = err?.err === "No hay datos"
                const statusCode = isNotFound ? 404 : 500
                const message = isNotFound ? "Rutina no encontrada" : "Error al obtener rutina con ejercicios"
                return next(new AppError(message, statusCode))
            }

            if(!datosRoutine){
                return next(new AppError("Rutina no encontrada", 404))
            }

            return res.status(200).json(datosRoutine)
        })
    }
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

    await routinesmodel.findByNameOrSlug(name, function(err, datosRoutine){
        if(err){
            const isNotFound = err?.err === "No hay datos"
            const statusCode = isNotFound ? 404 : 500
            const message = isNotFound ? "Rutina no encontrada" : "Error al buscar rutina"
            return next(new AppError(message, statusCode))
        }

        if(!datosRoutine || (Array.isArray(datosRoutine) && datosRoutine.length == 0)){
            return next(new AppError("Rutina no encontrada", 404))
        }

        return res.status(200).json(datosRoutine)
    })
})

// #region UPDATE - CSR
/* <=============================== 5. UPDATEROUTINE ===============================> */
// Actualizamos la rutina.
exports.updateRoutineCSR = wrapAsync(async function (req,res, next) {    
    const {id} = req.params
    let { name } = req.body

    console.log("id", id);

    let completeRoutine = {}  
   
    /* <================== PARTE 1 ==================> */
    // Espera una promesa de lo que devuelva la funciÃ³n "findById" del modelo. 
    await routinesmodel.findById(id, async function(err,objetoDatos){
        if(err){
            console.log("ERROR UPDATE ROUTINE SSR");

            next(new AppError(err, 500))
        }else{     
            completeRoutine = objetoDatos[0]
        }

        let updateRoutine = {}           
        updateRoutine = {            
            name: name
        }

        completeRoutine.name = updateRoutine.name
        
        // Realizamos la redirecciÃ³n en la promesa de la actualizaciÃ³n.
        await routinesmodel.updateById(id, updateRoutine, function(err, datosRutinaActualizada){
            if(err){
                console.log("ERROR UPDATE BY ID SSR");

                next(new AppError(err, 500))
            } else{
                res.status(200).json(datosRutinaActualizada);
            }
        })
    })
})

// #region CREATEROUTINE - CSR
/* <=============================== 7. CREATEROUTINE ===============================> */
exports.createRoutineCSR = wrapAsync(async function (req, res, next) {
    const { name } = req.body
    
        let newRoutine = {}

        newRoutine = {
            name: name
        }

        // Realizamos la redirecciÃ³n en la promesa de la creaciÃ³n.
        await routinesmodel.create(newRoutine,function(err,datosRutinaCreada){
            if(err){
                console.log(err)
                console.log(datosRutinaCreada)

                console.log("ERROR CREATE ROUTINE CSR");

                res.status(500).json({error: err})
            } else{
                res.status(200).json({ datosRutinaCreada })
            }
        })
    
});

// #region DELETE - CSR
/* <=============================== 8. DELETEROUTINE ===============================> */
exports.deleteRoutineCSR = wrapAsync(async function (req, res, next) {
    const { id } = req.params;
        await routinesmodel.findById(id, async function (err, objetoDatos) {
            if (err) {
                return next(new AppError("Rutina no encontrada", 404));
            }

            if (!objetoDatos || objetoDatos.length == 0) {
                return next(new AppError("Rutina no encontrada", 404));
            }

            /* <================== PARTE 2 ==================> */
            await routinesmodel.delete(id, function (err, datosRutinaEliminada) {
                if (err) {
                    return next(new AppError("Error al eliminar la rutina", 500));
                }else {
                    return res.status(200).json({ msg: "Rutina eliminada correctamente" });
                }
            });
        });
});

