const oAuthController = require("../controllers/oAuth.controller")
const express = require("express")
const router = express.Router()

// LOGIN OAUTH GOOGLE
router.post("/googleLogin", oAuthController.googleLogin)

module.exports = router