/**
 * @swagger
 * tags:
 *   name: Usuarios
 *   description: API para gestionar usuarios
 */

/**
 * @swagger
 * tags:
 *   name: Misiones de Usuario
 *   description: API para gestionar la asignación y progreso de misiones para los usuarios
 */

/**
 * @swagger
 * /users:
 *   get:
 *     summary: Obtener todos los usuarios
 *     tags: [Usuarios]
 *     security:
 *       - bearerAuth: []
 *     responses:
 *       200:
 *         description: Lista de usuarios obtenida exitosamente
 *       401:
 *         description: Token inválido o faltante
 *       400:
 *         description: Error en la solicitud
 *       403:
 *         description: No tienes permiso para realizar esta petición (solo admins)
 *       500:
 *         description: Error interno del servidor
 *
 *   post:
 *     summary: Registrar un nuevo usuario
 *     tags: [Usuarios]
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             required:
 *               - username
 *               - name
 *               - password
 *               - birthdate
 *               - email
 *             properties:
 *               username:
 *                 type: string
 *                 description: Nombre de usuario
 *               name:
 *                 type: string
 *                 description: Nombre completo del usuario
 *               password:
 *                 type: string
 *                 format: password
 *                 description: Contraseña (mínimo 8 caracteres, con mayúscula, minúscula, número y carácter especial)
 *               birthdate:
 *                 type: string
 *                 format: date
 *                 description: Fecha de nacimiento
 *               email:
 *                 type: string
 *                 format: email
 *                 description: Correo electrónico
 *               height:
 *                 type: number
 *                 description: Altura en cm (130-230)
 *               weight:
 *                 type: number
 *                 description: Peso en kg (40-200)
 *               objective:
 *                 type: integer
 *                 description: "Objetivo: 0 (Mantenimiento), 1 (Pérdida de peso), 2 (Ganancia muscular), 3 (Rendimiento)"
 *                 enum: [0, 1, 2, 3]
 *     responses:
 *       201:
 *         description: Usuario registrado exitosamente (incluye token si no es admin)
 *       400:
 *         description: Datos inválidos o contraseña no cumple requisitos
 *       403:
 *         description: No tienes permisos para realizar esta petición
 *       500:
 *         description: Error interno del servidor
 */

/**
 * @swagger
 * /users/login:
 *   post:
 *     summary: Iniciar sesión de usuario
 *     tags: [Usuarios]
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             required:
 *               - username
 *               - password
 *             properties:
 *               username:
 *                 type: string
 *                 description: Nombre de usuario
 *               password:
 *                 type: string
 *                 format: password
 *                 description: Contraseña
 *     responses:
 *       200:
 *         description: Inicio de sesión exitoso (devuelve token)
 *       401:
 *         description: Usuario y/o contraseña incorrectos
 *       404:
 *         description: Usuario no encontrado
 *       500:
 *         description: Error interno del servidor
 */

/**
 * @swagger
 * /users/logout:
 *   get:
 *     summary: Cerrar sesión de usuario
 *     tags: [Usuarios]
 *     security:
 *       - bearerAuth: []
 *     responses:
 *       200:
 *         description: Sesión cerrada exitosamente
 *       401:
 *         description: Token inválido o faltante
 *       500:
 *         description: Error interno del servidor
 */

/**
 * @swagger
 * /users/{id}:
 *   get:
 *     summary: Obtener un usuario por ID
 *     tags: [Usuarios]
 *     security:
 *       - bearerAuth: []
 *     parameters:
 *       - in: path
 *         name: id
 *         required: true
 *         description: ID del usuario
 *         schema:
 *           type: string
 *     responses:
 *       200:
 *         description: Usuario obtenido exitosamente
 *       401:
 *         description: Token inválido o faltante
 *       403:
 *         description: No autorizado para realizar esta operación (solo admins)
 *       404:
 *         description: Usuario no encontrado
 *
 *   patch:
 *     summary: Actualizar un usuario por ID
 *     tags: [Usuarios]
 *     security:
 *       - bearerAuth: []
 *     parameters:
 *       - in: path
 *         name: id
 *         required: true
 *         description: ID del usuario
 *         schema:
 *           type: string
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             properties:
 *               username:
 *                 type: string
 *                 description: Nombre de usuario
 *               name:
 *                 type: string
 *                 description: Nombre completo del usuario
 *               currentPassword:
 *                 type: string
 *                 format: password
 *                 description: Contraseña actual (requerida para cambiar contraseña)
 *               newPassword:
 *                 type: string
 *                 format: password
 *                 description: Nueva contraseña (mínimo 8 caracteres, con mayúscula, minúscula, número y carácter especial)
 *               email:
 *                 type: string
 *                 format: email
 *                 description: Correo electrónico
 *               height:
 *                 type: number
 *                 description: Altura en cm (130-230)
 *               weight:
 *                 type: number
 *                 description: Peso en kg (40-200)
 *               objective:
 *                 type: integer
 *                 description: "Objetivo: 0 (Mantenimiento), 1 (Pérdida de peso), 2 (Ganancia muscular), 3 (Rendimiento)"
 *                 enum: [0, 1, 2, 3]
 *     responses:
 *       200:
 *         description: Usuario actualizado exitosamente
 *       400:
 *         description: Datos inválidos o contraseña incorrecta
 *       401:
 *         description: Token inválido o faltante
 *       404:
 *         description: Usuario no encontrado
 *       500:
 *         description: Error interno del servidor
 *
 *   delete:
 *     summary: Eliminar un usuario por ID
 *     tags: [Usuarios]
 *     security:
 *       - bearerAuth: []
 *     parameters:
 *       - in: path
 *         name: id
 *         required: true
 *         description: ID del usuario
 *         schema:
 *           type: string
 *     responses:
 *       200:
 *         description: Usuario eliminado exitosamente (sesión destruida si es el propio usuario)
 *       401:
 *         description: Token inválido o faltante
 *       403:
 *         description: No estás autorizado para eliminar este usuario
 *       404:
 *         description: Usuario no encontrado
 *       500:
 *         description: Error interno del servidor
 */

