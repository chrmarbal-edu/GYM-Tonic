/* <=============================== DEPENDENCIAS ===============================> */
const friendRequestsModel = require("../models/friendRequests.model")
const friendsModel = require("../models/friends.model")
const AppError = require("../utils/AppError")
const { handleSqlError } = require("../utils/sqlErrors")

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
                next(new AppError(handleSqlError(err), 400))
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
                next(new AppError(handleSqlError(err), 404))
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
                next(new AppError(handleSqlError(err), 400))
            } else{
                res.status(201).json(datosFriendRequestCreada)
            }
        })
    } else{
        next(new AppError("No estás autorizado para realizar esta operación", 403))
    }
})


/* <=============================== ACCEPT FRIEND REQUEST ===============================> */
// Al aceptar: creamos la fila en Friends y eliminamos la solicitud, en una sola acción.
// El cliente recibe la nueva amistad con los datos del usuario amigo (para refrescar la UI).
exports.acceptFriendRequest = wrapAsync(async function(req, res, next) {
    const {id} = req.params
    const userLogued = req.userLogued

    if(!userLogued){
        return next(new AppError("No estás autorizado para realizar esta operación", 403))
    }

    friendRequestsModel.findById(id, async function(err, datosFriendRequest){
        if(err){
            return next(new AppError(handleSqlError(err), 404))
        }

        if(!datosFriendRequest || datosFriendRequest.frequest_receiver != userLogued.user_id){
            return next(new AppError("No puedes aceptar esta solicitud", 403))
        }

        const newFriendship = {
            friend_userid1: datosFriendRequest.frequest_sender,
            friend_userid2: datosFriendRequest.frequest_receiver
        }

        // 1) Crear amistad
        friendsModel.create(newFriendship, function(errCreate, datosAmistadCreada){
            if(errCreate){
                return next(new AppError(handleSqlError(errCreate), 400))
            }

            // 2) Eliminar la solicitud aceptada (ya no la necesitamos)
            friendRequestsModel.delete(id, function(errDelete){
                if(errDelete){
                    // Si falla el delete, ya tenemos la amistad creada; devolvemos
                    // un warning pero respuesta 200 para que el cliente refresque.
                    return res.status(200).json({
                        friendship: datosAmistadCreada,
                        warning: "Amistad creada, pero no se pudo eliminar la solicitud original"
                    })
                }
                res.status(200).json({ friendship: datosAmistadCreada })
            })
        })
    })
})

/* <=============================== FIND FRIEND REQUESTS BY USER ID ===============================> */
exports.findFriendRequestsByUserId = wrapAsync(async function(req, res, next) {
    const { userId } = req.params
    const userLogued = req.userLogued

    if(!userLogued || (userLogued.user_role != 1 && userLogued.user_id != userId)){
        return next(new AppError("No estás autorizado para realizar esta operación", 403))
    }

    friendRequestsModel.findByUserId(userId, function(err, rows){
        if(err){
            return next(new AppError(handleSqlError(err), 500))
        }

        const incoming = []
        const outgoing = []
        for(const r of (rows || [])){
            if(r.frequest_receiver == userId){
                incoming.push(r)
            } else {
                outgoing.push(r)
            }
        }

        res.status(200).json({ incoming, outgoing })
    })
})

/* <=============================== REJECT FRIEND REQUEST ===============================> */
exports.rejectFriendRequest = wrapAsync(async function(req, res, next) {
    const {id} = req.params

    const userLogued = req.userLogued

    if(userLogued){
        await friendRequestsModel.findById(id, async function(err, datosFriendRequest){
            if(err){
                next(new AppError(handleSqlError(err), 404))
            } else{
                if(datosFriendRequest.frequest_receiver == userLogued.user_id){
                    friendRequestsModel.delete(id, function(err, datosFriendRequestEliminada){
                        if(err){
                            next(new AppError(handleSqlError(err), 404))
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
// Permite a admin borrar cualquier solicitud, y al sender cancelar la suya pendiente.
exports.deleteFrequestById = wrapAsync(async function(req, res, next) {
    const {id} = req.params
    const userLogued = req.userLogued

    if(!userLogued){
        return next(new AppError("No estás autorizado para realizar esta operación", 403))
    }

    friendRequestsModel.findById(id, function(err, fr){
        if(err){
            return next(new AppError(handleSqlError(err), 404))
        }

        const isAdmin = userLogued.user_role == 1
        const isSender = fr && fr.frequest_sender == userLogued.user_id

        if(!isAdmin && !isSender){
            return next(new AppError("No estás autorizado para realizar esta operación", 403))
        }

        friendRequestsModel.delete(id, function(errDel, datosFriendRequestEliminada){
            if(errDel){
                return next(new AppError(handleSqlError(errDel), 404))
            }
            res.status(200).json(datosFriendRequestEliminada)
        })
    })
})