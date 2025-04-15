package edu.ucne.doers.presentation.padres

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.doers.data.local.entity.CanjeoEntity
import edu.ucne.doers.data.local.entity.HijoEntity
import edu.ucne.doers.data.local.entity.PadreEntity
import edu.ucne.doers.data.local.entity.RecompensaEntity
import edu.ucne.doers.data.local.entity.TareaEntity
import edu.ucne.doers.data.local.entity.TareaHijo
import edu.ucne.doers.data.local.entity.TransaccionHijo
import edu.ucne.doers.data.local.model.CondicionRecompensa
import edu.ucne.doers.data.local.model.CondicionTarea
import edu.ucne.doers.data.local.model.EstadoCanjeo
import edu.ucne.doers.data.local.model.EstadoRecompensa
import edu.ucne.doers.data.local.model.EstadoTarea
import edu.ucne.doers.data.local.model.EstadoTareaHijo
import edu.ucne.doers.data.local.model.TipoTransaccion
import edu.ucne.doers.data.remote.Resource
import edu.ucne.doers.data.repository.AuthRepository
import edu.ucne.doers.data.repository.CanjeoRepository
import edu.ucne.doers.data.repository.HijoRepository
import edu.ucne.doers.data.repository.PadreRepository
import edu.ucne.doers.data.repository.RecompensaRepository
import edu.ucne.doers.data.repository.TareaHijoRepository
import edu.ucne.doers.data.repository.TareaRepository
import edu.ucne.doers.data.repository.TransaccionHijoRepository
import edu.ucne.doers.presentation.sign_in.GoogleAuthUiClient
import edu.ucne.doers.presentation.sign_in.SignInResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class PadreViewModel @Inject constructor(
    private val padreRepository: PadreRepository,
    private val hijoRepository: HijoRepository,
    private val tareaHijoRepository: TareaHijoRepository,
    private val tareaRepository: TareaRepository,
    private val recompensaRepository: RecompensaRepository,
    private val canjeoRepository: CanjeoRepository,
    private val authRepository: AuthRepository,
    private val googleAuthUiClient: GoogleAuthUiClient,
    private val transaccionHijoRepository: TransaccionHijoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PadreUiState())
    val uiState = _uiState.asStateFlow()

    private val _hijos = MutableStateFlow<List<HijoEntity>>(emptyList())
    val hijos: StateFlow<List<HijoEntity>> = _hijos.asStateFlow()

    private val _tareasHijo = MutableStateFlow<List<TareaHijo>>(emptyList())
    val tareasHijo: StateFlow<List<TareaHijo>> = _tareasHijo.asStateFlow()

    private val _recompensas = MutableStateFlow<List<RecompensaEntity>>(emptyList())
    val recompensas: StateFlow<List<RecompensaEntity>> = _recompensas.asStateFlow()

    private val _canjeoHijo = MutableStateFlow<List<CanjeoEntity>>(emptyList())
    val canjeoHijo: StateFlow<List<CanjeoEntity>> = _canjeoHijo.asStateFlow()

    private val _tareas = MutableStateFlow<List<TareaEntity>>(emptyList())
    val tareas: StateFlow<List<TareaEntity>> = _tareas.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    init {
        viewModelScope.launch {
            checkAuthenticatedUser()
        }
    }

    private suspend fun checkAuthenticatedUser() {
        if (isAuthenticated()) {
            getCurrentUser()
        }
    }

    fun validarTarea(tarea: TareaHijo) {
        viewModelScope.launch {
            try {
                val tareaAprobada = tarea.copy(
                    estado = EstadoTareaHijo.APROBADA,
                    fechaVerificacion = Date()
                )
                tareaHijoRepository.save(tareaAprobada).collect { result ->
                    when (result) {
                        is Resource.Success -> Log.d(
                            "PadreViewModel",
                            "TareaHijo aprobada guardada"
                        )

                        is Resource.Error -> throw Exception("Error al guardar TareaHijo: ${result.message}")
                        is Resource.Loading -> Log.d("PadreViewModel", "Guardando TareaHijo...")
                    }
                }

                val hijo = hijoRepository.find(tarea.hijoId ?: 0)
                val tareaOriginal = tareaRepository.find(tarea.tareaId)
                if (hijo != null && tareaOriginal != null) {
                    val puntosGanados = tareaOriginal.puntos
                    val hijoActualizado = hijo.copy(saldoActual = hijo.saldoActual + puntosGanados)
                    hijoRepository.save(hijoActualizado).collect { result ->
                        when (result) {
                            is Resource.Success -> {
                                getHijosByPadre(hijo.padreId)
                            }

                            is Resource.Error -> throw Exception("Error al actualizar hijo: ${result.message}")
                            is Resource.Loading -> Log.d("PadreViewModel", "Actualizando saldo...")
                        }
                    }
                    val transaccion = TransaccionHijo(
                        transaccionId = 0,
                        hijoId = hijo.hijoId,
                        tipo = TipoTransaccion.RECIBIDO,
                        monto = puntosGanados,
                        descripcion = "Puntos recibidos por tarea: ${tareaOriginal.descripcion}",
                        fecha = Date()
                    )
                    transaccionHijoRepository.save(transaccion).collect { result ->
                        when (result) {
                            is Resource.Success -> Log.d(
                                "PadreViewModel",
                                "Transacción guardada exitosamente"
                            )
                            is Resource.Error -> Log.e(
                                "PadreViewModel",
                                "Error al guardar transacción: ${result.message}"
                            )
                            is Resource.Loading -> Log.d(
                                "PadreViewModel",
                                "Guardando transacción..."
                            )
                        }
                    }
                } else {
                    throw Exception("Hijo o tarea no encontrados")
                }
                val tareaActualizada = tareaOriginal.copy(
                    estado = EstadoTarea.COMPLETADA,
                    condicion = CondicionTarea.INACTIVA
                )
                tareaRepository.save(tareaActualizada).collect { result ->
                    when (result) {
                        is Resource.Success -> Log.d(
                            "PadreViewModel",
                            "TareaEntity actualizada a COMPLETADA e INACTIVA"
                        )

                        is Resource.Error -> throw Exception("Error al actualizar tarea: ${result.message}")
                        is Resource.Loading -> Log.d("PadreViewModel", "Guardando TareaEntity...")
                    }
                }
                getTareasHijo()
                _toastMessage.value = "Tarea validada con éxito"

            } catch (e: Exception) {
                _errorMessage.value = "Error al validar tarea: ${e.message}"
            }
        }
    }

    fun rechazarTarea(tareaHijo: TareaHijo) {
        viewModelScope.launch {
            try {
                tareaHijoRepository.delete(tareaHijo)
                _toastMessage.value = "Tarea rechazada con éxito"
                _tareasHijo.update { tareas ->
                    tareas.filterNot { it.tareaHijoId == tareaHijo.tareaHijoId }
                }
                getTareasHijo()
            } catch (e: Exception) {
                _errorMessage.value = "Error al rechazar tarea: ${e.message}"
            }
        }
    }

    fun validarRecompensa(canjeo: CanjeoEntity) {
        viewModelScope.launch {
            try {
                val canjeoAprobado = canjeo.copy(
                    estado = EstadoCanjeo.APROBADO,
                    fecha = Date()
                )
                canjeoRepository.save(canjeoAprobado).collect { result ->
                    when (result) {
                        is Resource.Success -> Log.d("PadreViewModel", "Canjeo aprobado guardado")
                        is Resource.Error -> throw Exception("Error al guardar canjeo: ${result.message}")
                        is Resource.Loading -> Log.d("PadreViewModel", "Guardando canjeo...")
                    }
                }

                val hijo = hijoRepository.find(canjeo.hijoId ?: 0)
                val recompensaOriginal = recompensaRepository.find(canjeo.recompensaId)
                if (hijo != null && recompensaOriginal != null) {
                    val puntosGastados = recompensaOriginal.puntosNecesarios
                    val hijoActualizado = hijo.copy(saldoActual = hijo.saldoActual - puntosGastados)
                    hijoRepository.save(hijoActualizado).collect { result ->
                        when (result) {
                            is Resource.Success -> {
                                getHijosByPadre(hijo.padreId)
                            }

                            is Resource.Error -> throw Exception("Error al actualizar hijo: ${result.message}")
                            is Resource.Loading -> Log.d("PadreViewModel", "Actualizando saldo...")
                        }
                    }
                    val transaccion = TransaccionHijo(
                        transaccionId = 0,
                        hijoId = hijo.hijoId,
                        tipo = TipoTransaccion.CONSUMIDO,
                        monto = puntosGastados,
                        descripcion = "Puntos gastados por recompensa: ${recompensaOriginal.descripcion}",
                        fecha = Date()
                    )
                    transaccionHijoRepository.save(transaccion).collect { result ->
                        when (result) {
                            is Resource.Success -> Log.d(
                                "PadreViewModel",
                                "Transacción guardada exitosamente"
                            )
                            is Resource.Error -> Log.e(
                                "PadreViewModel",
                                "Error al guardar transacción: ${result.message}"
                            )
                            is Resource.Loading -> Log.d(
                                "PadreViewModel",
                                "Guardando transacción..."
                            )
                        }
                    }
                } else {
                    throw Exception("Hijo o recompensa no encontrados")
                }
                val recompensaActualizada = recompensaOriginal.copy(
                    estado = EstadoRecompensa.COMPLETADA,
                    condicion = CondicionRecompensa.INACTIVA
                )
                recompensaRepository.save(recompensaActualizada).collect { result ->
                    when (result) {
                        is Resource.Success -> Log.d(
                            "PadreViewModel",
                            "RecompensaEntity actualizada a COMPLETADA e INACTIVA"
                        )
                        is Resource.Error -> throw Exception("Error al actualizar recompensa: ${result.message}")
                        is Resource.Loading -> Log.d("PadreViewModel", "Guardando RecompensaEntity...")
                    }
                }
                getRecompensasHijo()
                _toastMessage.value = "Recompensa validada con éxito"
            } catch (e: Exception) {
                _errorMessage.value = "Error al validar recompensa: ${e.message}"
            }
        }
    }

    fun rechazarRecompensa(canjeo: CanjeoEntity) {
        viewModelScope.launch {
            try {
                canjeoRepository.delete(canjeo)
                _toastMessage.value = "Recompensa rechazada con éxito"
                _canjeoHijo.update { canjeos ->
                    canjeos.filterNot { it.canjeoId == canjeo.canjeoId }
                }
                getRecompensasHijo()
            } catch (e: Exception) {
                _errorMessage.value = "Error al rechazar recompensa: ${e.message}"
            }
        }
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    fun setLoading(isLoading: Boolean) {
        _uiState.update { it.copy(isLoading = isLoading) }
    }

    fun setSignInError(error: String?) {
        _uiState.update { it.copy(signInError = error) }
    }

    fun onSignInResult(result: SignInResult, onSuccessNavigate: () -> Unit) {
        viewModelScope.launch {
            setLoading(true)

            val userId = result.data?.userId ?: run {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        signInError = "Id de usuario inválido",
                        isSignInSuccessful = false
                    )
                }
                return@launch
            }

            val existingPadre = padreRepository.find(userId)
            if (existingPadre != null) {
                _uiState.update {
                    it.copy(
                        isSignInSuccessful = true,
                        signInError = null,
                        padreId = existingPadre.padreId,
                        nombre = existingPadre.nombre,
                        email = existingPadre.email,
                        fotoPerfil = existingPadre.profilePictureUrl,
                        codigoSala = existingPadre.codigoSala,
                        isLoading = false
                    )
                }
                onSuccessNavigate()
                return@launch
            }

            val codigoSala = generateUniqueRoomCode()

            _uiState.update {
                it.copy(
                    isSignInSuccessful = true,
                    signInError = result.errorMessage,
                    padreId = userId,
                    nombre = result.data.userName ?: "Padre",
                    email = result.data.email,
                    fotoPerfil = result.data.profilePictureUrl,
                    codigoSala = codigoSala,
                    isLoading = false
                )
            }

            guardarPadre(onSuccessNavigate)
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

    private fun getCurrentUser() {
        viewModelScope.launch {
            setLoading(true)
            val currentUser = authRepository.getUser()
            if (currentUser != null) {
                val existingPadre = padreRepository.find(currentUser.userId)
                val codigoSala = existingPadre?.codigoSala ?: generateUniqueRoomCode()

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

                getHijosByPadre(currentUser.userId)
                getTareasHijo()
                getRecompensasHijo()
            } else {
                _uiState.update {
                    it.copy(
                        isSignInSuccessful = false,
                        signInError = "No user logged in"
                    )
                }
            }
            setLoading(false)
        }
    }

    suspend fun isAuthenticated(): Boolean {
        return try {
            val user = authRepository.getUser()
            user?.userId != null
        } catch (e: Exception) {
            false
        }
    }

    private suspend fun generateUniqueRoomCode(): String {
        var code: String
        var existsRemotely: Boolean
        var existingPadreLocal: PadreEntity?

        do {
            code = UUID.randomUUID().toString().substring(0, 6).uppercase()
            val localResource = padreRepository.getAll().firstOrNull()
            val padres = if (localResource is Resource.Success) localResource.data else emptyList()
            existingPadreLocal = padres?.find { it.codigoSala == code }
            existsRemotely = try {
                padreRepository.getByCodigoSala(code) != null
            } catch (e: Exception) {
                false
            }
        } while (existingPadreLocal != null || existsRemotely)

        return code
    }

    private suspend fun guardarPadre(onSuccess: () -> Unit) {
        val padre = _uiState.value.toEntity()
        padreRepository.save(padre).collect { result ->
            when (result) {
                is Resource.Loading -> setLoading(true)
                is Resource.Success -> {
                    setLoading(false)
                    _uiState.update { it.copy(isSuccess = true) }
                    onSuccess()
                }

                is Resource.Error -> {
                    setLoading(false)
                    val alreadyExists = result.message?.contains("UNIQUE constraint failed") == true
                    if (alreadyExists) {
                        _uiState.update { it.copy(isSuccess = true) }
                        onSuccess()
                    } else {
                        setSignInError(result.message ?: "Error al guardar padre")
                    }
                }
            }
        }
    }

    fun getHijosByPadre(padreId: String) {
        viewModelScope.launch {
            hijoRepository.getAll().collect { resource ->
                if (resource is Resource.Success) {
                    val hijosDelPadre =
                        resource.data?.filter { it.padreId == padreId } ?: emptyList()
                    _hijos.value = hijosDelPadre
                    getRecompensasHijo()
                } else if (resource is Resource.Error) {
                    _hijos.value = resource.data?.filter { it.padreId == padreId } ?: emptyList()
                }
            }
        }
    }

    private fun getRecompensasHijo() {
        viewModelScope.launch {
            recompensaRepository.getAll().collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                    }

                    is Resource.Success -> {
                        _recompensas.value = resource.data!!
                    }

                    is Resource.Error -> {
                        _uiState.update { it.copy(errorMessage = resource.message) }
                    }
                }
            }

        }
    }

    private fun getTareasHijo() {
        viewModelScope.launch {
            tareaHijoRepository.getAll().collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                    }

                    is Resource.Success -> {
                        _tareasHijo.value = resource.data!!
                    }

                    is Resource.Error -> {
                        _uiState.update { it.copy(errorMessage = resource.message) }
                    }
                }
            }
        }
    }
}

fun PadreUiState.toEntity() = PadreEntity(
    padreId = this.padreId ?: "",
    nombre = nombre ?: "",
    email = this.email ?: "",
    profilePictureUrl = this.fotoPerfil ?: "",
    codigoSala = this.codigoSala ?: ""
)