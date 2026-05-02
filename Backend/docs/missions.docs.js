/**
 * @swagger
 * tags:
 *   name: Misiones
 *   description: API para gestionar misiones
 */

/**
 * @swagger
 * /missions:
 *   get:
 *     summary: Obtener todas las misiones
 *     tags: [Misiones]
 *     security:
 *       - bearerAuth: []
 *     responses:
 *       200:
 *         description: Lista de misiones obtenida exitosamente
 *       400:
 *         description: Error en la solicitud
 *       403:
 *         description: No estás registrado
 *       500:
 *         description: Error interno del servidor
 */

/**
 * @swagger
 * /missions/new:
 *   post:
 *     summary: Crear una nueva misión
 *     tags: [Misiones]
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
 *               - type
 *               - points
 *               - objetive
 *             properties:
 *               name:
 *                 type: string
 *                 description: Nombre de la misión
 *               type:
 *                 type: string
 *                 description: Tipo de la misión
 *               points:
 *                 type: number
 *                 description: Puntos de la misión
 *               objetive:
 *                 type: string
 *                 description: Objetivo de la misión
 *     responses:
 *       200:
 *         description: Misión creada exitosamente
 *       403:
 *         description: No autorizado para realizar esta operación (solo admins)
 *       500:
 *         description: Error interno del servidor
 */

/**
 * @swagger
 * /missions/{id}:
 *   get:
 *     summary: Obtener una misión por ID
 *     tags: [Misiones]
 *     security:
 *       - bearerAuth: []
 *     parameters:
 *       - in: path
 *         name: id
 *         required: true
 *         description: ID de la misión
 *         schema:
 *           type: string
 *     responses:
 *       200:
 *         description: Misión obtenida exitosamente
 *       403:
 *         description: No estás registrado
 *       404:
 *         description: Misión no encontrada
 *
 *   patch:
 *     summary: Actualizar una misión por ID
 *     tags: [Misiones]
 *     security:
 *       - bearerAuth: []
 *     parameters:
 *       - in: path
 *         name: id
 *         required: true
 *         description: ID de la misión
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
 *                 description: Nombre de la misión
 *               type:
 *                 type: string
 *                 description: Tipo de la misión
 *               points:
 *                 type: number
 *                 description: Puntos de la misión
 *               objetive:
 *                 type: string
 *                 description: Objetivo de la misión
 *     responses:
 *       200:
 *         description: Misión actualizada exitosamente
 *       403:
 *         description: No autorizado para realizar esta operación (solo admins)
 *       404:
 *         description: Misión no encontrada
 *       500:
 *         description: Error interno del servidor
 *
 *   delete:
 *     summary: Eliminar una misión por ID
 *     tags: [Misiones]
 *     security:
 *       - bearerAuth: []
 *     parameters:
 *       - in: path
 *         name: id
 *         required: true
 *         description: ID de la misión
 *         schema:
 *           type: string
 *     responses:
 *       200:
 *         description: Misión eliminada correctamente
 *       403:
 *         description: No autorizado para realizar esta operación (solo admins)
 *       404:
 *         description: Misión no encontrada
 *       500:
 *         description: Error interno del servidor
 */