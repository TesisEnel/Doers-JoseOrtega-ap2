package edu.ucne.doers.data.remote.dto

import edu.ucne.doers.data.local.model.EstadoTareaHijo
import java.util.Date

data class CanjeoDto(
    val canjeoId: Int,
    val hijoId: Int,
    val recompensaId: Int,
    val fecha: Date,
    val estado : EstadoTareaHijo
)