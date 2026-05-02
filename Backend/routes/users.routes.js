const usersController = require("../controllers/users.controller")
const express = require("express")
const router = express.Router()
const jwtMW = require("../middlewares/jwt.mw")
const rutasProtegidasMW = require("../middlewares/rutasProtegidas.mw")
const multer = require("multer")

const storage = multer.memoryStorage()
const upload = multer({storage: storage})

// FIND ALL USERS
router.get("/", jwtMW.authenticate, rutasProtegidasMW.requireAdmin, usersController.findAllUsers)

// REGISTER
router.post("/", usersController.register)

// UPDATE USER BY ID
router.patch("/:id", jwtMW.authenticate, usersController.updateUser)

// DELETE USER BY ID
router.delete("/:id", jwtMW.authenticate, usersController.deleteUser)

// LOGIN
router.post("/login", usersController.login)

// LOGOUT
router.get("/logout", jwtMW.authenticate, usersController.logout)

// FIND BY ID
router.get("/:id", jwtMW.authenticate, rutasProtegidasMW.requireAdmin, usersController.findUserById)

module.exports = router