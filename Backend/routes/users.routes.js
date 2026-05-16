const usersController = require("../controllers/users.controller")
const express = require("express")
const router = express.Router()
const jwtMW = require("../middlewares/jwt.mw")
const rutasProtegidasMW = require("../middlewares/rutasProtegidas.mw")
const multer = require("multer")
const path = require("path")

// Configuración de Multer para almacenamiento local en disco
const storage = multer.diskStorage({
    destination: (req, file, cb) => {
        cb(null, 'public/images/users') // Ruta donde se guardarán las fotos
    },
    filename: (req, file, cb) => {
        const ext = path.extname(file.originalname)
        cb(null, req.body.username + ext)
    }
})
const upload = multer({ storage: storage })

// #region USERS

// REGISTER
router.post("/", upload.single('image'), usersController.register)

// LOGIN
router.post("/login", usersController.login)

// LOGOUT
router.get("/logout", jwtMW.authenticate, usersController.logout)

// FIND ALL USERS
router.get("/", jwtMW.authenticate, rutasProtegidasMW.requireAdmin, usersController.findAllUsers)

// #endregion

// #region USER MISSIONS

// FIND ALL
router.get("/missions", jwtMW.authenticate, usersController.findAllUserMissions)

// FIND BY USER ID
router.get("/missions/user/:userId", jwtMW.authenticate, usersController.findUserMissionByUserId)

// FIND BY MISSION ID
router.get("/missions/mission/:missionId", jwtMW.authenticate, usersController.findUserMissionByMissionId)

// CREATE USER MISSION
router.post("/missions", jwtMW.authenticate, usersController.createUserMission)

// UPDATE USER MISSION BY ID
router.patch("/missions/:id", jwtMW.authenticate, usersController.updateUserMission)

// DELETE USER MISSION BY ID
router.delete("/missions/:id", jwtMW.authenticate, usersController.deleteUserMission)

// FIND BY ID
router.get("/missions/:id", jwtMW.authenticate, rutasProtegidasMW.requireAdmin, usersController.findUserMissionById)

// #endregion

// #region USER DYNAMIC ROUTES

// UPDATE USER BY ID
router.patch("/:id", jwtMW.authenticate, upload.single('image'), usersController.updateUser)

// DELETE USER BY ID
router.delete("/:id", jwtMW.authenticate, usersController.deleteUser)

// FIND BY ID
router.get("/:id", jwtMW.authenticate, usersController.findUserById)

// #endregion

module.exports = router