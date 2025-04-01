package edu.ucne.doers.data.remote.dto

data class HijoDto(
    val hijoId: Int,
    val padreId: String,
    val nombre: String,
    val saldoActual: Int,
    val balance: Int,
    val fotoPerfil: String?
)