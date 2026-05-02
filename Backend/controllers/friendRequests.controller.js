/* <=============================== DEPENDENCIAS ===============================> */
const friendRequestsModel = require("../models/friendRequests.model")
const AppError = require("../utils/AppError")

// Función Para Capturar Errores Asíncronos
function wrapAsync(fn) {
    return function (req, res, next) {
        fn(req, res, next).catch(e => {
            next(e)
        })
    }
}

/* <=============================== FIND ALL FRIEND REQUESTS ===============================> */
exports.findAllFriendRequests = wrapAsync(async function(req, res, next) {
    const userLogued = req.userLogued
    
    if(userLogued && userLogued.user_role == 1){
        await friendRequestsModel.findAll(function(err, datosFriendRequests) {
            if(err){
                next(new AppError(err, 400))
            } else{
                res.status(200).json(datosFriendRequests)
            }
        })
    } else{
        next(new AppError("No estás autorizado para realizar esta operación", 403))
    }
})

/* <=============================== FIND FRIEND REQUEST BY ID ===============================> */
exports.findFriendRequestById = wrapAsync(async function(req, res, next) {
    const {id} = req.params
    const userLogued = req.userLogued
    
    if(userLogued && userLogued.user_role == 1){
        await friendRequestsModel.findById(id, function(err, datosFriendRequest) {
            if(err){
                next(new AppError(err, 404))
            } else{
                res.status(200).json(datosFriendRequest)
            }
        })
    } else{
        next(new AppError("No estás autorizado para realizar esta operación", 403))
    }
})

/* <=============================== CREATE FRIEND REQUEST ===============================> */
exports.create = wrapAsync(async function(req, res, next) {
    const { sender, receiver } = req.body
    const userLogued = req.userLogued

    let newFRequest = {
        frequest_sender: sender,
        frequest_receiver: receiver
    }

    if(userLogued && (userLogued.user_id != receiver && userLogued.user_id == sender)){
        await friendRequestsModel.create(newFRequest, function(err, datosFriendRequestCreada){
            if(err){
                next(new AppError(err, 400))
            } else{
                res.status(201).json(datosFriendRequestCreada)
            }
        })
    } else{
        next(new AppError("No estás autorizado para realizar esta operación", 403))
    }
})


/* <=============================== ACCEPT FRIEND REQUEST ===============================> */
exports.acceptFriendRequest = wrapAsync(async function(req, res, next) {
    const {id} = req.params

    const userLogued = req.userLogued

    if(userLogued){
        await friendRequestsModel.findById(id, async function(err, datosFriendRequest){
            if(err){
                next(new AppError(err, 404))
            } else{
                if(datosFriendRequest.frequest_receiver == userLogued.user_id){
                    friendRequestsModel.update(id, 1, function(err, datosFriendRequestActualizada){
                        if(err){
                            next(new AppError(err, 400))
                        } else{
                            res.status(200).json(datosFriendRequestActualizada)
                        }
                    })
                }
            }
        })
    } else{
        next(new AppError("No estás autorizado para realizar esta operación", 403))
    }
})

/* <=============================== REJECT FRIEND REQUEST ===============================> */
exports.rejectFriendRequest = wrapAsync(async function(req, res, next) {
    const {id} = req.params

    const userLogued = req.userLogued

    if(userLogued){
        await friendRequestsModel.findById(id, async function(err, datosFriendRequest){
            if(err){
                next(new AppError(err, 404))
            } else{
                if(datosFriendRequest.frequest_receiver == userLogued.user_id){
                    friendRequestsModel.delete(id, function(err, datosFriendRequestEliminada){
                        if(err){
                            next(new AppError(err, 404))
                        } else{
                            res.status(200).json(datosFriendRequestEliminada)
                        }
                    })
                }
            }
        })
    } else{
        next(new AppError("No estás autorizado para realizar esta operación", 403))
    }
})

/* <=============================== DELETE FRIEND REQUEST ===============================> */
exports.deleteFrequestById = wrapAsync(async function(req, res, next) {
    const {id} = req.params

    const userLogued = req.userLogued

    if(userLogued && userLogued.user_role == 1){
        await friendRequestsModel.delete(id, function(err, datosFriendRequestEliminada){
            if(err){
                next(new AppError(err, 404))
            } else{
                res.status(200).json(datosFriendRequestEliminada)
            }
        })
    } else{
        next(new AppError("No estás autorizado para realizar esta operación", 403))
    }
})