package edu.ucne.doers.data.remote.dto

import edu.ucne.doers.data.local.model.EstadoTareaHijo
import java.util.Date

data class SolicitudRecompensaDto(
    val solicitudId: Int,
    val recompensaId: Int,
    val hijoId: Int,
    val fecha: Date,
    val estado: EstadoTareaHijo
)