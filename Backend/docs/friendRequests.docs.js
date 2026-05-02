/**
 * @swagger
 * tags:
 *   name: Solicitudes de Amistad
 *   description: API para gestionar solicitudes de amistad
 */

/**
 * @swagger
 * /:
 *   get:
 *     summary: Obtener todas las solicitudes de amistad
 *     tags: [Solicitudes de Amistad]
 *     security:
 *       - bearerAuth: []
 *     responses:
 *       200:
 *         description: Lista de solicitudes de amistad obtenida exitosamente
 *       400:
 *         description: Error en la solicitud
 *       403:
 *         description: No autorizado para realizar esta operación (solo admins)
 *       500:
 *         description: Error interno del servidor
 *
 *   post:
 *     summary: Crear una nueva solicitud de amistad
 *     tags: [Solicitudes de Amistad]
 *     security:
 *       - bearerAuth: []
 *     requestBody:
 *       required: true
 *       content:
 *         application/json:
 *           schema:
 *             type: object
 *             required:
 *               - sender
 *               - receiver
 *             properties:
 *               sender:
 *                 type: string
 *                 description: ID del usuario que envía la solicitud
 *               receiver:
 *                 type: string
 *                 description: ID del usuario que recibe la solicitud
 *     responses:
 *       201:
 *         description: Solicitud de amistad creada exitosamente
 *       400:
 *         description: Datos inválidos
 *       403:
 *         description: No autorizado para realizar esta operación
 */

/**
 * @swagger
 * /accept/{id}:
 *   patch:
 *     summary: Aceptar una solicitud de amistad
 *     tags: [Solicitudes de Amistad]
 *     security:
 *       - bearerAuth: []
 *     parameters:
 *       - in: path
 *         name: id
 *         required: true
 *         description: ID de la solicitud de amistad
 *         schema:
 *           type: string
 *     responses:
 *       200:
 *         description: Solicitud de amistad aceptada exitosamente
 *       403:
 *         description: No autorizado para realizar esta operación
 *       404:
 *         description: Solicitud de amistad no encontrada
 */

/**
 * @swagger
 * /reject/{id}:
 *   patch:
 *     summary: Rechazar una solicitud de amistad
 *     tags: [Solicitudes de Amistad]
 *     security:
 *       - bearerAuth: []
 *     parameters:
 *       - in: path
 *         name: id
 *         required: true
 *         description: ID de la solicitud de amistad
 *         schema:
 *           type: string
 *     responses:
 *       200:
 *         description: Solicitud de amistad rechazada exitosamente
 *       403:
 *         description: No autorizado para realizar esta operación
 *       404:
 *         description: Solicitud de amistad no encontrada
 */

/**
 * @swagger
 * /{id}:
 *   get:
 *     summary: Obtener una solicitud de amistad por ID
 *     tags: [Solicitudes de Amistad]
 *     security:
 *       - bearerAuth: []
 *     parameters:
 *       - in: path
 *         name: id
 *         required: true
 *         description: ID de la solicitud de amistad
 *         schema:
 *           type: string
 *     responses:
 *       200:
 *         description: Solicitud de amistad obtenida exitosamente
 *       403:
 *         description: No autorizado para realizar esta operación (solo admins)
 *       404:
 *         description: Solicitud de amistad no encontrada
 *
 *   delete:
 *     summary: Eliminar una solicitud de amistad por ID
 *     tags: [Solicitudes de Amistad]
 *     security:
 *       - bearerAuth: []
 *     parameters:
 *       - in: path
 *         name: id
 *         required: true
 *         description: ID de la solicitud de amistad
 *         schema:
 *           type: string
 *     responses:
 *       200:
 *         description: Solicitud de amistad eliminada exitosamente
 *       403:
 *         description: No autorizado para realizar esta operación (solo admins)
 *       404:
 *         description: Solicitud de amistad no encontrada
 */