package edu.ucne.doers.presentation.tareas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.doers.data.local.entity.TareaEntity
import edu.ucne.doers.data.local.model.CondicionTarea
import edu.ucne.doers.data.local.model.EstadoTarea
import edu.ucne.doers.data.local.model.PeriodicidadTarea
import edu.ucne.doers.data.remote.Resource
import edu.ucne.doers.data.repository.PadreRepository
import edu.ucne.doers.data.repository.TareaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TareaViewModel @Inject constructor(
    private val tareaRepository: TareaRepository,
    private val padreRepository: PadreRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        TareaUiState(
            tareaId = 0,
            descripcion = "",
            puntos = 0,
            padreId = "",
            estado = EstadoTarea.PENDIENTE,
            periodicidad = null,
            condicion = CondicionTarea.INACTIVA
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        getAllTareas()
        loadPadreId()
    }

    private fun loadPadreId() {
        viewModelScope.launch {
            val currentPadre = padreRepository.getCurrentUser()
            if (currentPadre == null) {
                _uiState.update {
                    it.copy(errorMessage = "No se encontró un usuario autenticado. Por favor, inicia sesión nuevamente.")
                }
            } else {
                _uiState.update {
                    it.copy(padreId = currentPadre.padreId)
                }
            }
        }
    }

    fun getAllTareas() {
        viewModelScope.launch {
            tareaRepository.getAll().collect { result ->
                when (result) {
                    is Resource.Loading -> _uiState.update { it.copy(isLoading = true) }
                    is Resource.Success -> _uiState.update {
                        it.copy(listaTareas = result.data ?: emptyList(), isLoading = false)
                    }
                    is Resource.Error -> _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message ?: "Error desconocido al cargar tareas"
                        )
                    }
                }
            }
        }
    }

    fun save() {
        viewModelScope.launch {
            if (isValid()) {
                tareaRepository.save(_uiState.value.toEntity()).collect { result ->
                    when (result) {
                        is Resource.Loading -> _uiState.update { it.copy(isLoading = true) }
                        is Resource.Success -> {
                            getAllTareas()
                            _uiState.update { it.copy(errorMessage = null, isLoading = false) }
                            new()
                        }
                        is Resource.Error -> _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = result.message ?: "Error al guardar tarea"
                            )
                        }
                    }
                }
            }
        }
    }

    fun find(tareaId: Int) {
        viewModelScope.launch {
            val tarea = tareaRepository.find(tareaId)
            if (tarea != null) {
                _uiState.update {
                    it.copy(
                        tareaId = tarea.tareaId,
                        descripcion = tarea.descripcion,
                        puntos = tarea.puntos,
                        estado = tarea.estado,
                        periodicidad = tarea.periodicidad,
                        padreId = tarea.padreId,
                        condicion = tarea.condicion
                    )
                }
            }
        }
    }

    fun delete(tarea: TareaEntity) {
        viewModelScope.launch {
            tareaRepository.delete(tarea).let { result ->
                when (result) {
                    is Resource.Success -> getAllTareas()
                    is Resource.Error -> _uiState.update { it.copy(errorMessage = result.message) }
                    is Resource.Loading -> _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }

    fun onCondicionChange(tareaId: Int, nuevaCondicion: CondicionTarea) {
        viewModelScope.launch {
            val tarea = tareaRepository.find(tareaId)
            if (tarea != null) {
                val tareaActualizada = tarea.copy(condicion = nuevaCondicion)
                tareaRepository.save(tareaActualizada).collect {}
                getAllTareas()
            }
        }
    }

    fun new() {
        _uiState.update {
            it.copy(
                tareaId = 0,
                descripcion = "",
                puntos = 0,
                periodicidad = null,
                condicion = CondicionTarea.INACTIVA,
                estado = EstadoTarea.PENDIENTE,
                errorMessage = null
            )
        }
    }

    fun onDescripcionChange(descripcion: String) {
        val descripcionRegularExpression = "^[A-Za-z0-9\\s'.,:áéíóúÁÉÍÓÚñÑ-]+$".toRegex()

        _uiState.update {
            it.copy(
                descripcion = descripcion,
                errorMessage = if (descripcion.isBlank()) "La descripción no puede estar vacía"
                else if (!descripcion.matches(descripcionRegularExpression)) "La descripción solo puede contener letras"
                else null
            )
        }
    }

    fun onPuntosChange(puntos: Int) {
        _uiState.update {
            it.copy(
                puntos = puntos,
                errorMessage = if (puntos <= 0) "Los puntos deben ser mayor a 0" else null
            )
        }
    }

    fun onPeriodicidadChange(periodicidad: PeriodicidadTarea) {
        _uiState.update {
            it.copy(
                periodicidad = periodicidad
            )
        }
    }

    private fun isValid(): Boolean {
        val state = uiState.value
        val isValid = state.descripcion.isNotBlank() && state.puntos > 0 && state.periodicidad != null

        if (!isValid) {
            _uiState.update { it.copy(errorMessage = "Todos los campos son requeridos.") }
        }
        return isValid
    }
}

fun TareaUiState.toEntity() = TareaEntity(
    tareaId = tareaId,
    descripcion = descripcion,
    puntos = puntos,
    padreId = padreId,
    estado = estado,
    periodicidad = periodicidad,
    condicion = condicion
)