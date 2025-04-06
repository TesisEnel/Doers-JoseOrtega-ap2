package edu.ucne.doers.presentation.hijos

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import edu.ucne.doers.data.local.entity.HijoEntity
import edu.ucne.doers.data.local.entity.TareaHijo
import edu.ucne.doers.data.local.model.EstadoTareaHijo
import edu.ucne.doers.data.local.model.PeriodicidadTarea
import edu.ucne.doers.data.remote.Resource
import edu.ucne.doers.data.repository.AuthRepository
import edu.ucne.doers.data.repository.HijoRepository
import edu.ucne.doers.data.repository.TareaHijoRepository
import edu.ucne.doers.data.repository.TareaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HijoViewModel @Inject constructor(
    private val hijoRepository: HijoRepository,
    private val tareaRepository: TareaRepository,
    private val tareaHijoRepository: TareaHijoRepository,
    private val authRepository: AuthRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(HijoUiState())
    val uiState = _uiState.asStateFlow()

    private val sharedPreferences = context.getSharedPreferences("HijoPrefs", Context.MODE_PRIVATE)
    private val _periodicidadesDisponibles = MutableStateFlow<List<String>>(emptyList())
    val periodicidadesDisponibles = _periodicidadesDisponibles.asStateFlow()

    init {
        checkAuthenticatedHijo()
        actualizarPeriodicidades()
    }

    private fun checkAuthenticatedHijo() {
        viewModelScope.launch {
            val hijoId = sharedPreferences.getInt("hijoId", 0)
            if (hijoId > 0) {
                val hijo = hijoRepository.find(hijoId)
                println("Imagen recuperada desde la BD: ${hijo?.fotoPerfil}")
                if (hijo != null) {
                    _uiState.update {
                        it.copy(
                            hijoId = hijo.hijoId,
                            padreId = hijo.padreId,
                            nombre = hijo.nombre,
                            fotoPerfil = hijo.fotoPerfil,
                            isAuthenticated = true
                        )
                    }
                    loadTareas()
                } else {
                    sharedPreferences.edit().remove("hijoId").apply()
                    _uiState.update {
                        it.copy(
                            hijoId = 0,
                            padreId = "",
                            nombre = "",
                            isAuthenticated = false
                        )
                    }
                }
            }
        }
    }

    fun isAuthenticated(): Boolean {
        return uiState.value.isAuthenticated
    }

    fun loginChild(nombre: String, codigoSala: String) {
        viewModelScope.launch {
            if (nombre.isBlank()) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        signInError = "El nombre no puede estar vacío",
                        isSignInSuccessful = false,
                        isSuccess = false,
                        isAuthenticated = false
                    )
                }
                return@launch
            }

            _uiState.update { it.copy(isLoading = true, signInError = null) }

            when (val resource = hijoRepository.loginHijo(nombre, codigoSala)) {
                is Resource.Success -> {
                    // 🔐 Obtener el padreId real desde Firebase
                    val firebaseUserId = authRepository.getUser()?.userId

                    // ⛔ Si no lo obtuvo, usar API como respaldo
                    val padreId = firebaseUserId ?: hijoRepository.getPadreIdByCodigoSala(codigoSala)

                    if (padreId == null) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                signInError = "Código de sala inválido",
                                isSignInSuccessful = false,
                                isSuccess = false,
                                isAuthenticated = false
                            )
                        }
                        return@launch
                    }

                    // 🔍 Buscar si ya existe
                    val existingHijo = hijoRepository.findByNombreAndPadreId(nombre, padreId)

                    if (existingHijo != null) {
                        _uiState.update {
                            it.copy(
                                hijoId = existingHijo.hijoId,
                                padreId = existingHijo.padreId,
                                nombre = existingHijo.nombre,
                                fotoPerfil = existingHijo.fotoPerfil,
                                codigoSala = codigoSala,
                                isSuccess = true,
                                isSignInSuccessful = true,
                                isLoading = false,
                                signInError = null,
                                isAuthenticated = true
                            )
                        }
                        sharedPreferences.edit().putInt("hijoId", existingHijo.hijoId).apply()

                    } else {
                        val newHijo = HijoEntity(
                            hijoId = 0,
                            padreId = padreId,
                            nombre = nombre
                        )

                        hijoRepository.save(newHijo).collect { result ->
                            when (result) {
                                is Resource.Success -> {
                                    val savedHijo = hijoRepository.findByNombreAndPadreId(nombre, padreId)
                                    if (savedHijo != null) {
                                        _uiState.update {
                                            it.copy(
                                                hijoId = savedHijo.hijoId,
                                                padreId = savedHijo.padreId,
                                                nombre = savedHijo.nombre,
                                                fotoPerfil = savedHijo.fotoPerfil,
                                                codigoSala = codigoSala,
                                                isSuccess = true,
                                                isSignInSuccessful = true,
                                                isLoading = false,
                                                signInError = null,
                                                isAuthenticated = true
                                            )
                                        }
                                        sharedPreferences.edit().putInt("hijoId", savedHijo.hijoId).apply()
                                    } else {
                                        _uiState.update {
                                            it.copy(
                                                isSuccess = false,
                                                isSignInSuccessful = false,
                                                isLoading = false,
                                                signInError = "Error al guardar el hijo",
                                                isAuthenticated = false
                                            )
                                        }
                                    }
                                }

                                is Resource.Error -> {
                                    _uiState.update {
                                        it.copy(
                                            isSuccess = false,
                                            isSignInSuccessful = false,
                                            isLoading = false,
                                            signInError = result.message,
                                            isAuthenticated = false
                                        )
                                    }
                                }

                                is Resource.Loading -> {
                                    _uiState.update { it.copy(isLoading = true) }
                                }
                            }
                        }
                    }
                }

                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
                            isSuccess = false,
                            isSignInSuccessful = false,
                            isLoading = false,
                            signInError = resource.message,
                            isAuthenticated = false
                        )
                    }
                }

                is Resource.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    //    fun getHijosByPadre(padreId: String) {
