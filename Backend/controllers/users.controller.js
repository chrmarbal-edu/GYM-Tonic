/* <=============================== DEPENDENCIAS ===============================> */
const userModel = require("../models/users.model")
const userMissionsModel = require("../models/userMissions.model")
const AppError = require("../utils/AppError")
const bcrypt = require("../utils/bcrypt")
const jwtMW = require("../middlewares/jwt.mw")
const { DateTime } = require("mssql")
const crypto = require("crypto")
const fs = require("fs")
const path = require("path")

// Función Para Capturar Errores Asíncronos
function wrapAsync(fn) {
    return function (req, res, next) {
        fn(req, res, next).catch(e => {
            next(e)
        })
    }
}

function isDefaultProfilePicture(picturePath) {
    if (!picturePath || typeof picturePath !== "string") {
        return true
    }
    return picturePath.replace(/\\/g, "/").includes("default/user.jpg")
}

function resolveProfilePictureFilePath(picturePath) {
    if (!picturePath || isDefaultProfilePicture(picturePath)) {
        return null
    }

    let normalized = picturePath.replace(/\\/g, "/")
    if (normalized.startsWith("public/")) {
        return normalized
    }
    if (!normalized.startsWith("images/")) {
        normalized = `images/${normalized}`
    }
    return path.join("public", normalized).replace(/\\/g, "/")
}

function deleteProfilePictureFile(picturePath) {
    const filePath = resolveProfilePictureFilePath(picturePath)
    if (!filePath) {
        return
    }

    try {
        if (fs.existsSync(filePath)) {
            fs.unlinkSync(filePath)
        }
    } catch (e) {
        console.error(`No se pudo eliminar la foto de perfil: ${filePath}`, e)
    }
}

// #region USERS

/* <=============================== FIND ALL USERS ===============================> */
exports.findAllUsers = wrapAsync(async function (req,res,next) {
    await userModel.findAll(async function(err, datosUser){
        if(err){
            next(new AppError(err,400))
        } else{
            const userLogued = req.userLogued

            // Usuarios normales reciben datos publicos (sin password ni info sensible)
            // para poder buscar amigos. Admin recibe todo.
            if(userLogued.user_role != 1){
                const sanitized = (datosUser || []).map(u => ({
                    user_id: u.user_id,
                    user_username: u.user_username,
                    user_name: u.user_name,
                    user_picture: u.user_picture
                }))
                return res.status(200).json(sanitized)
            }

            res.status(200).json(datosUser)
        }
    })
})

/* <=============================== FIND USER BY ID ===============================> */
exports.findUserById = wrapAsync(async function (req,res,next){
    const {id} = req.params
    const userLogued = req.userLogued

    if(!userLogued || (userLogued.user_role != 1 && userLogued.user_id != id)){
        return next(new AppError("No estás autorizado para realizar esta petición", 403))
    }

    await userModel.findById(id,function(err,datosUsuario){
        if(err){
            next(new AppError(err,404))
        }

        if(!datosUsuario || datosUsuario.length == 0) {
            return next(new AppError("Usuario no encontrado", 404))
        }

        if(userLogued.user_role != 1){
            delete datosUsuario.user_password
        }

        res.status(200).json(datosUsuario)
    })
})

