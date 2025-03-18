package edu.ucne.doers.presentation.padres

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.identity.SignInClient
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.doers.data.local.entity.PadreEntity
import edu.ucne.doers.data.repository.AuthRepository
import edu.ucne.doers.data.repository.PadreRepository
import edu.ucne.doers.presentation.sign_in.GoogleAuthUiClient
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
    private val googleAuthUiClient: GoogleAuthUiClient
) : ViewModel() {
    private val _uiState = MutableStateFlow(PadreUiState())
    val uiState = _uiState.asStateFlow()

    init {
        //getPadres()
        viewModelScope.launch { checkAuthenticatedUser() }
    }

    private suspend fun checkAuthenticatedUser() {
        if (isAuthenticated()) {
            getCurrentUser()
        }
    }

    fun setLoading(isLoading: Boolean) {
        _uiState.update { it.copy(isLoading = isLoading) }
    }

    fun setSignInError(error: String?) {
        _uiState.update { it.copy(signInError = error) }
    }

    fun onSignInResult(result: SignInResult) {
        viewModelScope.launch {
            try {
                setLoading(true)
                Log.d("PadreViewModel", "onSignInResult: data = ${result.data}, error = ${result.errorMessage}")
                _uiState.update {
                    it.copy(
                        isSignInSuccessful = result.data != null,
                        signInError = result.errorMessage,
                        padreId = result.data?.userId,
                        nombre = result.data?.userName ?: "Padre",
                        email = result.data?.email,
                        fotoPerfil = result.data?.profilePictureUrl,
                        isLoading = false
                    )
                }
                if (result.data != null) {
                    guardarPadre()
                }
                Log.d("PadreViewModel","Después de onSignInResult - Estado final: isLoading = ${_uiState.value.isLoading}, " + "isSignInSuccessful = ${_uiState.value.isSignInSuccessful}, " + "nombre = ${_uiState.value.nombre}")
            } catch (e: Exception) {
                Log.e("PadreViewModel", "Error en onSignInResult: ${e.message}", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        signInError = "Error al procesar el inicio de sesión: ${e.message}"
                    )
                }
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            try {
                setLoading(true)
                googleAuthUiClient.signOut()
                _uiState.update {
                    it.copy(
                        isSignInSuccessful = false,
                        padreId = null,
                        nombre = "",
                        email = null,
                        fotoPerfil = null,
                        signInError = null,
                        isLoading = false
                    )
                }
                padreRepository.delete(_uiState.value.toEntity())
            } catch (e: Exception) {
                Log.e("PadreViewModel", "Error en signOut: ${e.message}", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        signInError = "Error al cerrar sesión: ${e.message}"
                    )
                }
            }
        }
    }

    fun getCurrentUser() {
        viewModelScope.launch {
            try {
                setLoading(true)
                val currentUser = authRepository.getUser()
                Log.d("PadreViewModel", "getCurrentUser - currentUser: $currentUser")
                if (currentUser != null) {
                    _uiState.update {
                        it.copy(
                            isSignInSuccessful = true,
                            padreId = currentUser.userId,
                            nombre = currentUser.userName ?: "Padre",
                            email = currentUser.email,
                            fotoPerfil = currentUser.profilePictureUrl,
                            signInError = null
                        )
                    }
                    Log.d("PadreViewModel", "getCurrentUser - Estado actualizado: isSignInSuccessful = ${_uiState.value.isSignInSuccessful}")
                } else {
                    _uiState.update {
                        it.copy(
                            isSignInSuccessful = false,
                            signInError = "No user logged in"
                        )
                    }
                }
                setLoading(false)
            } catch (e: Exception) {
                Log.e("PadreViewModel", "Error en getCurrentUser: ${e.message}", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        signInError = "Error al obtener el usuario: ${e.message}"
                    )
                }
            }
        }
    }

    suspend fun isAuthenticated(): Boolean {
        return try {
            val user = authRepository.getUser()
            Log.d("PadreViewModel", "isAuthenticated - user: $user")
            val isAuthenticated = user?.userId != null
            Log.d("PadreViewModel", "isAuthenticated - resultado: $isAuthenticated")
            isAuthenticated
        } catch (e: Exception) {
            Log.e("PadreViewModel", "Error en isAuthenticated: ${e.message}", e)
            false
        }
    }

    /*
    Comentado por si deba usarse en un Futuro

    private fun logout() {
        viewModelScope.launch {
            try {
                authRepository.logout()
            } catch (e: Exception) {
                Log.e("PadreViewModel", "Error en logout: ${e.message}", e)
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
    }*/

    private suspend fun guardarPadre() {
        padreRepository.save(_uiState.value.toEntity())
    }

    /*
    Comentado por si deba usarse en un Futuro

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
    }*/
}

fun PadreUiState.toEntity() = PadreEntity(
    padreId = this.padreId ?: "",
    nombre = nombre ?: "",
    email = this.email ?: "",
    profilePictureUrl = this.fotoPerfil ?: "",
    codigoSala = this.codigoSala ?: ""
)