const oAuthController = require("../controllers/oAuth.controller")
const express = require("express")
const router = express.Router()

// LOGIN OAUTH GOOGLE
router.post("/googleLogin", oAuthController.googleLogin)

// LOGIN OAUTH FACEBOOK
router.post("/facebookLogin", oAuthController.facebookLogin)

module.exports = router