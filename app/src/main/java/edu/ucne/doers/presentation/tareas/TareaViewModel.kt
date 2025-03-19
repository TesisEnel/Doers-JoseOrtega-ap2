package edu.ucne.doers.presentation.tareas

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.doers.data.local.entity.TareaEntity
import edu.ucne.doers.data.local.model.CondicionTarea
import edu.ucne.doers.data.local.model.PeriodicidadTarea
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
    private val _uiState = MutableStateFlow(TareaUiState(
        tareaId = 0,
        descripcion = "",
        puntos = 0,
        padreId = "",
        imagenURL = "",
        periodicidad = null,
    ))
    val uiState = _uiState.asStateFlow()

    private fun loadPadreId() {
        viewModelScope.launch {
            val currentPadre = padreRepository.getCurrentUser()
            if (currentPadre == null) {
                Log.e("RecompensaViewModel", "Error: No se encontró un PadreEntity para el usuario autenticado")
                _uiState.update {
                    it.copy(errorMessage = "No se encontró un usuario autenticado. Por favor, inicia sesión nuevamente.")
                }
            } else {
                _uiState.update {
                    it.copy(padreId = currentPadre.padreId)
                }
                Log.d("RecompensaViewModel", "padreId cargado: ${currentPadre.padreId}")
            }
        }
    }

    init {
        getAllTareas()
        loadPadreId()
    }

    fun getAllTareas() {
        viewModelScope.launch {
            tareaRepository.getAll().collect { listaTareas ->
                _uiState.update {
                    it.copy(listaTareas = listaTareas)

                }
            }
        }
    }

    fun save(){
        viewModelScope.launch{
            if(isValidate()) {
                tareaRepository.save(_uiState.value.toEntity())
                _uiState.update { it.copy(errorMessage = null) }
                new()
            }
        }
    }

    fun find(tareaId: Int){
        viewModelScope.launch {
            val tarea = tareaRepository.find(tareaId)

            if (tarea != null) {
                _uiState.update {
                    it.copy(
                        tareaId = tarea.tareaId,
                        descripcion = tarea.descripcion,
                        puntos = tarea.puntos,
                        estado = tarea.estado,
                        periodicidad = tarea.periodicidad
                    )
                }
            }
        }
    }

    fun delete(tarea: TareaEntity){
        viewModelScope.launch {
            tareaRepository.delete(tarea)
        }
    }

    fun onCondicionChange(tareaId: Int, nuevaCondicion: CondicionTarea) {
        viewModelScope.launch {
            val tarea = tareaRepository.find(tareaId)
            if (tarea != null) {
                val tareaActualizada = tarea.copy(condicion = nuevaCondicion)
                tareaRepository.save(tareaActualizada)
            }
        }
    }

    fun new(){
        _uiState.value = TareaUiState(
            tareaId = 0,
            descripcion = "",
            puntos = 0,
            padreId = "",
            imagenURL = "",
            periodicidad = null
        )
    }

    fun onDescripcionChange(descripcion: String){
        val descripcionRegularExpression = "^[A-Za-z0-9\\s'.,:áéíóúÁÉÍÓÚ-]+$".toRegex()
        _uiState.update {
            it.copy(
                descripcion = descripcion,
                errorMessage = if (descripcion.isBlank()) "La descripción no puede estar vacía"
                else if (!descripcion.matches(descripcionRegularExpression)) "La descripción solo puede contener letras"
                else null
            )
        }
    }

    fun onPuntosChange(puntos: Int){
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

    private fun isValidate(): Boolean {
        val state = uiState.value

        if (state.descripcion.isBlank() || state.puntos <= 0 || state.periodicidad == null) {
            _uiState.update { it.copy(errorMessage = "Todos los campos son requeridos.") }
            return false
        }
        return true
    }
}

fun TareaUiState.toEntity() = TareaEntity(
    tareaId = this.tareaId,
    descripcion = this.descripcion,
    puntos = this.puntos,
    padreId = this.padreId,
    imagenURL = this.imagenURL,
    estado = this.estado,
    periodicidad = this.periodicidad,
    condicion = this.condicion
)