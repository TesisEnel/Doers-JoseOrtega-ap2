package edu.ucne.doers.presentation.recompensa

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.doers.data.local.entity.RecompensaEntity
import edu.ucne.doers.data.local.model.EstadoRecompensa
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
    private val padreRepository: PadreRepository,
    application: Application
) : ViewModel() {
    private val appContext: Application = application
    private val _uiState = MutableStateFlow(RecompensaUiState())
    val uiState = _uiState.asStateFlow()

    private suspend fun loadPadreId() {
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

    init {
        viewModelScope.launch {
            loadPadreId()
            loadRecompensas()
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
            if (padreId.isNullOrEmpty()) {
                Log.e("RecompensaViewModel", "padreId no está disponible al cargar recompensas")
                _uiState.update {
                    it.copy(errorMessage = "No se pudo cargar el usuario. Por favor, inicia sesión nuevamente.")
                }
                return@launch
            }
            recompensaRepository.getRecompensasByPadreId(padreId).collect { recompensas ->
                _uiState.update { it.copy(recompensas = recompensas.map { it.toUiState() }) }
                Log.d("RecompensaViewModel", "Recompensas cargadas para padreId $padreId: $recompensas")
            }
        }
    }

    fun save() {
        viewModelScope.launch {
            if(isValidate()) {
                recompensaRepository.save(_uiState.value.toEntity())
                _uiState.update { it.copy(errorMessage = null) }
                new()
            }
        }
    }

    fun saveRecompensa(recompensa: RecompensaEntity) {
        viewModelScope.launch {
            recompensaRepository.save(recompensa)
            Log.d("RecompensaViewModel", "Recompensa actualizada: $recompensa")
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
                estado = EstadoRecompensa.DISPONIBLE,
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
                    val updatedRecompensa = recompensa.copy(estado = estado.toString())
                    recompensaRepository.save(updatedRecompensa)
                    loadRecompensas()
                }
            } else {
                _uiState.update { it.copy(estado = estado) }
            }
        }
    }

    fun updateAvailability(recompensaId: Int, isAvailable: Boolean) {
        viewModelScope.launch {
            val recompensa = recompensaRepository.find(recompensaId)
            if (recompensa != null) {
                val newEstado = if (isAvailable) EstadoRecompensa.DISPONIBLE else EstadoRecompensa.AGOTADA
                val updatedRecompensa = recompensa.copy(estado = newEstado.toString())
                recompensaRepository.save(updatedRecompensa)
                loadRecompensas()
            }
        }
    }

    fun find(recompensaId: Int) {
        viewModelScope.launch {
            if (recompensaId > 0) {
                val dto = recompensaRepository.find(recompensaId)
                if (dto != null) {
                    _uiState.update {
                        it.copy(
                            recompensaId = dto.recompensaId,
                            descripcion = dto.descripcion,
                            imagenURL = dto.imagenURL,
                            puntosNecesarios = dto.puntosNecesarios,
                            estado = EstadoRecompensa.valueOf(dto.estado)
                        )
                    }
                }
            }
        }
    }
    private fun isValidate(): Boolean {
        val state = uiState.value

        if (state.descripcion.isBlank() || state.puntosNecesarios <= 0) {
            _uiState.update { it.copy(errorMessage = "Todos los campos son requeridos.") }
            return false
        }
        return true
    }
}

fun RecompensaUiState.toEntity() = RecompensaEntity(
    recompensaId = recompensaId,
    padreId = padreId,
    descripcion = descripcion,
    imagenURL = imagenURL,
    puntosNecesarios = puntosNecesarios,
    estado = estado.toString()
)