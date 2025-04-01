package edu.ucne.doers.data.remote.dto

import edu.ucne.doers.data.local.model.EstadoTareaHijo
import java.util.Date

data class TareaHijoDto(
    val tareaHijoId: Int,
    val tareaId: Int,
    val hijoId: Int,
    val estado: EstadoTareaHijo,
    val fechaVerificacion: Date?
)