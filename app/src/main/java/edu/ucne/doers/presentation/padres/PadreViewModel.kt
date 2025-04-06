package edu.ucne.doers.presentation.padres

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.doers.data.local.entity.HijoEntity
import edu.ucne.doers.data.local.entity.PadreEntity
import edu.ucne.doers.data.local.entity.RecompensaEntity
import edu.ucne.doers.data.local.entity.TareaEntity
import edu.ucne.doers.data.local.entity.TareaHijo
import edu.ucne.doers.data.local.model.EstadoRecompensa
import edu.ucne.doers.data.local.model.EstadoTarea
import edu.ucne.doers.data.local.model.EstadoTareaHijo
import edu.ucne.doers.data.repository.AuthRepository
import edu.ucne.doers.data.repository.HijoRepository
import edu.ucne.doers.data.repository.PadreRepository
import edu.ucne.doers.data.repository.TareaHijoRepository
import edu.ucne.doers.data.repository.TareaRepository
import edu.ucne.doers.presentation.extension.collectFirstOrNull
import edu.ucne.doers.presentation.sign_in.GoogleAuthUiClient
import edu.ucne.doers.presentation.sign_in.SignInResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class PadreViewModel @Inject constructor(
    private val padreRepository: PadreRepository,
    private val hijoRepository: HijoRepository,
    private val tareaHijoRepository: TareaHijoRepository,
    private val tareaRepository: TareaRepository,
    private val authRepository: AuthRepository,
    private val googleAuthUiClient: GoogleAuthUiClient
) : ViewModel() {
    private val _uiState = MutableStateFlow(PadreUiState())
    val uiState = _uiState.asStateFlow()

    private val _hijos = MutableStateFlow<List<HijoEntity>>(emptyList())
    val hijos: StateFlow<List<HijoEntity>> = _hijos.asStateFlow()

    private val _tareasHijo = MutableStateFlow<List<TareaHijo>>(emptyList())
    val tareasHijo: StateFlow<List<TareaHijo>> = _tareasHijo.asStateFlow()

    private val _recompensasPendientesMap = MutableStateFlow<Map<String, List<RecompensaEntity>>>(emptyMap())
    val recompensasPendientesMap: StateFlow<Map<String, List<RecompensaEntity>>> = _recompensasPendientesMap.asStateFlow()

    private val _tareas = MutableStateFlow<List<TareaEntity>>(emptyList())
    val tareas: StateFlow<List<TareaEntity>> = _tareas.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    init {
        viewModelScope.launch { checkAuthenticatedUser() }
    }

    private fun getHijosByPadre(padreId: String) {
        viewModelScope.launch {
            hijoRepository.getAll().collect { allHijos ->
                val hijosDelPadre = allHijos.filter { it.padreId == padreId }
                _hijos.value = hijosDelPadre
                cargarRecompensasPendientesPorHijo(padreId)
            }
        }
    }

    private fun cargarRecompensasPendientesPorHijo(padreId: String) {
        viewModelScope.launch {
            val hijosConRecompensas = hijoRepository.getHijosConRecompensasByPadreId(padreId)
            val recompensasMap = hijosConRecompensas.associate { entry ->
                entry.hijo.hijoId.toString() to entry.recompensas.filter {
                    it.estado == EstadoRecompensa.PENDIENTE.toString()
                }
            }
            _recompensasPendientesMap.value = recompensasMap
        }
    }


    private fun getTareasHijo() {
        viewModelScope.launch {
            tareaHijoRepository.getAll().collect { tareas ->
                _tareasHijo.value = tareas
            }
        }
    }

    fun validarTarea(tarea: TareaHijo) {
        viewModelScope.launch {
            try {
                val tareaAprobada = tarea.copy(
                        estado = EstadoTareaHijo.APROBADA
                        )
                tareaHijoRepository.save(tareaAprobada)

                val hijo = hijoRepository.find(tarea.hijoId ?: 0)
                val tareaOriginal = tareaRepository.find(tarea.tareaId)

                if (hijo != null && tareaOriginal != null) {
                    val actualizado = hijo.copy(saldoActual = hijo.saldoActual + tareaOriginal.puntos)
                    hijoRepository.save(actualizado)

                    _toastMessage.value = "Tarea validada con éxito"
                }

                // ✅ ACTUALIZA EL CONTADOR Y ESTADO
                val tareaPadre = tareaRepository.find(tarea.tareaId)
                if (tareaPadre != null) {
                    val tareaActualizada = tareaPadre.copy(
                        estado = EstadoTarea.COMPLETADA
                    )
                    tareaRepository.save(tareaActualizada)
                }

                // Actualiza el flujo en pantalla
                getTareasHijo()

            } catch (e: Exception) {
                Log.e("PadreViewModel", "Error al validar tarea: ${e.message}")
            }
        }
    }

    fun rechazarTarea(tarea: TareaHijo) {
        viewModelScope.launch {
            try {
                tareaHijoRepository.delete(tarea)
                _toastMessage.value = "Tarea rechazada con éxito"

                // Eliminar de la lista local
                _tareasHijo.update { tareas ->
                    tareas.filterNot { it.tareaHijoId == tarea.tareaHijoId }
                }
                getTareasHijo()

            } catch (e: Exception) {
                Log.e("PadreViewModel", "Error al rechazar tarea: ${e.message}")
            }
        }
    }

    fun clearToast() {
        _toastMessage.value = null
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

                    // AQUÍ LLAMAS LOS MÉTODOS PARA CARGAR DATOS
                    getHijosByPadre(currentUser.userId)
                    getTareasHijo()
                    cargarRecompensasPendientesPorHijo(currentUser.userId)

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

    /*private suspend fun generateUniqueRoomCode(): String {
        var code: String
        var existingPadre: PadreEntity?

        do {
            code = UUID.randomUUID().toString().substring(0, 6).uppercase()

            // Usar firstOrNull y emptyList con tipo explícito
            val result = padreRepository.getAll().firstOrNull()
            val padres = result?.data ?: emptyList<PadreEntity>()

            existingPadre = padres.find { it.codigoSala == code }

        } while (existingPadre != null)

        return code
    }

     */


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