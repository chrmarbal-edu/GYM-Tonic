const exercisesController = require("../controllers/exercises.controller")
const express = require("express")
const router = express.Router()
const jwtMW = require("../middlewares/jwt.mw")
const rutasProtegidasMW = require("../middlewares/rutasProtegidas.mw")

// FIND ALL EXERCISES
router.get("/", jwtMW.authenticate, exercisesController.findAllExercises)

// CREATE EXERCISE
router.post("/", jwtMW.authenticate, rutasProtegidasMW.requireAdmin, exercisesController.createExercise)

// UPDATE EXERCISE BY ID
router.patch("/:id", jwtMW.authenticate, rutasProtegidasMW.requireAdmin, exercisesController.updateExerciseById)

// DELETE EXERCISE BY ID
router.delete("/:id", jwtMW.authenticate, rutasProtegidasMW.requireAdmin, exercisesController.deleteExerciseById)

// FIND EXERCISE BY ID
router.get("/:id", jwtMW.authenticate, exercisesController.findExerciseById)

module.exports = router