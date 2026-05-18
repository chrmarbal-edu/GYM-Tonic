const routinesController = require("../controllers/routines.controller")
const express = require("express")
const router = express.Router()
const jwtMW = require("../middlewares/jwt.mw")
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
        const dir = path.join("public", "images", "routines")
        ensureDir(dir)
        cb(null, dir)
    },
    filename: (req, file, cb) => {
        const base = (req.body.name || "routine")
            .toLowerCase()
            .replace(/[^a-z0-9]+/g, "_")
            .replace(/^_|_$/g, "") || "routine"
        cb(null, `${base}${path.extname(file.originalname)}`)
    }
})

const upload = multer({ storage })
const routineUpload = upload.fields([{ name: "image", maxCount: 1 }])
const optionalMultipart = require("../utils/optionalMultipart.js")

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
router.patch(
    "/routine/:id",
    jwtMW.authenticate,
    optionalMultipart(routineUpload),
    routinesController.updateRoutineCSR
)

// DELETE ROUTINE BY ID - CSR
router.delete("/routine/:id", jwtMW.authenticate, routinesController.deleteRoutineCSR)

// CREATE ROUTINE - CSR
router.post(
    "/routine/new",
    jwtMW.authenticate,
    optionalMultipart(routineUpload),
    routinesController.createRoutineCSR
)

module.exports = router
