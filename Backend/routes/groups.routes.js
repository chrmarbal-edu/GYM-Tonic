const groupController = require("../controllers/groups.controller")
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
const groupRoutineUpload = upload.fields([{ name: "image", maxCount: 1 }])
const optionalMultipart = require("../utils/optionalMultipart.js")

// FIND ALL GROUPS - CSR
router.get("/", jwtMW.authenticate, groupController.findAllGroupsCSR)
// Grupos del usuario logueado (por membresía)
router.get("/my", jwtMW.authenticate, groupController.findMyGroupsCSR)
// FIND GROUP BY ID - CSR
router.get("/:id", jwtMW.authenticate, groupController.findGroupByIdCSR)

// UPDATE GROUP BY ID - CSR
router.patch("/:id", jwtMW.authenticate, rutasProtegidasMW.requireAdmin, groupController.updateGroupCSR)

// DELETE GROUP BY ID - CSR
router.delete("/:id", jwtMW.authenticate, rutasProtegidasMW.requireAdmin, groupController.deleteGroupCSR)

// CREATE GROUP - CSR (usuario autenticado)
router.post("/new", jwtMW.authenticate, groupController.createGroupCSR)

// Miembros y rutinas de grupo
router.get("/:id/members", jwtMW.authenticate, groupController.findGroupMembersCSR)

router.get("/:id/routines", jwtMW.authenticate, groupController.findGroupRoutinesCSR)

router.post("/:id/join", jwtMW.authenticate, groupController.joinGroupCSR)

router.delete("/:id/leave", jwtMW.authenticate, groupController.leaveGroupCSR)

router.post("/:id/members", jwtMW.authenticate, groupController.addUserToGroupCSR)

router.delete("/:id/members/:userId", jwtMW.authenticate, groupController.removeUserFromGroupCSR)

router.post("/:id/routines", jwtMW.authenticate, optionalMultipart(groupRoutineUpload), groupController.addGroupRoutineCSR)

router.patch("/:id/routines/:routineId", jwtMW.authenticate, optionalMultipart(groupRoutineUpload), groupController.updateGroupRoutineCSR)

router.delete("/:id/routines/:routineId", jwtMW.authenticate, groupController.deleteGroupRoutineCSR)

module.exports = router
