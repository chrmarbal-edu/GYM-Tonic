// #region <DEPENDENCIAS>
/* <=============================== DEPENDENCIAS ===============================> */
const missionmodel = require("../models/missions.model.js")
const userModel = require("../models/users.model.js")
const fs = require("fs").promises
const AppError = require("../utils/AppError")
const bcrypt = require("../utils/bcrypt")
const jwtMW = require("../middlewares/jwt.mw")
const { log } = require("console")


function wrapAsync(fn) {
    return function(req, res, next) {
        fn(req, res, next).catch(e => {
            next(e);
        });
    }
}
/* 
400 - BAD REQUEST (EL SERVIDOR NO PUEDE PROCESAR LA SOLICITUD)
404 - NOT FOUND (NO EXISTE EN EL SERVIDOR EL RECURSO PEDIDO)
500 - GENÉRICO (ALGO HA IDO MAL EN EL SERVIDOR)
*/

// #region <---CSR GROUPS--->

// #region FINDALL - CSR
/* <=============================== 2. FINDALLMISSIONS ===============================> */
// Buscamos todas las misiones.
exports.findAllMissionsCSR = wrapAsync(async function (req,res,next) { 
    // Espera una promesa de lo que devuelva la función "findAll" del modelo.
    await missionmodel.findAll(async function(err, datosMissions){
        if(err){
            next(new AppError(err,400))
        } else{
            res.status(200).json(datosMissions)
        }
    })        
})

// #region FIND-ID - CSR
/* <=============================== 3. FINDMISSIONBYID ===============================> */
// Buscamos las misiones por "id".
exports.findMissionByIdCSR = wrapAsync(async function (req,res,next){
    // Traemos por parámetro el id enviado como parámetro por la ruta.
    const {id} = req.params
    const userLogued = req.userLogued;
    // Espera una promesa de lo que devuelva la función "findById" del modelo.
    if(!userLogued){
        return next(new AppError("No estás registrado!", 403))
    }else{
        await missionmodel.findById(id,function(err,datosMissions){
            if(err){
                next(new AppError(err,404))
            } 

            if(!datosMissions || datosMissions.length == 0) {
                return next(new AppError("Usuario no encontrado", 404))
            }

            res.status(200).json(datosMissions)

        })
    }
})

// #region UPDATE - CSR
/* <=============================== 5. UPDATEMISSION ===============================> */
// Actualizamos la misión.
exports.updateMissionCSR = wrapAsync(async function (req,res, next) {    
    const {id} = req.params
    let { name, type, points, objetive } = req.body

    console.log("id", id);

    let completeMission    
   
    /* <================== PARTE 1 ==================> */
    // Espera una promesa de lo que devuelva la función "findById" del modelo. 
    await missionmodel.findById(id, async function(err,objetoDatos){
        if(err){
            console.log("ERROR UPDATE GROUP SSR");

            next(new AppError(err, 500))
        }else{     
            completeMission = objetoDatos[0]
        }

        let updateMission = {}           
        updateMission = {            
            name: name,
            type: type,
            points: points,
            objetive: objetive
        }

        completeMission.name = updateMission.name
        completeMission.type = updateMission.type
        completeMission.points = updateMission.points
        completeMission.objetive = updateMission.objetive

        
        // Realizamos la redirección en la promesa de la actualización.
        await missionmodel.updateById(id, updateMission, function(err, datosMissionActualizada){
            if(err){
                console.log("ERROR UPDATE BY ID SSR");

                next(err, 500)
            } else{
                res.status(200).json(datosMissionActualizada);
            }
        })
    })
})

// #region CREATEMISSION - CSR
/* <=============================== 7. CREATEMISSION ===============================> */
exports.createMissionCSR = wrapAsync(async function (req, res, next) {
    const { name, type, points, objetive } = req.body

        let newMission = {}

        newMission = {
            name: name,
            type: type,
            points: points,
            objetive: objetive
        }

        // Realizamos la redirección en la promesa de la creación.
        await missionmodelModel.create(newMission,function(err,datosMisionCreada){
            if(err){
                console.log(err)
                console.log(datosMisionCreada)

                console.log("ERROR CREATE MISSIONS CSR");

                res.status(500).json({error: err})
            } else{
                res.status(200).json({ datosMisionCreada })
            }
        })
    
});

// #region DELETE - CSR
/* <=============================== 8. DELETEMISSION ===============================> */
exports.deleteMissionCSR = wrapAsync(async function (req, res, next) {
    const { id } = req.params;
        await missionmodelModel.findById(id, async function (err, objetoDatos) {
            if (err) {
                return next(new AppError("Misión no encontrada", 404));
            }

            if (!objetoDatos || objetoDatos.length == 0) {
                return next(new AppError("Misión no encontrada", 404));
            }

            /* <================== PARTE 2 ==================> */
            await missionmodelModel.delete(id, function (err, datosMisionEliminada) {
                if (err) {
                    return next(new AppError("Error al eliminar la misión", 500));
                }else {
                    return res.status(200).json({ msg: "Misión eliminada correctamente" });
                }
            });
        });
});
