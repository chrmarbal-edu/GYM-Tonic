const AppError = require("../utils/AppError")

const checkRole = (req, role) =>{
    if(req && req.userLogued && req.userLogued.user_role && req.userLogued.user_role == role){
        return true
    }else{
        return false
    }
}

exports.requireAdmin = (req,res,next) =>{
    if(checkRole(req,1)){
        next()
    }else{
        next(new AppError("No estás autorizado",403))
    }
}