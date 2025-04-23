package edu.ucne.doers.data.remote.dto

import com.squareup.moshi.Json

data class PadreDto(
    val padreId: String,
    val nombre: String,
    @Json(name = "fotoPerfilUrl")
    val profilePictureUrl: String?,
    val email: String,
    val codigoSala: String
)