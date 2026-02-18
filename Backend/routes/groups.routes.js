const groupController = require("../controllers/groups.controller")
const express = require("express")
const router = express.Router()
const jwtMW = require("../middlewares/jwt.mw")
const rutasProtegidasMW = require("../middlewares/rutasProtegidas.mw")

// FIND ALL GROUPS - CSR
router.get("/", jwtMW.authenticate, groupController.findAllGroupsCSR)
// FIND GROUP BY ID - CSR
router.get("/:id", jwtMW.authenticate, groupController.findGroupByIdCSR)

// UPDATE GROUP BY ID - CSR
router.patch("/:id", jwtMW.authenticate, rutasProtegidasMW.requireAdmin, groupController.updateGroupCSR)

// DELETE GROUP BY ID - CSR
router.delete("/:id", jwtMW.authenticate, rutasProtegidasMW.requireAdmin, groupController.deleteGroupCSR)

// CREATE GROUP - CSR
router.post("/new", jwtMW.authenticate, rutasProtegidasMW.requireAdmin, groupController.createGroupCSR)

module.exports = router