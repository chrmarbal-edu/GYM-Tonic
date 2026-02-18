const missionController = require("../controllers/missions.controller")
const express = require("express")
const router = express.Router()
const jwtMW = require("../middlewares/jwt.mw")
const rutasProtegidasMW = require("../middlewares/rutasProtegidas.mw")

// FIND ALL MISSIONS - CSR
router.get("/", jwtMW.authenticate, missionController.findAllMissionsCSR)

// FIND MISSION BY ID - CSR
router.get("/:id", jwtMW.authenticate, missionController.findMissionByIdCSR)

// UPDATE MISSION BY ID - CSR
router.patch("/:id", jwtMW.authenticate, rutasProtegidasMW.requireAdmin, missionController.updateMissionCSR)

// DELETE MISSION BY ID - CSR
router.delete("/:id", jwtMW.authenticate, rutasProtegidasMW.requireAdmin, missionController.deleteMissionCSR)

// CREATE MISSION - CSR
router.post("/new", jwtMW.authenticate, rutasProtegidasMW.requireAdmin, missionController.createMissionCSR)

module.exports = router