package edu.ucne.doers.presentation.hijos

import edu.ucne.doers.data.local.entity.HijoEntity
import edu.ucne.doers.data.local.entity.TareaEntity

data class HijoUiState(
    val hijoId: Int? = null,
    val padreId: String? = "",
    val nombre: String? = "",
    val codigoSala: String? = "",
    val hijos: List<HijoEntity> = emptyList(),
    val fotoPerfil: String? = "",
    val isSuccess: Boolean = false,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null,
    val errorMessage: String? = null,
    val isAuthenticated: Boolean = false,
    val saldoActual: Int = 0,
    val balance: Int = 0,
    val listaTareas: List<TareaEntity> = emptyList(),
    val listaTareasFiltradas: List<TareaEntity> = emptyList(),
    val successMessage: String? = null,
    val ultimaTareaProcesada: Int? = null
)

fun HijoEntity.toUiState() = HijoUiState(
    hijoId = hijoId,
    padreId = padreId,
    nombre = nombre,
    fotoPerfil = fotoPerfil,
    saldoActual = saldoActual,
    balance = balance
)

fun HijoUiState.toEntity(): HijoEntity? {
    return hijoId?.let {
        HijoEntity(
            hijoId = it,
            padreId = padreId ?: "",
            nombre = nombre ?: "",
            saldoActual = saldoActual,
            balance = balance,
            fotoPerfil = fotoPerfil
        )
    }
}