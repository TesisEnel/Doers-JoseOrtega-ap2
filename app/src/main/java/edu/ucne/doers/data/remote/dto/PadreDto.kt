package edu.ucne.doers.data.remote.dto

data class PadreDto(
    val padreId: String,
    val nombre: String,
    val profilePictureUrl: String?,
    val email: String,
    val codigoSala: String
)