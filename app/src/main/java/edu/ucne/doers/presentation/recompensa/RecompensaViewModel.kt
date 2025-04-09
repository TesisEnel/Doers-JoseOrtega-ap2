package edu.ucne.doers.presentation.recompensa

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.doers.data.local.entity.RecompensaEntity
import edu.ucne.doers.data.local.model.CondicionRecompensa
import edu.ucne.doers.data.local.model.EstadoRecompensa
import edu.ucne.doers.data.remote.Resource
import edu.ucne.doers.data.repository.PadreRepository
import edu.ucne.doers.data.repository.RecompensaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class RecompensaViewModel @Inject constructor(
    private val recompensaRepository: RecompensaRepository,
    private val padreRepository: PadreRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(RecompensaUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            loadPadreId()
            loadRecompensas()
        }
    }

    private suspend fun loadPadreId() {
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

    fun savePhotoFromUri(context: Context, uri: Uri) {
        viewModelScope.launch {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (bitmap != null) {
                val file = File(context.filesDir, "recompensa_${System.currentTimeMillis()}.jpg")
                FileOutputStream(file).use { out ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
                }
                _uiState.update { it.copy(imagenURL = file.absolutePath) }
            }
        }
    }

    fun loadRecompensas() {
        viewModelScope.launch {
            val padreId = uiState.value.padreId
            if (padreId.isEmpty()) {
                _uiState.update {
                    it.copy(errorMessage = "No se pudo cargar el usuario. Por favor, inicia sesión nuevamente.")
                }
                return@launch
            }
            recompensaRepository.getRecompensasByPadreId(padreId).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        println("Loading recompensas...")
                        _uiState.update { it.copy(isLoading = true) }
                    }
                    is Resource.Success -> {
                        println("Recompensas cargadas: ${resource.data}")
                        _uiState.update {
                            it.copy(
                                recompensas = resource.data ?: emptyList(),
                                isLoading = false,
                                errorMessage = null
                            )
                        }
                    }
                    is Resource.Error -> {
                        println("Error cargando recompensas: ${resource.message}")
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = resource.message ?: "Error al cargar recompensas"
                            )
                        }
                    }
                }
            }
        }
    }

    fun save() {
        viewModelScope.launch {
            if (isValidate()) {
                recompensaRepository.save(_uiState.value.toEntity()).collect { resource ->
                    when (resource) {
                        is Resource.Loading -> _uiState.update { it.copy(isLoading = true) }
                        is Resource.Success -> {
                            _uiState.update { it.copy(isLoading = false, errorMessage = null) }
                            new()
                        }
                        is Resource.Error -> _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = resource.message ?: "Error al guardar recompensa"
                            )
                        }
                    }
                }
            }
        }
    }

    fun delete(recompensa: RecompensaEntity) {
        viewModelScope.launch {
            val file = File(recompensa.imagenURL)
            if (file.exists()) file.delete()
            recompensaRepository.delete(recompensa)
            loadRecompensas()
        }
    }

    fun new() {
        _uiState.update {
            it.copy(
                recompensaId = 0,
                descripcion = "",
                imagenURL = "",
                puntosNecesarios = 0,
                condicionRecompensa = CondicionRecompensa.INACTIVA,
                errorMessage = null
            )
        }
    }

    fun onDescripcionChange(descripcion: String) {
        _uiState.update {
            it.copy(
                descripcion = descripcion,
                errorMessage = if (descripcion.isBlank()) "Debes rellenar el campo Descripción" else null
            )
        }
    }

    fun onPuntosNecesariosChange(puntosNecesarios: Int) {
        _uiState.update { it.copy(puntosNecesarios = puntosNecesarios) }
    }

    fun onEstadoChange(estado: EstadoRecompensa, recompensaId: Int? = null) {
        viewModelScope.launch {
            if (recompensaId != null) {
                val recompensa = recompensaRepository.find(recompensaId)
                if (recompensa != null) {
                    val updatedRecompensa = recompensa.copy(estado = estado)
                    recompensaRepository.save(updatedRecompensa).collect { resource ->
                        when (resource) {
                            is Resource.Success -> {
                                _uiState.update { it.copy(successMessage = "Estado cambiado a ${estado.nombreMostrable()}") }
                            }
                            is Resource.Error -> _uiState.update { it.copy(errorMessage = resource.message) }
                            is Resource.Loading -> _uiState.update { it.copy(isLoading = true) }
                        }
                    }
                }
            } else {
                _uiState.update { it.copy(estado = estado) }
            }
        }
    }

    fun onCondicionChange(recompensaId: Int, nuevaCondicion: CondicionRecompensa) {
        viewModelScope.launch {
            val recompensa = recompensaRepository.find(recompensaId)
            if (recompensa != null) {
                val updatedRecompensa = recompensa.copy(condicion = nuevaCondicion)
                recompensaRepository.save(updatedRecompensa).collect { resource ->
                    when (resource) {
                        is Resource.Loading -> {
                            _uiState.update { it.copy(isLoading = true) }
                        }
                        is Resource.Success -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    successMessage = "Condición cambiada a ${nuevaCondicion.nombreMostrable()}"
                                )
                            }
                            loadRecompensas()
                        }
                        is Resource.Error -> {
                            _uiState.update {
                                it.copy(
                                    isLoading = false,
                                    errorMessage = resource.message ?: "Error al guardar la condición"
                                )
                            }
                        }
                    }
                }
            } else {
                _uiState.update { it.copy(errorMessage = "Recompensa no encontrada") }
            }
        }
    }

    fun clearImage() {
        _uiState.update { current ->
            current.copy(imagenURL = "")
        }
    }

    private fun isValidate(): Boolean {
        val state = uiState.value
        if (state.descripcion.isBlank()) {
            _uiState.update { it.copy(errorMessage = "El campo Descripción es requerido") }
            return false
        }
        if (state.puntosNecesarios <= 0) {
            _uiState.update { it.copy(errorMessage = "El campo Puntos Necesarios debe ser mayor a 0") }
            return false
        }
        return true
    }
}

fun RecompensaUiState.toEntity() = RecompensaEntity(
    recompensaId = recompensaId,
    padreId = padreId,
    hijoId = hijoId,
    descripcion = descripcion,
    imagenURL = imagenURL,
    puntosNecesarios = puntosNecesarios,
    estado = estado,
    condicion = condicionRecompensa
)

fun RecompensaEntity.toUiState() = RecompensaUiState(
    recompensaId = recompensaId,
    padreId = padreId,
    hijoId = hijoId,
    descripcion = descripcion,
    imagenURL = imagenURL,
    puntosNecesarios = puntosNecesarios,
    estado = estado,
    condicionRecompensa = condicion
)