package edu.ucne.doers.presentation.tareas

import edu.ucne.doers.data.local.entity.TareaEntity
import edu.ucne.doers.data.local.model.EstadoTarea

data class TareaUiState(
    val tareaId: Int = 0,
    val descripcion: String = "",
    val puntos: Int = 0,
    val padreId: String,
    val imagenURL: String = "",
    val estado: EstadoTarea = EstadoTarea.PENDIENTE,
    val listaTareas: List<TareaEntity> = emptyList(),
    val errorMessage: String? = null,
    val isLoanding: Boolean = false
)