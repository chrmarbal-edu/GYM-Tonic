package edu.gymtonic_app.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import edu.gymtonic_app.data.local.localModel.routineExercise.RoutineExerciseEntity
import edu.gymtonic_app.data.local.localModel.routineExercise.RoutineExerciseWithExerciseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineExerciseDao {

    @Query("""
        SELECT e.*, rxe.routine_x_exercise_reps
        FROM exercises e
        INNER JOIN routine_x_exercise rxe
            ON e.exercise_id = rxe.routine_x_exercise_exerciseid
        WHERE rxe.routine_x_exercise_routineid = :routineId
        ORDER BY rxe.routine_x_exercise_id ASC
    """)
    fun getExercisesForRoutine(routineId: Int): Flow<List<RoutineExerciseWithExerciseEntity>>

    @Query("""
        SELECT routine_x_exercise_exerciseid FROM routine_x_exercise
        WHERE routine_x_exercise_routineid = :routineId
        ORDER BY routine_x_exercise_id ASC
    """)
    fun getExerciseIdsForRoutine(routineId: Int): Flow<List<Int>>

    @Query("""
        SELECT * FROM routine_x_exercise
        WHERE routine_x_exercise_routineid = :routineId
        AND routine_x_exercise_exerciseid = :exerciseId
        LIMIT 1
    """)
    suspend fun getRelationship(routineId: Int, exerciseId: Int): RoutineExerciseEntity?

    @Query("""
        SELECT COUNT(*) FROM routine_x_exercise
        WHERE routine_x_exercise_routineid = :routineId
    """)
    fun countExercisesInRoutine(routineId: Int): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun linkExerciseToRoutine(relation: RoutineExerciseEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun linkMultipleExercisesToRoutine(relations: List<RoutineExerciseEntity>): List<Long>

    @Query("""
        DELETE FROM routine_x_exercise
        WHERE routine_x_exercise_routineid = :routineId
        AND routine_x_exercise_exerciseid = :exerciseId
    """)
    suspend fun unlinkExerciseFromRoutine(routineId: Int, exerciseId: Int): Int

    @Query("""
        DELETE FROM routine_x_exercise
        WHERE routine_x_exercise_routineid = :routineId
    """)
    suspend fun deleteAllExercisesForRoutine(routineId: Int): Int

    @Delete
    suspend fun deleteRelationship(relation: RoutineExerciseEntity): Int
}