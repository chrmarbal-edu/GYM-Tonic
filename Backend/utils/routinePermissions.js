const AppError = require("./AppError")
const groupmodel = require("../models/groups.model.js")
const groupUserModel = require("../models/groupUsers.model.js")

const loadGroup = (id) =>
    new Promise((resolve, reject) => {
        groupmodel.findById(id, (err, data) => {
            if (err && err.err === "No hay datos") {
                resolve(null)
            } else if (err) {
                reject(err)
            } else {
                resolve(data)
            }
        })
    })

const loadGroupMembership = (groupId, userId) =>
    new Promise((resolve, reject) => {
        groupUserModel.findMembership(groupId, userId, (err, data) => {
            if (err && err.err === "No hay datos") {
                resolve(null)
            } else if (err) {
                reject(err)
            } else {
                resolve(data)
            }
        })
    })

const isAdmin = (user) => Number(user?.user_role) === 1

const isPersonalRoutine = (routine) =>
    Number(routine.routine_is_personal_routine) === 1 ||
    (routine.routine_is_personal_routine == null &&
        routine.routine_creator_id != null &&
        Number(routine.routine_is_group_routine) === 0)

const isSystemRoutine = (routine) =>
    Number(routine.routine_is_group_routine) === 0 &&
    !isPersonalRoutine(routine) &&
    (routine.routine_creator_id == null || routine.routine_creator_id === undefined)

const isGroupMember = async (groupId, userId) => {
    if (!Number.isFinite(groupId) || !Number.isFinite(userId)) {
        return false
    }
    const membership = await loadGroupMembership(groupId, userId)
    return membership != null
}

const canManageRoutine = async (user, routine) => {
    if (!user || !routine) {
        return false
    }

    if (isAdmin(user)) {
        return true
    }

    const userId = Number(user.user_id)
    if (!Number.isFinite(userId)) {
        return false
    }

    const creatorId =
        routine.routine_creator_id != null ? Number(routine.routine_creator_id) : null

    if (isPersonalRoutine(routine) && creatorId !== null && creatorId === userId) {
        return true
    }

    if (Number(routine.routine_is_group_routine) === 1 && routine.routine_groupid != null) {
        const group = await loadGroup(routine.routine_groupid)
        if (group && Number(group.group_creator_id) === userId) {
            return true
        }
    }

    return false
}

const canViewRoutine = async (user, routine) => {
    if (!user || !routine) {
        return false
    }

    if (isAdmin(user)) {
        return true
    }

    if (isSystemRoutine(routine)) {
        return true
    }

    const userId = Number(user.user_id)
    if (!Number.isFinite(userId)) {
        return false
    }

    if (Number(routine.routine_is_group_routine) === 1 && routine.routine_groupid != null) {
        return isGroupMember(Number(routine.routine_groupid), userId)
    }

    if (isPersonalRoutine(routine)) {
        const creatorId =
            routine.routine_creator_id != null ? Number(routine.routine_creator_id) : null
        return creatorId !== null && creatorId === userId
    }

    return false
}

const assertCanManageRoutine = async (user, routine) => {
    const allowed = await canManageRoutine(user, routine)
    if (!allowed) {
        throw new AppError("No tienes permiso para gestionar esta rutina", 403)
    }
}

const assertCanViewRoutine = async (user, routine) => {
    const allowed = await canViewRoutine(user, routine)
    if (!allowed) {
        throw new AppError("No tienes permiso para ver esta rutina", 403)
    }
}

module.exports = {
    isAdmin,
    isSystemRoutine,
    isPersonalRoutine,
    canManageRoutine,
    canViewRoutine,
    assertCanManageRoutine,
    assertCanViewRoutine
}
