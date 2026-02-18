package edu.gymtonic_app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import edu.gymtonic_app.data.model.RoutineExerciseEntity

@Dao
interface RoutineExerciseDao {

    // -------------------------
    // INSERT
    // -------------------------

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRelation(relation: RoutineExerciseEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRelations(relations: List<RoutineExerciseEntity>): List<Long>

    // -------------------------
    // DELETE
    // -------------------------

    @Delete
    suspend fun deleteRelation(relation: RoutineExerciseEntity): Int

    // Quitar un ejercicio de una rutina
    @Query("""
        DELETE FROM routine_x_exercise
        WHERE routine_x_exercise_routineid = :routineId
          AND routine_x_exercise_exerciseid = :exerciseId
    """)
    suspend fun removeExerciseFromRoutine(routineId: Int, exerciseId: Int): Int

    // Quitar TODOS los ejercicios de una rutina
    @Query("""
        DELETE FROM routine_x_exercise
        WHERE routine_x_exercise_routineid = :routineId
    """)
    suspend fun removeAllExercisesFromRoutine(routineId: Int): Int

    // Quitar una rutina de TODOS los ejercicios (poco común, pero útil si borras rutina)
    @Query("""
        DELETE FROM routine_x_exercise
        WHERE routine_x_exercise_routineid = :routineId
    """)
    suspend fun removeRoutine(routineId: Int): Int

    // -------------------------
    // QUERIES (FLOW)
    // -------------------------

    @Query("SELECT * FROM routine_x_exercise")
    fun getAllRelations(): Flow<List<RoutineExerciseEntity>>

    // Relaciones de una rutina
    @Query("""
        SELECT * FROM routine_x_exercise
        WHERE routine_x_exercise_routineid = :routineId
    """)
    fun getExercisesOfRoutine(routineId: Int): Flow<List<RoutineExerciseEntity>>

    // Relaciones de un ejercicio
    @Query("""
        SELECT * FROM routine_x_exercise
        WHERE routine_x_exercise_exerciseid = :exerciseId
    """)
    fun getRoutinesOfExercise(exerciseId: Int): Flow<List<RoutineExerciseEntity>>

    // -------------------------
    // UTILS
    // -------------------------

    // Comprobar si existe la relación (evitar duplicados manualmente)
    @Query("""
        SELECT COUNT(*) FROM routine_x_exercise
        WHERE routine_x_exercise_routineid = :routineId
          AND routine_x_exercise_exerciseid = :exerciseId
    """)
    suspend fun relationExists(routineId: Int, exerciseId: Int): Int

    // Contar ejercicios de una rutina
    @Query("""
        SELECT COUNT(*) FROM routine_x_exercise
        WHERE routine_x_exercise_routineid = :routineId
    """)
    fun countExercisesInRoutine(routineId: Int): Flow<Int>
}