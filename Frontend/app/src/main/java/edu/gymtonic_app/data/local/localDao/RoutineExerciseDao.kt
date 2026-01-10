package edu.gymtonic_app.data.local.localDao

import androidx.room.*
import edu.gymtonic_app.data.local.model.RoutineExerciseEntity

@Dao
interface RoutineExerciseDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutineExercise(routineExercise: RoutineExerciseEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutineExercises(routineExercises: List<RoutineExerciseEntity>)

    @Update
    suspend fun updateRoutineExercise(routineExercise: RoutineExerciseEntity)

    @Delete
    suspend fun deleteRoutineExercise(routineExercise: RoutineExerciseEntity)

    @Query("SELECT * FROM routine_x_exercise WHERE routine_x_exercise_id = :id")
    suspend fun getById(id: Int): RoutineExerciseEntity?

    @Query("SELECT * FROM routine_x_exercise")
    suspend fun getAll(): List<RoutineExerciseEntity>

    @Query("SELECT * FROM routine_x_exercise WHERE routine_x_exercise_routineid = :routineId")
    suspend fun getByRoutineId(routineId: Int): List<RoutineExerciseEntity>
}