/* <=============================== UPDATE USER ===============================> */
exports.updateUser = wrapAsync(async function (req,res, next) {    
    const {id} = req.params
    let { username, name, currentPassword = "", newPassword = "", email, height, weight, objective, picture} = req.body
    
    // BUSCAMOS USUARIO
    await userModel.findById(id, async function(err,userFounded){
        if(err){
            next(new AppError(err, 500))
        }else{       
            console.log(userFounded)

            // USERNAME
            if(username && username != ""){
                userFounded.user_username = username
            }

            // PASSWORD
            if(newPassword){
                if(newPassword.length < 8){
                    next(new AppError("Aumenta la longitud de la contraseña en 8 caracteres como mínimo",400))
                } else if(!newPassword.match(/[A-Z]/)){
                    next(new AppError("La contraseña debe tener al menos una mayúscula",400))
                } else if(!newPassword.match(/[a-z]/)){
                    next(new AppError("La contraseña debe tener al menos una minúscula",400))
                } else if(!newPassword.match(/[/\d/]/)){
                    next(new AppError("La contraseña debe tener al menos un número",400))
                } else if(!newPassword.match(/^(?=.*[!@#$%^&*(),.?":{}|<>_=+-])/)){
                    next(new AppError("La contraseña debe tener al menos un carácter especial",400))
                } else{
                    const validado = await bcrypt.compareLogin(currentPassword, userFounded.user_password)
                    if(validado){
                        userFounded.user_password = await bcrypt.hashPassword(newPassword)
                    } else{
                        next(new AppError("La contraseña actual es incorrecta", 400))
                    }
                }
            }

            // NAME 
            if(name && name != ""){
                userFounded.user_name = name
            }

            // EMAIL
            if(email && email != ""){
                userFounded.user_email = email
            }

            // WEIGHT
            if(weight && weight > 40 && weight < 200){
                userFounded.user_weight = weight
            }

            // HEIGHT
            if(height && height > 130 && height < 230){
                userFounded.user_height = height
            }

            // OBJECTIVE
            if(objective !== undefined && objective !== ""){
                userFounded.user_objective = objective
            }
            
            // PICTURE
            if (picture === "default") {
                deleteProfilePictureFile(userFounded.user_picture)
                userFounded.user_picture = "images/users/default/user.jpg"
            } else if (req.file) {
                // Obtenemos la extensión original del archivo (ej: .jpg, .png)
                const extension = path.extname(req.file.originalname)
                const fileName = `${userFounded.user_username}${extension}`
                const targetPath = path.join("public", "images", "users", fileName).replace(/\\/g, "/")
                const newPicturePath = `images/users/${fileName}`

                if (userFounded.user_picture !== newPicturePath) {
                    deleteProfilePictureFile(userFounded.user_picture)
                }

                // Renombramos el archivo físicamente al nombre del usuario en la carpeta destino
                fs.renameSync(req.file.path, targetPath)

                // Guardamos en DB la ruta empezando desde 'users' como solicitaste
                userFounded.user_picture = newPicturePath
            }

            // ACTUALIZAMOS USUARIO
            await userModel.updateById(id, userFounded, function(err, datosUsuarioActualizado){
                if(err){
                    next(new AppError(err, 500))
                } else{
                    // Si el usuario que está modificando datos es el mismo, actualizamos los datos de la sesión
                    if (req.userLogued && req.userLogued.user_id == id) {
                        Object.assign(req.userLogued, datosUsuarioActualizado)
                        res.status(200).json(req.userLogued)
                    } else{
                        res.status(200).json(datosUsuarioActualizado)
                    }
                }
            })
        }
    })
})

/* <=============================== REGISTER ===============================> */
exports.register = wrapAsync(async function (req, res, next) {
    let { username, name, password, birthdate, email, height, weight, objective, oauth, picture} = req.body

    // Si es un registro por OAuth, generamos una password aleatoria (UUID) y saltamos validaciones
    if (oauth) {
        password = crypto.randomUUID()
    }

    // VALIDACIONES DE CONTRASEÑA
    if(!oauth && (!password || password.length<8)){
        next(new AppError("Aumenta la longitud de la contraseña en 8 caracteres como mínimo",400))
    } else if(!oauth && !password.match(/[A-Z]/)){
        next(new AppError("La contraseña debe tener una mayúscula",400))
    } else if(!oauth && !password.match(/[a-z]/)){
        next(new AppError("La contraseña debe tener una minúscula",400))
    } else if(!oauth && !password.match(/[/\d/]/)){
        next(new AppError("La contraseña debe tener un número",400))
    } else if(!oauth && !password.match(/^(?=.*[!@#$%^&*(),.?":{}|<>_=+-])/)){
        next(new AppError("La contraseña debe tener un carácter especial",400))
    } else{
        // Gestión de la imagen de perfil
        let userPicture = "users/default/user.jpg"
        
        if (req.file) {
            // Obtenemos la extensión original del archivo (ej: .jpg, .png)
            const extension = path.extname(req.file.originalname)
            const fileName = `${username}${extension}`
            const targetPath = path.join("public", "images", "users", fileName).replace(/\\/g, "/")

            // Renombramos el archivo físicamente al nombre del usuario en la carpeta destino
            fs.renameSync(req.file.path, targetPath)

            // Guardamos en DB la ruta empezando desde 'users' como solicitaste
            userPicture = `images/users/${fileName}`
        } else if (picture) {
            userPicture = picture
        }

        let newUser = {
            user_username: username,
            user_name: name,
            user_password: password,
            user_birthdate: birthdate,
            user_email: email,
            user_height: height,
            user_weight: weight,
            user_objective: objective,
            user_picture: userPicture,
            user_oauth: oauth || null
        }

        newUser.user_password = await bcrypt.hashPassword(newUser.user_password)

        if(!req.userLogued || (req.userLogued && req.userLogued.user_role == 1)){
            await userModel.create(newUser,function(err,datosUsuarioCreado){
                if(err){
                    return next(new AppError(err, 500))
                } else{
                    if(req.userLogued && req.userLogued.user_role == 1){
                        res.status(201).json({user: datosUsuarioCreado, token: null})
                    } else if(!req.userLogued){
                        const jwtToken = jwtMW.createJWT(req, res, next, datosUsuarioCreado)

                        const userLogued = {
                            data: datosUsuarioCreado,
                            token: jwtToken
                        }

                        res.status(201).json(userLogued)
                    }
                }
            })
        } else{
            return next(new AppError("No tienes permisos para realizar esta petición", 403))
        }
    }
})

/* <=============================== DELETE USER ===============================> */
exports.deleteUser = wrapAsync(async function (req, res, next) {
    const { id } = req.params
    const userLogued = req.userLogued

    if (userLogued && (userLogued.user_role == 1 || userLogued.user_id == id)) {
        await userModel.findById(id, async function (err, userFounded) {
            if (err) {
                return next(new AppError("Usuario no encontrado", 404))
            }

            if (!userFounded || userFounded.length == 0) {
                return next(new AppError("Usuario no encontrado", 404))
            }

            await userModel.delete(id, function (err, datosUsuarioEliminado) {
                if (err) {
                    return next(new AppError("Error al eliminar el usuario", 500))
                } else{
                    // Si se elimina a sí mismo, cerrar sesión
                    if (userLogued.idUser == id) {
                        return res.status(200).json({ msg: "Usuario eliminado, sesión destruida" })
                    } else {
                        return res.status(200).json(datosUsuarioEliminado)
                    }
                }
                
            })
        })
    } else {
        return next(new AppError("No estás autorizado para eliminar este usuario", 403))
    }
})

/* <=============================== LOGIN ===============================> */
exports.login = wrapAsync(async(req, res, next) => {
    let {username, password} = req.body
    
    await userModel.findByUsername(username, async function(err, userFound) {
        if(err){
            next(new AppError(err, 404))
        } else{
            let userFounded = userFound[0]

            const validado = await bcrypt.compareLogin(password, userFounded.user_password)
            if(validado){
                const jwtToken = jwtMW.createJWT(req, res, next, userFounded)
                const userLogued = {
                    data: userFounded,
                    token: jwtToken
                }

                res.status(200).json(userLogued)
            } else{
                next(new AppError("Usuario y/o contraseña incorrectos", 401))
            }
        }
    })
})

/* <=============================== LOGOUT ===============================> */
exports.logout = wrapAsync(async(req,res,next) => {
    res.status(200).json({msg: "Token Eliminado y Sesión Destruida"})
})

// #endregion

// #region USER X MISSION

/* <=============================== FIND ALL USER MISSIONS ===============================> */
exports.findAllUserMissions = wrapAsync(async function (req,res,next){
    const userLogued = req.userLogued

    if(userLogued){
        await userMissionsModel.findAll(function(err,datosUserMissions){
            if(err){
                next(new AppError(err,400))
            } else{
                res.status(200).json(datosUserMissions)
            }
        })
    } else {
        return next(new AppError("No tienes permisos para realizar esta petición", 403))
    }
})

/* <=============================== FIND USER MISSION BY ID ===============================> */
exports.findUserMissionById = wrapAsync(async function (req,res,next){
    const {id} = req.params
    const userLogued = req.userLogued

    if(userLogued && userLogued.user_role == 1){
        await userMissionsModel.findById(id, function(err,datosUserMission){
            if(err){
                next(new AppError(err,404))
            } else{
                if(!datosUserMission || datosUserMission.length == 0) {
                    return next(new AppError("Usuario no encontrado", 404))
                }

                res.status(200).json(datosUserMission)
            }
        })
    } else {
        return next(new AppError("No estás autorizado para realizar esta petición", 403))
    }
})

/* <=============================== FIND USER MISSION BY USER ID ===============================> */
exports.findUserMissionByUserId = wrapAsync(async function (req,res,next){
    const {userId} = req.params
    const userLogued = req.userLogued

    if(userLogued && (userLogued.user_id == userId || userLogued.user_role == 1)){
        await userMissionsModel.findByUserId(userId, async function(err,datosUserMission){
            if(err){
                return next(new AppError(err,404))
            } 

            if(!datosUserMission || datosUserMission.length == 0) {
                return res.status(200).json({ missions: [], notifications: [] })
            }

            const hoy = new Date();
            const misionesActivas = [];
            const notifications = [];

            // Procesamos cada misión para comprobar caducidad
            for (const mision of datosUserMission) {
                const fechaExpiracion = new Date(mision.user_x_mission_expiration);

                if (fechaExpiracion < hoy && !mision.user_x_mission_completed) {
                    // Si ha caducado y no está completada: Notificar y Borrar
                    notifications.push({
                        message: `La misión "${mision.mission_name}" ha caducado.`,
                        mission_name: mision.mission_name,
                        expired: true
                    });

                    await new Promise((resolve) => {
                        userMissionsModel.delete(mision.user_x_mission_id, resolve);
                    });
                } else {
                    misionesActivas.push(mision);
                }
            }

            res.status(200).json({
                missions: misionesActivas,
                notifications: notifications
            });
        })
    } else{
        return next(new AppError("No estás autorizado para realizar esta petición", 403))
    }
})

/* <=============================== FIND USER MISSION BY MISSION ID ===============================> */
exports.findUserMissionByMissionId = wrapAsync(async function (req,res,next){
    const {missionId} = req.params
    const userLogued = req.userLogued

    if(userLogued){
        await userMissionsModel.findByMissionId(missionId, function(err,datosUserMission){
            if(err){
                next(new AppError(err,404))
            } else{
                if(!datosUserMission || datosUserMission.length == 0) {
                    return next(new AppError("Misión no encontrada", 404))
                }

                res.status(200).json(datosUserMission)
            }
        })
    } else{
        return next(new AppError("No estás autorizado para realizar esta petición", 403))
    }
})

/* <=============================== CREATE USER MISSION ===============================> */
exports.createUserMission = wrapAsync(async function (req,res,next){
    const {userId, missionId, expiration, progress} = req.body
    const userLogued = req.userLogued

    if(userLogued && (userLogued.user_role == 1 || userLogued.user_id == userId)){
        let newUserMission = {}

        newUserMission.userId = userId
        newUserMission.missionId = missionId
        newUserMission.expiration = expiration
        newUserMission.completed = false
        newUserMission.progress = progress || 0

        await userMissionsModel.create(newUserMission, function(err,datosUserMissionCreado){
            if(err){
                next(new AppError(err, 500))
            } else{
                res.status(201).json(datosUserMissionCreado)
            }
        })
    } else{
        return next(new AppError("No estás autorizado para realizar esta petición", 403))
    }
})

/* <=============================== UPDATE USER MISSION ===============================> */
exports.updateUserMission = wrapAsync(async function (req,res,next){
    const {id} = req.params
    const {userId, missionId, expiration, completed, progress} = req.body
    const userLogued = req.userLogued
    
    if(userLogued && userLogued.user_role == 1){
        await userMissionsModel.findById(id, async function(err, missionFound) {
            if(err){
                return next(new AppError(err, 404))
            } else{
                if(!missionFound || missionFound.length == 0){
                    return next(new AppError("Misión no encontrada", 404))
                }
            } 

            let updateData = missionFound
            
            if(userId){
                updateData.user_x_mission_userid = userId
            }
            
            if(missionId){
                updateData.user_x_mission_missionid = missionId
            }

            if(expiration){
                updateData.user_x_mission_expiration = expiration
            }

            if(completed){
                updateData.user_x_mission_completed = completed
            }

            if(progress !== undefined){
                updateData.user_x_mission_progress = progress
            }

            await userMissionsModel.updateById(id, updateData, function(err, datosUserMissionActualizada) {
                if(err){
                    return next(new AppError(err, 500))
                } else{
                    res.status(200).json(datosUserMissionActualizada)
                }
            })
        })
    } else{
        return next(new AppError("No estás autorizado para realizar esta petición", 403))
    }
})

/* <=============================== COMPLETE MISSION BY ID ===============================> */
exports.completeMissionById = wrapAsync(async function (req,res,next){
    const {id} = req.params
    const userLogued = req.userLogued
    
    const missionFound = await userMissionsModel.findById(id)

    if(!missionFound || missionFound.length == 0){
        return next(new AppError("Misión no encontrada", 404))
    } else{
        if(userLogued && userLogued.user_id == missionFound.user_x_mission_userid){
            const today = new Date()
            const expirationDate = new Date(missionFound.user_x_mission_expiration)

            if(expirationDate < today){
                return next(new AppError("Misión Expirada", 400))
            } else{
                await userMissionsModel.completeMission(id, function(err, datosUserMissionActualizada) {
                    if(err){
                        return next(new AppError(err, 500))
                    } else{
                        res.status(200).json(datosUserMissionActualizada)
                    }
                })
            }
        } else{
            return next(new AppError("No estás autorizado para realizar esta petición", 403))
        }
    }

})

/* <=============================== DELETE USER MISSION ===============================> */
exports.deleteUserMission = wrapAsync(async function (req,res,next){
    const {id} = req.params
    const userLogued = req.userLogued

    if(userLogued && userLogued.user_role == 1){
        await userMissionsModel.delete(id, function(err, datosUserMissionEliminada){
            if(err){
                next(new AppError(err, 500))
            } else{
                res.status(200).json(datosUserMissionEliminada)
            }
        })
    } else{
        return next(new AppError("No estás autorizado para realizar esta petición", 403))
    }
})
// #endregion