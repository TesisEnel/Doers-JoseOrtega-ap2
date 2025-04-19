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
    private val _uiState = MutableStateFlow(
        RecompensaUiState(
            recompensaId = 0,
            descripcion = "",
            puntosNecesarios = 0,
            padreId = "",
            estado = EstadoRecompensa.PENDIENTE,
            condicion = CondicionRecompensa.INACTIVA
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
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
                getAllRecompensas()
            }
        }
    }

    private fun handleError(errorMessage: String?) {
        _uiState.update {
            it.copy(
                isLoading = false,
                errorMessage = errorMessage ?: "Error desconocido"
            )
        }
    }

    fun getAllRecompensas() {
        val padreId = uiState.value.padreId
        if (padreId.isNotBlank()) {
            viewModelScope.launch {
                recompensaRepository.getAll(padreId).collect { result ->
                    when (result) {
                        is Resource.Loading -> _uiState.update { it.copy(isLoading = true) }
                        is Resource.Success -> _uiState.update {
                            it.copy(listaRecompensas = result.data ?: emptyList(), isLoading = false)
                        }
                        is Resource.Error -> handleError(result.message)
                    }
                }
            }
        } else {
            handleError("No se ha encontrado un ID de padre válido para cargar recompensas.")
        }
    }

    fun savePhotoFromUri(context: Context, uri: Uri) {
        viewModelScope.launch {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()

                if (bitmap != null) {
                    val file = File(context.filesDir, "recompensa_${System.currentTimeMillis()}.jpg")
                    FileOutputStream(file).use { out ->
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
                    }
                    _uiState.update { it.copy(imagenURL = file.absolutePath) }
                } else {
                    handleError("No se pudo cargar la imagen")
                }
            } catch (e: Exception) {
                handleError("Error al procesar la imagen: ${e.message}")
            }
        }
    }

    fun save() {
        viewModelScope.launch {
            if (uiState.value.padreId.isBlank()) {
                handleError("ID de padre no válido")
                return@launch
            }
            if (isValid()) {
                recompensaRepository.save(_uiState.value.toEntity()).collect { result ->
                    when (result) {
                        is Resource.Loading -> _uiState.update { it.copy(isLoading = true) }
                        is Resource.Success -> {
                            getAllRecompensas() // Refresca la lista desde Room
                            _uiState.update { it.copy(errorMessage = null, isLoading = false) }
                            new()
                        }
                        is Resource.Error -> handleError(result.message)
                    }
                }
            }
        }
    }

    fun find(recompensaId: Int) {
        viewModelScope.launch {
            val recompensa = recompensaRepository.find(recompensaId)
            if (recompensa != null) {
                _uiState.update {
                    it.copy(
                        recompensaId = recompensa.recompensaId,
                        descripcion = recompensa.descripcion,
                        puntosNecesarios = recompensa.puntosNecesarios,
                        estado = recompensa.estado,
                        padreId = recompensa.padreId,
                        condicion = recompensa.condicion
                    )
                }
            } else {
                handleError("No se encontró la recompensa con ID $recompensaId")
            }
        }
    }

    fun delete(recompensa: RecompensaEntity) {
        viewModelScope.launch {
            when (val result = recompensaRepository.delete(recompensa)) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                    getAllRecompensas()
                }
                is Resource.Error -> handleError(result.message)
                is Resource.Loading -> _uiState.update { it.copy(isLoading = true) }
            }
        }
    }

    fun new() {
        _uiState.update {
            it.copy(
                recompensaId = 0,
                descripcion = "",
                puntosNecesarios = 0,
                condicion = CondicionRecompensa.INACTIVA,
                estado = EstadoRecompensa.PENDIENTE,
                imagenURL = "",
                errorMessage = null
            )
        }
    }

    fun onDescripcionChange(descripcion: String) {
        val descripcionRegularExpression = "^[A-Za-z0-9\\s'.,:;!?áéíóúÁÉÍÓÚñÑ-]+$".toRegex()

        _uiState.update {
            it.copy(
                descripcion = descripcion,
                errorMessage = when {
                    descripcion.isBlank() -> "La descripción no puede estar vacía"
                    !descripcion.matches(descripcionRegularExpression) -> "La descripción contiene caracteres no permitidos"
                    else -> null
                }
            )
        }
    }

    fun onCondicionChange(recompensaId: Int, nuevaCondicion: CondicionRecompensa) {
        viewModelScope.launch {
            val recompensa = recompensaRepository.find(recompensaId)
            if (recompensa != null) {
                val recompensaActualizada = recompensa.copy(condicion = nuevaCondicion)
                recompensaRepository.save(recompensaActualizada).collect {}
                getAllRecompensas()
            } else {
                handleError("No se encontró la recompensa con ID $recompensaId")
            }
        }
    }

    fun onPuntosNecesariosChange(puntosNecesarios: Int) {
        _uiState.update {
            it.copy(
                puntosNecesarios = puntosNecesarios,
                errorMessage = if (puntosNecesarios <= 0) "Los puntos deben ser mayor a 0" else null
            )
        }
    }

    fun clearImage() {
        _uiState.update { it.copy(imagenURL = "") }
    }

    private fun isValid(): Boolean {
        val state = uiState.value
        val isValid = state.descripcion.isNotBlank() && state.puntosNecesarios > 0

        if (!isValid) {
            _uiState.update { it.copy(errorMessage = "Todos los campos son requeridos.") }
        }
        return isValid
    }
}

fun RecompensaUiState.toEntity() = RecompensaEntity(
    recompensaId = recompensaId,
    descripcion = descripcion,
    puntosNecesarios = puntosNecesarios,
    padreId = padreId,
    estado = estado,
    condicion = condicion,
    imagenURL = imagenURL
)