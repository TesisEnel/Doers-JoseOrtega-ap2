package edu.ucne.doers.presentation.hijos

import edu.ucne.doers.data.local.entity.HijoEntity

data class HijoUiState(
    val hijoId: Int? = null,
    val padreId: String? = "",
    val nombre: String? = "",
    val codigoSala: String? = "",
    val hijos: List<HijoEntity> = emptyList(),
    val isSuccess: Boolean = false,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null,
    val errorMessage: String? = null,
    val isAuthenticated: Boolean = false
)

fun HijoEntity.toUiState() = HijoUiState(
    hijoId = hijoId,
    padreId = padreId,
    nombre = nombre,
)

fun HijoUiState.toEntity() = hijoId?.let {
    HijoEntity(
        hijoId = it,
        padreId = it.toString(),
        nombre = it.toString(),
    )
}
