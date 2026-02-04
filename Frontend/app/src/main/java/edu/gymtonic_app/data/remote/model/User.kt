package edu.gymtonic_app.data.remote.model


import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("user_birthdate")
    val userBirthdate: String,
    @SerializedName("user_email")
    val userEmail: String,
    @SerializedName("user_height")
    val userHeight: Int,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("user_name")
    val userName: String,
    @SerializedName("user_objective")
    val userObjective: Int,
    @SerializedName("user_password")
    val userPassword: String,
    @SerializedName("user_points")
    val userPoints: Int,
    @SerializedName("user_role")
    val userRole: Int,
    @SerializedName("user_username")
    val userUsername: String,
    @SerializedName("user_weight")
    val userWeight: Int
)