package edu.gymtonic_app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import edu.gymtonic_app.data.local.localModel.ExerciseEntity
import kotlinx.coroutines.flow.Flow
import edu.gymtonic_app.data.local.localModel.RoutineExerciseEntity

@Dao
interface RoutineExerciseDao {

    // -------------------------
    // READ - Obtener ejercicios de una rutina
    // -------------------------

    /**
     * Obtiene todos los IDs de ejercicios de una rutina
     * Retorna Flow para observar cambios
     */
    @Query("""
        SELECT routine_x_exercise_exerciseid FROM routine_x_exercise
        WHERE routine_x_exercise_routineid = :routineId
        ORDER BY routine_x_exercise_id ASC
    """)
    fun getExerciseIdsForRoutine(routineId: Int): Flow<List<Int>>

    /**
     * Obtiene todos los ejercicios (entidades completas) de una rutina
     * Join con tabla exercises
     */
    @Query("""
        SELECT e.* FROM exercises e
        INNER JOIN routine_x_exercise rxe 
            ON e.exercise_id = rxe.routine_x_exercise_exerciseid
        WHERE rxe.routine_x_exercise_routineid = :routineId
        ORDER BY rxe.routine_x_exercise_id ASC
    """)
    fun getExercisesForRoutine(routineId: Int): Flow<List<ExerciseEntity>>

    /**
     * Obtiene una relación específica
     */
    @Query("""
        SELECT * FROM routine_x_exercise
        WHERE routine_x_exercise_routineid = :routineId 
        AND routine_x_exercise_exerciseid = :exerciseId
        LIMIT 1
    """)
    suspend fun getRelationship(routineId: Int, exerciseId: Int): RoutineExerciseEntity?

    /**
     * Cuenta cuántos ejercicios tiene una rutina
     */
    @Query("""
        SELECT COUNT(*) FROM routine_x_exercise
        WHERE routine_x_exercise_routineid = :routineId
    """)
    fun countExercisesInRoutine(routineId: Int): Flow<Int>

    // -------------------------
    // WRITE - Crear relaciones
    // -------------------------

    /**
     * Inserta una relación ejercicio-rutina
     * Retorna el ID de la relación insertada
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun linkExerciseToRoutine(relation: RoutineExerciseEntity): Long

    /**
     * Inserta múltiples relaciones (batch)
     * Usado cuando se crea rutina con múltiples ejercicios
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun linkMultipleExercisesToRoutine(relations: List<RoutineExerciseEntity>): List<Long>

    // -------------------------
    // WRITE - Eliminar relaciones
    // -------------------------

    /**
     * Elimina un ejercicio de una rutina
     */
    @Query("""
        DELETE FROM routine_x_exercise
        WHERE routine_x_exercise_routineid = :routineId 
        AND routine_x_exercise_exerciseid = :exerciseId
    """)
    suspend fun unlinkExerciseFromRoutine(routineId: Int, exerciseId: Int): Int

    /**
     * Elimina TODOS los ejercicios de una rutina
     * Usado cuando se borra una rutina
     */
    @Query("""
        DELETE FROM routine_x_exercise
        WHERE routine_x_exercise_routineid = :routineId
    """)
    suspend fun deleteAllExercisesForRoutine(routineId: Int): Int

    /**
     * Elimina una relación completa
     */
    @Delete
    suspend fun deleteRelationship(relation: RoutineExerciseEntity): Int
}