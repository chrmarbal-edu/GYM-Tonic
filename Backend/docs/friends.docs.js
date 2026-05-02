/**
 * @swagger
 * tags:
 *   name: Amistades
 *   description: API para gestionar amistades
 */

/**
 * @swagger
 * /friends:
 *   get:
 *     summary: Obtener todas las amistades
 *     tags: [Amistades]
 *     security:
 *       - bearerAuth: []
 *     responses:
 *       200:
 *         description: Lista de amistades obtenida exitosamente
 *       400:
 *         description: Error en la solicitud
 *       403:
 *         description: No autorizado para realizar esta operación (solo admins)
 *       500:
 *         description: Error interno del servidor
 *
 *   post:
 *     summary: Crear una nueva amistad
 *     tags: [Amistades]
 *     security:
 *       - bearerAuth: []
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             required:
 *               - userId1
 *               - userId2
 *             properties:
 *               userId1:
 *                 type: string
 *                 description: ID del primer usuario
 *               userId2:
 *                 type: string
 *                 description: ID del segundo usuario
 *     responses:
 *       201:
 *         description: Amistad creada exitosamente
 *       400:
 *         description: Datos inválidos
 *       403:
 *         description: No autorizado para realizar esta operación (solo admins)
 */

/**
 * @swagger
 * /friends/user/{userId}:
 *   get:
 *     summary: Obtener amistades por ID de usuario
 *     tags: [Amistades]
 *     security:
 *       - bearerAuth: []
 *     parameters:
 *       - in: path
 *         name: userId
 *         required: true
 *         description: ID del usuario
 *         schema:
 *           type: string
 *     responses:
 *       200:
 *         description: Lista de amistades obtenida exitosamente
 *       403:
 *         description: No autorizado para realizar esta operación
 *       404:
 *         description: Usuario no encontrado
 */

/**
 * @swagger
 * /friends/{id}:
 *   get:
 *     summary: Obtener una amistad por ID
 *     tags: [Amistades]
 *     security:
 *       - bearerAuth: []
 *     parameters:
 *       - in: path
 *         name: id
 *         required: true
 *         description: ID de la amistad
 *         schema:
 *           type: string
 *     responses:
 *       200:
 *         description: Amistad obtenida exitosamente
 *       403:
 *         description: No autorizado para realizar esta operación (solo admins)
 *       404:
 *         description: Amistad no encontrada
 *
 *   delete:
 *     summary: Eliminar una amistad por ID
 *     tags: [Amistades]
 *     security:
 *       - bearerAuth: []
 *     parameters:
 *       - in: path
 *         name: id
 *         required: true
 *         description: ID de la amistad
 *         schema:
 *           type: string
 *     responses:
 *       200:
 *         description: Amistad eliminada exitosamente
 *       403:
 *         description: No autorizado para realizar esta operación
 *       404:
 *         description: Amistad no encontrada
 *       500:
 *         description: Error interno del servidor
 */