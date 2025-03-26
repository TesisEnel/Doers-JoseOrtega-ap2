package edu.ucne.doers.presentation.tareas.hijo

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.doers.data.local.entity.HijoEntity
import edu.ucne.doers.data.local.entity.TareaHijo
import edu.ucne.doers.data.local.model.EstadoTareaHijo
import edu.ucne.doers.data.repository.HijoRepository
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
    private val tareaRepository: TareaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        HijoUiState(
            hijoId = 0,
            padreId = "",
            nombre = "",
            estado = EstadoTareaHijo.PENDIENTE_VERIFICACION,
            saldoActual = 0,
            balance = 0,
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        loadTareas()
    }

    fun completarTarea(tareaId: Int) {
        viewModelScope.launch {
            val tarea = tareaRepository.find(tareaId)
            val hijo = hijoRepository.find(_uiState.value.hijoId) // Verifica que el hijo existe

            if (tarea != null && hijo != null) { // Asegura que ambos existen antes de insertar
                val tareaHijo = TareaHijo(
                    tareaId = tareaId,
                    hijoId = _uiState.value.hijoId,
                    estado = EstadoTareaHijo.PENDIENTE_VERIFICACION
                )
                hijoRepository.insertTareaHijo(tareaHijo)
                loadTareas()
            } else {
                Log.e("HijoViewModel", "Error: hijoId o tareaId no existen en la base de datos.")
            }
        }
    }


    fun loadTareas() {
        viewModelScope.launch {
            // Obtener tareas activas y las ya completadas por el hijo
            val tareasActivas = tareaRepository.getActiveTasks().first()
            val tareasCompletadas = hijoRepository.getTareasHijo(_uiState.value.hijoId).first()

            // Filtrar: mostrar solo tareas activas NO completadas
            val tareasFiltradas = tareasActivas.filter { tarea ->
                tareasCompletadas.none { it.tareaId == tarea.tareaId }
            }

            _uiState.update {
                it.copy(listaTareas = tareasFiltradas)
            }
        }
    }
}

fun HijoUiState.ToEntity() = HijoEntity(
    hijoId = hijoId,
    padreId = padreId,
    nombre = nombre,
    saldoActual = saldoActual,
    balance = balance
)