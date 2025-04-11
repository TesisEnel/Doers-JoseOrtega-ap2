package edu.ucne.doers.data.remote.dto

import edu.ucne.doers.data.local.model.EstadoRecompensa

data class CanjeoDto(
    val canjeoId: Int,
    val hijoId: Int,
    val recompensaId: Int,
    val fecha: String,
    val estado : EstadoRecompensa
)