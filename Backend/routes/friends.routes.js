const friendsController = require("../controllers/friends.controller")
const express = require("express")
const router = express.Router()
const jwtMW = require("../middlewares/jwt.mw")
const rutasProtegidasMW = require("../middlewares/rutasProtegidas.mw")

// FIND ALL
router.get("/", rutasProtegidasMW.requireAdmin, jwtMW.authenticate, friendsController.findAllFriends)

// CREATE FRIENDSHIP
router.post("/", rutasProtegidasMW.requireAdmin, jwtMW.authenticate, friendsController.create)

// DELETE FRIENDSHIP BY ID
router.delete("/:id", jwtMW.authenticate, friendsController.deleteFriendById)

// FIND FRIENDS BY USERID
router.get("/user/:userId", jwtMW.authenticate, friendsController.findFriendsByUserId)

// FIND BY ID
router.get("/:id", rutasProtegidasMW.requireAdmin, jwtMW.authenticate, friendsController.findFriendById)

module.exports = router