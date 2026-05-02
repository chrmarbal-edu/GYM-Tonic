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
 *       400:
 *         description: Error en la solicitud
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
 *                 description: Nombre del grupo
 *     responses:
 *       200:
 *         description: Grupo creado exitosamente
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
 *       403:
 *         description: No autorizado para realizar esta operación (solo admins)
 *       404:
 *         description: Grupo no encontrado
 *       500:
 *         description: Error interno del servidor
 */