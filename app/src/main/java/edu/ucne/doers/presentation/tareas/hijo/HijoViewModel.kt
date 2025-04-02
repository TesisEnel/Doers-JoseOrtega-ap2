package edu.ucne.doers.presentation.tareas.hijo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.doers.data.local.entity.HijoEntity
import edu.ucne.doers.data.local.entity.TareaHijo
import edu.ucne.doers.data.local.model.EstadoTareaHijo
import edu.ucne.doers.data.remote.Resource
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
            listaTareas = emptyList(),
            listaTareasFiltradas = emptyList()
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _periodicidadesDisponibles = MutableStateFlow<List<String>>(emptyList())
    val periodicidadesDisponibles = _periodicidadesDisponibles.asStateFlow()

    init {
        loadTareas()
    }

    fun completarTarea(tareaId: Int) {
        viewModelScope.launch {
            val tarea = tareaRepository.find(tareaId)
            val hijo = hijoRepository.find(_uiState.value.hijoId)

            if (tarea != null && hijo != null) {
                val tareaHijo = TareaHijo(
                    tareaId = tareaId,
                    hijoId = _uiState.value.hijoId,
                    estado = EstadoTareaHijo.PENDIENTE_VERIFICACION
                )
                hijoRepository.insertTareaHijo(tareaHijo)
                loadTareas()
            }
        }
    }

    private fun loadTareas() {
        viewModelScope.launch {
            tareaRepository.getActiveTasks().collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                    }

                    is Resource.Success -> {
                        val tareasActivas = resource.data ?: emptyList()
                        val tareasCompletadas = hijoRepository.getTareasHijo(_uiState.value.hijoId).first()
                        val tareasFiltradas = tareasActivas.filter { tarea ->
                            tareasCompletadas.none { it.tareaId == tarea.tareaId }
                        }

                        _uiState.update {
                            it.copy(
                                listaTareas = tareasFiltradas,
                                listaTareasFiltradas = tareasFiltradas,
                                isLoading = false,
                                errorMessage = null
                            )
                        }

                        actualizarPeriodicidades()
                    }

                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = resource.message ?: "Error al cargar tareas"
                            )
                        }
                    }
                }
            }
        }
    }


    private fun actualizarPeriodicidades() {
        viewModelScope.launch {
            val periodicidades = listOf("Todas") + _uiState.value.listaTareas
                .mapNotNull { it.periodicidad?.nombreMostrable }
                .distinct()

            _periodicidadesDisponibles.value = periodicidades
        }
    }

    fun filtrarTareas(periodicidad: String) {
        viewModelScope.launch {
            val todasLasTareas = _uiState.value.listaTareas

            val tareasFiltradas = if (periodicidad == "Todas") {
                todasLasTareas
            } else {
                todasLasTareas.filter { it.periodicidad?.nombreMostrable == periodicidad }
            }

            _uiState.update { it.copy(listaTareasFiltradas = tareasFiltradas) }
        }
    }
}
