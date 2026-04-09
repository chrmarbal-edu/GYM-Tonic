/* <=============================== DEPENDENCIAS ===============================> */
const exercisesModel = require("../models/exercises.model")
const AppError = require("../utils/AppError")

// Función Para Capturar Errores Asíncronos
function wrapAsync(fn) {
    return function (req, res, next) {
        fn(req, res, next).catch(e => {
            next(e)
        })
    }
}

/* <=============================== FIND ALL EXERCISES ===============================> */
exports.findAllExercises = wrapAsync(async function(req, res, next) {
    await exercisesModel.findAll(async function(err, datosExercises) {
        if(err){
            next(new AppError(err, 400))
        } else{
            res.status(200).json(datosExercises)
        }
    })    
})

/* <=============================== FIND EXERCISE BY ID ===============================> */
exports.findExerciseById = wrapAsync(async function (req, res, next) {
    const {id} = req.params

    await exercisesModel.findById(id, function(err, datosExercise){
        if(err){
            next(new AppError(err, 404))
        }

        if(!datosExercise || datosExercise.length == 0){
            return next(new AppError("Ejercicio no encontrado", 404))
        }

        res.status(200).json(datosExercise)
    })
})

/* <=============================== CREATE EXERCISE ===============================> */
exports.createExercise = wrapAsync(async function(req, res, next) {
    const { name, description, type, video, image } = req.body

    let newExercise = {}

    newExercise = {
        name: name,
        description: description,
        type: type,
        video: video,
        image: image
    }

    await exercisesModel.create(newExercise, function(err, datosEjercicioCreado){
        if(err){
            next(new AppError(err, 500))
        } else{
            res.status(201).json(datosEjercicioCreado)
        }
    })
})

/* <=============================== UPDATE EXERCISE BY ID ===============================> */
exports.updateExerciseById = wrapAsync(async function (req, res, next) {
    const {id} = req.params

    let { name, description, type, video, image } = req.body

    // BUSCAMOS EL EJERCICIO
    await exercisesModel.findById(id, async function (err, exerciseFounded) {
        if(err){
            next(new AppError(err, 500))
        } else{
            if(!exerciseFounded || exerciseFounded.length == 0){
                next(new AppError("No se ha encontrado el ejercicio", 404))
            }

            // NAME
            if(name && name != ""){
                exerciseFounded.exercise_name = name
            }

            // DESCRIPTION
            if(description && description != ""){
                exerciseFounded.exercise_description = description
            }

            // TYPE
            if(type && type != ""){
                exerciseFounded.exercise_type = type
            }

            // VIDEO
            if(video && video != ""){
                exerciseFounded.exercise_video = video
            }

            // IMAGE
            if(image && image != ""){
                exerciseFounded.exercise_image = image
            }
            
            // ACTUALIZAMOS EJERCICIO
            await exercisesModel.updateById(id, exerciseFounded, function (err, datosEjercicioActualizado) {
                if(err){
                    next(new AppError(err, 500))
                } else{
                    res.status(200).json(datosEjercicioActualizado)
                }
            })
        }
    })
})

/* <=============================== DELETE EXERCISE BY ID ===============================> */
exports.deleteExerciseById = wrapAsync(async function (req, res, next) {
    const {id} = req.params
    const userLogued = req.userLogued

    if(userLogued && userLogued.user_role == 1){
        await exercisesModel.delete(id, function(err, datosEjercicioEliminado){
            if(err){
                next(new AppError("Error al eliminar el ejercicio", 500))
            } else{
                res.status(200).json(datosEjercicioEliminado)
            }
        })
    } else{
        next(new AppError("No estás autorizado para realizar esta operación", 403))
    }
})