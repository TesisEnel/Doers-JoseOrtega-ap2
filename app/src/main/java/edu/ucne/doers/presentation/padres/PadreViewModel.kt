package edu.ucne.doers.presentation.padres

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.doers.data.local.entity.PadreEntity
import edu.ucne.doers.data.repository.AuthRepository
import edu.ucne.doers.data.repository.PadreRepository
import edu.ucne.doers.presentation.sign_in.SignInResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PadreViewModel @Inject constructor(
    private val padreRepository: PadreRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(PadreUiState())
    val uiState = _uiState.asStateFlow()

    init {
        getPadres()
    }

    fun onSignInResult(result: SignInResult) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isSignInSuccessful = result.data != null,
                    signInError = result.errorMessage,
                    padreId = result.data?.userId,
                    nombre = result.data?.userName,
                    email = result.data?.email,
                    fotoPerfil = result.data?.profilePictureUrl,
                )
            }
            val padre = _uiState.value.padres.find { result ->
                result.email == _uiState.value.email
            }
            if (padre == null) {
                padreRepository.save(_uiState.value.toEntity())
            }
        }
    }

    private fun getPadres() {
        viewModelScope.launch {
            padreRepository.getAll().collect { padres ->
                _uiState.update {
                    it.copy(padres = padres)
                }
            }
        }
    }

    fun getCurrentUser() {
        viewModelScope.launch {
            val currentUser = authRepository.getUser()
            val usuarioActual = padreRepository.findEmail(currentUser ?: "")

            _uiState.update {
                it.copy(
                    nombre = usuarioActual?.nombre,
                    email = usuarioActual?.email,
                    fotoPerfil = usuarioActual?.profilePictureUrl,
                    codigoSala = usuarioActual?.codigoSala
                )
            }
        }
    }

    fun isAuthenticated(): Boolean {
        return authRepository.getUser() != null
    }

    private fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

    private fun updateUiState(update: (PadreUiState) -> PadreUiState) {
        _uiState.update(update)
    }

    private fun handleSelectedPadre(usuarioId: String) {
        viewModelScope.launch {
            cargarPadreSeleccionado(usuarioId)
        }
    }

    private fun handleDeleteEvent() {
        viewModelScope.launch {
            padreRepository.delete(_uiState.value.toEntity())
        }
    }

    private suspend fun guardarPadre() {
        padreRepository.save(_uiState.value.toEntity())
    }

    private fun cargarPadreSeleccionado(padreId: String) = viewModelScope.launch {
        val padre = padreRepository.find(padreId)
        if (padre != null) {
            _uiState.update {
                it.copy(
                    padreId = padre.padreId,
                    nombre = padre.nombre,
                    email = padre.email,
                    fotoPerfil = padre.profilePictureUrl,
                    codigoSala = padre.codigoSala
                )
            }
        }
    }

    private fun PadreUiState.toEntity() = PadreEntity(
        padreId = this.padreId ?: "",
        nombre = nombre ?: "",
        email = this.email ?: "",
        profilePictureUrl = this.fotoPerfil ?: "",
        codigoSala = this.codigoSala ?: ""
    )
}