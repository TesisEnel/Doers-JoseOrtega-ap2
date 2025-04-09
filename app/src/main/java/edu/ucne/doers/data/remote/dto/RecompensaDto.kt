package edu.ucne.doers.data.remote.dto

import edu.ucne.doers.data.local.model.EstadoRecompensa

data class RecompensaDto(
    val recompensaId: Int,
    val padreId: String,
    val descripcion: String,
    val imagenURL: String,
    val puntosNecesarios: Int,
    val estado: EstadoRecompensa
)