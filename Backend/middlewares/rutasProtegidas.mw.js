const AppError = require("../utils/AppError")

const checkRole = (req, role) =>{
    if(req.session && req.session.userLogued && req.session.userLogued.data && req.session.userLogued.data.user_role && req.session.userLogued.data.user_role == role){
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