// #region <DEPENDENCIAS>
/* <=============================== DEPENDENCIAS ===============================> */
const routinesmodel = require("../models/routines.model.js")
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
/* <=============================== 2. FINDALLROUTINES ===============================> */
// Buscamos todos los grupos.
exports.findAllRoutinesCSR = wrapAsync(async function (req,res,next) { 
    // Espera una promesa de lo que devuelva la función "findAll" del modelo.
    await routinesmodel.findAll(async function(err, datosRoutines){
        if(err){
            next(new AppError(err,400))
        } else{
            res.status(200).json(datosRoutines)
        }
    })        
})

// #region FIND-ID - CSR
/* <=============================== 3. FINDROUTINEBYID ===============================> */
// Buscamos los grupos por "id".
exports.findRoutineByIdCSR = wrapAsync(async function (req,res,next){
    // Traemos por parámetro el id enviado como parámetro por la ruta.
    const {id} = req.params
    const userLogued = req.session.userLogued.data;
    // Espera una promesa de lo que devuelva la función "findById" del modelo.
    if(!userLogued){
        return next(new AppError("No estás registrado!", 403))
    }else{
        await routinesmodel.findById(id,function(err,datosRoutines){
            if(err){
                next(new AppError(err,404))
            } 

            if(!datosRoutines || datosRoutines.length == 0) {
                return next(new AppError("Rutina no encontrada", 404))
            }

            res.status(200).json(datosRoutines)

        })
    }
})

// #region UPDATE - CSR
/* <=============================== 5. UPDATEROUTINE ===============================> */
// Actualizamos la rutina.
exports.updateRoutineCSR = wrapAsync(async function (req,res, next) {    
    const {id} = req.params
    let { name } = req.body

    console.log("id", id);

    let completeRoutine = {}  
   
    /* <================== PARTE 1 ==================> */
    // Espera una promesa de lo que devuelva la función "findById" del modelo. 
    await routinesmodel.findById(id, async function(err,objetoDatos){
        if(err){
            console.log("ERROR UPDATE ROUTINE SSR");

            next(new AppError(err, 500))
        }else{     
            completeRoutine = objetoDatos[0]
        }

        let updateRoutine = {}           
        updateRoutine = {            
            name: name
        }

        completeRoutine.name = updateRoutine.name
        
        // Realizamos la redirección en la promesa de la actualización.
        await routinesmodel.updateById(id, updateRoutine, function(err, datosRutinaActualizada){
            if(err){
                console.log("ERROR UPDATE BY ID SSR");

                next(new AppError(err, 500))
            } else{
                res.status(200).json(datosRutinaActualizada);
            }
        })
    })
})

// #region CREATEROUTINE - CSR
/* <=============================== 7. CREATEROUTINE ===============================> */
exports.createRoutineCSR = wrapAsync(async function (req, res, next) {
    const { name } = req.body
    
        let newRoutine = {}

        newRoutine = {
            name: name
        }

        // Realizamos la redirección en la promesa de la creación.
        await routinesmodel.create(newRoutine,function(err,datosRutinaCreada){
            if(err){
                console.log(err)
                console.log(datosRutinaCreada)

                console.log("ERROR CREATE ROUTINE CSR");

                res.status(500).json({error: err})
            } else{
                res.status(200).json({ datosRutinaCreada })
            }
        })
    
});

// #region DELETE - CSR
/* <=============================== 8. DELETEROUTINE ===============================> */
exports.deleteRoutineCSR = wrapAsync(async function (req, res, next) {
    const { id } = req.params;
        await routinesmodel.findById(id, async function (err, objetoDatos) {
            if (err) {
                return next(new AppError("Rutina no encontrada", 404));
            }

            if (!objetoDatos || objetoDatos.length == 0) {
                return next(new AppError("Rutina no encontrada", 404));
            }

            /* <================== PARTE 2 ==================> */
            await routinesmodel.delete(id, function (err, datosRutinaEliminada) {
                if (err) {
                    return next(new AppError("Error al eliminar la rutina", 500));
                }else {
                    return res.status(200).json({ msg: "Rutina eliminada correctamente" });
                }
            });
        });
});
