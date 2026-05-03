/**
 * @swagger
 * tags:
 *   name: Rutinas
 *   description: API para gestionar rutinas
 */

/**
 * @swagger
 * /routines:
 *   get:
 *     summary: Obtener todas las rutinas
 *     tags: [Rutinas]
 *     security:
 *       - bearerAuth: []
 *     responses:
 *       200:
 *         description: Lista de rutinas obtenida exitosamente
 *       400:
 *         description: Error en la solicitud
 *       500:
 *         description: Error interno del servidor
 */

/**
 * @swagger
 * /routine/by-name/{name}:
 *   get:
 *     summary: Obtener una rutina por nombre o slug
 *     tags: [Rutinas]
 *     security:
 *       - bearerAuth: []
 *     parameters:
 *       - in: path
 *         name: name
 *         required: true
 *         description: Nombre o slug de la rutina
 *         schema:
 *           type: string
 *     responses:
 *       200:
 *         description: Rutina obtenida exitosamente
 *       403:
 *         description: No estas registrado
 *       404:
 *         description: Rutina no encontrada
 *       500:
 *         description: Error interno del servidor
 */

/**
 * @swagger
 * /routine/{id}:
 *   get:
 *     summary: Obtener una rutina por ID
 *     tags: [Rutinas]
 *     security:
 *       - bearerAuth: []
 *     parameters:
 *       - in: path
 *         name: id
 *         required: true
 *         description: ID de la rutina
 *         schema:
 *           type: string
 *     responses:
 *       200:
 *         description: Rutina obtenida exitosamente
 *       403:
 *         description: No estás registrado
 *       404:
 *         description: Rutina no encontrada
 *
 *   patch:
 *     summary: Actualizar una rutina por ID
 *     tags: [Rutinas]
 *     security:
 *       - bearerAuth: []
 *     parameters:
 *       - in: path
 *         name: id
 *         required: true
 *         description: ID de la rutina
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
 *                 description: Nombre de la rutina
 *     responses:
 *       200:
 *         description: Rutina actualizada exitosamente
 *       403:
 *         description: No autorizado para realizar esta operación (solo admins)
 *       404:
 *         description: Rutina no encontrada
 *       500:
 *         description: Error interno del servidor
 *
 *   delete:
 *     summary: Eliminar una rutina por ID
 *     tags: [Rutinas]
 *     security:
 *       - bearerAuth: []
 *     parameters:
 *       - in: path
 *         name: id
 *         required: true
 *         description: ID de la rutina
 *         schema:
 *           type: string
 *     responses:
 *       200:
 *         description: Rutina eliminada correctamente
 *       403:
 *         description: No autorizado para realizar esta operación (solo admins)
 *       404:
 *         description: Rutina no encontrada
 *       500:
 *         description: Error interno del servidor
 */

/**
 * @swagger
 * /routine/new:
 *   post:
 *     summary: Crear una nueva rutina
 *     tags: [Rutinas]
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
 *                 description: Nombre de la rutina
 *     responses:
 *       200:
 *         description: Rutina creada exitosamente
 *       403:
 *         description: No autorizado para realizar esta operación (solo admins)
 *       500:
 *         description: Error interno del servidor
 */
