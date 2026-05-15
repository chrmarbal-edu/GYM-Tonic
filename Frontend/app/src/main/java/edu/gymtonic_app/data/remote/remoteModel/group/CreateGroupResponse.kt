package edu.gymtonic_app.data.remote.remoteModel.group

import com.google.gson.annotations.SerializedName

/**
 * Soporta respuesta plana del grupo o el formato legacy `{ "datosGrupoCreado": { ... } }`.
 */
data class CreateGroupResponse(
    @SerializedName("datosGrupoCreado")
    val datosGrupoCreado: GroupDto? = null,
    @SerializedName("group_id")
    val group_id: Int? = null,
    @SerializedName("group_name")
    val group_name: String? = null,
    @SerializedName("group_description")
    val group_description: String? = null,
    @SerializedName("group_image")
    val group_image: String? = null,
    @SerializedName("group_points")
    val group_points: Int? = null,
    @SerializedName("group_creator_id")
    val group_creator_id: Int? = null
) {
    fun toGroupDto(): GroupDto {
        datosGrupoCreado?.let { return it }

        val id = group_id
        require(id != null && id > 0) { "Respuesta inválida al crear el grupo" }

        return GroupDto(
            group_id = id,
            group_name = group_name,
            group_description = group_description,
            group_image = group_image,
            group_points = group_points ?: 0,
            group_creator_id = group_creator_id ?: 0
        )
    }
}
