package edu.ucne.doers.data.remote.dto

import edu.ucne.doers.data.local.model.EstadoCanjeo
import java.util.Date

data class CanjeoDto(
    val canjeoId: Int,
    val hijoId: Int? = null,
    val recompensaId: Int,
    val fecha: Date?,
    val estado : EstadoCanjeo
)