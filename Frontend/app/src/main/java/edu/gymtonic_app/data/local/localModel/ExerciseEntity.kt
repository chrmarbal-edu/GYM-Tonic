package edu.gymtonic_app.data.local.localModel

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "exercises")
data class ExerciseEntity(
    @SerializedName("exercise_id")
    @PrimaryKey(autoGenerate = true)
    val exercise_id: Int = 0,

    @SerializedName("exercise_name")
    val exercise_name: String,

    @SerializedName("exercise_description")
    val exercise_description: String,

    @SerializedName("exercise_type")
    val exercise_type: Int,

    @SerializedName("    (exercise_name, exercise_description, exercise_type, exercise_video, exercise_image)\n")
    val exercise_video: String? = null,

    @SerializedName("exercise_image")
    val exercise_image: String? = null,

    @Ignore
    //Para guardar el favorito en la local, boolean como en clase
    val is_favorite: Boolean = false
)