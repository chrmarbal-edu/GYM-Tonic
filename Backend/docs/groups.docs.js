/**
 * @swagger
 * tags:
 *   name: Grupos
 *   description: API para gestionar grupos
 */

/**
 * @swagger
 * /groups:
 *   get:
 *     summary: Obtener todos los grupos
 *     tags: [Grupos]
 *     security:
 *       - bearerAuth: []
 *     responses:
 *       200:
 *         description: Lista de grupos obtenida exitosamente
 *       401:
 *         description: Token inválido o faltante
 *       400:
 *         description: Error en la solicitud
 *       403:
 *         description: No estás registrado
 *       500:
 *         description: Error interno del servidor
 */

/**
 * @swagger
 * /groups/my:
 *   get:
 *     summary: Obtener los grupos a los que pertenece el usuario logueado
 *     tags: [Grupos]
 *     security:
 *       - bearerAuth: []
 *     responses:
 *       200:
 *         description: Lista de mis grupos obtenida exitosamente
 *       401:
 *         description: Token inválido o faltante
 *       403:
 *         description: No estás registrado
 *       500:
 *         description: Error interno del servidor
 */

/**
 * @swagger
 * /groups/new:
 *   post:
 *     summary: Crear un nuevo grupo
 *     tags: [Grupos]
 *     security:
 *       - bearerAuth: []
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             required:
 *               - name
 *             properties:
 *               name:
 *                 type: string
 *               description:
 *                 type: string
 *               image:
 *                 type: string
 *               points:
 *                 type: number
 *     responses:
 *       200:
 *         description: Grupo creado exitosamente
 *       401:
 *         description: Token inválido o faltante
 *       400:
 *         description: Datos inválidos
 *       403:
 *         description: No autorizado para realizar esta operación (solo admins)
 *       500:
 *         description: Error interno del servidor
 */

/**
 * @swagger
 * /groups/{id}:
 *   get:
 *     summary: Obtener un grupo por ID
 *     tags: [Grupos]
 *     security:
 *       - bearerAuth: []
 *     parameters:
 *       - in: path
 *         name: id
 *         required: true
 *         description: ID del grupo
 *         schema:
 *           type: string
 *     responses:
 *       200:
 *         description: Grupo obtenido exitosamente
 *       401:
 *         description: Token inválido o faltante
 *       403:
 *         description: No estás registrado
 *       404:
 *         description: Grupo no encontrado
 *
 *   patch:
 *     summary: Actualizar un grupo por ID
 *     tags: [Grupos]
 *     security:
 *       - bearerAuth: []
 *     parameters:
 *       - in: path
 *         name: id
 *         required: true
 *         description: ID del grupo
 *         schema:
 *           type: string
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             properties:
 *               name:
 *                 type: string
 *                 description: Nombre del grupo
 *     responses:
 *       200:
 *         description: Grupo actualizado exitosamente
 *       401:
 *         description: Token inválido o faltante
 *       403:
 *         description: No autorizado para realizar esta operación (solo admins)
 *       404:
 *         description: Grupo no encontrado
 *       500:
 *         description: Error interno del servidor
 *
 *   delete:
 *     summary: Eliminar un grupo por ID
 *     tags: [Grupos]
 *     security:
 *       - bearerAuth: []
 *     parameters:
 *       - in: path
 *         name: id
 *         required: true
 *         description: ID del grupo
 *         schema:
 *           type: string
 *     responses:
 *       200:
 *         description: Grupo eliminado correctamente
 *       401:
 *         description: Token inválido o faltante
 *       403:
 *         description: No autorizado para realizar esta operación (solo admins)
 *       404:
 *         description: Grupo no encontrado
 *       500:
 *         description: Error interno del servidor
 */

/**
 * @swagger
 * /groups/{id}/join:
 *   post:
 *     summary: Unirse a un grupo
 *     tags: [Grupos]
 *     security:
 *       - bearerAuth: []
 *     parameters:
 *       - in: path
 *         name: id
 *         required: true
 *         schema:
 *           type: string
 *     responses:
 *       201:
 *         description: Te has unido al grupo
 *       409:
 *         description: Ya perteneces al grupo
 */

/**
 * @swagger
 * /groups/{id}/leave:
 *   post:
 *     summary: Abandonar un grupo
 *     tags: [Grupos]
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
 *         description: Has abandonado el grupo
 *       403:
 *         description: El creador no puede abandonar el grupo
 */

/**
 * @swagger
 * /groups/{id}/members:
 *   get:
 *     summary: Obtener integrantes de un grupo
 *     tags: [Grupos]
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
 *         description: Lista de miembros
 */

/**
 * @swagger
 * /groups/{id}/routines:
 *   get:
 *     summary: Obtener rutinas asignadas a un grupo
 *     tags: [Grupos]
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
 *         description: Lista de rutinas
 */

/**
 * @swagger
 * /groups/{id}/add-user:
 *   post:
 *     summary: Añadir un usuario al grupo (solo creador)
 *     tags: [Grupos]
 *     security:
 *       - bearerAuth: []
 *     parameters:
 *       - in: path
 *         name: id
 *         required: true
 *         schema:
 *           type: string
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             properties:
 *               user_id:
 *                 type: integer
 *               range:
 *                 type: integer
 *     responses:
 *       201:
 *         description: Usuario añadido
 */

/**
 * @swagger
 * /groups/{id}/remove-user/{userId}:
 *   delete:
 *     summary: Eliminar un usuario del grupo
 *     tags: [Grupos]
 *     security:
 *       - bearerAuth: []
 *     parameters:
 *       - in: path
 *         name: id
 *         required: true
 *         schema:
 *           type: string
 *       - in: path
 *         name: userId
 *         required: true
 *         schema:
 *           type: string
 *     responses:
 *       200:
 *         description: Usuario eliminado
 */

/**
 * @swagger
 * /groups/{id}/add-routine:
 *   post:
 *     summary: Añadir una rutina al grupo
 *     tags: [Grupos]
 *     security:
 *       - bearerAuth: []
 *     parameters:
 *       - in: path
 *         name: id
 *         required: true
 *         schema:
 *           type: string
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             properties:
 *               name:
 *                 type: string
 *               exercise_ids:
 *                 type: array
 *                 items:
 *                   type: integer
 *     responses:
 *       201:
 *         description: Rutina creada y asignada a miembros
 */