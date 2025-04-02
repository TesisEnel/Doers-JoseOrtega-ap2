package edu.ucne.doers.presentation.tareas.hijo

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
import edu.ucne.doers.data.repository.HijoRepository
import edu.ucne.doers.data.repository.TareaHijoRepository
import edu.ucne.doers.data.repository.TareaRepository
import kotlinx.coroutines.delay
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
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        HijoUiState(
            hijoId = 0,
            padreId = "",
            nombre = "",
            saldoActual = 0,
            balance = 0,
            listaTareas = emptyList(),
            listaTareasFiltradas = emptyList(),
            errorMessage = null,
            successMessage = null,
            isLoading = false,
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _periodicidadesDisponibles = MutableStateFlow<List<String>>(emptyList())
    val periodicidadesDisponibles = _periodicidadesDisponibles.asStateFlow()

    private val sharedPreferences = context.getSharedPreferences("HijoPrefs", Context.MODE_PRIVATE)

    init {
        loadTareas()
        actualizarPeriodicidades()
        cargarHijoId()

    }

    private fun cargarHijoId() {
        viewModelScope.launch {
            try {
                val hijoId = sharedPreferences.getInt("hijoId", 0)
                if (hijoId > 0) {
                    val hijo = hijoRepository.find(hijoId)
                    if (hijo != null) {
                        Log.d("DEBUG", "Hijo cargado con ID: ${hijo.hijoId}")
                        _uiState.update { it.copy(hijoId = hijo.hijoId) }
                    } else {
                        Log.e("ERROR", "No se encontró el hijo con ID: $hijoId")
                        sharedPreferences.edit().remove("hijoId").apply()
                    }
                }
            } catch (e: Exception) {
                Log.e("ERROR", "Error al cargar el hijoId: ${e.message}")
            }
        }
    }

    fun loginChild(nombre: String, codigoSala: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, signInError = null) }

            when (val resource = hijoRepository.loginHijo(nombre, codigoSala)) {
                is Resource.Success -> {
                    val padreId = hijoRepository.getPadreIdByCodigoSala(codigoSala)
                    if (padreId == null) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                signInError = "Código de sala inválido",
                                isSignInSuccessful = false,
                                isAuthenticated = false
                            )
                        }
                        return@launch
                    }

                    val existingHijo = hijoRepository.findByNombreAndPadreId(nombre, padreId)
                    if (existingHijo != null) {
                        _uiState.update {
                            it.copy(
                                hijoId = existingHijo.hijoId,
                                padreId = existingHijo.padreId,
                                nombre = existingHijo.nombre,
                                isAuthenticated = true
                            )
                        }
                        // Guardamos el hijoId en SharedPreferences para persistencia
                        setHijoId(existingHijo.hijoId)
                    } else {
                        val newHijo = HijoEntity(
                            hijoId = 0,
                            padreId = padreId,
                            nombre = nombre
                        )
                        hijoRepository.save(newHijo)
                        val savedHijo = hijoRepository.findByNombreAndPadreId(nombre, padreId)
                        if (savedHijo != null) {
                            _uiState.update {
                                it.copy(
                                    hijoId = savedHijo.hijoId,
                                    padreId = savedHijo.padreId,
                                    nombre = savedHijo.nombre,
                                    isAuthenticated = true
                                )
                            }
                            // Guardamos el hijoId en SharedPreferences para persistencia
                            setHijoId(savedHijo.hijoId)
                        } else {
                            _uiState.update {
                                it.copy(
                                    isSuccess = false,
                                    signInError = "Error al crear el hijo",
                                    isAuthenticated = false
                                )
                            }
                        }
                    }
                }

                is Resource.Error -> {
                    _uiState.update {
                        it.copy(
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

    fun setHijoId(hijoId: Int) {
        sharedPreferences.edit().putInt("hijoId", hijoId).apply()
        _uiState.update { it.copy(hijoId = hijoId) }
    }

    fun completarTarea(tareaId: Int) {
        viewModelScope.launch {
            if (_uiState.value.hijoId == 0) {
                Log.d("DEBUG", "hijoId no está inicializado, intentando cargarlo...")
                cargarHijoId()
            }

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


    private fun loadTareas() {
        viewModelScope.launch {
            try {
                val tareasActivas = tareaRepository.getActiveTasks().first()
                val tareasCompletadas = tareaHijoRepository.getAll().first()
                    .filter { it.hijoId == _uiState.value.hijoId }

                val tareasDisponibles = tareasActivas.filter { tarea ->
                    !tareasCompletadas.any {
                        it.tareaId == tarea.tareaId &&
                                it.estado == EstadoTareaHijo.APROBADA
                    }
                }

                _uiState.update {
                    it.copy(
                        listaTareas = tareasDisponibles,
                        listaTareasFiltradas = tareasDisponibles,
                        isLoading = false
                    )
                }

            } catch (e: Exception) {
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


