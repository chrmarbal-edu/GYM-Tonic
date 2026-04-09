// #region <DEPENDENCIAS>
/* <=============================== DEPENDENCIAS ===============================> */
const groupmodel = require("../models/groups.model.js")
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
/* <=============================== 2. FINDALLGROUPS ===============================> */
// Buscamos todos los grupos.
exports.findAllGroupsCSR = wrapAsync(async function (req,res,next) { 
    // Espera una promesa de lo que devuelva la función "findAll" del modelo.
    await groupmodel.findAll(async function(err, datosGroups){
        if(err){
            next(new AppError(err,400))
        } else{
            res.status(200).json(datosGroups)
        }
    })        
})

// #region FIND-ID - CSR
/* <=============================== 3. FINDGROUPBYID ===============================> */
// Buscamos los grupos por "id".
exports.findGroupByIdCSR = wrapAsync(async function (req,res,next){
    // Traemos por parámetro el id enviado como parámetro por la ruta.
    const {id} = req.params
    const userLogued = req.userLogued;
    // Espera una promesa de lo que devuelva la función "findById" del modelo.
    if(!userLogued){
        return next(new AppError("No estás registrado!", 403))
    }else{
        await groupmodel.findById(id,function(err,datosGroups){
            if(err){
                next(new AppError(err,404))
            } 

            if(!datosGroups || datosGroups.length == 0) {
                return next(new AppError("Usuario no encontrado", 404))
            }

            res.status(200).json(datosGroups)


        })
    }
})

// #region UPDATE - CSR
/* <=============================== 5. UPDATEGROUP ===============================> */
// Actualizamos el grupo.
exports.updateGroupCSR = wrapAsync(async function (req,res, next) {    
    const {id} = req.params
    let { name } = req.body

    console.log("id", id);

    let completeGroup    
   
    /* <================== PARTE 1 ==================> */
    // Espera una promesa de lo que devuelva la función "findById" del modelo. 
    await groupmodel.findById(id, async function(err,objetoDatos){
        if(err){
            console.log("ERROR UPDATE GROUP SSR");

            next(new AppError(err, 500))
        }else{     
            completeGroup = objetoDatos[0]
        }

        let updateGroup = {}           
        updateGroup = {            
            name: name
        }

        completeGroup.name = updateGroup.name

        
        // Realizamos la redirección en la promesa de la actualización.
        await groupmodel.updateById(id, updateGroup, function(err, datosGrupoActualizado){
            if(err){
                console.log("ERROR UPDATE BY ID SSR");

                next(err, 500)
            } else{
                res.status(200).json(datosGrupoActualizado);
            }
        })
    })
})

// #region CREATEGROUP - CSR
/* <=============================== 7. CREATEGROUP ===============================> */
exports.createGroupCSR = wrapAsync(async function (req, res, next) {
    const { name } = req.body
    
        let newGroup = {}

        newGroup = {
            name: name
        }

        // Realizamos la redirección en la promesa de la creación.
        await groupmodelModel.create(newGroup,function(err,datosGrupoCreado){
            if(err){
                console.log(err)
                console.log(datosGrupoCreado)

                console.log("ERROR CREATE GROUPS CSR");

                res.status(500).json({error: err})
            } else{
                res.status(200).json({ datosGrupoCreado })
            }
        })
    
});

// #region DELETE - CSR
/* <=============================== 8. DELETEGROUP ===============================> */
exports.deleteGroupCSR = wrapAsync(async function (req, res, next) {
    const { id } = req.params;
        await groupmodelModel.findById(id, async function (err, objetoDatos) {
            if (err) {
                return next(new AppError("Grupo no encontrado", 404));
            }

            if (!objetoDatos || objetoDatos.length == 0) {
                return next(new AppError("Grupo no encontrado", 404));
            }

            /* <================== PARTE 2 ==================> */
            await groupmodelModel.delete(id, function (err, datosGrupoEliminado) {
                if (err) {
                    return next(new AppError("Error al eliminar el grupo", 500));
                }else {
                    return res.status(200).json({ msg: "Grupo eliminado correctamente" });
                }
            });
        });
});
