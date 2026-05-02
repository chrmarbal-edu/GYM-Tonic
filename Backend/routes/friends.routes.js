const friendsController = require("../controllers/friends.controller")
const express = require("express")
const router = express.Router()
const jwtMW = require("../middlewares/jwt.mw")
const rutasProtegidasMW = require("../middlewares/rutasProtegidas.mw")

// FIND ALL
router.get("/", jwtMW.authenticate, rutasProtegidasMW.requireAdmin, friendsController.findAllFriends)

// CREATE FRIENDSHIP
router.post("/", jwtMW.authenticate, rutasProtegidasMW.requireAdmin, friendsController.create)

// DELETE FRIENDSHIP BY ID
router.delete("/:id", jwtMW.authenticate, friendsController.deleteFriendById)

// FIND FRIENDS BY USERID
router.get("/user/:userId", jwtMW.authenticate, friendsController.findFriendsByUserId)

// FIND BY ID
router.get("/:id", jwtMW.authenticate, rutasProtegidasMW.requireAdmin, friendsController.findFriendById)

module.exports = router