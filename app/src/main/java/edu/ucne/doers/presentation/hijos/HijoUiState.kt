package edu.ucne.doers.presentation.hijos

import edu.ucne.doers.data.local.entity.HijoEntity
import edu.ucne.doers.data.local.entity.RecompensaEntity
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
    val listaRecompensas: List<RecompensaEntity> = emptyList(),
    val listaRecompensasFiltradas: List<RecompensaEntity> = emptyList(),
    val successMessage: String? = null,
    val ultimaAccionProcesada: Int? = null
)

fun HijoEntity.toUiState() = HijoUiState(
    hijoId = this.hijoId,
    padreId = this.padreId,
    nombre = this.nombre,
    fotoPerfil = this.fotoPerfil,
    saldoActual = this.saldoActual,
    balance = this.balance
)

fun HijoUiState.toEntity(): HijoEntity? {
    return hijoId?.let {
        HijoEntity(
            hijoId = hijoId,
            padreId = this.padreId ?: "",
            nombre = this.nombre ?: "",
            saldoActual = this.saldoActual,
            balance = this.balance,
            fotoPerfil = this.fotoPerfil
        )
    }
}