//        viewModelScope.launch {
//            hijoRepository.getAll().collect { hijos ->
//                val hijosFiltrados = hijos.filter { it.padreId == padreId }
//                _uiState.update { it.copy(hijos = hijosFiltrados) }
//            }
//        }
//    }
    fun getHijosByPadre(padreId: String) {
        viewModelScope.launch {
            hijoRepository.getAll().collect { result ->
                when (result) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                    is Resource.Success -> {
                        val hijosFiltrados = result.data?.filter { it.padreId == padreId } ?: emptyList()
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                hijos = hijosFiltrados
                            )
                        }
                        Log.d("HijosDebug", "Recibidos: ${result.data}")
                    }
                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = result.message ?: "Error al obtener hijos"
                            )
                        }
                    }
                }
            }
        }
    }


    fun agregarPuntos(hijo: HijoEntity, puntosAgregados: Int) {
        if (puntosAgregados > 0) {
            val updatedHijo = hijo.copy(saldoActual = hijo.saldoActual + puntosAgregados)
            _uiState.update {
                it.copy(
                    hijos = it.hijos.map { existingHijo ->
                        if (existingHijo.hijoId == hijo.hijoId) updatedHijo else existingHijo
                    }
                )
            }
            viewModelScope.launch {
                hijoRepository.save(updatedHijo).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            Log.d("HijoViewModel", "✅ Puntos guardados local y API")
                            getHijosByPadre(updatedHijo.padreId) // 🔁 Recarga desde servidor
                        }

                        is Resource.Error -> {
                            Log.e("HijoViewModel", "❌ Error al guardar puntos: ${result.message}")
                            _uiState.update { it.copy(errorMessage = result.message) }
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    fun eliminarHijo(hijo: HijoEntity) {
        viewModelScope.launch {
            when (val result = hijoRepository.delete(hijo)) {
                is Resource.Success -> {
                    Log.d("HijoViewModel", "✅ Hijo eliminado local y API")
                    getHijosByPadre(hijo.padreId)
                }

                is Resource.Error -> {
                    Log.e("HijoViewModel", "❌ Error al eliminar hijo: ${result.message}")
                    _uiState.update {
                        it.copy(errorMessage = result.message)
                    }
                }

                is Resource.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    fun actualizarNombre(nombre: String) {
        viewModelScope.launch {
            val updatedHijo = uiState.value.hijoId?.let { hijoRepository.find(it) }

            if (updatedHijo != null) {
                val updatedEntity = updatedHijo.copy(nombre = nombre)

                hijoRepository.save(updatedEntity).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            Log.d("HijoViewModel", "✅ Nombre actualizado correctamente")
                            _uiState.update {
                                it.copy(nombre = nombre)
                            }
                        }

                        is Resource.Error -> {
                            Log.e(
                                "HijoViewModel",
                                "❌ Error al actualizar nombre: ${result.message}"
                            )
                            _uiState.update {
                                it.copy(
                                    errorMessage = "Error al actualizar nombre: ${result.message}"
                                )
                            }
                        }

                        is Resource.Loading -> {
                            _uiState.update { it.copy(isLoading = true) }
                        }
                    }
                }
            }
        }
    }

    fun actualizarFotoPerfil(nuevaFoto: String) {
        viewModelScope.launch {
            val currentHijo = uiState.value.hijoId?.let { hijoRepository.find(it) }

            if (currentHijo != null) {
                val updatedHijo = currentHijo.copy(fotoPerfil = nuevaFoto)
                hijoRepository.save(updatedHijo)
                _uiState.update { it.copy(fotoPerfil = nuevaFoto) }
            }
        }
    }


    // Funciones en el ViewModel de DjMarte
    fun completarTarea(tareaId: Int) {
        viewModelScope.launch {

            val hijoId = _uiState.value.hijoId
            if (hijoId == 0) {
                Log.e("ERROR", "El hijoId sigue sin estar inicializado")
                _uiState.update { it.copy(errorMessage = "Error: No se encontró el ID del hijo") }
                return@launch
            }

            Log.d("DEBUG", "Hijo ID en completarTarea: $hijoId")

            _uiState.update {
                it.copy(
                    isLoading = true,
                    ultimaTareaProcesada = tareaId,
                    errorMessage = null,
                    successMessage = null
                )
            }

            try {
                val tareasHijo = tareaHijoRepository.getAll().first()
                val existePendiente = tareasHijo.any {
                    it.tareaId == tareaId &&
                            it.hijoId == hijoId &&
                            it.estado == EstadoTareaHijo.PENDIENTE_VERIFICACION
                }

                if (existePendiente) {
                    throw Exception("Ya tienes esta tarea pendiente de verificación")
                }

                tareaHijoRepository.save(
                    TareaHijo(
                        tareaId = tareaId,
                        hijoId = hijoId,
                        estado = EstadoTareaHijo.PENDIENTE_VERIFICACION
                    )
                )

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        successMessage = "Tarea enviada para revisión",
                        ultimaTareaProcesada = null
                    )
                }

                loadTareas()

            } catch (e: Exception) {
                Log.e("ERROR", "Error al completar tarea: ${e.message}")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Error al completar tarea",
                        ultimaTareaProcesada = null
                    )
                }
            }
        }
    }

    fun loadTareas() {
        viewModelScope.launch {
            try {
                val tareasActivas = tareaRepository.getActiveTasks().first()
                Log.d("DEBUG", "Tareas activas: $tareasActivas")

                val tareasCompletadas = tareaHijoRepository.getAll().first()
                    .filter { it.hijoId == _uiState.value.hijoId }
                Log.d("DEBUG", "Tareas completadas del hijo: $tareasCompletadas")

                val tareasDisponibles = tareasActivas.filter { tarea ->
                    !tareasCompletadas.any {
                        it.tareaId == tarea.tareaId &&
                                it.estado == EstadoTareaHijo.APROBADA
                    }
                }

                Log.d("DEBUG", "Tareas disponibles después de filtro: $tareasDisponibles")

                _uiState.update {
                    it.copy(
                        listaTareas = tareasDisponibles,
                        listaTareasFiltradas = tareasDisponibles,
                        isLoading = false
                    )
                }

            } catch (e: Exception) {
                Log.e("ERROR", "Error cargando tareas: ${e.message}")
                _uiState.update {
                    it.copy(
                        errorMessage = "Error cargando tareas: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun filtrarTareas(periodicidad: String) {
        viewModelScope.launch {
            val todasTareas = _uiState.value.listaTareas

            val filtradas = if (periodicidad == "Todas") {
                todasTareas
            } else {
                todasTareas.filter {
                    it.periodicidad?.nombreMostrable == periodicidad
                }
            }

            _uiState.update {
                it.copy(listaTareasFiltradas = filtradas)
            }
        }
    }

    private fun actualizarPeriodicidades() {
        viewModelScope.launch {
            val periodicidades =
                listOf("Todas") + PeriodicidadTarea.entries.map { it.nombreMostrable }
            _periodicidadesDisponibles.value = periodicidades
        }
    }

    fun clearMessages() {
        _uiState.update {
            it.copy(
                errorMessage = null,
                successMessage = null
            )
        }
    }
}
