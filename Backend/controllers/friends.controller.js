/* <=============================== DEPENDENCIAS ===============================> */
const friendsModel = require("../models/friends.model")
const AppError = require("../utils/AppError")

// Función Para Capturar Errores Asíncronos
function wrapAsync(fn) {
    return function (req, res, next) {
        fn(req, res, next).catch(e => {
            next(e)
        })
    }
}

/* <=============================== FIND ALL FRIENDSHIPS ===============================> */
exports.findAllFriends = wrapAsync(async function(req, res, next) {
    const userLogued = req.userLogued

    if(userLogued && userLogued.user_role == 1){
        await friendsModel.findAll(function(err, datosFriendships) {
            if(err){
                next(new AppError(err, 400))
            } else{
                res.status(200).json(datosFriendships)
            }
        })
    } else{
        next(new AppError("No estás autorizado para realizar esta operación", 403))
    }
})

/* <=============================== FIND FRIENDSHIPS BY USERID ===============================> */
exports.findFriendsByUserId = wrapAsync(async function (req, res, next) {
    const { userId } = req.params
    const userLogued = req.userLogued
    if(userLogued && (userLogued.user_role == 1 || userLogued.user_id == userId)){
        await friendsModel.findByUserId(userId, async function(err, datosFriendships){
            if(err){
                next(new AppError(err, 404))
            } else{
                res.status(200).json(datosFriendships)
            }
        })

    } else{
        next(new AppError("No estás autorizado para realizar esta operación", 403))
    }
})

/* <=============================== FIND FRIENDSHIP BY ID ===============================> */
exports.findFriendById = wrapAsync(async function (req, res, next) {
    const {id} = req.params

    const userLogued = req.userLogued

    if(userLogued && userLogued.user_role == 1){
        await friendsModel.findById(id, function(err, datosFriendship){
            if(err){
                next(new AppError(err, 404))
            }

            if(!datosFriendship || datosFriendship.length == 0){
                return next(new AppError("Amistad no encontrada", 404))
            }

            res.status(200).json(datosFriendship)
        })
    } else{
        next(new AppError("No estás autorizado para realizar esta operación", 403))
    }
})

/* <=============================== CREATE ===============================> */
exports.create = wrapAsync(async function (req, res, next) {
    const { userId1, userId2 } = req.body

    const userLogued = req.userLogued

    if(userLogued && userLogued.user_role == 1){
        let newFriendship = {}

        newFriendship = {
            friend_userid1: userId1,
            friend_userid2: userId2
        }

        await friendsModel.create(newFriendship, function(err, datosAmistadCreada) {
            if(err){
                next(new AppError(err, 400))
            } else{
                res.status(201).json(datosAmistadCreada)
            }
        })
    } else{
        next(new AppError("No estás autorizado para realizar esta operación", 403))
    }
})

/* <=============================== DELETE FRIENDSHIP BY ID ===============================> */
exports.deleteFriendById = wrapAsync(async function (req, res, next) {
    const {id} = req.params

    const userLogued = req.userLogued

    await friendsModel.findById(id, async function(err, datosFriendship){
        if(err){
            next(new AppError(err, 404))
        } else{
            if(userLogued && (userLogued.user_role == 1 || (userLogued.user_id == datosFriendship.friend_userid1 || userLogued.user_id == datosFriendship.friend_userid2))){
                await friendsModel.delete(id, function(error, datosAmistadEliminada){
                    if(error){
                        next(new AppError(error, 500))
                    } else{
                        res.status(200).json(datosAmistadEliminada)
                    }
                })
            }
        }
    })
})