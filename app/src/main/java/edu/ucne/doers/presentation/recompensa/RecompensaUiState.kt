package edu.ucne.doers.presentation.recompensa

import edu.ucne.doers.data.local.model.EstadoRecompensa

data class RecompensaUiState(
    val recompensaId: Int = 0,
    val padreId: String = "",
    val hijoId: Int = 0,
    val descripcion: String = "",
    val imagenURL: String = "",
    val puntosNecesarios: Int = 0,
    val estado: EstadoRecompensa = EstadoRecompensa.DISPONIBLE,
    val errorMessage: String? = null,
    val recompensas: List<RecompensaUiState> = emptyList(),
    val isLoading: Boolean = false,
)