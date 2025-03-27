package edu.ucne.doers.presentation.padres

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.doers.data.local.entity.PadreEntity
import edu.ucne.doers.data.repository.AuthRepository
import edu.ucne.doers.data.repository.PadreRepository
import edu.ucne.doers.presentation.extension.collectFirstOrNull
import edu.ucne.doers.presentation.sign_in.GoogleAuthUiClient
import edu.ucne.doers.presentation.sign_in.SignInResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
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

                val existingPadre = result.data?.userId?.let { padreRepository.find(it) }
                val codigoSala = if (existingPadre != null && existingPadre.codigoSala.isNotEmpty()) {
                    existingPadre.codigoSala
                } else {
                    val newCode = generateUniqueRoomCode()
                    newCode
                }

                _uiState.update {
                    it.copy(
                        isSignInSuccessful = result.data != null,
                        signInError = result.errorMessage,
                        padreId = result.data?.userId,
                        nombre = result.data?.userName ?: "Padre",
                        email = result.data?.email,
                        fotoPerfil = result.data?.profilePictureUrl,
                        codigoSala = codigoSala,
                        isLoading = false
                    )
                }
                if (result.data != null) {
                    guardarPadre()
                }
            } catch (e: Exception) {
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
            } catch (e: Exception) {
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
                if (currentUser != null) {
                    val existingPadre = padreRepository.find(currentUser.userId)
                    val codigoSala = if (existingPadre != null && existingPadre.codigoSala.isNotEmpty()) {
                        existingPadre.codigoSala
                    } else {
                        val newCode = generateUniqueRoomCode()
                        newCode
                    }

                    _uiState.update {
                        it.copy(
                            isSignInSuccessful = true,
                            padreId = currentUser.userId,
                            nombre = currentUser.userName ?: "Padre",
                            email = currentUser.email,
                            fotoPerfil = currentUser.profilePictureUrl,
                            codigoSala = codigoSala,
                            signInError = null
                        )
                    }
                    guardarPadre()
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
            val isAuthenticated = user?.userId != null
            isAuthenticated
        } catch (e: Exception) {
            false
        }
    }

    private suspend fun generateUniqueRoomCode(): String {
        var code: String
        do {
            code = UUID.randomUUID().toString().substring(0, 6).uppercase()
            val padres = padreRepository.getAll().collectFirstOrNull() ?: emptyList()
            val existingPadre = padres.find { it.codigoSala == code }
        } while (existingPadre != null)
        return code
    }

    private suspend fun guardarPadre() {
        padreRepository.save(_uiState.value.toEntity())
    }
}

fun PadreUiState.toEntity() = PadreEntity(
    padreId = this.padreId ?: "",
    nombre = nombre ?: "",
    email = this.email ?: "",
    profilePictureUrl = this.fotoPerfil ?: "",
    codigoSala = this.codigoSala ?: ""
)