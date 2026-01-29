/* <=============================== DEPENDENCIAS ===============================> */
const userModel = require("../models/users.model")
const fs = require("fs").promises
const AppError = require("../utils/AppError")
const bcrypt = require("../utils/bcrypt")
const jwtMW = require("../middlewares/jwt.mw")

// Función Para Capturar Errores Asíncronos
function wrapAsync(fn) {
    return function (req, res, next) {
        fn(req, res, next).catch(e => {
            next(e)
        })
    }
}


/* <=============================== FIND ALL USERS ===============================> */
exports.findAllUsers = wrapAsync(async function (req,res,next) { 
    await userModel.findAll(async function(err, datosUser){
        if(err){
            next(new AppError(err,400))
        } else{                
            const userLogued = req.session.userLogued.data

            if(userLogued.user_role != 1){
                return next(new AppError("No tienes permiso para realizar esta petición", 403))
            }
                  
            res.status(200).json(datosUser)
        }
    })        
})

/* <=============================== FIND USER BY ID ===============================> */
exports.findUserById = wrapAsync(async function (req,res,next){
    const {id} = req.params
    // const userLogued = req.session.userLogued.data
    // console.log(id);
    await userModel.findById(id,function(err,datosUsuario){
        if(err){
            next(new AppError(err,404))
        } 

        if(!datosUsuario || datosUsuario.length == 0) {
            return next(new AppError("Usuario no encontrado", 404))
        }

        res.status(200).json(datosUsuario)
    })
})

/* <=============================== UPDATE USER ===============================> */
exports.updateUser = wrapAsync(async function (req,res, next) {    
    const {id} = req.params
    // const userLogued = req.session.userLogued.data
    let { username, name, currentPassword = "", newPassword = "", email, height, weight, objective} = req.body
    
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
            if(objective && objective > 0){
                userFounded.user_objective = objective
            }
            
            // ACTUALIZAMOS USUARIO
            await userModel.updateById(id, userFounded, function(err, datosUsuarioActualizado){
                if(err){
                    next(err, 500)
                } else{
                    res.status(200).json(datosUsuarioActualizado)
                }
            })
        }
    })
})

/* <=============================== REGISTER ===============================> */
exports.register = wrapAsync(async function (req, res, next) {
    // const userLogued = req.session.userLogued?.data;
    let { username, name, password, birthdate, email, height, weight, objective} = req.body

    // VALIDACIONES DE CONTRASEÑA
     if(password.length<8){
         next(new AppError("Aumenta la longitud de la contraseña en 8 caracteres como mínimo",400))
     }else if(!password.match(/[A-Z]/)){
        next(new AppError("La contraseña debe tener una mayúscula",400))
     }else if(!password.match(/[a-z]/)){
        next(new AppError("La contraseña debe tener una minúscula",400))
     }else if(!password.match(/[/\d/]/)){
        next(new AppError("La contraseña debe tener un número",400))
     }else if(!password.match(/^(?=.*[!@#$%^&*(),.?":{}|<>_=+-])/)){
        next(new AppError("La contraseña debe tener un carácter especial",400))
     }else{
        let newUser = {}

        newUser = {
            user_username: username,
            user_name: name,
            user_password: password,
            user_birthdate: birthdate,
            user_email: email,
            user_height: height,
            user_weight: weight,
            user_objective: objective
        }

        newUser.user_password = await bcrypt.hashPassword(newUser.user_password)

        if(!req.session.userLogued || (req.session.userLogued && req.session.userLogued.data.user_role == 1)){
            await userModel.create(newUser,function(err,datosUsuarioCreado){
                if(err){
                    return next(new AppError(err, 500))
                } else{
                    
                    if(req.session.userLogued && req.session.userLogued.data.user_role == 1){
                        res.status(200).json(datosUsuarioCreado)
                    } else if(!req.session.userLogued){
                        const jwtToken = jwtMW.createJWT(req, res, next, newUser)
                        const userLogued = {
                            data: newUser,
                            token: jwtToken
                        }

                        req.session.userLogued = userLogued
                        res.status(200).json(datosUsuarioCreado)
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
    const userLogued = req.session.userLogued.data

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
                        const jwtDestroyed = jwtMW.destroyJWT(req);
                        if (jwtDestroyed) {
                            req.session.destroy((err) => {
                                if (err) {
                                    return next(new AppError("Error al destruir la sesión", 500))
                                }
                                return res.status(200).json({ msg: "Usuario eliminado, sesión destruida" })
                            })
                        } else {
                            return next(new AppError("Error al eliminar el Token o Sesión Inexistente", 500))
                        }
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

// LOGIN
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
                req.session.userLogued = userLogued
                
                res.status(200).json(userLogued)
            } else{
                next(new AppError("Usuario y/o contraseña incorrectos", 401))
            }
        }
    })
})


// LOGOUT
exports.logout = wrapAsync(async(req,res,next) => {
    const jwtDestroyed = jwtMW.destroyJWT(req)
    
    console.log(jwtDestroyed)
    console.log(req.session)

    if(jwtDestroyed){
        req.session.destroy((err) => {
            if(err){
                return next(new AppError("Error al destruir la sesión", 500))
            }            
        })
    } else{
        next(new AppError("Error al Eliminar el Token o Sesión Inexistente", 500))
    }

    res.status(200).json({msg: "Token Eliminado y Sesión Destruida"})
})