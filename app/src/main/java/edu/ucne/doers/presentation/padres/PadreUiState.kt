package edu.ucne.doers.presentation.padres

import edu.ucne.doers.data.local.entity.PadreEntity

data class PadreUiState (
    val padreId: String? = null,
    val nombre: String? = "",
    val email: String? = "",
    val fotoPerfil: String? = "",
    val codigoSala: String? = "",
    val confirmarContrasena: String? = "",
    val padres: List<PadreEntity> = emptyList(),
    val isSuccess: Boolean = false,
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null
)