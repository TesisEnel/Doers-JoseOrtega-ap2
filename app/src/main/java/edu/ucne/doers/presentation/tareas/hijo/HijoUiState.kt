package edu.ucne.doers.presentation.tareas.hijo

import edu.ucne.doers.data.local.entity.TareaEntity
import edu.ucne.doers.data.local.model.EstadoTareaHijo

data class HijoUiState (
    val hijoId: Int = 0,
    val padreId: String,
    val nombre: String = "",
    val estado: EstadoTareaHijo = EstadoTareaHijo.PENDIENTE_VERIFICACION,
    val saldoActual: Int = 0,
    val balance: Int = 0,
    val listaTareas: List<TareaEntity> = emptyList(),
    val listaTareasFiltradas: List<TareaEntity> = emptyList(),
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isLoading: Boolean = false,
    val ultimaTareaProcesada: Int? = null,
    val isAuthenticated: Boolean = false,
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null,
    val isSuccess: Boolean = false
)