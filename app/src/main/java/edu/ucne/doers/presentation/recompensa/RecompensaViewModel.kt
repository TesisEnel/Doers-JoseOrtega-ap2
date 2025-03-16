package edu.ucne.doers.presentation.recompensa

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import edu.ucne.doers.data.local.entity.RecompensaEntity
import edu.ucne.doers.data.local.model.EstadoRecompensa
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
    application: Application
) : ViewModel() {
    private val appContext: Application = application
    private val _uiState = MutableStateFlow(RecompensaUiState())
    val uiState = _uiState.asStateFlow()

    init {
        getRecompensas()
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

    fun getRecompensas() {
        viewModelScope.launch {
            recompensaRepository.getAll().collect { recompensas ->
                _uiState.update { it.copy(recompensas = recompensas) }
            }
        }
    }

    fun save(recompensa: RecompensaEntity? = null) {
        viewModelScope.launch {
            val entityToSave = recompensa ?: _uiState.value.toEntity()
            recompensaRepository.save(entityToSave)
            getRecompensas()
        }
    }

    fun delete(recompensa: RecompensaEntity) {
        viewModelScope.launch {
            val file = File(recompensa.imagenURL)
            if (file.exists()) file.delete()
            recompensaRepository.delete(recompensa)
            getRecompensas()
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
                    val updatedRecompensa = recompensa.copy(estado = estado)
                    recompensaRepository.save(updatedRecompensa)
                    getRecompensas()
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
                val updatedRecompensa = recompensa.copy(estado = newEstado)
                recompensaRepository.save(updatedRecompensa)
                getRecompensas() // Refrescar lista solo con los datos actualizados
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
                            estado = dto.estado
                        )
                    }
                }
            }
        }
    }
}

fun RecompensaUiState.toEntity() = RecompensaEntity(
    recompensaId = recompensaId,
    //padreId = "",
    descripcion = descripcion,
    imagenURL = imagenURL,
    puntosNecesarios = puntosNecesarios,
    estado = estado
)