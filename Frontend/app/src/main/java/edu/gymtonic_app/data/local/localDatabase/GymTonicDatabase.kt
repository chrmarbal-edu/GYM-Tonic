package edu.gymtonic_app.data.local.localDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import edu.gymtonic_app.data.local.localDao.*
import edu.gymtonic_app.data.local.model.*

@Database(
    entities = [
        UserEntity::class,
        ExerciseEntity::class,
        RoutineEntity::class,
        MissionEntity::class,
        GroupEntity::class,          // ahora es "grupos"
        RoutineExerciseEntity::class,
        UserRoutineEntity::class,
        GroupUserEntity::class,
        FriendEntity::class,
        FrequestEntity::class,
        UserMissionEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class GymTonicDatabase : RoomDatabase() {

    // DAOs principales
    abstract fun userDao(): UserDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun routineDao(): RoutineDao
    abstract fun missionDao(): MissionDao
    abstract fun groupDao(): GroupDao

    // DAOs de relaciones
    abstract fun routineExerciseDao(): RoutineExerciseDao
    abstract fun userRoutineDao(): UserRoutineDao
    abstract fun groupUserDao(): GroupUserDao
    abstract fun friendDao(): FriendDao
    abstract fun frequestDao(): FrequestDao
    abstract fun userMissionDao(): UserMissionDao

    companion object {
        @Volatile
        private var INSTANCE: GymTonicDatabase? = null

        fun getInstance(context: Context): GymTonicDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GymTonicDatabase::class.java,
                    "gymtonic.db"
                )
                    .fallbackToDestructiveMigration() // Solo para desarrollo
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}