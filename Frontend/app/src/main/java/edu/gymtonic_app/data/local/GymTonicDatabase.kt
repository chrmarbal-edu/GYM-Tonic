package edu.gymtonic_app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import edu.gymtonic_app.data.local.dao.*
import edu.gymtonic_app.data.local.localModel.ExerciseEntity
import edu.gymtonic_app.data.local.localModel.social.FrequestEntity
import edu.gymtonic_app.data.local.localModel.social.FriendEntity
import edu.gymtonic_app.data.local.localModel.group.GroupEntity
import edu.gymtonic_app.data.local.localModel.group.GroupUserEntity
import edu.gymtonic_app.data.local.localModel.MissionEntity
import edu.gymtonic_app.data.local.localModel.rutine.RoutineEntity
import edu.gymtonic_app.data.local.localModel.routineExercise.RoutineExerciseEntity
import edu.gymtonic_app.data.local.localModel.user.UserEntity
import edu.gymtonic_app.data.local.localModel.userMission.UserMissionEntity
import edu.gymtonic_app.data.local.localModel.userRoutine.UserRoutineEntity

@Database(
    entities = [
        UserEntity::class,
        ExerciseEntity::class,
        RoutineEntity::class,
        MissionEntity::class,
        GroupEntity::class,
        RoutineExerciseEntity::class,
        UserRoutineEntity::class,
        GroupUserEntity::class,
        FriendEntity::class,
        FrequestEntity::class,
        UserMissionEntity::class,
    ],
    version = 6,
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
