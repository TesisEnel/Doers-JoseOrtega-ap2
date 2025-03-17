package edu.ucne.doers.presentation.tareas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.doers.data.local.entity.TareaEntity
import edu.ucne.doers.data.local.model.EstadoTarea
import edu.ucne.doers.data.repository.TareaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TareaViewModel @Inject constructor(
    private val tareaRepository: TareaRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(TareaUiState())
    val uiState = _uiState.asStateFlow()

    init {
        getAllTareas()
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
                        estado = tarea.estado
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

    fun new(){
        _uiState.value = TareaUiState()
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

    fun onEstadoChange(estado: EstadoTarea) {
        _uiState.update {
            it.copy(
                estado = estado
            )
        }
    }

    private fun isValidate(): Boolean {
        val state = uiState.value

        if (state.descripcion.isBlank() || state.puntos <= 0) {
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
    estado = this.estado
)