/**
 * @swagger
 * /users/missions:
 *   get:
 *     summary: Obtener todas las misiones de todos los usuarios
 *     tags: [Misiones de Usuario]
 *     security:
 *       - bearerAuth: []
 *     responses:
 *       200:
 *         description: Lista de misiones de usuarios obtenida
 *       403:
 *         description: No autorizado
 *
 *   post:
 *     summary: Asignar una misión a un usuario
 *     tags: [Misiones de Usuario]
 *     security:
 *       - bearerAuth: []
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             required:
 *               - userId
 *               - missionId
 *             properties:
 *               userId:
 *                 type: integer
 *               missionId:
 *                 type: integer
 *               expiration:
 *                 type: string
 *                 format: date-time
 *     responses:
 *       201:
 *         description: Misión asignada exitosamente
 *       403:
 *         description: No autorizado
 */

/**
 * @swagger
 * /users/missions/user/{userId}:
 *   get:
 *     summary: Obtener misiones asignadas a un usuario específico
 *     tags: [Misiones de Usuario]
 *     security:
 *       - bearerAuth: []
 *     parameters:
 *       - in: path
 *         name: userId
 *         required: true
 *         schema:
 *           type: integer
 *     responses:
 *       200:
 *         description: Misiones del usuario obtenidas
 *       404:
 *         description: Misiones no encontradas
 */

/**
 * @swagger
 * /users/missions/mission/{missionId}:
 *   get:
 *     summary: Obtener todos los usuarios que tienen una misión específica
 *     tags: [Misiones de Usuario]
 *     security:
 *       - bearerAuth: []
 *     parameters:
 *       - in: path
 *         name: missionId
 *         required: true
 *         schema:
 *           type: integer
 *     responses:
 *       200:
 *         description: Lista de usuarios para esa misión
 *       404:
 *         description: Misión no encontrada
 */

/**
 * @swagger
 * /users/missions/{id}:
 *   get:
 *     summary: Obtener una asignación de misión por su ID (Admin)
 *     tags: [Misiones de Usuario]
 *     security:
 *       - bearerAuth: []
 *     parameters:
 *       - in: path
 *         name: id
 *         required: true
 *         schema:
 *           type: string
 *     responses:
 *       200:
 *         description: Datos de la misión de usuario
 *       403:
 *         description: Solo para administradores
 *       404:
 *         description: No encontrado
 *
 *   patch:
 *     summary: Actualizar una misión de usuario (Admin)
 *     tags: [Misiones de Usuario]
 *     security:
 *       - bearerAuth: []
 *     parameters:
 *       - in: path
 *         name: id
 *         required: true
 *         schema:
 *           type: string
 *     requestBody:
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             properties:
 *               userId:
 *                 type: integer
 *               missionId:
 *                 type: integer
 *               expiration:
 *                 type: string
 *                 format: date-time
 *               completed:
 *                 type: integer
 *                 description: "0: No completada, 1: Completada"
 *                 enum: [0, 1]
 *     responses:
 *       200:
 *         description: Misión actualizada correctamente
 *       403:
 *         description: No autorizado
 *
 *   delete:
 *     summary: Eliminar una asignación de misión (Admin)
 *     tags: [Misiones de Usuario]
 *     security:
 *       - bearerAuth: []
 *     parameters:
 *       - in: path
 *         name: id
 *         required: true
 *         schema:
 *           type: string
 *     responses:
 *       200:
 *         description: Asignación eliminada
 *       403:
 *         description: No autorizado
 */