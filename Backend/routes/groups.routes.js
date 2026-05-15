const groupController = require("../controllers/groups.controller")
const express = require("express")
const router = express.Router()
const jwtMW = require("../middlewares/jwt.mw")
const rutasProtegidasMW = require("../middlewares/rutasProtegidas.mw")

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
router.post("/:id/routines", jwtMW.authenticate, groupController.addGroupRoutineCSR)
router.delete("/:id/routines/:routineId", jwtMW.authenticate, groupController.deleteGroupRoutineCSR)

module.exports = router