const friendRequestsController = require("../controllers/friendRequests.controller")
const express = require("express")
const router = express.Router()
const jwtMW = require("../middlewares/jwt.mw")
const rutasProtegidasMW = require("../middlewares/rutasProtegidas.mw")

// FIND ALL
router.get("/", jwtMW.authenticate, rutasProtegidasMW.requireAdmin, friendRequestsController.findAllFriendRequests)

// CREATE
router.post("/", jwtMW.authenticate, friendRequestsController.create)

// ACCEPT FRIEND REQUEST
router.patch("/accept/:id", jwtMW.authenticate, friendRequestsController.acceptFriendRequest)

// REJECT FRIEND REQUEST
router.patch("/reject/:id", jwtMW.authenticate, friendRequestsController.rejectFriendRequest)

// DELETE
router.delete("/:id", jwtMW.authenticate, friendRequestsController.deleteFrequestById)

// FIND BY ID
router.get("/:id", jwtMW.authenticate, rutasProtegidasMW.requireAdmin, friendRequestsController.findFriendRequestById)

module.exports = router