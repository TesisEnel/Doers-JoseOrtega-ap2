package edu.ucne.doers.presentation.hijos

import edu.ucne.doers.data.local.entity.HijoEntity

data class HijoUiState(
    val hijoId: Int? = null,
    val nombre: String? = "",
    val codigoSala: String? = "",
    val hijos: List<HijoEntity> = emptyList(),
    val isSuccess: Boolean = false,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null
)
