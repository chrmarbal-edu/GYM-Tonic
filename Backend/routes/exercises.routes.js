const exercisesController = require("../controllers/exercises.controller")
const express = require("express")
const router = express.Router()
const jwtMW = require("../middlewares/jwt.mw")
const rutasProtegidasMW = require("../middlewares/rutasProtegidas.mw")
const multer = require("multer")
const path = require("path")
const fs = require("fs")

const ensureDir = (dir) => {
    if (!fs.existsSync(dir)) {
        fs.mkdirSync(dir, { recursive: true })
    }
}

const storage = multer.diskStorage({
    destination: (req, file, cb) => {
        if (file.fieldname === "video") {
            const dir = path.join("public", "videos", "exercises")
            ensureDir(dir)
            return cb(null, dir)
        }
        const dir = path.join("public", "images", "exercises")
        ensureDir(dir)
        cb(null, dir)
    },
    filename: (req, file, cb) => {
        const base = (req.body.name || "exercise")
            .toLowerCase()
            .replace(/[^a-z0-9]+/g, "_")
            .replace(/^_|_$/g, "") || "exercise"
        cb(null, `${base}${path.extname(file.originalname)}`)
    }
})

const upload = multer({ storage })

const exerciseUpload = upload.fields([
    { name: "video", maxCount: 1 },
    { name: "image", maxCount: 1 }
])

// FIND ALL EXERCISES
router.get("/", jwtMW.authenticate, exercisesController.findAllExercises)

// FIND EXERCISES BY TYPE
router.get("/type/:type", jwtMW.authenticate, exercisesController.findExercisesByType)

// CREATE EXERCISE
router.post(
    "/",
    jwtMW.authenticate,
    rutasProtegidasMW.requireAdmin,
    exerciseUpload,
    exercisesController.createExercise
)

// UPDATE EXERCISE BY ID
router.patch(
    "/:id",
    jwtMW.authenticate,
    rutasProtegidasMW.requireAdmin,
    exerciseUpload,
    exercisesController.updateExerciseById
)

// DELETE EXERCISE BY ID
router.delete("/:id", jwtMW.authenticate, rutasProtegidasMW.requireAdmin, exercisesController.deleteExerciseById)

// FIND EXERCISE BY ID
router.get("/:id", jwtMW.authenticate, exercisesController.findExerciseById)

module.exports = router
