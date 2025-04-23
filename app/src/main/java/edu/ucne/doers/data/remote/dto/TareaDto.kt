package edu.ucne.doers.data.remote.dto

import edu.ucne.doers.data.local.model.CondicionTarea
import edu.ucne.doers.data.local.model.EstadoTarea
import edu.ucne.doers.data.local.model.PeriodicidadTarea

data class TareaDto(
    val tareaId: Int,
    val padreId: String,
    val descripcion: String,
    val puntos: Int,
    val estado: EstadoTarea,
    val periodicidad: PeriodicidadTarea?,
    val condicion: CondicionTarea
)