const routinesController = require("../controllers/routines.controller")
const express = require("express")
const router = express.Router()
const jwtMW = require("../middlewares/jwt.mw")
const rutasProtegidasMW = require("../middlewares/rutasProtegidas.mw")

// FIND ALL ROUTINES - CSR
router.get("/routines", jwtMW.authenticate, routinesController.findAllRoutinesCSR)

// FIND ROUTINE CATEGORIES - CSR
router.get("/routines/categories", jwtMW.authenticate, routinesController.findRoutineCategoriesCSR)

// FIND ROUTINE BY NAME/SLUG - CSR
router.get("/routine/by-name/:name", jwtMW.authenticate, routinesController.findRoutineByNameCSR)

// FIND ROUTINE WITH EXERCISES BY ID - CSR
router.get("/routine/:id/with-exercises", jwtMW.authenticate, routinesController.findRoutineWithExercisesByIdCSR)

// FIND ROUTINE BY ID - CSR
router.get("/routine/:id", jwtMW.authenticate, routinesController.findRoutineByIdCSR)

// UPDATE ROUTINE BY ID - CSR
router.patch("/routine/:id", jwtMW.authenticate, rutasProtegidasMW.requireAdmin, routinesController.updateRoutineCSR)

// DELETE ROUTINE BY ID - CSR
router.delete("/routine/:id", jwtMW.authenticate, rutasProtegidasMW.requireAdmin, routinesController.deleteRoutineCSR)

// CREATE ROUTINE - CSR
router.post("/routine/new", jwtMW.authenticate, rutasProtegidasMW.requireAdmin, routinesController.createRoutineCSR)

module.exports = router
