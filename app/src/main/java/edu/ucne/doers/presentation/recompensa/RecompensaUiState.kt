package edu.ucne.doers.presentation.recompensa

import edu.ucne.doers.data.local.entity.RecompensaEntity
import edu.ucne.doers.data.local.model.CondicionRecompensa
import edu.ucne.doers.data.local.model.EstadoRecompensa

data class RecompensaUiState(
    val recompensaId: Int = 0,
    val padreId: String = "",
    val descripcion: String = "",
    val imagenURL: String = "",
    val puntosNecesarios: Int = 0,
    val estado: EstadoRecompensa = EstadoRecompensa.PENDIENTE,
    val condicion: CondicionRecompensa = CondicionRecompensa.ACTIVA,
    val errorMessage: String? = null,
    val listaRecompensas: List<RecompensaEntity> = emptyList(),
    val isLoading: Boolean = false,
)