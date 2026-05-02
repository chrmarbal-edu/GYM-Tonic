/**
 * @swagger
 * tags:
 *   name: Ejercicios
 *   description: API para gestionar ejercicios
 */

/**
 * @swagger
 * /exercises:
 *   get:
 *     summary: Obtener todos los ejercicios
 *     tags: [Ejercicios]
 *     security:
 *       - bearerAuth: []
 *     responses:
 *       200:
 *         description: Lista de ejercicios obtenida exitosamente
 *       401:
 *         description: Token inválido o faltante
 *       500:
 *         description: Error interno del servidor
 *
 *   post:
 *     summary: Crear un nuevo ejercicio
 *     tags: [Ejercicios]
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
 *               - description
 *             properties:
 *               name:
 *                 type: string
 *                 description: Nombre del ejercicio
 *               description:
 *                 type: string
 *                 description: Descripción del ejercicio
 *               type:
 *                 type: string
 *                 description: Tipo de ejercicio
 *               video:
 *                 type: string
 *                 description: URL del video del ejercicio
 *               image:
 *                 type: string
 *                 description: URL de la imagen del ejercicio
 *     responses:
 *       201:
 *         description: Ejercicio creado exitosamente
 *       400:
 *         description: Datos inválidos
 *       403:
 *         description: No autorizado para realizar esta operación
 *       500:
 *         description: Error interno del servidor
 */

/**
 * @swagger
 * /exercises/{id}:
 *   get:
 *     summary: Obtener un ejercicio por ID
 *     tags: [Ejercicios]
 *     security:
 *       - bearerAuth: []
 *     parameters:
 *       - in: path
 *         name: id
 *         required: true
 *         description: ID del ejercicio
 *         schema:
 *           type: string
 *     responses:
 *       200:
 *         description: Ejercicio obtenido exitosamente
 *       401:
 *         description: Token inválido o faltante
 *       404:
 *         description: Ejercicio no encontrado
 *
 *   patch:
 *     summary: Actualizar un ejercicio por ID
 *     tags: [Ejercicios]
 *     security:
 *       - bearerAuth: []
 *     parameters:
 *       - in: path
 *         name: id
 *         required: true
 *         description: ID del ejercicio
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
 *                 description: Nombre del ejercicio
 *               description:
 *                 type: string
 *                 description: Descripción del ejercicio
 *               type:
 *                 type: string
 *                 description: Tipo de ejercicio
 *               video:
 *                 type: string
 *                 description: URL del video del ejercicio
 *               image:
 *                 type: string
 *                 description: URL de la imagen del ejercicio
 *     responses:
 *       200:
 *         description: Ejercicio actualizado exitosamente
 *       400:
 *         description: Datos inválidos
 *       401:
 *         description: Token inválido o faltante
 *       403:
 *         description: No autorizado para realizar esta operación (solo admins)
 *       404:
 *         description: Ejercicio no encontrado
 *       500:
 *         description: Error interno del servidor
 *
 *   delete:
 *     summary: Eliminar un ejercicio por ID
 *     tags: [Ejercicios]
 *     security:
 *       - bearerAuth: []
 *     parameters:
 *       - in: path
 *         name: id
 *         required: true
 *         description: ID del ejercicio
 *         schema:
 *           type: string
 *     responses:
 *       200:
 *         description: Ejercicio eliminado exitosamente
 *       401:
 *         description: Token inválido o faltante
 *       403:
 *         description: No autorizado para realizar esta operación (solo admins)
 *       404:
 *         description: Ejercicio no encontrado
 *       500:
 *         description: Error interno del servidor
 */