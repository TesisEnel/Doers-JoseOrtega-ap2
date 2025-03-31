package edu.ucne.doers.presentation.hijos

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import edu.ucne.doers.data.local.entity.HijoEntity
import edu.ucne.doers.data.remote.Resource
import edu.ucne.doers.data.repository.HijoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HijoViewModel @Inject constructor(
    private val hijoRepository: HijoRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(HijoUiState())
    val uiState = _uiState.asStateFlow()

    private val sharedPreferences = context.getSharedPreferences("HijoPrefs", Context.MODE_PRIVATE)

    init {
        checkAuthenticatedHijo()
    }

    private fun checkAuthenticatedHijo() {
        viewModelScope.launch {
            val hijoId = sharedPreferences.getInt("hijoId", 0)
            if (hijoId > 0) {
                val hijo = hijoRepository.find(hijoId)
                println("Imagen recuperada desde la BD: ${hijo?.fotoPerfil}")
                if (hijo != null) {
                    _uiState.update {
                        it.copy(
                            hijoId = hijo.hijoId,
                            padreId = hijo.padreId,
                            nombre = hijo.nombre,
                            fotoPerfil = hijo.fotoPerfil,
                            isAuthenticated = true
                        )
                    }
                } else {
                    sharedPreferences.edit().remove("hijoId").apply()
                    _uiState.update {
                        it.copy(
                            hijoId = 0,
                            padreId = "",
                            nombre = "",
                            isAuthenticated = false
                        )
                    }
                }
            }
        }
    }

    fun isAuthenticated(): Boolean {
        return uiState.value.isAuthenticated
    }

    fun loginChild(nombre: String, codigoSala: String) {
        viewModelScope.launch {
            if (nombre.isBlank()) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        signInError = "El nombre no puede estar vacío",
                        isSignInSuccessful = false,
                        isSuccess = false,
                        isAuthenticated = false
                    )
                }
                return@launch
            }

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
                                isSuccess = false,
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
                                fotoPerfil = existingHijo.fotoPerfil,
                                codigoSala = codigoSala,
                                isSuccess = true,
                                isSignInSuccessful = true,
                                isLoading = false,
                                signInError = null,
                                isAuthenticated = true
                            )
                        }
                        sharedPreferences.edit().putInt("hijoId", existingHijo.hijoId).apply()
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
                                    fotoPerfil = savedHijo.fotoPerfil,
                                    codigoSala = codigoSala,
                                    isSuccess = true,
                                    isSignInSuccessful = true,
                                    isLoading = false,
                                    signInError = null,
                                    isAuthenticated = true
                                )
                            }
                            sharedPreferences.edit().putInt("hijoId", savedHijo.hijoId).apply()
                        } else {
                            _uiState.update {
                                it.copy(
                                    isSuccess = false,
                                    isSignInSuccessful = false,
                                    isLoading = false,
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
                            isSuccess = false,
                            isSignInSuccessful = false,
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

    fun getHijosByPadre(padreId: String) {
        viewModelScope.launch {
            hijoRepository.getAll().collect { hijos ->
                val hijosFiltrados = hijos.filter { it.padreId == padreId }
                _uiState.update { it.copy(hijos = hijosFiltrados) }
            }
        }
    }

    fun agregarPuntos(hijo: HijoEntity, puntosAgregados: Int) {
        if (puntosAgregados > 0) {
            val updatedHijo = hijo.copy(saldoActual = hijo.saldoActual + puntosAgregados)
            _uiState.update {
                it.copy(
                    hijos = it.hijos.map { existingHijo ->
                        if (existingHijo.hijoId == hijo.hijoId) updatedHijo else existingHijo
                    }
                )
            }
            viewModelScope.launch {
                hijoRepository.save(updatedHijo)
            }
        }
    }

    fun eliminarHijo(hijo: HijoEntity) {
        viewModelScope.launch {
            hijoRepository.delete(hijo)
        }
    }

    fun actualizarNombre(nombre: String) {
        viewModelScope.launch {
            val updatedHijo = uiState.value.hijoId?.let { hijoRepository.find(it) }

            if (updatedHijo != null) {
                val updatedEntity = updatedHijo.copy(nombre = nombre)
                hijoRepository.save(updatedEntity)
                _uiState.update {
                    it.copy(
                        nombre = nombre
                    )
                }
            }
        }
    }

    fun actualizarFotoPerfil(nuevaFoto: String) {
        viewModelScope.launch {
            val currentHijo = uiState.value.hijoId?.let { hijoRepository.find(it) }

            if (currentHijo != null) {
                val updatedHijo = currentHijo.copy(fotoPerfil = nuevaFoto)
                hijoRepository.save(updatedHijo)
                _uiState.update { it.copy(fotoPerfil = nuevaFoto) }
            }
        }
    }
}
