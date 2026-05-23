/* <=============================== DEPENDENCIAS ===============================> */
const userModel = require("../models/users.model")
const userMissionsModel = require("../models/userMissions.model")
const AppError = require("../utils/AppError")
const bcrypt = require("../utils/bcrypt")
const jwtMW = require("../middlewares/jwt.mw")
const { DateTime } = require("mssql")
const crypto = require("crypto")
const nodemailer = require("nodemailer")
const fs = require("fs")
const path = require("path")
const jwt = require("jsonwebtoken")
const sql = require("mssql")
const dbConn = require("../utils/mssql.config")

// Función Para Capturar Errores Asíncronos
function wrapAsync(fn) {
    return function (req, res, next) {
        fn(req, res, next).catch(e => {
            next(e)
        })
    }
}

// Configuración Nodemailer
const transporter = nodemailer.createTransport({
    service: "gmail",
    auth: {
        user: process.env.EMAIL_USER,
        pass: process.env.GOOGLE_APP_PASSWORD
    }
})

// Función Para Enviar Correos Electrónicos
async function sendEmail(to, subject, htmlBody) {
    await transporter.sendMail({
        from: process.env.EMAIL_USER,
        to: to,
        subject: subject,
        html: htmlBody,
        attachments: [
            {
                filename: 'gymtonic_logo.png',
                path: path.join(__dirname, '../public/images/gymtonic_logo.png'), 
                cid: 'logo_gymtonic'
            }
        ]
    });
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

async function assignMissionsByObjective(userId, objective) {
    try {
        const pool = await sql.connect(dbConn)
        const missionsResponse = await pool.request()
            .input("objective", sql.Int, objective)
            .query("SELECT * FROM Missions WHERE mission_objective = @objective")
        
        const missions = missionsResponse.recordset
        const now = new Date()

        for (const mission of missions) {
            let expiration = new Date()
            if (mission.mission_type === 0) { // Daily
                expiration.setDate(now.getDate() + 1)
            } else if (mission.mission_type === 1) { // Weekly
                expiration.setDate(now.getDate() + 7)
            } else if (mission.mission_type === 2) { // Monthly
                expiration.setDate(now.getDate() + 30)
            }

            const checkResponse = await pool.request()
                .input("userId", sql.Int, userId)
                .input("missionId", sql.Int, mission.mission_id)
                .query(`
                    SELECT 1 FROM User_X_Mission 
                    WHERE user_x_mission_userid = @userId AND user_x_mission_missionid = @missionId
                `)
            
            if (checkResponse.recordset.length === 0) {
                await pool.request()
                    .input("userId", sql.Int, userId)
                    .input("missionId", sql.Int, mission.mission_id)
                    .input("expiration", sql.DateTime, expiration)
                    .query(`
                        INSERT INTO User_X_Mission (
                            user_x_mission_userid, user_x_mission_missionid, user_x_mission_expiration, user_x_mission_completed, user_x_mission_progress, user_x_mission_points_deducted
                        ) VALUES (
                            @userId, @missionId, @expiration, 0, 0, 0
                        )
                    `)
            }
        }
    } catch (err) {
        console.error("Error inside assignMissionsByObjective:", err)
        throw err;
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

    if(!userLogued){
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
            const oldObjective = userFounded.user_objective
            let objectiveChanged = false
            if(objective !== undefined && objective !== ""){
                if(userFounded.user_objective != objective) {
                    userFounded.user_objective = objective
                    objectiveChanged = true
                }
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
            await userModel.updateById(id, userFounded, async function(err, datosUsuarioActualizado){
                if(err){
                    next(new AppError(err, 500))
                } else{
                    if (objectiveChanged) {
                        try {
                            const pool = await sql.connect(dbConn)
                            await pool.request()
                                .input("userId", sql.Int, id)
                                .query("DELETE FROM User_X_Mission WHERE user_x_mission_userid = @userId AND user_x_mission_completed = 0")
                            await assignMissionsByObjective(id, userFounded.user_objective)
                        } catch (assignErr) {
                            console.error("Error updating user missions on objective change:", assignErr)
                        }
                    }
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
    } else {
        // VALIDACIONES DE CONTRASEÑA (solo para registros no-OAuth)
        if(!password || password.length < 8){
            return next(new AppError("Aumenta la longitud de la contraseña en 8 caracteres como mínimo",400))
        } else if(!password.match(/[A-Z]/)){
            return next(new AppError("La contraseña debe tener al menos una mayúscula",400))
        } else if(!password.match(/[a-z]/)){
            return next(new AppError("La contraseña debe tener al menos una minúscula",400))
        } else if(!password.match(/[0-9]/)){ // Corregido el regex para números
            return next(new AppError("La contraseña debe tener al menos un número",400))
        } else if(!password.match(/^(?=.*[!@#$%^&*(),.?":{}|<>_=+-])/)){
            return next(new AppError("La contraseña debe tener al menos un carácter especial",400))
        }
    }
    
    let confirmationCode = null;
    let codeExpiration = null;

    // Solo generamos código y enviamos email si NO es un registro por OAuth
    if (!oauth) {
        confirmationCode = Math.floor(100000 + Math.random() * 900000).toString()
        codeExpiration = new Date(Date.now() + 10 * 60 * 1000);
        try {
            // Ruta corregida para el template HTML
            const templatePath = path.join(__dirname, '../public/html/confirmation_email.html');
            let emailHtml = await fs.promises.readFile(templatePath, 'utf8');
            emailHtml = emailHtml.replace('{{confirmationCode}}', confirmationCode);
            await sendEmail(email, '¡Confirma tu cuenta en GymTonic!', emailHtml);
        } catch (emailError) {
            console.error("Error al enviar el correo de confirmación:", emailError);
            return next(new AppError("Error al enviar el correo de confirmación. Por favor, inténtalo de nuevo.", 500));
        }
    }

    let userPicture = "images/users/default/user.jpg"
    
    if (req.file) {
        const extension = path.extname(req.file.originalname)
        const fileName = `${username}${extension}`
        const targetPath = path.join("public", "images", "users", fileName).replace(/\\/g, "/")

        fs.renameSync(req.file.path, targetPath)

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
        await userModel.create(newUser, async function(err,datosUsuarioCreado){
            if(err){
                return next(new AppError(err, 500))
            } else{
                // Autoassign missions
                try {
                    await assignMissionsByObjective(datosUsuarioCreado.user_id, datosUsuarioCreado.user_objective)
                } catch (assignErr) {
                    console.error("Error auto-assigning missions during registration:", assignErr)
                }
                if(req.userLogued && req.userLogued.user_role == 1){
                    const response = { user: datosUsuarioCreado, token: null }
                    if (!oauth) {
                        response.confirmationCode = confirmationCode;
                        response.codeExpiration = codeExpiration;
                    }
                    res.status(201).json(response)
                } else if(!req.userLogued){
                    const jwtToken = jwtMW.createJWT(req, res, next, datosUsuarioCreado)
                    
                    const userLogued = {
                        data: datosUsuarioCreado,
                        token: jwtToken
                    }

                    if (!oauth) {
                        userLogued.confirmationCode = confirmationCode;
                        userLogued.codeExpiration = codeExpiration;
                    }

                    res.status(201).json(userLogued)
                }
            }
        })
    } else{
        return next(new AppError("No tienes permisos para realizar esta petición", 403))
    }
})

/* <=============================== RECOVER ACCOUNT ===============================> */
exports.recoverAccount = wrapAsync(async function (req, res, next) {
    const { email, newPassword } = req.body

    if (!email || !newPassword) {
        return next(new AppError("El email y la nueva contraseña son obligatorios", 400))
    }

    await userModel.findByEmail(email, async function(err, userFound) {
        if (err) {
            return next(new AppError(err, 404))
        }

        const isSamePassword = await bcrypt.compareLogin(newPassword, userFound.user_password)
        if (isSamePassword) {
            return next(new AppError("La nueva contraseña no puede ser igual a la contraseña actual", 400))
        }

        if (newPassword.length < 8 || !newPassword.match(/[A-Z]/) || !newPassword.match(/[a-z]/) || !newPassword.match(/[0-9]/) || !newPassword.match(/^(?=.*[!@#$%^&*(),.?":{}|<>_=+-])/)) {
            return next(new AppError("La nueva contraseña no cumple con los requisitos de seguridad", 400))
        }

        const code = Math.floor(100000 + Math.random() * 900000).toString()

        // Generamos un token de recuperación con el código y la nueva password (expira en 10 min)
        const recoveryToken = jwt.sign(
            { userId: userFound.user_id, code, newPassword },
            process.env.SECRET_JWT,
            { expiresIn: '10m' }
        )

        try {
            const templatePath = path.join(__dirname, '../public/html/recover_account.html')
            let emailHtml = await fs.promises.readFile(templatePath, 'utf8')
            
            emailHtml = emailHtml.replace('{{confirmationCode}}', code)
            
            await sendEmail(email, 'Recuperación de cuenta - GymTonic', emailHtml)

            res.status(200).json({ 
                msg: "Código de recuperación enviado al email",
                recoveryToken,
                expiresAt: new Date(Date.now() + 10 * 60 * 1000)
            })
        } catch (emailError) {
            console.error("Error al enviar email de recuperación:", emailError)
            return next(new AppError("Error al enviar el correo de recuperación", 500))
        }
    })
})

/* <=============================== CHANGE PASSWORD ===============================> */
exports.changePassword = wrapAsync(async function (req, res, next) {
    const { code, recoveryToken } = req.body

    if (!code || !recoveryToken) {
        return next(new AppError("Faltan el código o el token para procesar el cambio", 400))
    }

    try {
        // Verificamos el token de recuperación
        const decoded = jwt.verify(recoveryToken, process.env.SECRET_JWT)

        // Validamos que el código enviado por el usuario sea el que guardamos en el token
        if (decoded.code !== code) {
            return next(new AppError("El código introducido es incorrecto", 400))
        }

        // Buscamos al usuario por el ID que viene en el token
        await userModel.findById(decoded.userId, async function(err, userFound) {
            if (err || !userFound) return next(new AppError("Usuario no encontrado", 404))

            // Encriptamos la nueva password que estaba "en espera" dentro del token
            const hashedPassword = await bcrypt.hashPassword(decoded.newPassword)
            const updatedUser = { ...userFound, user_password: hashedPassword }

            await userModel.updateById(userFound.user_id, updatedUser, function (err, result) {
                if (err) return next(new AppError("Error al actualizar la contraseña", 500))
                res.status(200).json({ msg: "Contraseña actualizada correctamente. Ya puedes iniciar sesión." })
            })
        })
    } catch (err) {
        return next(new AppError("El proceso de recuperación ha expirado o el token es inválido", 401))
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
        await userMissionsModel.findByUserId(userId, function(err, data){
            if(err){
                return next(new AppError(err,404))
            } 

            if(!data) {
                return res.status(200).json({ missions: [], expiredMissions: [], notifications: [] })
            }

            // El modelo ya devuelve el objeto formateado con: missions, expiredMissions y notifications
            res.status(200).json(data);
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
    
    if(!userLogued){
        return next(new AppError("No autorizado", 403))
    }

    const missionFound = await new Promise((resolve, reject) => {
        userMissionsModel.findById(id, (err, data) => {
            if(err) reject(err)
            else resolve(data)
        })
    }).catch(err => null)

    if(!missionFound){
        return next(new AppError("Misión no encontrada", 404))
    }

    // Validar ownership
    if(userLogued.user_id !== missionFound.user_x_mission_userid){
        return next(new AppError("No estás autorizado para completar esta misión", 403))
    }

    // Validar si ya completada
    if(missionFound.user_x_mission_completed){
        return next(new AppError("La misión ya ha sido completada", 400))
    }

    // Validar expiración
    const today = new Date()
    const expirationDate = new Date(missionFound.user_x_mission_expiration)
    
    if(expirationDate < today){
        return next(new AppError("La misión ha expirado", 400))
    }

    const pool = await sql.connect(dbConn)
    const mDetails = await pool.request()
        .input("missionId", sql.Int, missionFound.user_x_mission_missionid)
        .query("SELECT * FROM Missions WHERE mission_id = @missionId")
    
    if (mDetails.recordset.length === 0) {
        return next(new AppError("Misión no encontrada en el catálogo", 404))
    }
    const missionPoints = mDetails.recordset[0].mission_points || 0

    // Marcar como completada
    await userMissionsModel.completeMission(id, async function(err, datosUserMissionActualizada) {
        if(err){
            return next(new AppError(err, 500))
        } else{
            try {
                const userRes = await pool.request()
                    .input("userId", sql.Int, missionFound.user_x_mission_userid)
                    .query("SELECT user_points FROM Users WHERE user_id = @userId")
                
                if (userRes.recordset.length > 0) {
                    const currentPoints = userRes.recordset[0].user_points || 0
                    const newPoints = currentPoints + missionPoints
                    
                    await pool.request()
                        .input("userId", sql.Int, missionFound.user_x_mission_userid)
                        .input("points", sql.Int, newPoints)
                        .query("UPDATE Users SET user_points = @points WHERE user_id = @userId")
                }
            } catch (pointsErr) {
                console.error("Error adding points to user on mission completion:", pointsErr)
            }

            res.status(200).json(datosUserMissionActualizada)
        }
    })
})

/* <=============================== UPDATE MISSION PROGRESS ===============================> */
exports.updateMissionProgress = wrapAsync(async function (req,res,next){
    const {id} = req.params
    const {progress} = req.body
    const userLogued = req.userLogued
    
    if(!userLogued){
        return next(new AppError("No autorizado", 403))
    }

    if(progress === undefined || progress === null || typeof progress !== 'number'){
        return next(new AppError("Progress debe ser un número", 400))
    }

    if(progress < 0){
        return next(new AppError("Progress no puede ser negativo", 400))
    }

    const missionFound = await new Promise((resolve, reject) => {
        userMissionsModel.findById(id, (err, data) => {
            if(err) reject(err)
            else resolve(data)
        })
    }).catch(err => null)

    if(!missionFound){
        return next(new AppError("Misión no encontrada", 404))
    }

    // Validar ownership
    if(userLogued.user_id !== missionFound.user_x_mission_userid){
        return next(new AppError("No estás autorizado para actualizar esta misión", 403))
    }

    // Validar expiración
    const today = new Date()
    const expirationDate = new Date(missionFound.user_x_mission_expiration)
    
    if(expirationDate < today){
        return next(new AppError("La misión ha expirado", 400))
    }

    // Validar si ya completada
    if(missionFound.user_x_mission_completed){
        return next(new AppError("La misión ya ha sido completada", 400))
    }

    // Actualizar progreso (y auto-completar si llega al target)
    const updatedData = {
        ...missionFound,
        user_x_mission_progress: progress
    }

    await userMissionsModel.updateById(id, updatedData, function(err, datosActualizado) {
        if(err){
            return next(new AppError(err, 500))
        } else{
            res.status(200).json(datosActualizado)
        }
    })
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